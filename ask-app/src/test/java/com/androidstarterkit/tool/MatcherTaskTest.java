package com.androidstarterkit.tool;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MatcherTaskTest {
  private MatcherTask.MatchHandler listener;

  @Before
  public void setUp() throws Exception {
    listener = mock(MatcherTask.MatchHandler.class);
  }

  @Test
  public void matcherTest() throws Exception {
    String str = "<activity android:name=\".sample.activity.ScrollViewActivity\" android:label=\"ScrollView\"/>";

    MatcherTask task = new MatcherTask("android:([\\w.]+)=", str);
    task.match(1, listener);

    verify(listener, times(1)).handle("name");
    verify(listener).handle("label");
    verify(listener, never()).handle("labels");
  }
}
