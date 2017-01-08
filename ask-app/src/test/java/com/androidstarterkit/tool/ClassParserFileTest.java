//package com.androidstarterkit.tool;
//
//import com.androidstarterkit.util.FileUtils;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThat;
//
//public class ClassParserFileTest {
//  private List<String> lines = Collections.EMPTY_LIST;
//
//  @Before
//  public void setUp() throws Exception {
//    if (lines.size() <= 0) {
//      final String filePath = FileUtils.getRootPath().replace("ask-app",
//          "ask-remote-module/src/main/java/com/androidstarterkit/module/SampleActivity.java");
//
//      lines = FileUtils.readFile(new File(filePath));
//    }
//  }
//
//  @Test
//  public void testListInheritClasses() throws Exception {
//    List<ClassInfo> actual = new ArrayList<>();
//    List<String> expected = Arrays.asList("AppCompatActivity", "NavigationView");
//
//    for (String line : lines) {
//      ClassParser.mergeDistinct(actual, ClassParser.listInheritClass(line));
//    }
//
//    assertList(expected, actual);
//  }
//
//  @Test
//  public void testListParameterClasses() throws Exception {
//    List<ClassInfo> actual = new ArrayList<>();
//    List<String> expected = Arrays.asList("Bundle", "View", "Menu", "MenuItem");
//
//    for (String line : lines) {
//      ClassParser.mergeDistinct(actual, ClassParser.listParameterClass(line));
//    }
//
//    assertList(expected, actual);
//  }
//
//  @Test
//  public void testSplitClassWithGeneric() throws Exception {
//    List<String> expected = Arrays.asList("ClassName4", "GenericClass");
//
//    System.out.println("test = " + ClassParser.listGenericTypeClass("ClassName4<GenericClass> class;").get(0).getName());
//    assertList(expected, ClassParser.listGenericTypeClass("ClassName4<GenericClass> class;"));
//    assertList(expected, ClassParser.listGenericTypeClass("ClassName4 <GenericClass>"));
//  }
//
//  @Test
//  public void testListFieldClasses2() throws Exception {
//    List<String> expected = Arrays.asList("Context", "List", "AndroidPlatform", "ViewHolder", "LayoutInflater", "ImageView", "TextView");
//
//    List<ClassInfo> result = new ArrayList<>();
//
//    final String filePath = FileUtils.getRootPath().replace("ask-app",
//        "ask-remote-module/src/main/java/com/androidstarterkit/module/ui/adapter/GridViewAdapter.java");
//    lines = FileUtils.readFile(new File(filePath));
//
//    for (String line : lines) {
//      List<ClassInfo> classNames = ClassParser.listVariableClass(line);
//      if (classNames.size() > 0) {
//        ClassParser.mergeDistinct(result, classNames);
//      }
//    }
//
//    assertList(expected, result);
//  }
//
//  private void assertList(List<String> expectedClassNames, List<ClassInfo> actualClassInfos) {
//    assertEquals(expectedClassNames.size(), actualClassInfos.size());
//    for (String expectedClassName : expectedClassNames) {
//      for (ClassInfo actualClassInfo : actualClassInfos) {
//        assertThat(true, is(expectedClassName.equals(actualClassInfo.getName())));
//      }
//    }
//  }
//}
