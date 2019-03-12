package com.erkprog.madlocationtracker.ui.detailedFitActivity;

import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.HeartRateModel;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;
import com.erkprog.madlocationtracker.utils.Utils;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
    long trackingTime = fitActivity.getTrackingDuration();
    mView.showDistance(Utils.getFormattedDistance(distance));
    mView.showDuration(Utils.getFormattedDuration(totalDurationMillis));
    float avgSpeed = distance / ((float) trackingTime / 1000);
    avgSpeed = avgSpeed * 3600 / 1000;
    mView.showAvgSpeed(Utils.getFormattedSpeed(avgSpeed));
    mView.showTrackingTime(Utils.getFormattedDuration(trackingTime));
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
  public void getHeartRate() {
    Utils.logd(TAG, "getHeartRate data , fitId " + mFitActivity.getId());
    mRepository.getDatabase().heartRateDao()
        .getHeartRateByFitId(mFitActivity.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new MaybeObserver<List<HeartRateModel>>() {
          @Override
          public void onSubscribe(Disposable d) {

          }

          @Override
          public void onSuccess(List<HeartRateModel> heartRateModels) {
            if (!isAttached()) {
              return;
            }
            if (heartRateModels.size() == 0) {
              mView.hideGraph();
              mView.showMessage(R.string.no_heart_rate_data);
              return;
            }
            if (heartRateModels.size() < 4) {
              mView.hideGraph();
              mView.showMessage(R.string.not_enough_hr_data);
              return;
            }
            Utils.logd(TAG, "heartRate data size : " + heartRateModels.size());
            long referenceTimestamp = heartRateModels.get(0).getDate().getTime();
            List<Entry> entries = new ArrayList<>();
            for (HeartRateModel model : heartRateModels) {
              entries.add(new Entry((float) (model.getDate().getTime() - referenceTimestamp), (float) model.getValue()));
            }
            mView.plotGraph(entries, referenceTimestamp);
          }

          @Override
          public void onError(Throwable e) {
            Utils.logd(TAG, "heartRate error: " + e);
          }

          @Override
          public void onComplete() {
            Utils.logd(TAG, "heart Rate empty");
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
