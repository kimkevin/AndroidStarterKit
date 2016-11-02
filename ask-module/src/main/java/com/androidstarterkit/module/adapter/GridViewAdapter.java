package com.androidstarterkit.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstarterkit.module.R;
import com.androidstarterkit.module.models.AndroidPlatform;
import com.bumptech.glide.Glide;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {
  private Context context;
  private List<AndroidPlatform> platforms;

  public GridViewAdapter(Context context, List<AndroidPlatform> platforms) {
    this.context = context;
    this.platforms = platforms;
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
    AndroidPlatform platform = (AndroidPlatform) getItem(position);

    ViewHolder viewHolder;

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.layout_grid_item, parent, false);

      viewHolder = new ViewHolder();
      viewHolder.img = (ImageView) convertView.findViewById(R.id.cell_img);
      viewHolder.txt = (TextView) convertView.findViewById(R.id.cell_txt);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    Glide.with(context).load(platform.getLogoUrl()).into(viewHolder.img);
    viewHolder.txt.setText(platform.getVerCode());

    return convertView;
  }

  static class ViewHolder {
    public ImageView img;
    public TextView txt;
  }
}
