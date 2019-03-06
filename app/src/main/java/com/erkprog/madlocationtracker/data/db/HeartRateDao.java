package com.erkprog.madlocationtracker.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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
