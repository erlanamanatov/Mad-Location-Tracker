package com.erkprog.madlocationtracker.data.entity;

public class ChronometerController {

  private static ChronometerController instance;

  private long baseTime;

  private long duration;

  private ChronometerController() {

  }

  public static ChronometerController getInstance() {
    if (instance == null) {
      instance = new ChronometerController();
    }
    return instance;
  }

  public long getBaseTime() {
    return baseTime;
  }

  public void setBaseTime(long baseTime) {
    this.baseTime = baseTime;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }
}