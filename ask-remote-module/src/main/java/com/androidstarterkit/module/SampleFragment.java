package com.androidstarterkit.module;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.androidstarterkit.module.adapter.MainBaseAdapter;
import com.androidstarterkit.module.sample.SampleType;

import java.util.Arrays;

public class SampleFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_listview, container, false);

    ListView listView = (ListView) view.findViewById(R.id.list_view);

    MainBaseAdapter adapter = new MainBaseAdapter(getActivity(),
        Arrays.asList(SampleType.values()));
    listView.setAdapter(adapter);
    return view;
  }
}
