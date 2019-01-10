package com.erkprog.madlocationtracker.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

@Dao
public interface LocationDao {

 @Insert(onConflict = OnConflictStrategy.REPLACE)
 void addLocation(LocationItem location);
}
