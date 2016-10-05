package com.androidstarterkit.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.androidstarterkit.module.adapter.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity{
  private List<AndroidPlatform> platforms = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listview_main);

    platforms.add(new AndroidPlatform("applepie", "1.0", 1));
    platforms.add(new AndroidPlatform("bananabread", "1.1", 2));
    platforms.add(new AndroidPlatform("cupcake", "1.5", 3));
    platforms.add(new AndroidPlatform("donut", "1.6", 4));
    platforms.add(new AndroidPlatform("eclair", "2.0", 5));
    platforms.add(new AndroidPlatform("froyo", "2.2", 8));
    platforms.add(new AndroidPlatform("gingerbread", "2.3", 9));
    platforms.add(new AndroidPlatform("honeycomb", "3.0", 11));
    platforms.add(new AndroidPlatform("icecreamsandwich", "4.0", 14));
    platforms.add(new AndroidPlatform("kitkat", "4.4", 19));
    platforms.add(new AndroidPlatform("lollipop", "5.0", 21));
    platforms.add(new AndroidPlatform("marshmallow", "6.0", 23));
    platforms.add(new AndroidPlatform("nougat", "7.0", 24));

    ListView listView = (ListView) findViewById(R.id.list_view);

    ListViewAdapter adapter = new ListViewAdapter(this, platforms);
    listView.setAdapter(adapter);
  }
}
