package com.erkprog.madlocationtracker;

public interface FitActivityStateListener {

  void onHeartRateRead(int value);

  void onBluetoothConnectionStateChanged(String state);

  void onStartMeasuringHeartRate();

}
