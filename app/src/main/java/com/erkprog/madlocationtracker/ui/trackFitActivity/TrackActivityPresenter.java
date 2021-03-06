package com.erkprog.madlocationtracker.ui.trackFitActivity;

import android.location.Location;
import android.util.Log;

import com.erkprog.madlocationtracker.FitActivityStateListener;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.utils.Utils;

public class TrackActivityPresenter implements TrackActivityContract.Presenter, FitActivityStateListener {

  private static final String TAG = "TrackActivityPresenter";

  private TrackActivityContract.View mView;

  @Override
  public void onServiceConnected(boolean isGettingLocationUpdates) {
    if (!isGettingLocationUpdates) {
      mView.setButtonsState(TrackFitActivity.BT_STATE_INITIAL);
      return;
    }

    if (mView.getCurrentLocation() != null && mView.isMapReady()) {
      mView.showCurrentPosition(mView.getCurrentLocation());
    }

    if (mView.getCurrentFitActivity() != null) {
      mView.showDistance(Utils.getFormattedDistance(mView.getCurrentFitActivity().getDistance()));

      // Set up buttons and time depending on activity's status
      if (mView.getCurrentFitActivity().getStatus() == FitActivity.STATUS_TRACKING) {
        mView.setButtonsState(TrackFitActivity.BT_STATE_TRACKING);
        mView.showDurationStateTracking();
      }
      if (mView.getCurrentFitActivity().getStatus() == FitActivity.STATUS_PAUSED) {
        mView.setButtonsState(TrackFitActivity.BT_STATE_PAUSED);
        mView.showDurationStatePaused();
      }

      //show route
      if (mView.getLocationsList() != null && mView.isMapReady()) {
        mView.showRoute(mView.getLocationsList());
      }
    }
  }

  @Override
  public void onBtStartClicked() {
    if (mView.getCurrentFitActivity() == null) {
      mView.startTracking();
      return;
    }
    if (mView.getCurrentFitActivity().getStatus() == FitActivity.STATUS_PAUSED) {
      mView.continueTracking();
    }
  }

  @Override
  public void onBtStopClicked() {
    if (mView.getCurrentFitActivity() != null) {
      switch (mView.getCurrentFitActivity().getStatus()) {
        case FitActivity.STATUS_PAUSED:
          mView.stopTracking();
          break;
        case FitActivity.STATUS_TRACKING:
          mView.pauseTracking();
          break;
      }
    }
  }

  @Override
  public void onLocationUpdatesStatusChanged(boolean requestingLocationUpdates) {
    if (requestingLocationUpdates) {
      mView.setButtonsState(TrackFitActivity.BT_STATE_TRACKING);
      return;
    }
    mView.setButtonsState(TrackFitActivity.BT_STATE_INITIAL);
  }

  @Override
  public void onTrackingBroadcastReceived(FitActivity usersActivity, Location location) {
    if (location != null) {
      mView.showCurrentPosition(location);
    }

    if (usersActivity != null) {
      mView.showDistance(Utils.getFormattedDistance(usersActivity.getDistance()));
      if (mView.getLocationsList() != null) {
        mView.showRoute(mView.getLocationsList());
      }
    }
  }

  @Override
  public boolean isAttached() {
    return mView != null;
  }

  @Override
  public void bind(TrackActivityContract.View view) {
    mView = view;
  }

  @Override
  public void unBind() {
    mView = null;
  }

  @Override
  public void onHeartRateRead(int value) {
    if (isAttached()) {
      if (value > 0) {
        mView.showHeartRateValue(value);
      } else {
        mView.showErrorHeartRate();
      }
    }
  }

  @Override
  public void onBluetoothConnectionStateChanged(String state) {
    if (isAttached()) {
      mView.showBluetoothConnectionState(state);
    }
  }

  @Override
  public void onStartMeasuringHeartRate() {
    if (isAttached()) {
      mView.onStartMeasuringHeartRate();
    }
  }
}
