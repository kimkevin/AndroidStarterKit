package com.androidstarterkit.util;

import com.androidstarterkit.tool.ClassParser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ClassParserLineTest {
  private List<String> actual = new ArrayList<>();
  private List<String> expected = new ArrayList<>();

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
    actual = ClassParser.listParameterClasses("  SlidingTab slidingTabLayout = (SlidingTab) findViewById(R.id.tabs);");

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

    assertEquals(1, actual.size());
  }

  @Test
  public void testListStaticClasses3() {
    actual = ClassParser.listStaticClasses("  getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,");

    assertEquals(0, actual.size());
  }

  @Test
  public void testListStaticClasses4() {
    actual = ClassParser.listStaticClasses("      return SlidingTabFragment.newInstance(position);");

    assertEquals(1, actual.size());
  }

  @Test
  public void testListStaticClasses5() {
    actual = ClassParser.listStaticClasses("        import com.androidstarterkit.module.widgets.SlidingTab;");

    assertEquals(0, actual.size());
  }

  @Test
  public void testListStaticClasses6() {
    actual = ClassParser.listStaticClasses("      SlidingTabFragment.newInstance ");

    assertEquals(1, actual.size());
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
}
