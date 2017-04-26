package com.androidstarterkit.injection.model;


import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MethodTest {
  private String elementStr = "{\n" +
      "                  \"class\": \"<main>\",\n" +
      "                  \"name\": \"onCreate\",\n" +
      "                  \"annotation\": [\n" +
      "                    \"@Override\"\n" +
      "                  ],\n" +
      "                  \"access_modifier\": \"protected\",\n" +
      "                  \"return_type\": \"void\",\n" +
      "                  \"parameter\": [\n" +
      "                    {\n" +
      "                      \"type\": \"Bundle\",\n" +
      "                      \"variable\": \"savedInstanceState\"\n" +
      "                    }\n" +
      "                  ],\n" +
      "                  \"lines\": [\n" +
      "                    \"analytics = new FireBaseAnalytics(this, FirebaseAnalytics.getInstance(this));\"\n" +
      "                  ]\n" +
      "                }";

  private String expected =
      "@Override\n"
      + "protected void onCreate(Bundle savedInstanceState) {\n"
      + "  super.onCreate(savedInstanceState);\n"
      + "}\n";

  private Method method;

  @Before
  public void setUp() throws Exception {
    Gson gson = new Gson();
    method = gson.fromJson(elementStr, Method.class);
  }

  @Test
  public void createMethodAsStringTest() throws Exception {
    Assert.assertNotNull(method.createMethodAsString());
    Assert.assertEquals(expected, method.createMethodAsString());
  }
}
