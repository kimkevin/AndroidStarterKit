package com.androidstarterkit.exception;


public class MultipleFlavorsException extends RuntimeException{
  public MultipleFlavorsException() {
    super("Multiple flavors project is not supported");
  }
}
