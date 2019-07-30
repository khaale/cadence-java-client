package org.springframework.cadence.sample.workflows;

import com.uber.cadence.workflow.WorkflowMethod;

public interface GreetingWorkflow {

  @WorkflowMethod(executionStartToCloseTimeoutSeconds = 100, taskList = "my_task_list")
  String getGreeting(String name);
}
