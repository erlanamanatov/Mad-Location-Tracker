package com.erkprog.madlocationtracker.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.erkprog.madlocationtracker.data.entity.HeartRateModel;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface HeartRateDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void addHeartRate(HeartRateModel heartRate);

  @Query("Select * from HeartRate where activity_id = :activityId")
  Maybe<List<HeartRateModel>> getHeartRateByFitId(long activityId);
}
