package com.androidstarterkit.sample.models;


import com.androidstarterkit.sample.R;

public class FragmentInfo {
  private Class fragmentClass;
  private int iconResId = R.mipmap.ic_launcher;

  public FragmentInfo(Class fragmentClass) {
    this.fragmentClass = fragmentClass;
  }

  public Class getFragmentClass() {
    return fragmentClass;
  }

  public int getIconResId() {
    return iconResId;
  }
}
