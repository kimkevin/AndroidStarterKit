package com.kimkevin.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.kimkevin.module.app.MainBaseAdapter;
import com.kimkevin.module.app.WidgetType;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_listview_main);

    ListView listView = (ListView) findViewById(R.id.list_view);

    MainBaseAdapter adapter = new MainBaseAdapter(this,
        Arrays.asList(WidgetType.values()));
    listView.setAdapter(adapter);
  }
}
