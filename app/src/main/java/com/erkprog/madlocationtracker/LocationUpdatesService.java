package com.erkprog.madlocationtracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LocationUpdatesService extends Service {

  private static final String TAG = LocationUpdatesService.class.getSimpleName();

  @androidx.annotation.Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  public void requestLocationUpdates() {
  }

  public void removeLocationUpdates() {
  }


  public class LocalBinder extends Binder {
    LocationUpdatesService getService() {
      return LocationUpdatesService.this;
    }
  }
}
