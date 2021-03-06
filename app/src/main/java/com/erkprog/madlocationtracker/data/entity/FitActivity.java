package com.erkprog.madlocationtracker.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.erkprog.madlocationtracker.utils.Utils;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "Activities")
public class FitActivity implements Parcelable {

  public static final int STATUS_TRACKING = 1;
  public static final int STATUS_PAUSED = 2;

  @PrimaryKey(autoGenerate = true)
  public long id;

  private String name;

  @ColumnInfo(name = "start_time")
  private Date startTime;

  @ColumnInfo(name = "end_time")
  private Date endTime;

  private float distance;

  @Ignore
  private int status;

  @ColumnInfo(name = "tracking_duration")
  private long trackingDuration;

  public FitActivity() {

  }

  public FitActivity(long id) {
    this.id = id;
    this.startTime = Calendar.getInstance().getTime();
    this.status = STATUS_TRACKING;
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

  public long getId() {
    return id;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public long getTrackingDuration() {
    return trackingDuration;
  }

  public void setTrackingDuration(long trackingDuration) {
    this.trackingDuration = trackingDuration;
  }

  @Override
  public String toString() {
    return "FitActivity{" +
        "id=" + id +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", distance=" + distance +
        ", status" + status +
        ", trackingDuration " + Utils.getFormattedDuration(trackingDuration) +
        '}';
  }

  private FitActivity(Parcel in) {
    this.id = in.readLong();
    this.name = in.readString();
    this.startTime = new Date(in.readLong());
    this.endTime = new Date(in.readLong());
    this.distance = in.readFloat();
    this.status = in.readInt();
    this.trackingDuration = in.readLong();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeString(this.name);
    dest.writeLong(this.startTime.getTime());
    dest.writeLong(this.endTime.getTime());
    dest.writeFloat(this.distance);
    dest.writeInt(this.status);
    dest.writeLong(this.trackingDuration);
  }

  public static final Parcelable.Creator<FitActivity> CREATOR = new Parcelable.Creator<FitActivity>() {

    @Override
    public FitActivity createFromParcel(Parcel source) {
      return new FitActivity(source);
    }

    @Override
    public FitActivity[] newArray(int size) {
      return new FitActivity[size];
    }
  };
}
