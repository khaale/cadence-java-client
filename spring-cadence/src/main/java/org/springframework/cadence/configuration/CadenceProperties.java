package org.springframework.cadence.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cadence")
public class CadenceProperties {

  private String domain;
  private String taskList;

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getTaskList() {
    return taskList;
  }

  public void setTaskList(String taskList) {
    this.taskList = taskList;
  }
}
