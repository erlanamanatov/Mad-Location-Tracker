package com.erkprog.madlocationtracker.data.repository;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.erkprog.madlocationtracker.data.db.AppDatabase;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.LocationItem;

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
    return mDatabase.acitivityDao().addActivity(fitActivity);
  }

  public void updateActivity(FitActivity fitActivity) {
    mDatabase.acitivityDao().updateActivity(fitActivity);
  }

}
