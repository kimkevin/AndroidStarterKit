package com.androidstarterkit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class ResourceMatcherTest {
    @Test
    public void testJavaFileMatcher() throws Exception {
        String codeLine = "setContentView(R.layout.main);\n" +
                "context.getResource().getDrawable(R.drawable.ic_title);\n" +
                "R.menu.home";
        ResourceMatcher.JavaFileMatcher mockMatcher = mock(ResourceMatcher.JavaFileMatcher.class);
        ResourceMatcher matcher = new ResourceMatcher(codeLine, mockMatcher);
        matcher.match();

        verify(mockMatcher).matched("layout", "main");
        verify(mockMatcher).matched("drawable", "ic_title");
        verify(mockMatcher).matched("menu", "home");
    }

    @Test
    public void testJavaValueMatcher() throws Exception {
        String codeLine = "editTextView.setText(getString(R.string.title));" +
                "imageView.getLayoutParam().width = getResource().getDimen(R.dimen.width);";

        ResourceMatcher.JavaValueMatcher mockMatcher = mock(ResourceMatcher.JavaValueMatcher.class);
        ResourceMatcher matcher = new ResourceMatcher(codeLine, mockMatcher);
        matcher.match();

        verify(mockMatcher).matched("string", "title");
        verify(mockMatcher).matched("dimen", "width");
    }

    @Test
    public void testXmlFileMatcher() throws Exception {
        String codeLine = "<android.support.design.widget.NavigationView\n" +
                "        android:id=\"@+id/nav_view\"\n" +
                "        android:layout_width=\"wrap_content\"\n" +
                "        android:layout_height=\"match_parent\"\n" +
                "        android:layout_gravity=\"start\"\n" +
                "        android:fitsSystemWindows=\"true\"\n" +
                "        android:background=\"@drawable/some_drawable\"\n"+
                "        app:headerLayout=\"@layout/nav_header_main\"\n" +
                "        app:menu=\"@menu/activity_main_drawer\"/>";

        ResourceMatcher.XmlFileMatcher mockMatcher = mock(ResourceMatcher.XmlFileMatcher.class);
        ResourceMatcher matcher = new ResourceMatcher(codeLine, mockMatcher);
        matcher.match();

        verify(mockMatcher).matched("layout", "nav_header_main");
        verify(mockMatcher).matched("drawable", "some_drawable");
        verify(mockMatcher).matched("menu", "activity_main_drawer");
    }

    @Test
    public void testXmlValueMatcher() throws Exception {
        String codeLine = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<RelativeLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    xmlns:tools=\"http://schemas.android.com/tools\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\"\n" +
                "    style=\"@style/some_style\""+
                "    android:paddingBottom=\"@dimen/activity_vertical_margin\"\n" +
                "    tools:context=\"com.androidstarterkit.sample.MainActivity\">\n" +
                "\n" +
                "    <TextView\n" +
                "        android:layout_width=\"wrap_content\"\n" +
                "        android:layout_height=\"wrap_content\"\n" +
                "        android:text=\"@string/hello_world\" />\n" +
                "</RelativeLayout>";

        ResourceMatcher.XmlValueMatcher mockMatcher = mock(ResourceMatcher.XmlValueMatcher.class);
        ResourceMatcher matcher = new ResourceMatcher(codeLine, mockMatcher);
        matcher.match();

        verify(mockMatcher).matched("dimen", "activity_vertical_margin");
        verify(mockMatcher).matched("style", "some_style");
        verify(mockMatcher).matched("string", "hello_world");
    }
}