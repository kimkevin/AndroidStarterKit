package com.kimkevin.module.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kimkevin.module.R;
import com.kimkevin.module.SlidingTabFragment;
import com.kimkevin.module.widgets.SlidingTabLayout;

public class SlidingIconTabAdapter extends FragmentPagerAdapter implements SlidingTabLayout.TabIconProvider {

  private static final int iconRes[] = {
      R.mipmap.ic_launcher,
      R.mipmap.ic_launcher
  };

  public SlidingIconTabAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);
  }

  @Override
  public Fragment getItem(int position) {
    return SlidingTabFragment.newInstance(position);
  }

  @Override
  public int getCount() {
    return iconRes.length;
  }

  @Override
  public int getPageIconResId(int position) {
    return iconRes[position];
  }
}
