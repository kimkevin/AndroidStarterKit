package com.kimkevin.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kimkevin.sample.adapter.RecyclerViewAdapter;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recyclerview_main);

    /**
     * find resource of RecyclerView and initialize it.
     */
    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    /**
     * set RecyclerViewAdapter to RecyclerView with Data.
     */
    recyclerView.setAdapter(new
        RecyclerViewAdapter(this, Arrays.asList(CoffeeType.values())));
  }
}

