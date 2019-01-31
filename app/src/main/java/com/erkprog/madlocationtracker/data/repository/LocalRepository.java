package com.erkprog.madlocationtracker.data.repository;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.location.Location;

import com.erkprog.madlocationtracker.data.db.AppDatabase;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.LocationItem;

import java.util.ArrayList;
import java.util.List;

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

  public void saveGeoFilteredTrack(long fitActivityId, List<Location> geoFilteredTrack) {
    List<LocationItem> formattedList = getFormattedLocations(fitActivityId, geoFilteredTrack);
    mDatabase.locationDao().addLocations(formattedList);
  }

  public void saveGeoFilteredTrack(long fitActivityId, List<Location> geoFilteredTrack, String tag) {
    List<LocationItem> formattedList = getFormattedLocations(fitActivityId, geoFilteredTrack, tag);
    mDatabase.locationDao().addLocations(formattedList);
  }

  private List<LocationItem> getFormattedLocations(long fitActivityId, List<Location> geoFilteredTrack) {
    List<LocationItem> list = new ArrayList<>();
    for (Location loc : geoFilteredTrack) {
      list.add(new LocationItem(loc, fitActivityId, LocationItem.TAG_GEO_FILTERED));
    }
    return list;
  }

  private List<LocationItem> getFormattedLocations(long fitActivityId, List<Location> geoFilteredTrack, String tag) {
    List<LocationItem> list = new ArrayList<>();
    for (Location loc : geoFilteredTrack) {
      list.add(new LocationItem(loc, fitActivityId, tag));
    }
    return list;
  }
}
