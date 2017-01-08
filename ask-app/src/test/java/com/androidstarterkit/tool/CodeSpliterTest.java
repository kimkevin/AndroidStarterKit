package com.androidstarterkit.tool;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class CodeSpliterTest {
  @Test
  public void testSplitComma() {
    List<String> actual = CodeSpliter.split("Context context, Map<CoffeeType, Coffee> dataSet", ',');

    assertEquals(2, actual.size());
    assertEquals("Context context", actual.get(0));
    assertEquals("Map<CoffeeType, Coffee> dataSet", actual.get(1));
  }

  @Test
  public void testSplitDot() {
    List<String> actual = CodeSpliter.split("Machine<CoffeeType<Type.Value>, Coffee>.Coffee<Country>", '.');

    assertEquals(2, actual.size());
    assertEquals("Machine<CoffeeType<Type.Value>, Coffee>", actual.get(0));
    assertEquals("Coffee<Country>", actual.get(1));

    actual = CodeSpliter.split("Map<CoffeeType, Coffee>", '.');
    assertEquals(1, actual.size());
    assertEquals("Map<CoffeeType, Coffee>", actual.get(0));

    actual = CodeSpliter.split("HashMap<Integer, SortedMap<Float, Double>, List<String>>.Class12", '.');
    assertEquals(2, actual.size());
    assertEquals("HashMap<Integer, SortedMap<Float, Double>, List<String>>", actual.get(0));
    assertEquals("Class12", actual.get(1));

    actual = CodeSpliter.split("Class11<Generic4>.Class12<Generic6>", '.');
    assertEquals(2, actual.size());
    assertEquals("Class11<Generic4>", actual.get(0));
    assertEquals("Class12<Generic6>", actual.get(1));
  }
}
