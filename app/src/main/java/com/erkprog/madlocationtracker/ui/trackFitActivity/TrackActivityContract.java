package com.erkprog.madlocationtracker.ui.trackFitActivity;

import com.erkprog.madlocationtracker.ILifeCycle;

public class TrackActivityContract {

  interface View {

    boolean isMapReady();

    boolean isCurrentLocationAvailable();

    void moveCameraToCurrentPosition();

    boolean isCurrentFitActivityAvailable();
  }

  interface Presenter extends ILifeCycle<View> {

    boolean isAttached();

    void onServiceConnected(boolean isGettingLocationUpdates);

  }
}
