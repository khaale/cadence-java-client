package org.springframework.cadence.annotation;

import com.uber.cadence.worker.Worker;
import java.util.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cadence.configuration.CadenceProperties;

public class CadenceWorkflowRegistrar {

  private Worker.Factory factory;
  private BeanFactory beanFactory;
  private CadenceProperties cadenceProperties;

  private List<WorkflowBeanDefinition> workflows = new ArrayList<>();
  private List<ActivitiesBeanDefinition> activities = new ArrayList<>();
  private Map<String, Worker> workerMap = new HashMap<>();

  public CadenceWorkflowRegistrar(
      Worker.Factory factory, BeanFactory beanFactory, CadenceProperties cadenceProperties) {
    this.factory = factory;
    this.beanFactory = beanFactory;
    this.cadenceProperties = cadenceProperties;
  }

  public void setWorkflows(List<WorkflowBeanDefinition> workflows) {
    this.workflows = workflows;
  }

  public void setActivities(List<ActivitiesBeanDefinition> activities) {
    this.activities = activities;
  }

  public void addWorkflowDefinition(WorkflowBeanDefinition definition) {
    workflows.add(definition);
  }

  public void addActivityDefinition(ActivitiesBeanDefinition definition) {
    activities.add(definition);
  }

  public void registerWorkers() {
    workflows.forEach(this::registerWorkflow);
    activities.forEach(this::registerActivities);
  }

  public void start() {
    factory.start();
  }

  public void stop() {
    factory.shutdown();
  }

  private Worker getOrNewWorker(String workerName) {
    return workerMap.computeIfAbsent(workerName, this::newWorker);
  }

  private Worker newWorker(String workerName) {
    String taskList =
        Optional.ofNullable(cadenceProperties.getWorkers().getOrDefault(workerName, null))
            .orElseThrow(
                () ->
                    new RuntimeException(
                        String.format("Missing configuration for worker '%s'", workerName)))
            .getTaskList();
    return factory.newWorker(taskList);
  }

  private void registerActivities(ActivitiesBeanDefinition definition) {
    Worker worker = getOrNewWorker(definition.getWorker());
    worker.registerActivitiesImplementations(beanFactory.getBean(definition.getBeanName()));
  }

  private void registerWorkflow(WorkflowBeanDefinition definition) {
    Worker worker = getOrNewWorker(definition.getWorker());

    //noinspection unchecked
    worker.addWorkflowImplementationFactory(
        definition.getWorkflowInterface(), () -> beanFactory.getBean(definition.getBeanName()));
  }
}
