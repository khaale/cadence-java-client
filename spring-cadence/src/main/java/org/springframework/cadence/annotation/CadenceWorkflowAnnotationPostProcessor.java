package org.springframework.cadence.annotation;

import com.uber.cadence.worker.Worker;
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
  private Worker.Factory workerFactory;

  @Override
  public void afterSingletonsInstantiated() {

    this.workflowDefinitions.forEach(this::registerWorkflow);
  }

  private void registerWorkflow(WorkflowBeanDefinition definition) {

    Worker worker = beanFactory.getBean(Worker.class);

    //noinspection unchecked
    worker.addWorkflowImplementationFactory(
        definition.getWorkflowInterface(), () -> beanFactory.getBean(definition.getBeanName()));

    workerFactory = beanFactory.getBean(Worker.Factory.class);
    workerFactory.start();
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

    workflowDefinitions =
        Arrays.stream(beanFactory.getBeanDefinitionNames())
            .map(bn -> this.getWorkflowDefinition(beanFactory, bn))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

    // forcibly set wf scopes to 'prototype' in order to get them instantiated every time
    workflowDefinitions.forEach(wd -> wd.getBeanDefinition().setScope("prototype"));
  }

  private Optional<WorkflowBeanDefinition> getWorkflowDefinition(
      ConfigurableListableBeanFactory beanFactory, String beanName) {
    this.beanFactory = beanFactory;
    try {
      BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

      String className = beanDefinition.getBeanClassName();
      if (className == null) {
        return Optional.empty();
      }

      Class clazz = Class.forName(className);

      Optional<Class> workflowInterfaceOpt =
          Arrays.stream(clazz.getInterfaces()).filter(this::isWorkflow).findFirst();
      return workflowInterfaceOpt.map(
          aClass -> new WorkflowBeanDefinition(beanName, beanDefinition, aClass, clazz));

    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void destroy() throws Exception {
    workerFactory.shutdown();
  }

  private static class WorkflowBeanDefinition {
    private String beanName;
    private BeanDefinition beanDefinition;
    private Class workflowInterface;
    private Class workflowImpl;

    WorkflowBeanDefinition(
        String beanName,
        BeanDefinition beanDefinition,
        Class workflowInterface,
        Class workflowImpl) {
      this.beanName = beanName;
      this.beanDefinition = beanDefinition;
      this.workflowInterface = workflowInterface;
      this.workflowImpl = workflowImpl;
    }

    String getBeanName() {
      return beanName;
    }

    BeanDefinition getBeanDefinition() {
      return beanDefinition;
    }

    Class getWorkflowInterface() {
      return workflowInterface;
    }

    public Class getWorkflowImpl() {
      return workflowImpl;
    }
  }
}
