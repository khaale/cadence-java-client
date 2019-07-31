package org.springframework.cadence.sample.workflows;

import com.uber.cadence.workflow.WorkflowMethod;

public interface GreetingWorkflow {

  @WorkflowMethod
  String getGreeting(String name);
}
