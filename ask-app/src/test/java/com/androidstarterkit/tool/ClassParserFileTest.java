package com.androidstarterkit.tool;

import com.androidstarterkit.util.FileUtils;

import org.junit.Before;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ClassParserFileTest {
  private List<String> lines = Collections.EMPTY_LIST;

  @Before
  public void setUp() throws Exception {
    if (lines.size() <= 0) {
      final String filePath = FileUtils.getRootPath().replace("ask-app",
          "ask-remote-module/src/main/java/com/androidstarterkit/module/SampleActivity.java");

      lines = FileUtils.readFile(new File(filePath));
    }
  }
}
