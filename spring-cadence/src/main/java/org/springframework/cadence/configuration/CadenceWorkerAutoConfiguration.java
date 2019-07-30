package org.springframework.cadence.configuration;

import com.uber.cadence.worker.Worker;
import org.springframework.cadence.annotation.CadenceWorkflowAnnotationPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CadenceWorkerAutoConfiguration {

  @Bean
  public Worker.Factory workerFactory() {
    return new Worker.Factory("sample");
  }

  @Bean
  public Worker worker() {
    Worker.Factory factory = workerFactory();
    return factory.newWorker("my_task_list");
  }

  @Bean
  public static CadenceWorkflowAnnotationPostProcessor cadenceWorkflowAnnotationPostProcessor() {
    return new CadenceWorkflowAnnotationPostProcessor();
  }
}
