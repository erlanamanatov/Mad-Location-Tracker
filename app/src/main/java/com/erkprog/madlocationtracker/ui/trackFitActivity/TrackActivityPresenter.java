package com.erkprog.madlocationtracker.ui.trackFitActivity;

public class TrackActivityPresenter implements TrackActivityContract.Presenter {

  private TrackActivityContract.View mView;

  @Override
  public void onServiceConnected(boolean isGettingLocationUpdates) {
    if (isGettingLocationUpdates && mView.isCurrentLocationAvailable() && mView.isMapReady()) {
      mView.moveCameraToCurrentPosition();
      if (mView.isCurrentFitActivityAvailable()) {

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
