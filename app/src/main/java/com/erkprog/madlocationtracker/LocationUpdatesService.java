package com.erkprog.madlocationtracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocationUpdatesService extends Service {

  @androidx.annotation.Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
