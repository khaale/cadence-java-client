package org.springframework.cadence.sample.activities;

import com.uber.cadence.activity.ActivityMethod;

public interface GreetingActivities {
  @ActivityMethod(scheduleToCloseTimeoutSeconds = 2)
  String composeGreeting(String name);
}
