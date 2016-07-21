package com.kimkevin.module;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.kimkevin.module.adapter.SlidingIconTabAdapter;
import com.kimkevin.module.widgets.SlidingTabLayout;

public class SlidingIconTabLayoutActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getSupportActionBar().setElevation(0);

    setContentView(R.layout.activity_slidingtablaout_main);

    SlidingIconTabAdapter adapter = new SlidingIconTabAdapter(getSupportFragmentManager());

    ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
    viewPager.setAdapter(adapter);

    SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
    slidingTabLayout.setCustomTabView(R.layout.tab_img_layout, R.id.tab_name_img);
    slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
      @Override
      public int getIndicatorColor(int position) {
        return Color.WHITE;
      }
    });

    slidingTabLayout.setViewPager(viewPager);
  }
}

