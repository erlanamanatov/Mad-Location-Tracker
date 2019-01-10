package com.erkprog.madlocationtracker.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

@Dao
public interface ActivityDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long addActivity(FitActivity activity);

  @Update
  void updateActivity(FitActivity activity);
}
