package com.kimkevin.module;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.kimkevin.module.adapter.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listview_main);

    /**
     * find resource of ListView and initialize it.
     */
    ListView listView = (ListView) findViewById(R.id.list_view);

    List<ListViewAdapter.Type> dataSet = new ArrayList<>();
    dataSet.add(ListViewAdapter.Type.Americano);
    dataSet.add(ListViewAdapter.Type.Cafelatte);
    dataSet.add(ListViewAdapter.Type.CafeMocha);
    dataSet.add(ListViewAdapter.Type.Cappuccino);

    /**
     * set RecyclerViewAdapter to RecyclerView with Data.
     */
    listView.setAdapter(new ListViewAdapter(this, dataSet));
  }
}
