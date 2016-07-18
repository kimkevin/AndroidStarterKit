package com.kimkevin.module;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.kimkevin.module.adapter.SlidingTabAdapter;
import com.kimkevin.module.widgets.SlidingTabLayout;

import java.util.Arrays;

public class SlidingTabLayoutActivity extends AppCompatActivity {
  private static final String TAG = SlidingTabLayoutActivity.class.getSimpleName();

  public enum MENU_TYPE {
    TAB_IMAGE,
    TAB_TEXT
  }

  private ViewPager mViewPager;
  private SlidingTabAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getSupportActionBar().setElevation(0);

    setContentView(R.layout.activity_slidingtablaout_main);

    mAdapter = new SlidingTabAdapter(getSupportFragmentManager(),
        Arrays.asList(new String[]{ "TAB1", "TAB2" }));
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mAdapter);

    initViews(MENU_TYPE.TAB_TEXT);
  }

  private void initViews(MENU_TYPE type) {
    SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
    if (type == MENU_TYPE.TAB_IMAGE) {
      mSlidingTabLayout.setCustomTabView(R.layout.tab_img_layout, R.id.tab_name_img);
    } else if (type == MENU_TYPE.TAB_TEXT){
      mSlidingTabLayout.setCustomTabView(R.layout.tab_txt_layout, R.id.tab_name_txt);
    }

    mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
      @Override
      public int getIndicatorColor(int position) {
        return Color.WHITE;
      }
    });
    mSlidingTabLayout.setViewPager(mViewPager);
  }
}

