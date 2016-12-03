package com.androidstarterkit.exception;


public class ActivityNotFoundException extends RuntimeException {
  public ActivityNotFoundException() {
    super("failed to find main activity");
  }
}
