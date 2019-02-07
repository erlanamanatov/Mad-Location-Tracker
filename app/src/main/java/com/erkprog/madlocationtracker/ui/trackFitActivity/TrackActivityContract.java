package com.erkprog.madlocationtracker.ui.trackFitActivity;

import android.location.Location;

import com.erkprog.madlocationtracker.ILifeCycle;
import com.erkprog.madlocationtracker.data.entity.FitActivity;

import java.util.Date;

public class TrackActivityContract {

  interface View {

    boolean isMapReady();

    void showCurrentPosition(Location location);

    Location getCurrentLocation();

    FitActivity getCurrentFitActivity();

    void showDistance(String distance);

    void showDuration(Date startDate);
  }

  interface Presenter extends ILifeCycle<View> {

    boolean isAttached();

    void onServiceConnected(boolean isGettingLocationUpdates);

  }
}
