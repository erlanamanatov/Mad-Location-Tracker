package com.erkprog.madlocationtracker.data.entity;

public class FitChronometer {

  private static FitChronometer instance;

  private long baseTime;

  private long pausedTime;

  private long duration;

  private FitChronometer() {

  }


  public static FitChronometer getInstance() {
    if (instance == null) {
      instance = new FitChronometer();
    }
    return instance;
  }

  public long getBaseTime() {
    return baseTime;
  }

  public void setBaseTime(long baseTime) {
    this.baseTime = baseTime;
  }

  public long getPausedTime() {
    return pausedTime;
  }

  public void setPausedTime(long pausedTime) {
    this.pausedTime = pausedTime;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }
}