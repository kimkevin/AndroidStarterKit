package com.androidstarterkit.module.widgets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidstarterkit.module.R;
import com.androidstarterkit.module.adapter.RecyclerViewAdapter;
import com.androidstarterkit.module.models.AndroidPlatform;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends Fragment {
  private static List<AndroidPlatform> platforms = new ArrayList<>();

  static {
    platforms.add(new AndroidPlatform("applepie", "1.0", 1));
    platforms.add(new AndroidPlatform("bananabread", "1.1", 2));
    platforms.add(new AndroidPlatform("cupcake", "1.5", 3));
    platforms.add(new AndroidPlatform("donut", "1.6", 4));
    platforms.add(new AndroidPlatform("eclair", "2.0", 5));
    platforms.add(new AndroidPlatform("froyo", "2.2", 8));
    platforms.add(new AndroidPlatform("gingerbread", "2.3", 9));
    platforms.add(new AndroidPlatform("honeycomb", "3.0", 11));
    platforms.add(new AndroidPlatform("icecreamsandwich", "4.0", 14));
    platforms.add(new AndroidPlatform("kitkat", "4.4", 19));
    platforms.add(new AndroidPlatform("lollipop", "5.0", 21));
    platforms.add(new AndroidPlatform("marshmallow", "6.0", 23));
    platforms.add(new AndroidPlatform("nougat", "7.0", 24));
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_recyclerview_main, null);

    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), platforms);
    recyclerView.setAdapter(recyclerViewAdapter);
    return view;
  }
}
