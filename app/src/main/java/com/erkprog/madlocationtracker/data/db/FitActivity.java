package com.erkprog.madlocationtracker.data.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Activities")
public class FitActivity {

  @PrimaryKey(autoGenerate = true)
  public long id;

  @ColumnInfo(name = "start_time")
  private Date startTime;

  public FitActivity(Date startTime) {
    this.startTime = startTime;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }
}
