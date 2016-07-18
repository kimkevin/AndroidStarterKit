package com.kimkevin.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kimkevin.module.R;

import java.util.List;

public class ListViewAdapter extends BaseAdapter{
  private Context context;
  private List<Type> dataSet;
  private LayoutInflater inflater;

  public enum Type {
    Americano,
    Cafelatte,
    CafeMocha,
    Cappuccino
  }

  public ListViewAdapter(Context context, List<Type> dataSet) {
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
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;

    if (convertView == null) {
      convertView = inflater.inflate(R.layout.layout_list_item, parent, false);

      viewHolder = new ViewHolder();
      viewHolder.title = (TextView) convertView.findViewById(R.id.title);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.title.setText(dataSet.get(position).name());

    return convertView;
  }

  static class ViewHolder {
    public TextView title;
  }
}
