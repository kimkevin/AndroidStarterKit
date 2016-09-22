package com.androidstarterkit.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.androidstarterkit.module.adapter.ListViewAdapter;

import java.util.Arrays;

//https://raw.githubusercontent.com/kimkevin/AndroidStarterKit/master/assets/logo_cupcake.png

public class ListViewActivity extends AppCompatActivity{
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listview_main);

    ListView listView = (ListView) findViewById(R.id.list_view);

    CoffeeType[] values = CoffeeType.values();
    ListViewAdapter adapter = new ListViewAdapter(this, Arrays.asList(values));
    listView.setAdapter(adapter);
  }
}
