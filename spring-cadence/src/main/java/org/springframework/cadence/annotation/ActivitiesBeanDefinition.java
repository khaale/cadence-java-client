package org.springframework.cadence.annotation;

public class ActivitiesBeanDefinition {
  private String worker;
  private String beanName;

  ActivitiesBeanDefinition(String worker, String beanName) {
    this.worker = worker;
    this.beanName = beanName;
  }

  String getBeanName() {
    return beanName;
  }

  public String getWorker() {
    return worker;
  }
}
