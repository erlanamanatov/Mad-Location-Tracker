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

  private float distance;

  public FitActivity() {

  }

  public FitActivity(long id, Date startTime) {
    this.id = id;
    this.startTime = startTime;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public float getDistance() {
    return distance;
  }

  public void setDistance(float distance) {
    this.distance = distance;
  }

  public void addDistance(float extra) {
    this.distance += extra;
  }
}
