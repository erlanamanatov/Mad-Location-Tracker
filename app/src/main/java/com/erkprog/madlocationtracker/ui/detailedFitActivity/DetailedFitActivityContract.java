package com.erkprog.madlocationtracker.ui.detailedFitActivity;

import com.erkprog.madlocationtracker.ILifeCycle;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

class DetailedFitActivityContract {

  interface View {

    void showMessage(int resId);

    void showDuration(String totalDuration);

    void showDistance(String formattedDistance);

    void showAvgSpeed(String formattedSpeed);

    void showTrackingTime(String formattedDuration);

    void plotGraph(List<Entry> entries, long reft);

    void hideGraph();

    void showStartOfRoute(LocationItem locationItem);

    void showEndOfRoute(LocationItem locationItem);

    void showRoute(List<LatLng> routePoints, LatLngBounds bounds);
  }

  interface Presenter extends ILifeCycle<View> {

    boolean isAttached();

    void getLocations();

    void processFitActivity(FitActivity fitActivity);

    void getHeartRate();
  }
}
