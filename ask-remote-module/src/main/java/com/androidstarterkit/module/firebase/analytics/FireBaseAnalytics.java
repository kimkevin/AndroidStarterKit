package com.androidstarterkit.module.firebase.analytics;


import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FireBaseAnalytics {
  /**
   * The {@code FirebaseAnalytics} used to record screen views.
   */
  private FirebaseAnalytics analytics;
  private Activity activity;

  public FireBaseAnalytics(Activity activity, FirebaseAnalytics analytics) {
    this.activity = activity;
    this.analytics = analytics;
  }

  /**
   * This string must be <= 36 characters long in order for setCurrentScreen to succeed.
   */
  public void recordScreenView(String screenName) {
    analytics.setCurrentScreen(activity, screenName, null /* class override */);
  }

  public void trackEvent(Bundle bundle) {
    analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
  }

  public void setUserProperty(String version) {
    analytics.setUserProperty("android_version", version);
  }
}
