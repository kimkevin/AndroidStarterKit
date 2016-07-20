package com.kimkevin.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.kimkevin.sample.adapter.ListViewAdapter;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listview_main);

    /**
     * find resource of ListView and initialize it.
     */
    ListView listView = (ListView) findViewById(R.id.list_view);

    /**
     * set ListViewAdapter to ListView with Data.
     */
    listView.setAdapter(new ListViewAdapter(this, Arrays.asList(CoffeeType.values())));
  }
}
