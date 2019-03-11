package com.erkprog.madlocationtracker;

import com.polidea.rxandroidble2.RxBleConnection;

public interface FitActivityStateListener {

  void onHeartRateRead(int value);

  void onBluetoothConnectionStateChanged(RxBleConnection.RxBleConnectionState state);
}
