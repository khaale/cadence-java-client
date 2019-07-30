package org.springframework.cadence.sample.workflows;

import org.springframework.stereotype.Component;

@Component
public class GreetingWorkflowImpl implements GreetingWorkflow {

  private int counter = 0;

  public GreetingWorkflowImpl() {
    System.out.println("GreetingWorkflowImpl created");
  }

  @Override
  public String getGreeting(String name) {
    counter++;
    return "Hello " + name + " (" + counter + ")";
  }
}
