package com.erkprog.madlocationtracker.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "locations",
    foreignKeys = @ForeignKey(entity = FitActivity.class,
        parentColumns = "id",
        childColumns = "activity_id",
    onDelete = CASCADE))
public class LocationItem {

  // locations only kalman filtered
  public static final String TAG_KALMAN_FILTERED = "kalman_filtered";
  // locations kalman and geo filtered
  public static final String TAG_GEO_FILTERED = "geo_filtered";

  @PrimaryKey(autoGenerate = true)
  public long id;

  private double latitude;

  private double longitude;

  @ColumnInfo(name = "activity_id")
  private long fitActivityId;

  private Date date;

  private String tag;

  public LocationItem() {

  }

  public LocationItem(Location location, long fitActivityId) {
    latitude = location.getLatitude();
    longitude = location.getLongitude();
    this.fitActivityId = fitActivityId;
    this.date = Calendar.getInstance().getTime();
    this.tag = TAG_KALMAN_FILTERED;
  }

  public LocationItem(Location location, long fitActivityId, String tag) {
    latitude = location.getLatitude();
    longitude = location.getLongitude();
    this.fitActivityId = fitActivityId;
    this.date = Calendar.getInstance().getTime();
    this.tag = tag;
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

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public Location getLocation() {
    Location location = new Location("");
    location.setLatitude(this.latitude);
    location.setLongitude(this.longitude);
    return location;
  }

  public LatLng getLatLng() {
    return new LatLng(this.latitude, this.longitude);
  }
}
