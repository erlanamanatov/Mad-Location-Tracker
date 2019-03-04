package com.erkprog.madlocationtracker.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "HeartRate",
    foreignKeys = @ForeignKey(entity = FitActivity.class,
        parentColumns = "id",
        childColumns = "activity_id"))
public class HeartRateModel {

  @PrimaryKey(autoGenerate = true)
  private long id;

  private int value;

  private Date date;

  @ColumnInfo(name = "activity_id")
  private long fitActivityId;

  public HeartRateModel(int value, long fitActivityId) {
    this.value = value;
    this.fitActivityId = fitActivityId;
    this.date = Calendar.getInstance().getTime();
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public long getFitActivityId() {
    return fitActivityId;
  }

  public void setFitActivityId(long fitActivityId) {
    this.fitActivityId = fitActivityId;
  }

  @Override
  public String toString() {
    return "HeartRateModel{" +
        "id=" + id +
        ", value=" + value +
        ", date=" + date +
        ", fitActivityId=" + fitActivityId +
        '}';
  }
}
