package com.erkprog.madlocationtracker.ui.trackFitActivity;

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
