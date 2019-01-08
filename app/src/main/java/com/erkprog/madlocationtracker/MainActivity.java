package com.erkprog.madlocationtracker;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private LocationUpdatesService mService = null;

  private boolean mBound = false;

  private final ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
      mService = binder.getService();
      mBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mService = null;
      mBound = false;
    }
  };

  Button buttonRequestLocationUpdates, buttonRemoveLocationUpdates;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init();
  }

  private void init() {
    buttonRequestLocationUpdates = findViewById(R.id.button_request_location);
    buttonRequestLocationUpdates.setOnClickListener(this);
    buttonRemoveLocationUpdates = findViewById(R.id.button_remove_locations);
    buttonRemoveLocationUpdates.setOnClickListener(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
    bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
        Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStop() {
    if (mBound) {
      unbindService(mServiceConnection);
      mBound = false;
    }
    super.onStop();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_request_location:
        mService.requestLocationUpdates();
        break;

      case R.id.button_remove_locations:
        mService.removeLocationUpdates();
        break;
    }
  }
}
