package com.androidstarterkit.module.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidstarterkit.module.ListViewActivity;
import com.androidstarterkit.module.R;
import com.androidstarterkit.module.RecyclerViewActivity;
import com.androidstarterkit.module.SlidingIconTabActivity;
import com.androidstarterkit.module.SlidingTabActivity;
import com.androidstarterkit.module.models.WidgetType;

import java.util.List;

public class MainBaseAdapter extends BaseAdapter {
  private Context context;
  private List<WidgetType> dataSet;
  private LayoutInflater inflater;

  public MainBaseAdapter(Context context, List<WidgetType> dataSet) {
    this.context = context;
    this.dataSet = dataSet;
    this.inflater = (LayoutInflater) context.getSystemService(
        Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public int getCount() {
    return dataSet.size();
  }

  @Override
  public Object getItem(int position) {
    return dataSet.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;

    if (convertView == null) {
      convertView = inflater.inflate(R.layout.layout_main_list_item, parent, false);

      viewHolder = new ViewHolder();
      viewHolder.title= (TextView) convertView.findViewById(R.id.title);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.title.setText(dataSet.get(position).name());

    convertView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        WidgetType widgetType = dataSet.get(position);
        Intent intent = null;

        switch (widgetType) {
          case RecyclerView:
            intent = new Intent(context, RecyclerViewActivity.class);
            break;
          case ListView:
            intent = new Intent(context, ListViewActivity.class);
            break;
          case SlidingTabLayout:
            intent = new Intent(context, SlidingTabActivity.class);
            break;
          case SlidingIconTabLayout:
            intent = new Intent(context, SlidingIconTabActivity.class);
            break;
        }

        context.startActivity(intent);
      }
    });

    return convertView;
  }

  static class ViewHolder {
    public TextView title;
  }
}
