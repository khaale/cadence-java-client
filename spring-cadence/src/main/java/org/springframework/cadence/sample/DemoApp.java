package org.springframework.cadence.sample;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;
import java.time.Duration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cadence.annotation.EnableCadence;
import org.springframework.cadence.sample.workflows.GreetingWorkflow;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCadence
public class DemoApp {

  public static final String GREETING_WORKER = "greetingWorker";
  public static final String GREETING_TASK_LIST = "GREETING";

  public static void main(String[] args) {
    ConfigurableApplicationContext app = SpringApplication.run(DemoApp.class, args);

    WorkflowClient client = WorkflowClient.newInstance("sample");

    WorkflowOptions wfOpts =
        new WorkflowOptions.Builder()
            .setExecutionStartToCloseTimeout(Duration.ofSeconds(10))
            .setTaskList(GREETING_TASK_LIST)
            .build();

    GreetingWorkflow wf1 = client.newWorkflowStub(GreetingWorkflow.class, wfOpts);
    System.out.println(wf1.getGreeting("world"));
    GreetingWorkflow wf2 = client.newWorkflowStub(GreetingWorkflow.class, wfOpts);
    System.out.println(wf2.getGreeting("another world"));

    System.exit(0);
  }
}
