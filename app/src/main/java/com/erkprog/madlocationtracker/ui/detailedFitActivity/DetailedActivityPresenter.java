package com.erkprog.madlocationtracker.ui.detailedFitActivity;

import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;
import com.erkprog.madlocationtracker.utils.Utils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DetailedActivityPresenter implements DetailedFitActivityContract.Presenter {
  private static final String TAG = "DetailedActivityPresent";

  private DetailedFitActivityContract.View mView;
  private LocalRepository mRepository;
  private FitActivity mFitActivity;

  DetailedActivityPresenter(LocalRepository repository, FitActivity fitActivity) {
    mRepository = repository;
    mFitActivity = fitActivity;
  }

  @Override
  public void processFitActivity(FitActivity fitActivity) {
    float distance = fitActivity.getDistance();
    long totalDurationMillis = fitActivity.getEndTime().getTime() - fitActivity.getStartTime().getTime();
    mView.showDistance(Utils.getFormattedDistance(distance));
    mView.showDuration(Utils.getFormattedTotalDuration(totalDurationMillis));
    float avgSpeed = distance / ((float) totalDurationMillis / 1000);
    avgSpeed = avgSpeed * 3600 / 1000;
    mView.showAvgSpeed(Utils.getFormattedSpeed(avgSpeed));
  }

  @Override
  public void getLocations() {
    mRepository.getDatabase().locationDao()
        .getLocationsByActivity(mFitActivity.getId(), LocationItem.TAG_GEO_FILTERED)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<List<LocationItem>>() {
          @Override
          public void onSuccess(List<LocationItem> locationItems) {
            if (isAttached() && locationItems.size() > 0) {
              mView.showTrack(locationItems);
            }
          }

          @Override
          public void onError(Throwable e) {
            if (isAttached()) {
              mView.showMessage(R.string.error_loading_detailed_data);
            }
          }
        });
  }

  @Override
  public boolean isAttached() {
    return mView != null;
  }

  @Override
  public void bind(DetailedFitActivityContract.View view) {
    mView = view;
  }

  @Override
  public void unBind() {
    mView = null;
  }
}
