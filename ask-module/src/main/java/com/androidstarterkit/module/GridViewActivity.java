package com.androidstarterkit.module;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidstarterkit.module.widgets.GridViewFragment;

public class GridViewActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sample_main);

    if (savedInstanceState == null) {
      GridViewFragment fragment = new GridViewFragment();

      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.container, fragment)
          .commit();
    }
  }
}
