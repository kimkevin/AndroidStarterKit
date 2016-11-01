package com.androidstarterkit;

import com.androidstarterkit.utils.FileUtil;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ClassParserFileTest {
  private List<String> lines = Collections.EMPTY_LIST;

  private List<String> actual = new ArrayList<>();
  private List<String> expected = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    if (lines.size() <= 0) {
      final String filePath = FileUtil.getRootPath().replace("ask-app",
          "ask-module/src/main/java/com/androidstarterkit/module/SlidingTabActivity.java");

      lines = FileUtil.readFile(new File(filePath));
    }
  }

  @Test
  public void testListInheritClassesForFile() throws Exception {
    actual.clear();
    expected = Arrays.asList("AppCompatActivity");

    for (String line : lines) {
      ClassParser.mergeDistinct(actual, ClassParser.listInheritClasses(line));
    }

    assertEquals(expected.size(), actual.size());
    assertList(actual, expected);
  }

  @Test
  public void testListParameterClassesForFile() throws Exception {
    actual.clear();
    expected = Arrays.asList("Bundle");

    for (String line : lines) {
      ClassParser.mergeDistinct(actual, ClassParser.listParameterClasses(line));
    }

    assertEquals(expected.size(), actual.size());
    assertList(actual, expected);
  }

  private void assertList(List<String> actual, List<String> expected) {
    for (String className : actual) {
      assertThat(true, is(expected.contains(className)));
    }
  }
}
