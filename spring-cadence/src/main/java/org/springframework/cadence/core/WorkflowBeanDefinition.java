package org.springframework.cadence.core;

import org.springframework.beans.factory.config.BeanDefinition;

public class WorkflowBeanDefinition {
  private String worker;
  private String beanName;
  private BeanDefinition beanDefinition;
  private Class workflowInterface;
  private Class workflowImpl;

  WorkflowBeanDefinition(
      String worker,
      String beanName,
      BeanDefinition beanDefinition,
      Class workflowInterface,
      Class workflowImpl) {
    this.worker = worker;
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

  public String getWorker() {
    return worker;
  }
}
