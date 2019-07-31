package org.springframework.cadence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CadenceWorkflow {
  String worker();
}
