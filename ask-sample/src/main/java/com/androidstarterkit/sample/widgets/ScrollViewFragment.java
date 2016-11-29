package com.androidstarterkit.sample.widgets;
import com.androidstarterkit.sample.widgets.ScrollViewFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidstarterkit.sample.R;

public class ScrollViewFragment extends Fragment {
  private static final String ARG_POSITION = "position";

  public static ScrollViewFragment newInstance(int position) {
    ScrollViewFragment fragment = new ScrollViewFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_POSITION, position);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main, null);
    TextView posTxt = (TextView) view.findViewById(R.id.pos_txt);
    posTxt.setText("ScrollView (Default)");
    return view;
  }
}
