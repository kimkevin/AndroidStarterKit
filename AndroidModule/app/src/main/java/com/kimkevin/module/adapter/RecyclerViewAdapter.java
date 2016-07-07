package com.kimkevin.module.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kimkevin.module.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
  private Context mContext;
  private List<Type> dataSet;

  public enum Type {
    Mask,
    NinePatchMask
  }

  public RecyclerViewAdapter(Context context, List<Type> dataSet) {
    mContext = context;
    this.dataSet = dataSet;
  }

  @Override
  public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(mContext).inflate(R.layout.layout_list_item, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
    holder.title.setText(dataSet.get(position).name());
    holder.title.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switch (dataSet.get(position)) {
          case Mask: {
            break;
          }
          case NinePatchMask: {
            break;
          }
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return dataSet.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView title;

    ViewHolder(View itemView) {
      super(itemView);
      title = (TextView) itemView.findViewById(R.id.title);
    }
  }
}


