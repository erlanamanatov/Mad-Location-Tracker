package com.erkprog.madlocationtracker.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import com.erkprog.madlocationtracker.data.entity.HeartRateModel;

@Dao
public interface HeartRateDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void addHeartRate(HeartRateModel heartRate);
}
