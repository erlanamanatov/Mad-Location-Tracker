package com.erkprog.madlocationtracker.ui.detailedFitActivity;

import com.erkprog.madlocationtracker.Utils;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;

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
  public void getLocations() {
    mRepository.getDatabase().locationDao()
        .getLocationsByActivity(mFitActivity.getId(), LocationItem.TAG_GEO_FILTERED)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<List<LocationItem>>() {
          @Override
          public void onSuccess(List<LocationItem> locationItems) {
            if (locationItems.size() > 0) {
              mView.showTrack(locationItems);
//              displayLocations();
            }
          }

          @Override
          public void onError(Throwable e) {
            Utils.logd(TAG, "Error getting locations, fitActivity id = " + String.valueOf(mFitActivity.getId()));
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
