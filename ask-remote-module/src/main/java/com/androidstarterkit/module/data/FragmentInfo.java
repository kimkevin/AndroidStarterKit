package com.androidstarterkit.module.data;


import com.androidstarterkit.module.R;

public class FragmentInfo {
  private Class fragmentClass;

  public FragmentInfo(Class fragmentClass) {
    this.fragmentClass = fragmentClass;
  }

  public Class getFragmentClass() {
    return fragmentClass;
  }

  public int getIconResId() {
    return R.mipmap.ic_launcher;
  }
}
