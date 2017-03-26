package com.androidstarterkit.module.firebase.crash;


import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

public class FireBaseCrashReport {

  public void reportError(Throwable throwable, Object... args) {
    if (args != null) {
      FirebaseCrash.log(argumentsAsString(args));
    }

    FirebaseCrash.report(throwable);
  }

  private String argumentsAsString(Object[] args) {
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

  public void report(Throwable throwable) {
//    new Exception("My first Android non-fatal error")
    FirebaseCrash.report(throwable);
  }

  public void log(String msg) {
//    FirebaseCrash.log("Activity created");
    FirebaseCrash.log(msg);
  }

  public void logcat(String tag, String msg) {
    FirebaseCrash.logcat(Log.INFO, tag, msg);
  }
}

//  // Button that causes the NullPointerException to be thrown.
//  Button crashButton = (Button) findViewById(R.id.crashButton);
//crashButton.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//    // Log that crash button was clicked. This version of Crash.log() will include the
//    // message in the crash report as well as show the message in logcat.
//    FirebaseCrash.logcat(Log.INFO, TAG, "Crash button clicked");
//
//    // If catchCrashCheckBox is checked catch the exception and report is using
//    // Crash.report(). Otherwise throw the exception and let Firebase Crash automatically
//    // report the crash.
//    if (catchCrashCheckBox.isChecked()) {
//    try {
//    throw new NullPointerException();
//    } catch (NullPointerException ex) {
//    // [START log_and_report]
//    FirebaseCrash.logcat(Log.ERROR, TAG, "NPE caught");
//    FirebaseCrash.report(ex);
//    // [END log_and_report]
//    }
//    } else {
//    throw new NullPointerException();
//    }
//    }
//    });
//
//    // Log that the Activity was created. This version of Crash.log() will include the message
//    // in the crash report but will not be shown in logcat.
//    // [START log_event]
//    FirebaseCrash.log("Activity created");
//// [END log_event]
