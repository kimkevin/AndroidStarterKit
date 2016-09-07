package com.androidstarterkit;

import com.androidstarterkit.utils.FileUtil;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ClassParserTest {
  private List<String> lines = Collections.EMPTY_LIST;

  private List<String> actual = new ArrayList<>();
  private List<String> expected = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    if (lines.size() <= 0) {
      final String filePath = FileUtil.getRootPath().replace("ask-app",
          "ask-module/src/main/java/com/androidstarterkit/module/SlidingTabLayoutActivity.java");

      lines = FileUtil.readFile(new File(filePath));
    }
  }

  @Test
  public void testListInheritClassesForFile() throws Exception {
    actual.clear();
    expected = Arrays.asList("AppCompatActivity");

    for (String line : lines) {
      actual.addAll(ClassParser.listInheritClasses(line));
    }

    assertEquals(expected.size(), actual.size());
    assertList(actual, expected);
  }

  @Test
  public void testListFieldClassesForFile() throws Exception {
    actual.clear();
    expected = Arrays.asList("SlidingTabAdapter", "ViewPager", "SlidingTabLayout");

    for (String line : lines) {
      actual.addAll(ClassParser.listFieldClasses(line));
    }

    assertEquals(expected.size(), actual.size());
    assertList(actual, expected);
  }

  @Test
  public void testListParameterClassesForFile() throws Exception {
    actual.clear();
    expected = Arrays.asList("Bundle");

    for (String line : lines) {
      actual.addAll(ClassParser.listParameterClasses(line));
    }

    assertEquals(expected.size(), actual.size());
    assertList(actual, expected);
  }

  @Test
  public void testListParameterClasses1() {
    actual = ClassParser.listParameterClasses("  public RecyclerViewAdapter(Context context, List<CoffeeType> dataSet) {");
    expected = Arrays.asList("Context", "CoffeeType", "List");

    assertEquals(expected.size(), actual.size());
    assertList(actual, expected);
  }

  @Test
  public void testListParameterClasses2() {
    actual = ClassParser.listParameterClasses("  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {");
    expected = Arrays.asList("LayoutInflater", "ViewGroup", "Bundle");

    assertEquals(expected.size(), actual.size());
    assertList(actual, expected);
  }

  @Test
  public void testListParameterClasses3() {
    actual = ClassParser.listParameterClasses("  public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {");
    expected = Arrays.asList("RecyclerViewAdapter");

    assertEquals(expected.size(), actual.size());
    assertList(actual, expected);
  }

  @Test
  public void testListParameterClasses4() {
    actual = ClassParser.listParameterClasses("  holder.title.setOnClickListener(new View.OnClickListener() {");

    assertEquals(0, actual.size());
  }

  @Test
  public void testListParameterClasses5() {
    actual = ClassParser.listParameterClasses("  SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);");

    assertEquals(0, actual.size());
  }

  @Test
  public void testListStaticClasses1() {
    actual = ClassParser.listStaticClasses("  http://www.apache.org/licenses/LICENSE-2.0");

    assertEquals(0, actual.size());
  }

  @Test
  public void testListStaticClasses2() {
    actual = ClassParser.listStaticClasses("  themeForegroundColor = outValue.data;");

    assertEquals(2, actual.size());
  }

  @Test
  public void testListStaticClasses3() {
    actual = ClassParser.listStaticClasses("  getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,");

    assertEquals(0, actual.size());
  }

  @Test
  public void testListStaticClasses4() {
    actual = ClassParser.listStaticClasses("      return SlidingTabFragment.newInstance(position);");

    assertEquals(2, actual.size());
  }

  @Test
  public void testListStaticClasses5() {
    actual = ClassParser.listStaticClasses("        import com.androidstarterkit.module.widgets.SlidingTabLayout;");

    assertEquals(0, actual.size());
  }

  @Test
  public void testListStaticClasses6() {
    actual = ClassParser.listStaticClasses("      SlidingTabFragment.newInstance ");

    assertEquals(2, actual.size());
  }

  @Test
  public void testListClassWithGeneric() {
    actual = ClassParser.listClassWithGeneric("List<ClassName>");
    expected = Arrays.asList("List", "ClassName");

    assertEquals(2, actual.size());
    assertList(actual, expected);
  }

  @Test
  public void testInnerClass() {
    assertEquals("Class", ClassParser.getOuterClass("Class.method()"));
    assertEquals("Class", ClassParser.getOuterClass("Class.variable"));
    assertEquals("Class", ClassParser.getOuterClass("Class.StaticClass"));
  }

  private void assertList(List<String> actual, List<String> expected) {
    for (String className : actual) {
      assertThat(true, is(expected.contains(className)));
    }
  }

  private boolean matched(String regex, String inputTxt) {
    return Pattern.matches(regex, inputTxt);
  }
}
