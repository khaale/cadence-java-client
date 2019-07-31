package org.springframework.cadence.sample.activities;

import static org.springframework.cadence.sample.DemoApp.GREETING_WORKER;

import org.springframework.cadence.annotation.CadenceActivities;

@CadenceActivities(worker = GREETING_WORKER)
public class GreetingActivitiesImpl implements GreetingActivities {
  @Override
  public String composeGreeting(String name) {
    return "Hello" + name;
  }
}
