package com.androidstarterkit.module.widget;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidstarterkit.module.R;
import com.androidstarterkit.module.adapter.SlidingIconTabAdapter;
import com.androidstarterkit.module.data.FragmentInfo;
import com.androidstarterkit.module.ui.ScrollViewFragment;

import java.util.ArrayList;
import java.util.List;


public class SlidingIconTabFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_slidingtablayout_main, null);

    List<FragmentInfo> fragmentInfos = new ArrayList<>();
    fragmentInfos.add(new FragmentInfo(ScrollViewFragment.class));
    fragmentInfos.add(new FragmentInfo(ScrollViewFragment.class));

    SlidingIconTabAdapter adapter = new SlidingIconTabAdapter(getActivity().getSupportFragmentManager(), fragmentInfos);

    ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
    viewPager.setAdapter(adapter);

    SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.tabs);
    slidingTabLayout.setCustomTabView(R.layout.tab_img_layout, R.id.tab_name_img);
    slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
      @Override
      public int getIndicatorColor(int position) {
        return Color.WHITE;
      }
    });

    slidingTabLayout.setViewPager(viewPager);
    return view;
  }
}
