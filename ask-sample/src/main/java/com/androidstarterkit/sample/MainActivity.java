package com.androidstarterkit.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidstarterkit.sample.views.SlidingTabFragment;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getSupportActionBar().setElevation(0);
    setContentView(R.layout.activity_sample_main);

    if (savedInstanceState == null) {
      SlidingTabFragment fragment = new SlidingTabFragment();

      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.container, fragment)
          .commit();
    }
  }
}
