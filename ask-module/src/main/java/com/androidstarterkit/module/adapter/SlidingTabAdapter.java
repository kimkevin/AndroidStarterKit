package com.androidstarterkit.module.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.androidstarterkit.module.SlidingTabFragment;

import java.util.List;

public class SlidingTabAdapter extends FragmentPagerAdapter {

  private List<String> dataSet;

  public SlidingTabAdapter(FragmentManager fragmentManager, List<String> dataSet) {
    super(fragmentManager);

    this.dataSet = dataSet;
  }

  @Override
  public Fragment getItem(int position) {
    return SlidingTabFragment.newInstance(position);
  }

  @Override
  public int getCount() {
    return dataSet.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return dataSet.get(position);
  }
}
