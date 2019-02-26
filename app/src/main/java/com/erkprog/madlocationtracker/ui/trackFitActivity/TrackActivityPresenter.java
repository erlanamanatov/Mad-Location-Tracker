package com.erkprog.madlocationtracker.ui.trackFitActivity;

import android.location.Location;
import android.util.Log;

import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.utils.Utils;

public class TrackActivityPresenter implements TrackActivityContract.Presenter {

  private static final String TAG = "TrackActivityPresenter";

  private TrackActivityContract.View mView;

  @Override
  public void onServiceConnected(boolean isGettingLocationUpdates) {
    if (!isGettingLocationUpdates) {
      Utils.logd(TAG, "set button to initial");
      mView.setButtonsState(TrackFitActivity.BT_STATE_INITIAL);
      return;
    }

    if (mView.getCurrentLocation() != null && mView.isMapReady()) {
      mView.showCurrentPosition(mView.getCurrentLocation());
    }

    if (mView.getCurrentFitActivity() != null) {
      mView.showDistance(Utils.getFormattedDistance(mView.getCurrentFitActivity().getDistance()));
      mView.showDuration(mView.getCurrentFitActivity().getStartTime());
      Utils.logd(TAG, "status " + mView.getCurrentFitActivity().getStatus());
      if (mView.getCurrentFitActivity().getStatus() == FitActivity.STATUS_TRACKING) {
        mView.setButtonsState(TrackFitActivity.BT_STATE_TRACKING);
      }
      if (mView.getCurrentFitActivity().getStatus() == FitActivity.STATUS_PAUSED) {
        mView.setButtonsState(TrackFitActivity.BT_STATE_PAUSED);
      }
    }

//    if (isGettingLocationUpdates) {
//      if (mView.getCurrentLocation() != null && mView.isMapReady()) {
//        mView.showCurrentPosition(mView.getCurrentLocation());
//      }
//      if (mView.getCurrentFitActivity() != null) {
//        mView.showDistance(Utils.getFormattedDistance(mView.getCurrentFitActivity().getDistance()));
//        mView.showDuration(mView.getCurrentFitActivity().getStartTime());
//
//      }
//    } else {
//      mView.setButtonsState(TrackFitActivity.BT_STATE_INITIAL);
//    }

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
    if (requestingLocationUpdates) {
      mView.setButtonsState(TrackFitActivity.BT_STATE_TRACKING);
      return;
    }
    mView.setButtonsState(TrackFitActivity.BT_STATE_INITIAL);
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
