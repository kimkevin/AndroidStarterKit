package com.androidstarterkit.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidstarterkit.module.widgets.ListViewFragment;

public class ListViewActivity extends AppCompatActivity{
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sample_main);

    if (savedInstanceState == null) {
      ListViewFragment fragment = new ListViewFragment();

      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.container, fragment)
          .commit();
    }
  }
}
