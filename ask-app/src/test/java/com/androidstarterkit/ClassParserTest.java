package com.androidstarterkit;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ClassParserTest {

  @Test
  public void testListParameterClasses1() {
    List<String> classNames = new ArrayList<>();
    List<String> expected = Arrays.asList("Context", "CoffeeType", "List");

    ClassParser.listParameterClasses(classNames, "  public RecyclerViewAdapter(Context context, List<CoffeeType> dataSet) {");

    assertEquals(expected.size(), classNames.size());

    for (String className : classNames) {
      assertThat(true, is(expected.contains(className)));
    }
  }

  @Test
  public void testListParameterClasses2() {
    List<String> classNames = new ArrayList<>();
    List<String> expected = Arrays.asList("LayoutInflater", "ViewGroup", "Bundle");

    ClassParser.listParameterClasses(classNames, "  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {");

    assertEquals(expected.size(), classNames.size());

    for (String className : classNames) {
      assertThat(true, is(expected.contains(className)));
    }
  }

  @Test
  public void testListParameterClasses3() {
    List<String> classNames = new ArrayList<>();
    List<String> expected = Arrays.asList("RecyclerViewAdapter");

    ClassParser.listParameterClasses(classNames, "  public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {");

    assertEquals(expected.size(), classNames.size());
    for (String className : classNames) {
      assertThat(true, is(expected.contains(className)));
    }
  }

  @Test
  public void testListParameterClasses4() {
    List<String> classNames = new ArrayList<>();

    ClassParser.listParameterClasses(classNames, "  holder.title.setOnClickListener(new View.OnClickListener() {");

    assertEquals(0, classNames.size());
  }

  @Test
  public void testListParameterClasses5() {
    List<String> classNames = new ArrayList<>();

    ClassParser.listParameterClasses(classNames, "  SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);");

    assertEquals(0, classNames.size());
  }

  @Test
  public void testListStaticClasses1() {
    List<String> classNames = new ArrayList<>();

    ClassParser.listStaticClasses(classNames, "  http://www.apache.org/licenses/LICENSE-2.0");

    assertEquals(0, classNames.size());
  }

  @Test
  public void testListStaticClasses2() {
    List<String> classNames = new ArrayList<>();

    ClassParser.listStaticClasses(classNames, "  themeForegroundColor = outValue.data;");

    assertEquals(2, classNames.size());
  }

  @Test
  public void testListStaticClasses3() {
    List<String> classNames = new ArrayList<>();

    ClassParser.listStaticClasses(classNames, "  getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,");

    assertEquals(0, classNames.size());
  }

  @Test
  public void testListStaticClasses4() {
    List<String> classNames = new ArrayList<>();

    ClassParser.listStaticClasses(classNames, "      return SlidingTabFragment.newInstance(position);");

    assertEquals(2, classNames.size());
  }

  @Test
  public void testListStaticClasses5() {
    List<String> classNames = new ArrayList<>();

    ClassParser.listStaticClasses(classNames, "        import com.androidstarterkit.module.widgets.SlidingTabLayout;");

    assertEquals(0, classNames.size());
  }

  @Test
  public void testListStaticClasses6() {
    List<String> classNames = new ArrayList<>();

    ClassParser.listStaticClasses(classNames, "      SlidingTabFragment.newInstance ");

    assertEquals(2, classNames.size());
  }

  private boolean matched(String regex, String inputTxt) {
    return Pattern.matches(regex, inputTxt);
  }
}
