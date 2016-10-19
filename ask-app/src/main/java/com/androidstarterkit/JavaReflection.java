package com.androidstarterkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class JavaReflection {

  interface listener {

  }

  BaseException exception;

  public JavaReflection() {
    Class<?>[] declaredClasses = getClass().getDeclaredClasses();
    for (Class myClass : declaredClasses) {
      System.out.println("declaredClass = " + myClass.toString());
    }

    Class<?>[] classes = getClass().getClasses();
    for (Class myClass : classes) {
      System.out.println("class = " + myClass.toString());
    }

    Class<?>[] interfaces = getClass().getInterfaces();
    for (Class myInterface : interfaces) {
      System.out.println("interface = " + myInterface.toString());
    }

    Field[] classes3 = getClass().getDeclaredFields();
    for (Field myClass : classes3) {
      System.out.println("field = " + myClass.toString());
    }

    Method[] declaredMethods = getClass().getDeclaredMethods();
    for (Method myMethod : declaredMethods) {
      System.out.println("declaredMethod = " + myMethod.toString());
    }

    Method[] methods = getClass().getMethods();
    for (Method myMethod : methods) {
      System.out.println("method = " + myMethod.toString());
    }

//    getClass().getGenericInterfaces()
//    getClass().getGenericSuperclass()
    System.out.println("typename = " + getClass().getTypeName());

    print(null);
  }

  public void print(Extension extension) {

  }
}
