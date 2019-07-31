package org.springframework.cadence.sample.workflows;

import static org.springframework.cadence.sample.DemoApp.GREETING_WORKER;

import com.uber.cadence.workflow.Workflow;
import org.springframework.cadence.annotation.CadenceWorkflow;
import org.springframework.cadence.sample.activities.GreetingActivities;

@CadenceWorkflow(worker = GREETING_WORKER)
public class GreetingWorkflowImpl implements GreetingWorkflow {

  private int counter = 0;

  private final GreetingActivities activities = Workflow.newActivityStub(GreetingActivities.class);

  @Override
  public String getGreeting(String name) {
    counter++;
    return activities.composeGreeting(name) + " (" + counter + ")";
  }
}
