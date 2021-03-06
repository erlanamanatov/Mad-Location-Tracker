package com.erkprog.madlocationtracker.ui.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.utils.Utils;
import com.erkprog.madlocationtracker.data.entity.FitActivity;

import java.util.Date;
import java.util.List;

public class FitActivityAdapter extends RecyclerView.Adapter<FitActivityAdapter.FitActivityViewHolder> {
  private List<FitActivity> mData;

  OnFitActivityClickListener mListener;

  public void setData(List<FitActivity> fitActivities) {
    mData.clear();
    mData = fitActivities;
  }

  interface OnFitActivityClickListener {
    void onFitActivityClick(FitActivity fitActivity);
  }

  FitActivityAdapter(List<FitActivity> data, OnFitActivityClickListener listener) {
    mData = data;
    mListener = listener;
  }

  @NonNull
  @Override
  public FitActivityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fit_activity_item, viewGroup, false);
    return new FitActivityViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull FitActivityViewHolder viewHolder, int i) {
    FitActivity fitActivity = mData.get(i);
    Date startTime = fitActivity.getStartTime();

    viewHolder.tvDate.setText(
        startTime != null ? Utils.getFormattedDate(startTime) : ""
    );

    viewHolder.tvTime.setText(
        Utils.getFormattedDuration(fitActivity.getTrackingDuration())
    );

    viewHolder.tvDistance.setText(
        Utils.getFormattedDistance(fitActivity.getDistance())
    );

    viewHolder.v.setOnClickListener(v -> {
      mListener.onFitActivityClick(fitActivity);
    });
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }

  class FitActivityViewHolder extends RecyclerView.ViewHolder {
    private TextView tvDate;
    private TextView tvDistance;
    private TextView tvTime;
    private View v;

    FitActivityViewHolder(@NonNull View itemView) {
      super(itemView);
      v = itemView;
      tvDate = itemView.findViewById(R.id.fitact_date);
      tvDistance = itemView.findViewById(R.id.fitact_distance);
      tvTime = itemView.findViewById(R.id.fitact_time);
    }
  }
}
