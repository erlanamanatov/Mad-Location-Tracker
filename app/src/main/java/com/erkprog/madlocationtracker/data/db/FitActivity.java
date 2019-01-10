package com.erkprog.madlocationtracker.data.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Activities")
public class FitActivity {

  @PrimaryKey(autoGenerate = true)
  public long id;

  private String name;

  @ColumnInfo(name = "start_time")
  private Date startTime;

  @ColumnInfo(name = "end_time")
  private Date endTime;

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

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "FitActivity{" +
        "id=" + id +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", distance=" + distance +
        '}';
  }
}
