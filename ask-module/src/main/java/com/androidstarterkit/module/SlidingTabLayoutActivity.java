package com.androidstarterkit.module;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.androidstarterkit.module.adapter.SlidingTabAdapter;
import com.androidstarterkit.module.widgets.SlidingTabLayout;

import java.util.Arrays;

public class SlidingTabLayoutActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getSupportActionBar().setElevation(0);

    setContentView(R.layout.activity_slidingtablaout_main);

    SlidingTabAdapter adapter = new SlidingTabAdapter(getSupportFragmentManager(),
        Arrays.asList(new String[] { "TAB1", "TAB2" }));

    ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
    viewPager.setAdapter(adapter);

    SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
    slidingTabLayout.setCustomTabView(R.layout.tab_txt_layout, R.id.tab_name_txt);

    slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
      @Override
      public int getIndicatorColor(int position) {
        return Color.WHITE;
      }
    });

    slidingTabLayout.setViewPager(viewPager);
  }
}

