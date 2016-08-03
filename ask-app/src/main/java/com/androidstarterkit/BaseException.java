package com.androidstarterkit;

public class BaseException extends Exception{
  public BaseException() {
  }

  public BaseException(String s) {
    super("FAILURE: " + s);
  }
}
