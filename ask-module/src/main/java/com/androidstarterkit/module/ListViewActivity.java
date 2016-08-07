package com.androidstarterkit.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.androidstarterkit.module.adapter.ListViewAdapter;

import java.util.Arrays;

public class ListViewActivity extends AppCompatActivity{

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
    ListViewAdapter adapter = new ListViewAdapter(this, Arrays.asList(CoffeeType.values()));
    listView.setAdapter(adapter);
  }
}
