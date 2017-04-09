package com.androidstarterkit.command;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AskJsonParserTest {

  @Test
  public void testPath() throws Exception {
    final String[] args = { "-l", "lv,rv", "/path/project" };
    CommandParser parser = new CommandParser(args);

    assertNotNull(parser.getPath());
    assertEquals("/path/project", parser.getPath());
  }

  @Test
  public void testWidget() throws Exception {
    final String[] args = { "-l", "lv,rv", "/path/project" };
    CommandParser parser = new CommandParser(args);

    assertNotNull(parser.getTabType());
    assertEquals(parser.getTabType(), TabType.SlidingTab);
  }

  @Test
  public void testWidgetWithIcon() throws Exception {
    final String[] args = { "-l", "lv,rv", "-i", "/path/project" };
    CommandParser parser = new CommandParser(args);

    assertNotNull(parser.getTabType());
    assertEquals(parser.getTabType(), TabType.SlidingIconTab);
    assertTrue(parser.hasIcon());
  }

  @Test
  public void testWidgetWithoutIcon() throws Exception {
    final String[] args = { "-l", "lv,rv", "/path/project" };
    CommandParser parser = new CommandParser(args);

    assertNotNull(parser.getTabType());
    assertEquals(parser.getTabType(), TabType.SlidingTab);
    assertFalse(parser.hasIcon());
  }
}
