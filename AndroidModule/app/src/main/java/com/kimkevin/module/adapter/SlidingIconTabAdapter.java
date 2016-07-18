package com.kimkevin.module.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kimkevin.module.R;
import com.kimkevin.module.SlidingTabFragment;
import com.kimkevin.module.widgets.SlidingTabLayout;

public class SlidingIconTabAdapter extends FragmentPagerAdapter implements SlidingTabLayout.TabIconProvider {

  private static final int iconRes[] = {
      R.drawable.ic_action_camera,
      R.drawable.ic_action_video
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
