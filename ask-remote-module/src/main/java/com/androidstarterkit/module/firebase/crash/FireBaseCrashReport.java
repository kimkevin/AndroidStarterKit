package com.androidstarterkit.module.firebase.crash;


import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

public class FireBaseCrashReport {

  public void reportError(Throwable throwable) {
    reportError(throwable, null);
  }

  public void reportError(Throwable throwable, Object... args) {
    if (args != null) {
      FirebaseCrash.log(argumentsToString(args));
    }

    FirebaseCrash.report(throwable);
  }

  public void log(String msg) {
    FirebaseCrash.log(msg);
  }

  public void logcat(String tag, String msg) {
    FirebaseCrash.logcat(Log.INFO, tag, msg);
  }

  private String argumentsToString(Object[] args) {
    StringBuilder builder = new StringBuilder();
    int length = args.length;
    for (int i = 0; i < length; i++) {
      Object arg = args[i];
      builder.append(arg);
      if (i < length - 1) {
        builder.append(" : ");
      }

    }
    return builder.toString();
  }
}
