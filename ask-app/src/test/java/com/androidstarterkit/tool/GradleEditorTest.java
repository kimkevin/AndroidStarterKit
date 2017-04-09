//package com.androidstarterkit.tool;
//
//import com.androidstarterkit.util.FileUtils;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.notNullValue;
//import static org.junit.Assert.assertThat;
//
//public class GradleEditorTest {
//  private static final String TEST_FILE_PATH = "/Users/kevin/Local/projects/git/AndroidStarterKit/ask-app/src/test/res/build.gradle";
//
//  private List<String> testCodelines;
//
//  @Before
//  public void setUp() throws Exception {
//    File file = new File(TEST_FILE_PATH);
//    testCodelines = FileUtils.readFile(file);
//
//    assertThat(testCodelines, notNullValue());
//  }
//
//  @Test
//  public void fileObjectShouldNotBeNull() throws Exception {
//    File file = new File(TEST_FILE_PATH);
//    List<String> lines = FileUtils.readFile(file);
//
//    assertThat(lines, notNullValue());
//  }
//
//  @Test
//  public void applyTest() {
//
//  }
//
//  private File getFileFromPath(Object obj, String fileName) {
//    ClassLoader classLoader = obj.getClass().getClassLoader();
//    URL resource = classLoader.getResource(fileName);
//
//    try {
//      return new File(resource.getJustPath());
//    } catch (NullPointerException exception) {
//      return null;
//    }
//  }
//
//  private InputStream getFileFromResource(String filename) {
//    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//    InputStream is = classloader.getResourceAsStream(filename);
//    return is;
//  }
//
//  private String getStringFromInputStream(InputStream is) {
//    BufferedReader br = null;
//    StringBuilder sb = new StringBuilder();
//
//    String line;
//    try {
//
//      br = new BufferedReader(new InputStreamReader(is));
//      while ((line = br.readLine()) != null) {
//        sb.append(line);
//      }
//
//    } catch (IOException e) {
//      e.printStackTrace();
//    } finally {
//      if (br != null) {
//        try {
//          br.close();
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      }
//    }
//
//    return sb.toString();
//  }
//}