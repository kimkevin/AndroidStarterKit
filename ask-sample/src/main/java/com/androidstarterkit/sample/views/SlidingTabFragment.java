package com.androidstarterkit.sample.views;
import com.androidstarterkit.sample.models.FragmentInfo;
import com.androidstarterkit.sample.widgets.GridViewFragment;
import com.androidstarterkit.sample.widgets.RecyclerViewFragment;
import com.androidstarterkit.sample.adapter.SlidingTabAdapter;
import com.androidstarterkit.sample.views.SlidingTabLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidstarterkit.sample.R;
import com.androidstarterkit.sample.adapter.SlidingTabAdapter;
import com.androidstarterkit.sample.models.FragmentInfo;
import com.androidstarterkit.sample.widgets.ScrollViewFragment;

import java.util.ArrayList;
import java.util.List;

public class SlidingTabFragment extends Fragment{

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_slidingtablayout_main, null);

    List<FragmentInfo> fragmentInfos = new ArrayList<>();
    fragmentInfos.add(new FragmentInfo(GridViewFragment.class));
    fragmentInfos.add(new FragmentInfo(RecyclerViewFragment.class));



    SlidingTabAdapter adapter = new SlidingTabAdapter(getActivity().getSupportFragmentManager(),
        fragmentInfos);

    ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
    viewPager.setAdapter(adapter);

    SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.tabs);
    slidingTabLayout.setCustomTabView(R.layout.tab_txt_layout, R.id.tab_name_txt);

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
