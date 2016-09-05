package com.androidstarterkit;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ClassParserTest {

  @Test
  public void testListParameterClasses1() {
    List<String> classNames = ClassParser.listParameterClasses("  public RecyclerViewAdapter(Context context, List<CoffeeType> dataSet) {");
    List<String> expected = Arrays.asList("Context", "CoffeeType", "List");

    assertEquals(expected.size(), classNames.size());

    for (String className : classNames) {
      assertThat(true, is(expected.contains(className)));
    }
  }

  @Test
  public void testListParameterClasses2() {
    List<String> classNames = ClassParser.listParameterClasses("  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {");
    List<String> expected = Arrays.asList("LayoutInflater", "ViewGroup", "Bundle");

    assertEquals(expected.size(), classNames.size());

    for (String className : classNames) {
      assertThat(true, is(expected.contains(className)));
    }
  }

  @Test
  public void testListParameterClasses3() {
    List<String> classNames = ClassParser.listParameterClasses("  public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {");
    List<String> expected = Arrays.asList("RecyclerViewAdapter");

    assertEquals(expected.size(), classNames.size());
    for (String className : classNames) {
      assertThat(true, is(expected.contains(className)));
    }
  }

  @Test
  public void testListParameterClasses4() {
    List<String> classNames = ClassParser.listParameterClasses("  holder.title.setOnClickListener(new View.OnClickListener() {");

    assertEquals(0, classNames.size());
  }

  @Test
  public void testListParameterClasses5() {
    List<String> classNames = ClassParser.listParameterClasses("  SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);");

    assertEquals(0, classNames.size());
  }

  @Test
  public void testListStaticClasses1() {
    List<String> classNames = ClassParser.listStaticClasses("  http://www.apache.org/licenses/LICENSE-2.0");

    assertEquals(0, classNames.size());
  }

  @Test
  public void testListStaticClasses2() {
    List<String> classNames = ClassParser.listStaticClasses("  themeForegroundColor = outValue.data;");

    assertEquals(2, classNames.size());
  }

  @Test
  public void testListStaticClasses3() {
    List<String> classNames = ClassParser.listStaticClasses("  getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,");

    assertEquals(0, classNames.size());
  }

  @Test
  public void testListStaticClasses4() {
    List<String> classNames = ClassParser.listStaticClasses("      return SlidingTabFragment.newInstance(position);");

    assertEquals(2, classNames.size());
  }

  @Test
  public void testListStaticClasses5() {
    List<String> classNames = ClassParser.listStaticClasses("        import com.androidstarterkit.module.widgets.SlidingTabLayout;");

    assertEquals(0, classNames.size());
  }

  @Test
  public void testListStaticClasses6() {
    List<String> classNames = ClassParser.listStaticClasses("      SlidingTabFragment.newInstance ");

    assertEquals(2, classNames.size());
  }

  @Test
  public void testListClassWithGeneric() {
    List<String> classNames = ClassParser.listClassWithGeneric("List<ClassName>");
    List<String> expected = Arrays.asList("List", "ClassName");

    assertEquals(2, classNames.size());
    for (String className : classNames) {
      assertThat(true, is(expected.contains(className)));
    }
  }

  @Test
  public void testInnerClass() {
    assertEquals("Class", ClassParser.getOuterClass("Class.method()"));
    assertEquals("Class", ClassParser.getOuterClass("Class.variable"));
    assertEquals("Class", ClassParser.getOuterClass("Class.StaticClass"));
  }

  private boolean matched(String regex, String inputTxt) {
    return Pattern.matches(regex, inputTxt);
  }
}
