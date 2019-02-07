package com.erkprog.madlocationtracker.ui.trackFitActivity;

import android.location.Location;

import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.utils.Utils;

public class TrackActivityPresenter implements TrackActivityContract.Presenter {

  private TrackActivityContract.View mView;

  @Override
  public void onServiceConnected(boolean isGettingLocationUpdates) {
    if (isGettingLocationUpdates && mView.getCurrentLocation() != null && mView.isMapReady()) {
      mView.showCurrentPosition(mView.getCurrentLocation());
      if (mView.getCurrentFitActivity() != null) {
        mView.showDistance(Utils.getFormattedDistance(mView.getCurrentFitActivity().getDistance()));
        mView.showDuration(mView.getCurrentFitActivity().getStartTime());
      }
    }
  }

  @Override
  public void onStartTrackingClicked() {
    mView.startTracking();
  }

  @Override
  public void onStopTrackingClicked() {
    mView.stopTracking();
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
