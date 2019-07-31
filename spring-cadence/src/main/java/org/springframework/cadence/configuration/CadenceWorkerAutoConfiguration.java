package org.springframework.cadence.configuration;

import com.uber.cadence.worker.Worker;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cadence.annotation.CadenceWorkflowAnnotationPostProcessor;
import org.springframework.cadence.annotation.CadenceWorkflowRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
  prefix = "cadence",
  name = "enabled",
  havingValue = "true",
  matchIfMissing = true
)
@EnableConfigurationProperties(CadenceProperties.class)
public class CadenceWorkerAutoConfiguration {

  @Autowired private BeanFactory beanFactory;

  @Autowired private CadenceProperties cadenceProperties;

  @Bean
  public Worker.Factory workerFactory() {

    Worker.FactoryOptions opts = new Worker.FactoryOptions.Builder().build();

    return new Worker.Factory(
        cadenceProperties.getHost(),
        cadenceProperties.getPort(),
        cadenceProperties.getDomain(),
        opts);
  }

  @Bean
  public CadenceWorkflowRegistrar cadenceWorkflowRegistrar() {
    return new CadenceWorkflowRegistrar(workerFactory(), beanFactory, cadenceProperties);
  }

  @Bean
  public static CadenceWorkflowAnnotationPostProcessor cadenceWorkflowAnnotationPostProcessor() {
    return new CadenceWorkflowAnnotationPostProcessor();
  }
}
