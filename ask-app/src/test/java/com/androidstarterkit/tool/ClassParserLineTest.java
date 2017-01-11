package com.androidstarterkit.tool;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClassParserLineTest {

  @Test
  public void testListConstructedClass() throws Exception {
    List<ClassInfo> expected = new ArrayList<>();
    expected.add(new ClassInfo("SlidingTabFragment"));

    final String codeline = "          .add(R.id.container, new SlidingTabFragment())";
    List<ClassInfo> actual = ClassParser.listConstructedClass(codeline);

    assertList(expected, actual);
  }

  @Test
  public void testListInheritanceClass() throws Exception {
    List<ClassInfo> actual = new ArrayList<>();
    List<ClassInfo> expected = new ArrayList<>();
    expected.add(new ClassInfo("BaseActivity1"));
    expected.add(new ClassInfo("BaseActivity2"));
    expected.add(new ClassInfo("BaseActivity3"));

    final String[] codeLines = {
        "public class MainActivity extends BaseActivity1",
        "    public class MainActivity extends BaseActivity2 implements Interface7, Interface8 {",
        "    public class MainActivity extends BaseActivity3 implements Interface9.Interface10, Interface11"
    };

    for (String codeLine : codeLines) {
      actual.addAll(ClassParser.listInheritanceClass(codeLine));
    }

    assertList(expected, actual);
  }

  @Test
  public void testListInterfaceClass() throws Exception {
    List<ClassInfo> actual = new ArrayList<>();
    List<ClassInfo> expected = new ArrayList<>();
    expected.add(new ClassInfo("Interface1", new ClassInfo("Interface2")));
    expected.add(new ClassInfo("Interface3"));
    expected.add(new ClassInfo("Interface4", new ClassInfo("Interface5")));
    expected.add(new ClassInfo("Interface6"));
    expected.add(new ClassInfo("Interface7"));
    expected.add(new ClassInfo("Interface8"));
    expected.add(new ClassInfo("Interface9", new ClassInfo("Interface10")));
    expected.add(new ClassInfo("Interface11"));

    final String[] codeLines = {
        "    implements Interface1.Interface2, Interface3 {",
        "    implements Interface4.Interface5, Interface6 {",
        "    public class MainActivity extends BaseActivity2 implements Interface7, Interface8 {",
        "    public class MainActivity extends BaseActivity3 implements Interface9.Interface10, Interface11 {"
    };

    for (String codeLine : codeLines) {
      actual.addAll(ClassParser.listInterfaceClass(codeLine));
    }

    assertList(expected, actual);
  }

  @Test
  public void testListFieldClasses1() throws Exception {
    List<ClassInfo> expected = new ArrayList<>();
    List<ClassInfo> actual = new ArrayList<>();

    expected.add(new ClassInfo("Class1"));
    expected.add(new ClassInfo("Class2"));
    expected.add(new ClassInfo("Class3"));
    expected.add(new ClassInfo("Class4", new ClassInfo("Class5")));
    expected.add(new ClassInfo("Class6"));
    expected.add(new ClassInfo("Class7"));
    expected.add(new ClassInfo("Class8", Arrays.asList(new ClassInfo("Generic1"))));
    expected.add(new ClassInfo("Class9", Arrays.asList(
        new ClassInfo("Generic2", Arrays.asList(new ClassInfo("Generic5"))))));
    expected.add(new ClassInfo("Class10", Arrays.asList(new ClassInfo("Generic3"))));
    expected.add(new ClassInfo("Class11", Arrays.asList(new ClassInfo("Generic4")),
        new ClassInfo("Class12", Arrays.asList(new ClassInfo("Generic6")))));
    expected.add(new ClassInfo("HashMap", Arrays.asList(
        new ClassInfo("KEY1"), new ClassInfo("SortedMap", Arrays.asList(
            new ClassInfo("KEY2"), new ClassInfo("VALUE"))))));
    expected.add(new ClassInfo("ScrollViewFragment"));

    final String[] codeLines = {
        " Class1 class1=",
        " Class2 class1 = ",
        " Class3 class1;",
        " Class4.Class5 class1;",
        " Class6[] class1;",
        " Class7 class1[] =",
        " Class8<Generic1> class1;",
        " Class9<Generic2<Generic5>>[] class1 =",
        " Class10<Generic3> class1[];",
        " Class11<Generic4>.Class12<Generic6> class1;",
        " HashMap<KEY1, SortedMap<KEY2, VALUE>> class1 =",
        "     ScrollViewFragment fragment = new ScrollViewFragment();"
    };

    for (String codeLine : codeLines) {
      actual.addAll(ClassParser.listVariableClass(codeLine));
    }

    assertList(expected, actual);
  }

  @Test
  public void testListFieldClasses2() throws Exception {
    final String[] codeLines = {"    return true;",
        "    } else if (id == R.id.nav_send) {",
        "    int id = item.getItemId();",
        "    public boolean onNavigationItemSelected(MenuItem item) {",
        "    return super.onOptionsItemSelected(item);",
        "   //noinspection SimplifiableIfStatement",
        "package com.androidstarterkit.sample;"};

    for (String codeLine : codeLines) {
      assertEquals(0, ClassParser.listVariableClass(codeLine).size());
    }
  }

  @Test
  public void testListParameterClass1() throws Exception {
    List<ClassInfo> actual = ClassParser.listParameterClass("  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {");
    List<ClassInfo> expected = Arrays.asList(new ClassInfo("LayoutInflater"), new ClassInfo("ViewGroup"), new ClassInfo("Bundle"));

    assertList(expected, actual);

    assertEquals(0, ClassParser.listParameterClass("    fab.setOnClickListener(new View.OnClickListener() {").size());
  }

  @Test
  public void testListParameterClass2() {
    List<ClassInfo> actual = ClassParser.listParameterClass("  public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {");
    List<ClassInfo> expected = new ArrayList<>();
    expected.add(new ClassInfo("RecyclerViewAdapter", new ClassInfo("ViewHolder")));

    assertList(expected, actual);
  }

  @Test
  public void testStaticMethodOfClass() {
    assertEquals(0, ClassParser.listStaticMethodOfClass("  http://www.apache.org/licenses/LICENSE-2.0").size());
    assertEquals(0, ClassParser.listStaticMethodOfClass("  themeForegroundColor = outValue.data;").size());
    assertEquals(0, ClassParser.listStaticMethodOfClass("        import com.androidstarterkit.module.widgets.SlidingTab;").size());
    assertEquals(0, ClassParser.listStaticMethodOfClass("    fab.setOnClickListener(new View.OnClickListener() {").size());

    assertList(Arrays.asList(new ClassInfo("Collections")),
        ClassParser.listStaticMethodOfClass("   Collections.sort(listofcountries);"));
    assertList(Arrays.asList(new ClassInfo("SlidingTabFragment")),
        ClassParser.listStaticMethodOfClass("      return SlidingTabFragment.newInstance(position);"));
    assertList(Arrays.asList(new ClassInfo("Glide")),
        ClassParser.listStaticMethodOfClass("    Glide.with(context).load(platform.getLogoUrl()).into(viewHolder.logo);"));
  }

  @Test
  public void testListDotClass() throws Exception {
    assertList(Arrays.asList(new ClassInfo("ScrollViewFragment")),
        ClassParser.listDotClass("    fragmentInfos.add(new FragmentInfo(ScrollViewFragment.class));"));

    assertList(Arrays.asList(new ClassInfo("ListViewFragment")),
        ClassParser.listDotClass("    fragmentInfos.add(new FragmentInfo(ListViewFragment.class));"));

    assertList(Arrays.asList(new ClassInfo("User", new ClassInfo("Account"))),
        ClassParser.listDotClass("    userList.add(User.Account.class);"));
  }

  @Test
  public void testSplitClass() throws Exception {
    final String[] codeLines = {
        "Machine<CoffeeType<Type.Value>, Coffee>.Coffee<Country>.Latte",
        "Machine<CoffeeType<Type.Value>, Coffee>.Coffee<Country>",
        "Map<CoffeeType, Coffee>",
        "HashMap<Integer, SortedMap<Float, Double>, List<String>>.Class12",
        "Class11<Generic4>.Class12<Generic6>",
        "Class11<Generic4>.Class12",
        "MyClass"
    };

    List<ClassInfo> actual = new ArrayList<>();
    List<ClassInfo> expected = new ArrayList<>();
    expected.add(new ClassInfo("Machine",
        Arrays.asList(
            new ClassInfo("CoffeeType",
                Arrays.asList(new ClassInfo("Type", new ClassInfo("Value")))),
            new ClassInfo("Coffee")),
        new ClassInfo("Coffee",
            Arrays.asList(new ClassInfo("Country")),
            new ClassInfo("Latte"))));
    expected.add(new ClassInfo("Machine",
        Arrays.asList(
            new ClassInfo("CoffeeType",
                Arrays.asList(
                    new ClassInfo("Type", new ClassInfo("Value")))),
            new ClassInfo("Coffee")),
        new ClassInfo("Coffee", Arrays.asList(new ClassInfo("Country")))));
    expected.add(new ClassInfo("Map",
        Arrays.asList(
            new ClassInfo("CoffeeType"),
            new ClassInfo("Coffee"))));
    expected.add(new ClassInfo("HashMap",
        Arrays.asList(new ClassInfo("Integer"),
            new ClassInfo("SortedMap",
                Arrays.asList(
                    new ClassInfo("Float"),
                    new ClassInfo("Double"))),
            new ClassInfo("List",
                Arrays.asList(
                    new ClassInfo("String")))),
        new ClassInfo("Class12")));
    expected.add(new ClassInfo("Class11",
        Arrays.asList(new ClassInfo("Generic4")),
        new ClassInfo("Class12",
            Arrays.asList(new ClassInfo("Generic6")))));
    expected.add(new ClassInfo("Class11",
        Arrays.asList(new ClassInfo("Generic4")),
        new ClassInfo("Class12")));
    expected.add(new ClassInfo("MyClass"));

    for (String codeLine : codeLines) {
      actual.add(ClassParser.splitClasses(codeLine));
    }

    assertList(expected, actual);
  }

  private void assertList(List<ClassInfo> expectedClassInfos, List<ClassInfo> actualClassInfos) {
    assertEquals(expectedClassInfos.size(), actualClassInfos.size());

    for (int i = 0, li = expectedClassInfos.size(); i < li; i++) {
      assertTrue(expectedClassInfos.get(i).equals(actualClassInfos.get(i)));
    }
  }
}
