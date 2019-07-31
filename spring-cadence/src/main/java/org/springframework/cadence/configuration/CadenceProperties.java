package org.springframework.cadence.configuration;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cadence")
public class CadenceProperties {

  private String host = "localhost";
  private int port = 7933;
  @NotBlank private String domain;

  public Map<String, Worker> getWorkers() {
    return workers;
  }

  private Map<String, Worker> workers = new HashMap<>();

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public static class Worker {
    @NotBlank private String taskList;

    public String getTaskList() {
      return taskList;
    }

    public void setTaskList(String taskList) {
      this.taskList = taskList;
    }
  }
}
