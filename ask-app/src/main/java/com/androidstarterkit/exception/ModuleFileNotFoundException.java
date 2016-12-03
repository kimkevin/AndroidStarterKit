package com.androidstarterkit.exception;


public class ModuleFileNotFoundException extends RuntimeException {
  public ModuleFileNotFoundException(String filename) {
    super("failed to find " + filename);
  }
}
