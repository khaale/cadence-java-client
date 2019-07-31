package org.springframework.cadence.annotation;

import com.uber.cadence.worker.Worker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.BeanFactory;

public class CadenceWorkflowRegistrar {

  private Worker.Factory factory;
  private BeanFactory beanFactory;

  private List<WorkflowBeanDefinition> workflows = new ArrayList<>();
  private List<ActivitiesBeanDefinition> activities = new ArrayList<>();
  private Map<String, Worker> workerMap = new HashMap<>();

  public CadenceWorkflowRegistrar(Worker.Factory factory, BeanFactory beanFactory) {
    this.factory = factory;
    this.beanFactory = beanFactory;
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

  private Worker getWorker(String workerName) {
    return workerMap.computeIfAbsent(workerName, w -> factory.newWorker("my_task_list"));
  }

  private void registerActivities(ActivitiesBeanDefinition definition) {
    Worker worker = getWorker(definition.getWorker());
    worker.registerActivitiesImplementations(beanFactory.getBean(definition.getBeanName()));
  }

  private void registerWorkflow(WorkflowBeanDefinition definition) {
    Worker worker = getWorker(definition.getWorker());

    //noinspection unchecked
    worker.addWorkflowImplementationFactory(
        definition.getWorkflowInterface(), () -> beanFactory.getBean(definition.getBeanName()));
  }
}
