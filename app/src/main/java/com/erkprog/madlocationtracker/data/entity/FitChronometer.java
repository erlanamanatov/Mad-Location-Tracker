package com.erkprog.madlocationtracker.data.entity;

import android.widget.Chronometer;

public class FitChronometer {

  private static FitChronometer instance;

  private FitChronometer() {

  }

  private Chronometer mChronometer;

  public static FitChronometer getInstance() {
    if (instance == null) {
      instance = new FitChronometer();
    }
    return instance;
  }

  public long getBase() {
    return mChronometer.getBase();
  }

  public void setBase(long base) {
    mChronometer.setBase(base);
  }
}