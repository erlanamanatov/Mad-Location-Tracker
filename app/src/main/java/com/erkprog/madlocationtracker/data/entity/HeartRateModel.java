package com.erkprog.madlocationtracker.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "HeartRate",
    foreignKeys = @ForeignKey(entity = FitActivity.class,
        parentColumns = "id",
        childColumns = "activity_id",
        onDelete = CASCADE))
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

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public int getValue() {
    return value;
  }

  public Date getDate() {
    return date;
  }

  public long getFitActivityId() {
    return fitActivityId;
  }

  @Override
  public String toString() {
    return "HeartRateModel{" +
        ", value=" + value +
        ", date=" + date +
        ", fitActivityId=" + fitActivityId +
        '}';
  }
}
