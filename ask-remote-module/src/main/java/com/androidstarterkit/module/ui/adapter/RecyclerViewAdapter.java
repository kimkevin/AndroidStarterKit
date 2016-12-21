package com.androidstarterkit.module.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstarterkit.module.data.AndroidPlatform;
import com.androidstarterkit.module.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
  private Context context;
  private List<AndroidPlatform> platforms;

  public RecyclerViewAdapter(Context context, List<AndroidPlatform> platforms) {
    this.context = context;
    this.platforms = platforms;
  }

  @Override
  public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(context).inflate(R.layout.layout_list_item, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
    AndroidPlatform platform = platforms.get(position);

    Glide.with(context).load(platform.getLogoUrl()).into(holder.logo);
    holder.name.setText(platform.getName());
    holder.platformVer.setText(platform.getVerCode());
  }

  @Override
  public int getItemCount() {
    return platforms.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView logo;
    public TextView name;
    public TextView platformVer;

    ViewHolder(View itemView) {
      super(itemView);

      logo = (ImageView) itemView.findViewById(R.id.img);
      name = (TextView) itemView.findViewById(R.id.name);
      platformVer = (TextView) itemView.findViewById(R.id.platform_ver);
    }
  }
}


