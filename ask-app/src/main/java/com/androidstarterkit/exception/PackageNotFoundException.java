package com.androidstarterkit.exception;

public class PackageNotFoundException extends RuntimeException {
  public PackageNotFoundException() {
    super("failed to find pacakage");
  }
}
