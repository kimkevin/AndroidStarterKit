package com.androidstarterkit.sample.adapter;
import com.androidstarterkit.sample.adapter.ListViewAdapter;
import com.androidstarterkit.sample.models.AndroidPlatform;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstarterkit.sample.models.AndroidPlatform;
import com.androidstarterkit.sample.R;
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
    AndroidPlatform platform = (AndroidPlatform) getItem(position);

    ViewHolder viewHolder;

    if (convertView == null) {
      convertView = inflater.inflate(R.layout.layout_list_item, parent, false);

      viewHolder = new ViewHolder();
      viewHolder.logo = (ImageView) convertView.findViewById(R.id.img);
      viewHolder.name = (TextView) convertView.findViewById(R.id.name);
      viewHolder.platformVer = (TextView) convertView.findViewById(R.id.platform_ver);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    Glide.with(context).load(platform.getLogoUrl()).into(viewHolder.logo);
    viewHolder.name.setText(platform.getName());
    viewHolder.platformVer.setText(platform.getVerCode());

    return convertView;
  }

  static class ViewHolder {
    public ImageView logo;
    public TextView name;
    public TextView platformVer;
  }
}
