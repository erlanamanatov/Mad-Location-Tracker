package com.erkprog.madlocationtracker;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

  private LocationUpdatesService mService = null;
  private static final int REQUEST_GPS = 1;

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
    PreferenceManager.getDefaultSharedPreferences(this)
        .registerOnSharedPreferenceChangeListener(this);
    setButtonsState(Utils.requestingLocationUpdates(this));
    bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
        Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStop() {
    if (mBound) {
      unbindService(mServiceConnection);
      mBound = false;
    }
    PreferenceManager.getDefaultSharedPreferences(this)
        .unregisterOnSharedPreferenceChangeListener(this);
    super.onStop();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_request_location:
        if (isGpsPersmissionGranted()) {
          if (isGpsEnabled()) {
            mService.requestLocationUpdates();
          } else {
            showTurnGpsOnDialog();
          }
        } else {
          requestGpsPermission();
        }
        break;

      case R.id.button_remove_locations:
        mService.removeLocationUpdates();
        break;
    }
  }

  private boolean isGpsEnabled() {
    LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  private boolean isGpsPersmissionGranted() {
    return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
  }

  private void requestGpsPermission() {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
  }

  private void showTurnGpsOnDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setMessage("Turn on gps in settings")
        .setTitle("Gps is disabled")
        .setPositiveButton("To settings", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
        .setNegativeButton("Cancel", (dialog, which) -> Toast.makeText(MainActivity.this, "Turn GPS on to get location updates", Toast.LENGTH_SHORT)
            .show());
    builder.show();
  }

  private void setButtonsState(boolean requestingLocationUpdates) {
    if (requestingLocationUpdates) {
      buttonRequestLocationUpdates.setEnabled(false);
      buttonRemoveLocationUpdates.setEnabled(true);
    } else {
      buttonRequestLocationUpdates.setEnabled(true);
      buttonRemoveLocationUpdates.setEnabled(false);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_GPS) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mService.requestLocationUpdates();
      } else {
        Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
      setButtonsState(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES, false));
    }
  }
}
