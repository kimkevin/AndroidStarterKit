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
  private Context context;
  private List<Type> dataSet;

  public enum Type {
    Americano,
    Cafelatte,
    CafeMocha,
    Cappuccino
  }

  public RecyclerViewAdapter(Context context, List<Type> dataSet) {
    this.context = context;
    this.dataSet = dataSet;
  }

  @Override
  public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(context).inflate(R.layout.layout_list_item, parent, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(final RecyclerViewAdapter.ViewHolder holder, final int position) {
    holder.title.setText(dataSet.get(position).name());
    holder.title.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO : do something by data type
        switch (dataSet.get(position)) {
          case Americano:
            break;
          case Cafelatte:
            break;
          case CafeMocha:
            break;
          case Cappuccino:
            break;
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


