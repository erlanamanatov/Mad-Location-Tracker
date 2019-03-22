package com.erkprog.madlocationtracker.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.HeartRateModel;
import com.erkprog.madlocationtracker.data.entity.LocationItem;

@Database(entities = {FitActivity.class, LocationItem.class, HeartRateModel.class},
    version = 1,
    exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

  public abstract ActivityDao activityDao();

  public abstract LocationDao locationDao();

  public abstract HeartRateDao heartRateDao();

}