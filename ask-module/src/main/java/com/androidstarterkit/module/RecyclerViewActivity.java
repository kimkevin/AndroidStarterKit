package com.androidstarterkit.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidstarterkit.module.widgets.RecyclerViewFragment;

public class RecyclerViewActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sample_main);

    if (savedInstanceState == null) {
      RecyclerViewFragment fragment = new RecyclerViewFragment();

      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.container, fragment)
          .commit();
    }
  }
}

