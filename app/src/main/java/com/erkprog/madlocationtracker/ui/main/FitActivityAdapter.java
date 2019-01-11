package com.erkprog.madlocationtracker.ui.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.data.db.FitActivity;

import java.util.Date;
import java.util.List;

public class FitActivityAdapter extends RecyclerView.Adapter<FitActivityAdapter.FitActivityViewHolder> {
  private List<FitActivity> mData;


  public FitActivityAdapter(List<FitActivity> data) {
    mData = data;
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
    Date endTime = fitActivity.getEndTime();
    long timeDiff = -1;
    if (startTime != null && endTime != null) {
      timeDiff = endTime.getTime() - startTime.getTime();
    }

    viewHolder.tvDate.setText(startTime.toString());
    viewHolder.tvDistance.setText(String.valueOf(fitActivity.getDistance()));
    viewHolder.tvTime.setText(String.valueOf(timeDiff));
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }

  public class FitActivityViewHolder extends RecyclerView.ViewHolder {
    private TextView tvDate;
    private TextView tvDistance;
    private TextView tvTime;

    public FitActivityViewHolder(@NonNull View itemView) {
      super(itemView);
      tvDate = itemView.findViewById(R.id.fitact_date);
      tvDistance = itemView.findViewById(R.id.fitact_distance);
      tvTime = itemView.findViewById(R.id.fitact_time);
    }
  }
}
