package org.springframework.cadence.annotation;

import com.uber.cadence.workflow.WorkflowMethod;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class CadenceWorkflowAnnotationPostProcessor
    implements SmartInitializingSingleton, BeanFactoryPostProcessor, DisposableBean {

  private ConfigurableListableBeanFactory beanFactory;
  private List<WorkflowBeanDefinition> workflowDefinitions;
  private List<ActivitiesBeanDefinition> activityDefinitions;
  private CadenceWorkflowRegistrar registrar;

  @Override
  public void afterSingletonsInstantiated() {

    this.registrar = beanFactory.getBean(CadenceWorkflowRegistrar.class);

    this.registrar.setActivities(this.activityDefinitions);
    this.registrar.setWorkflows(this.workflowDefinitions);

    registrar.registerWorkers();
    registrar.start();
  }

  private boolean isWorkflow(Class<?> workflowInterface) {
    Method result = null;
    for (Method m : workflowInterface.getMethods()) {
      if (m.getAnnotation(WorkflowMethod.class) != null) {
        if (result != null) {
          throw new IllegalArgumentException(
              "Workflow interface must have exactly one method "
                  + "annotated with @WorkflowMethod. Found \""
                  + result
                  + "\" and \""
                  + m
                  + "\"");
        }
        result = m;
      }
    }

    return result != null;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {

    this.beanFactory = beanFactory;

    workflowDefinitions =
        Arrays.stream(beanFactory.getBeanNamesForAnnotation(CadenceWorkflow.class))
            .map(this::getWorkflowDefinition)
            .collect(Collectors.toList());

    activityDefinitions =
        Arrays.stream(beanFactory.getBeanNamesForAnnotation(CadenceActivities.class))
            .map(this::getActivitiesDefinition)
            .collect(Collectors.toList());
  }

  private ActivitiesBeanDefinition getActivitiesDefinition(String beanName) {
    try {
      BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

      String className = beanDefinition.getBeanClassName();
      if (className == null) {
        throw new RuntimeException("Bean should provide a className");
      }

      Class clazz = Class.forName(className);
      CadenceActivities annotation =
          (CadenceActivities) clazz.getAnnotation(CadenceActivities.class);

      return new ActivitiesBeanDefinition(annotation.worker(), beanName);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private WorkflowBeanDefinition getWorkflowDefinition(String beanName) {
    try {
      BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

      String className = beanDefinition.getBeanClassName();
      if (className == null) {
        throw new RuntimeException("Bean should provide a className");
      }

      Class clazz = Class.forName(className);
      CadenceWorkflow annotation = (CadenceWorkflow) clazz.getAnnotation(CadenceWorkflow.class);

      Optional<Class> workflowInterfaceOpt =
          Arrays.stream(clazz.getInterfaces()).filter(this::isWorkflow).findFirst();
      return workflowInterfaceOpt
          .map(
              aClass ->
                  new WorkflowBeanDefinition(
                      annotation.worker(), beanName, beanDefinition, aClass, clazz))
          .orElseThrow(
              () -> new RuntimeException("Unable to find an interface with workflow marker."));

    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void destroy() throws Exception {
    registrar.stop();
  }
}
