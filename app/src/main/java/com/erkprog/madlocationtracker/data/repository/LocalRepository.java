package com.erkprog.madlocationtracker.data.repository;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.location.Location;

import com.erkprog.madlocationtracker.data.db.AppDatabase;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.HeartRateModel;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.erkprog.madlocationtracker.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocalRepository {

  private static final String TAG = "LocalRepository";

  private AppDatabase mDatabase;
  private Context mContext;

  public LocalRepository(Context context) {
    mDatabase = Room.databaseBuilder(context, AppDatabase.class, "madTrackerDb").build();
    mContext = context;
  }

  public AppDatabase getDatabase() {
    return mDatabase;
  }

  public void saveLocation(LocationItem location) {
    mDatabase.locationDao().addLocation(location);
  }

  public long addActivity(FitActivity fitActivity) {
    return mDatabase.activityDao().addActivity(fitActivity);
  }

  public void deleteActivity(FitActivity fitActivity) {
    mDatabase.activityDao().deleteActivity(fitActivity);
  }

  public void updateActivity(FitActivity fitActivity) {
    mDatabase.activityDao().updateActivity(fitActivity);
  }

  public void saveGeoFilteredTrack(long fitActivityId, List<Location> geoFilteredTrack) {
    if (geoFilteredTrack.size() > 0) {
      List<LocationItem> formattedList = getFormattedLocations(fitActivityId, geoFilteredTrack);
      mDatabase.locationDao().addLocations(formattedList);
    }
  }

  private List<LocationItem> getFormattedLocations(long fitActivityId, List<Location> geoFilteredTrack) {
    List<LocationItem> list = new ArrayList<>();
    for (Location loc : geoFilteredTrack) {
      list.add(new LocationItem(loc, fitActivityId, LocationItem.TAG_GEO_FILTERED));
    }
    return list;
  }

  public void saveHeartRate(HeartRateModel heartRate) {
    Completable.fromAction(() -> mDatabase.heartRateDao().addHeartRate(heartRate))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new CompletableObserver() {
          @Override
          public void onSubscribe(Disposable d) {

          }

          @Override
          public void onComplete() {
            Utils.logd(TAG, "heartrate saved, :" + heartRate.toString());
          }

          @Override
          public void onError(Throwable e) {
            Utils.loge(TAG, "saving error, heartrate: " + heartRate.toString());
          }
        });
  }
}
