package com.erkprog.madlocationtracker.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.erkprog.madlocationtracker.data.entity.LocationItem;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface LocationDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void addLocation(LocationItem location);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void addLocations(List<LocationItem> locations);

  @Query("Select * from locations where activity_id = :activityId and tag = :tag")
  Single<List<LocationItem>> getLocationsByActivity(long activityId, String tag);
}
