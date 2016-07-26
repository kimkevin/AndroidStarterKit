package com.androidstarterkit.module.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.androidstarterkit.module.R;
import com.androidstarterkit.module.SlidingTabFragment;
import com.androidstarterkit.module.widgets.SlidingTabLayout;

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
