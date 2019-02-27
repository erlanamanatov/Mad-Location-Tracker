package com.erkprog.madlocationtracker.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.LocationItem;

@Database(entities = {FitActivity.class, LocationItem.class},
    version = 1,
    exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

  public abstract ActivityDao activityDao();

  public abstract LocationDao locationDao();

}
