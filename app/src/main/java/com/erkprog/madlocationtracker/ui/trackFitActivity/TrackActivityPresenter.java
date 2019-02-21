package com.erkprog.madlocationtracker.ui.trackFitActivity;

import android.location.Location;

import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.utils.Utils;

public class TrackActivityPresenter implements TrackActivityContract.Presenter {

  private TrackActivityContract.View mView;

  @Override
  public void onServiceConnected(boolean isGettingLocationUpdates) {
    if (isGettingLocationUpdates) {
      if (mView.getCurrentLocation() != null && mView.isMapReady()) {
        mView.showCurrentPosition(mView.getCurrentLocation());
      }
      if (mView.getCurrentFitActivity() != null) {
        mView.showDistance(Utils.getFormattedDistance(mView.getCurrentFitActivity().getDistance()));
        mView.showDuration(mView.getCurrentFitActivity().getStartTime());
      }
    }
  }

  @Override
  public void onBtStartClicked() {
    if (mView.getCurrentFitActivity() == null) {
      mView.startTracking();
    } else {
      if (mView.getCurrentFitActivity().getStatus() == FitActivity.STATUS_PAUSED) {
        mView.continueTracking();
      }
    }
//    mView.startTracking();
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
    mView.setButtonsState(requestingLocationUpdates);
  }

  @Override
  public void onBroadcastReceived(FitActivity usersActivity, Location location) {
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

}
