package com.androidstarterkit;


import com.androidstarterkit.config.AskConfig;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AskTest {
  @Test
  public void envTest() throws Exception {
    assertEquals(Ask.env, AskConfig.PRODUCTION);
  }

  @Test
  public void outputTest() throws Exception {
    assertEquals(Ask.output, AskConfig.OUTPUT_PROJECT);
  }
}
