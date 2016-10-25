package com.androidstarterkit.module.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.androidstarterkit.module.models.FragmentInfo;
import com.androidstarterkit.module.widgets.DefaultFragment;
import com.androidstarterkit.module.views.SlidingTabLayout;

import java.util.List;

public class SlidingIconTabAdapter extends FragmentPagerAdapter implements SlidingTabLayout.TabIconProvider {

  private List<FragmentInfo> fragmentInfos;

  public SlidingIconTabAdapter(FragmentManager fragmentManager, List<FragmentInfo> fragmentInfos) {
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
  public int getPageIconResId(int position) {
    return fragmentInfos.get(position).getIconResId();
  }
}
