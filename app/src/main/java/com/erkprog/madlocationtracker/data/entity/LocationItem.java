package com.erkprog.madlocationtracker.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;

import java.util.Date;

@Entity(tableName = "locations",
    foreignKeys = @ForeignKey(entity = FitActivity.class,
        parentColumns = "id",
        childColumns = "activity_id"))
public class LocationItem {

  @PrimaryKey(autoGenerate = true)
  public long id;

  private double latitude;

  private double longitude;

  @ColumnInfo(name = "activity_id")
  private long fitActivityId;

  private Date date;

  public LocationItem() {

  }

  public LocationItem(Location location, long fitActivityId, Date date) {
    latitude = location.getLatitude();
    longitude = location.getLongitude();
    this.fitActivityId = fitActivityId;
    this.date = date;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public long getFitActivityId() {
    return fitActivityId;
  }

  public void setFitActivityId(long fitActivityId) {
    this.fitActivityId = fitActivityId;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Location getLocation() {
    Location location = new Location("");
    location.setLatitude(this.latitude);
    location.setLongitude(this.longitude);
    return location;
  }
}
