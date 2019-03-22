package com.erkprog.madlocationtracker.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.erkprog.madlocationtracker.data.entity.FitActivity;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface ActivityDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long addActivity(FitActivity activity);

  @Update
  void updateActivity(FitActivity activity);

  @Delete
  void deleteActivity(FitActivity activity);

  @Query("Select * from Activities where end_time is not null")
  Maybe<List<FitActivity>> getAllActivities();
}
