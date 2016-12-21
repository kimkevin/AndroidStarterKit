package com.androidstarterkit.tool;

import com.androidstarterkit.tool.ResourceMatcher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.androidstarterkit.tool.ResourceMatcher.MatchType.JAVA_FILE;
import static com.androidstarterkit.tool.ResourceMatcher.MatchType.JAVA_VALUE;
import static com.androidstarterkit.tool.ResourceMatcher.MatchType.XML_FILE;
import static com.androidstarterkit.tool.ResourceMatcher.MatchType.XML_VALUE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class ResourceMatcherTest {
    private ResourceMatcher.Handler mockHandler;

    @Before
    public void setUp() throws Exception {
        mockHandler = mock(ResourceMatcher.Handler.class);
    }

    @Test
    public void testJavaFileMatcher() throws Exception {
        String codeLine = "setContentView(R.layout.main);\n" +
                "context.getResource().getDrawable(R.drawable.ic_title);\n" +
                "R.menu.home";

        ResourceMatcher matcher = new ResourceMatcher(codeLine, JAVA_FILE);
        matcher.match(mockHandler);

        verify(mockHandler).handle("layout", "main");
        verify(mockHandler).handle("drawable", "ic_title");
        verify(mockHandler).handle("menu", "home");
    }

    @Test
    public void testJavaValueMatcher() throws Exception {
        String codeLine = "editTextView.setText(getString(R.string.title));" +
                "imageView.getLayoutParam().width = getResource().getDimen(R.dimen.width);";

        ResourceMatcher matcher = new ResourceMatcher(codeLine, JAVA_VALUE);
        matcher.match(mockHandler);

        verify(mockHandler).handle("string", "title");
        verify(mockHandler).handle("dimen", "width");
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

        ResourceMatcher matcher = new ResourceMatcher(codeLine, XML_FILE);
        matcher.match(mockHandler);

        verify(mockHandler).handle("layout", "nav_header_main");
        verify(mockHandler).handle("drawable", "some_drawable");
        verify(mockHandler).handle("menu", "activity_main_drawer");
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

        ResourceMatcher matcher = new ResourceMatcher(codeLine, XML_VALUE);
        matcher.match(mockHandler);

        verify(mockHandler).handle("dimen", "activity_vertical_margin");
        verify(mockHandler).handle("style", "some_style");
        verify(mockHandler).handle("string", "hello_world");
    }

    @Test
    public void resourceMatcherCanMatchMultipleTimes() throws Exception {
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

        ResourceMatcher matcher = new ResourceMatcher(codeLine, XML_VALUE);
        matcher.match(mockHandler);
        matcher.match(mockHandler);

        verify(mockHandler, times(2)).handle("dimen", "activity_vertical_margin");
        verify(mockHandler, times(2)).handle("style", "some_style");
        verify(mockHandler, times(2)).handle("string", "hello_world");
    }
}