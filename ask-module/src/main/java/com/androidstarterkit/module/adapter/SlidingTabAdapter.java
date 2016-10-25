package com.androidstarterkit.module.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.androidstarterkit.module.models.FragmentInfo;
import com.androidstarterkit.module.widgets.DefaultFragment;

import java.util.List;

public class SlidingTabAdapter extends FragmentPagerAdapter {

  private List<FragmentInfo> fragmentInfos;

  public SlidingTabAdapter(FragmentManager fragmentManager, List<FragmentInfo> fragmentInfos) {
    super(fragmentManager);

    this.fragmentInfos = fragmentInfos;
  }

  @Override
  public Fragment getItem(int position) {
    try {
      return (Fragment) Class.forName(fragmentInfos.get(position).getFragmentClass().getName())
          .getConstructor().newInstance();
    } catch (Exception e) {
      return new DefaultFragment();
    }
  }

  @Override
  public int getCount() {
    return fragmentInfos.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return "TAB" + position;
  }
}
