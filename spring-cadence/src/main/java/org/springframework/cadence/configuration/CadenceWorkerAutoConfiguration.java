package org.springframework.cadence.configuration;

import com.uber.cadence.worker.Worker;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cadence.annotation.CadenceWorkflowAnnotationPostProcessor;
import org.springframework.cadence.annotation.CadenceWorkflowRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CadenceWorkerAutoConfiguration {

  @Autowired private BeanFactory beanFactory;

  @Bean
  public Worker.Factory workerFactory() {
    return new Worker.Factory("sample");
  }

  @Bean
  public CadenceWorkflowRegistrar cadenceWorkflowRegistrar() {
    return new CadenceWorkflowRegistrar(workerFactory(), beanFactory);
  }

  @Bean
  public static CadenceWorkflowAnnotationPostProcessor cadenceWorkflowAnnotationPostProcessor() {
    return new CadenceWorkflowAnnotationPostProcessor();
  }
}
