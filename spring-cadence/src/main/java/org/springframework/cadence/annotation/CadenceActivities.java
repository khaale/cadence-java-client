package org.springframework.cadence.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
@Retention(RetentionPolicy.RUNTIME)
public @interface CadenceActivities {
  String worker();
}
