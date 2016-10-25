package com.androidstarterkit.sample.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidstarterkit.sample.R;

public class DefaultTabFragment extends Fragment {
  private static final String ARG_POSITION = "position";

  public static DefaultTabFragment newInstance(int position) {
    DefaultTabFragment fragment = new DefaultTabFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_POSITION, position);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main, null);
    TextView posTxt = (TextView) view.findViewById(R.id.pos_txt);
    posTxt.setText("Framgent");
    return view;
  }
}
