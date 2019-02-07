package com.erkprog.madlocationtracker.ui.trackFitActivity;

import android.location.Location;

import com.erkprog.madlocationtracker.ILifeCycle;
import com.erkprog.madlocationtracker.data.entity.FitActivity;

import java.util.Date;
import java.util.List;

class TrackActivityContract {

  interface View {

    boolean isMapReady();

    void showCurrentPosition(Location location);

    Location getCurrentLocation();

    FitActivity getCurrentFitActivity();

    List<Location> getLocationsList();

    void showDistance(String distance);

    void showDuration(Date startDate);

    void startTracking();

    void stopTracking();

    void setButtonsState(boolean requestingLocationUpdates);

    void showRoute(List<Location> locations);
  }

  interface Presenter extends ILifeCycle<View> {

    boolean isAttached();

    void onServiceConnected(boolean isGettingLocationUpdates);

    void onStartTrackingClicked();

    void onStopTrackingClicked();

    void onLocationUpdatesStatusChanged(boolean requestingLocationUpdates);

    void onBroadcastReceived(FitActivity usersActivity, Location newLocation);
  }
}
