package com.erkprog.madlocationtracker.ui.detailedFitActivity;

import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.HeartRateModel;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;
import com.erkprog.madlocationtracker.utils.Utils;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

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
  private long mFitActivityId;

  DetailedActivityPresenter(LocalRepository repository, long fitActivityId) {
    mRepository = repository;
    mFitActivityId = fitActivityId;
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
        .getLocationsByActivity(mFitActivityId, LocationItem.TAG_GEO_FILTERED)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<List<LocationItem>>() {
          @Override
          public void onSuccess(List<LocationItem> locationItems) {
            if (isAttached() && locationItems.size() > 0) {
              mView.showStartOfRoute(locationItems.get(0));
              mView.showEndOfRoute(locationItems.get(locationItems.size() - 1));
              processLocations(locationItems);
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

  private void processLocations(List<LocationItem> locationItems) {
    List<LatLng> routePoints = new ArrayList<>();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    for (LocationItem item : locationItems) {
      routePoints.add(item.getLatLng());
      builder.include(item.getLatLng());
    }

    LatLngBounds bounds = builder.build();
    bounds = adjustBoundsForMaxZoomLevel(bounds);

    if (isAttached()) {
      mView.showRoute(routePoints, bounds);
    }
  }

  private LatLngBounds adjustBoundsForMaxZoomLevel(LatLngBounds bounds) {
    LatLng sw = bounds.southwest;
    LatLng ne = bounds.northeast;
    double deltaLat = Math.abs(sw.latitude - ne.latitude);
    double deltaLon = Math.abs(sw.longitude - ne.longitude);

    final double zoomN = 0.001;
    if (deltaLat < zoomN) {
      sw = new LatLng(sw.latitude - (zoomN - deltaLat / 2), sw.longitude);
      ne = new LatLng(ne.latitude + (zoomN - deltaLat / 2), ne.longitude);
      bounds = new LatLngBounds(sw, ne);
    } else if (deltaLon < zoomN) {
      sw = new LatLng(sw.latitude, sw.longitude - (zoomN - deltaLon / 2));
      ne = new LatLng(ne.latitude, ne.longitude + (zoomN - deltaLon / 2));
      bounds = new LatLngBounds(sw, ne);
    }

    return bounds;
  }

  @Override
  public void getHeartRate() {
    Utils.logd(TAG, "getHeartRate data , fitId " + mFitActivityId);
    mRepository.getDatabase().heartRateDao()
        .getHeartRateByFitId(mFitActivityId)
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
            if (isAttached()) {
              mView.hideGraph();
            }
          }

          @Override
          public void onComplete() {
            Utils.logd(TAG, "heart Rate empty");
            if (isAttached()) {
              mView.hideGraph();
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
