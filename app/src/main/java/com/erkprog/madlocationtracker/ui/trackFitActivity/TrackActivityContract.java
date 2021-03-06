package com.erkprog.madlocationtracker.ui.trackFitActivity;

import android.location.Location;

import com.erkprog.madlocationtracker.FitActivityStateListener;
import com.erkprog.madlocationtracker.ILifeCycle;
import com.erkprog.madlocationtracker.data.entity.FitActivity;

import java.util.List;

class TrackActivityContract {

  interface View {

    boolean isMapReady();

    void showCurrentPosition(Location location);

    Location getCurrentLocation();

    FitActivity getCurrentFitActivity();

    List<Location> getLocationsList();

    void showDistance(String distance);

    void showDurationStatePaused();

    void showDurationStateTracking();

    void startTracking();

    void pauseTracking();

    void continueTracking();

    void stopTracking();

    void setButtonsState(int state);

    void showRoute(List<Location> locations);

    void showMessage(String message);

    void showHeartRateValue(int value);

    void showBluetoothConnectionState(String state);

    void onStartMeasuringHeartRate();

    void showErrorHeartRate();
  }

  interface Presenter extends ILifeCycle<View>, FitActivityStateListener {

    boolean isAttached();

    void onServiceConnected(boolean isGettingLocationUpdates);

    void onBtStartClicked();

    void onBtStopClicked();

    void onLocationUpdatesStatusChanged(boolean requestingLocationUpdates);

    void onTrackingBroadcastReceived(FitActivity usersActivity, Location newLocation);
  }
}
