package com.androidstarterkit.module.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.androidstarterkit.module.data.FragmentInfo;
import com.androidstarterkit.module.ui.view.ScrollViewFragment;
import com.androidstarterkit.module.widget.SlidingTabLayout;

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
      return new ScrollViewFragment();
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
