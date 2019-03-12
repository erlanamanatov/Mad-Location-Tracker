package com.erkprog.madlocationtracker.ui.detailedFitActivity;

import com.erkprog.madlocationtracker.ILifeCycle;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.github.mikephil.charting.data.Entry;

import java.util.List;

class DetailedFitActivityContract {

  interface View {

    void showTrack(List<LocationItem> locationItems);

    void showMessage(int resId);

    void showDuration(String totalDuration);

    void showDistance(String formattedDistance);

    void showAvgSpeed(String formattedSpeed);

    void showTrackingTime(String formattedDuration);

    void plotGraph(List<Entry> entries, long reft);

    void hideGraph();
  }

  interface Presenter extends ILifeCycle<View> {

    boolean isAttached();

    void getLocations();

    void processFitActivity(FitActivity fitActivity);

    void getHeartRate();
  }
}
