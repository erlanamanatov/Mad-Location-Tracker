package com.erkprog.madlocationtracker.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface ActivityDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long addActivity(FitActivity activity);

  @Update
  void updateActivity(FitActivity activity);

  @Query("Select * from Activities")
  Maybe<List<FitActivity>> getAllActivities();
}
