package com.androidstarterkit.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.androidstarterkit.module.app.MainBaseAdapter;
import com.androidstarterkit.module.app.WidgetType;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_mainlist_main);

    ListView listView = (ListView) findViewById(R.id.list_view);

    MainBaseAdapter adapter = new MainBaseAdapter(this,
        Arrays.asList(WidgetType.values()));
    listView.setAdapter(adapter);
  }
}
