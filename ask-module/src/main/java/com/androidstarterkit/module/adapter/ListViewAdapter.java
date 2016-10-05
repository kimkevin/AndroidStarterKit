package com.androidstarterkit.module.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstarterkit.module.AndroidPlatform;
import com.androidstarterkit.module.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ListViewAdapter extends BaseAdapter{
  private static final String TAG = ListViewAdapter.class.getSimpleName();

  private Context context;
  private List<AndroidPlatform> platforms;
  private LayoutInflater inflater;

  public ListViewAdapter(Context context, List<AndroidPlatform> platforms) {
    this.context = context;
    this.platforms = platforms;
    this.inflater = (LayoutInflater) context.getSystemService(
        Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public int getCount() {
    return platforms.size();
  }

  @Override
  public Object getItem(int position) {
    return platforms.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder viewHolder;
    AndroidPlatform platform = (AndroidPlatform) getItem(position);

    if (convertView == null) {
      convertView = inflater.inflate(R.layout.layout_list_item, parent, false);

      viewHolder = new ViewHolder();
      viewHolder.thumbImg = (ImageView) convertView.findViewById(R.id.img);
      viewHolder.titleTxt = (TextView) convertView.findViewById(R.id.title);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    Log.e(TAG, "check = " + platform.getLogoUrl());
    Glide.with(context).load(platform.getLogoUrl()).into(viewHolder.thumbImg);
    viewHolder.thumbImg.setBackgroundColor(Color.YELLOW);
    viewHolder.titleTxt.setText(platform.getName());

    return convertView;
  }

  static class ViewHolder {
    public TextView titleTxt;
    public ImageView thumbImg;
  }
}
