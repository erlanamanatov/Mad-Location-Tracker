package com.erkprog.madlocationtracker.ui.trackFitActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.erkprog.madlocationtracker.LocationUpdatesService;
import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.utils.Utils;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackFitActivity extends AppCompatActivity implements View.OnClickListener,
    SharedPreferences.OnSharedPreferenceChangeListener,
    OnMapReadyCallback,
    TrackActivityContract.View {
  private static final String TAG = "TrackFitActivity";

  Button buttonRequestLocationUpdates, buttonRemoveLocationUpdates;
  TextView tvDistance;
  Chronometer chronometer;

  private TrackActivityContract.Presenter mPresenter;

  private LocationUpdatesService mService = null;
  private static final int REQUEST_GPS = 1;
  private FitActivityReceiver mFitActivityReceiver;
  private GoogleMap mMap;

  private Polyline runningPathPolyline;
  private Circle locationAccuracy;
  private Marker userPositionMarker;
  private BitmapDescriptor userPositionIcon;
  private static final float ROUTE_WIDTH = 25;
  private static final String ROUTE_COLOR = "#801B60FE";

  private boolean mBound = false;

  private final ServiceConnection mServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      Utils.logd(TAG, "Service connected");
      LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
      mService = binder.getService();
      mBound = true;
      mPresenter.onServiceConnected(Utils.requestingLocationUpdates(TrackFitActivity.this));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mService = null;
      mBound = false;
    }
  };

  @Override
  public boolean isMapReady() {
    return mMap != null;
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.getUiSettings().setRotateGesturesEnabled(false);
    mMap.getUiSettings().setTiltGesturesEnabled(false);
    mMap.setOnMarkerClickListener(marker -> true);
    LatLng bishkek = new LatLng(42.88, 74.58);
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bishkek, 10));
  }

  @Override
  public void showCurrentPosition(Location location) {
    zoomMapTo(location);
    drawPositionMarker(location);
    drawLocationAccuracyCircle(location);
  }

  @Override
  public Location getCurrentLocation() {
    return mService != null ? mService.getCurrentLocation() : null;
  }

  @Override
  public FitActivity getCurrentFitActivity() {
    return mService != null ? mService.getCurrentFitActivity() : null;
  }

  @Override
  public List<Location> getLocationsList() {
    return mService != null ? mService.getLocationsList() : null;
  }

  @Override
  public void showDistance(String distance) {
    tvDistance.setText(distance);
  }

  @Override
  public void showDuration(Date startDate) {
    chronometer.setBase(Utils.getBaseForChronometer(startDate));
    chronometer.start();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_track_fit_activity);
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.active_map);
    mapFragment.getMapAsync(this);
    initViews();
    mFitActivityReceiver = new FitActivityReceiver();
    mPresenter = new TrackActivityPresenter();
    mPresenter.bind(this);
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
  protected void onResume() {
    super.onResume();
    LocalBroadcastManager.getInstance(this)
        .registerReceiver(mFitActivityReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
  }

  @Override
  protected void onPause() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mFitActivityReceiver);
    super.onPause();
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
  public void startTracking() {
    mService.startTracking();
    tvDistance.setText(getString(R.string.zero_meters));
    chronometer.setBase(SystemClock.elapsedRealtime());
    chronometer.start();
  }

  @Override
  public void stopTracking() {
    mService.stopTracking();
    chronometer.stop();
  }

  @Override
  public void setButtonsState(boolean requestingLocationUpdates) {
    if (requestingLocationUpdates) {
      buttonRequestLocationUpdates.setEnabled(false);
      buttonRemoveLocationUpdates.setEnabled(true);
    } else {
      buttonRequestLocationUpdates.setEnabled(true);
      buttonRemoveLocationUpdates.setEnabled(false);
    }
  }

  private void drawPositionMarker(Location location) {
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    if (userPositionMarker == null) {
      userPositionMarker = mMap.addMarker(new MarkerOptions()
          .position(latLng)
          .flat(true)
          .anchor(0.5f, 0.5f)
          .icon(userPositionIcon));
    } else {
      userPositionMarker.setPosition(latLng);
    }
  }

  private void drawLocationAccuracyCircle(Location location) {
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    if (locationAccuracy == null) {
      locationAccuracy = mMap.addCircle(new CircleOptions()
          .center(latLng)
          .fillColor(Color.argb(64, 0, 100, 100))
          .strokeColor(Color.argb(64, 0, 0, 0))
          .strokeWidth(0.0f)
          .radius(location.getAccuracy()));
    } else {
      locationAccuracy.setCenter(latLng);
      locationAccuracy.setRadius(location.getAccuracy());
    }
  }

  private void zoomMapTo(Location location) {
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    try {
      mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void showRoute(List<Location> locationList) {
    if (runningPathPolyline == null) {
      if (locationList.size() > 1) {
        PolylineOptions options = new PolylineOptions()
            .width(ROUTE_WIDTH).color(Color.parseColor(ROUTE_COLOR)).geodesic(true);
        options.addAll(getLatLngPoints(locationList));
        runningPathPolyline = mMap.addPolyline(options);
      }
    } else {
      Location toLocation = locationList.get(locationList.size() - 1);
      LatLng to = new LatLng(((toLocation.getLatitude())),
          ((toLocation.getLongitude())));
      List<LatLng> points = runningPathPolyline.getPoints();
      points.add(to);
      runningPathPolyline.setPoints(points);
    }
  }

  private List<LatLng> getLatLngPoints(List<Location> locationList) {
    List<LatLng> points = new ArrayList<>();
    for (Location location : locationList) {
      points.add(new LatLng(location.getLatitude(), location.getLongitude()));
    }
    return points;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
      mPresenter.onLocationUpdatesStatusChanged(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES, false));
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_start_tracking:
        if (isGpsPersmissionGranted()) {
          if (isGpsEnabled()) {
            mPresenter.onStartTrackingClicked();
          } else {
            showTurnGpsOnDialog();
          }
        } else {
          requestGpsPermission();
        }
        break;

      case R.id.button_stop_tracking:
        mPresenter.onStopTrackingClicked();
        break;
    }
  }

  private boolean isGpsEnabled() {
    LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  private boolean isGpsPersmissionGranted() {
    return ActivityCompat.checkSelfPermission(TrackFitActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
  }

  private void requestGpsPermission() {
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_GPS);
  }

  private void showTurnGpsOnDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setMessage(R.string.turn_on_gps)
        .setTitle(R.string.gps_disabled)
        .setPositiveButton(R.string.to_settings, (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
        .setNegativeButton(R.string.cancel, (dialog, which) ->
            Toast.makeText(TrackFitActivity.this, getString(R.string.turn_on_gps_to_get_updates), Toast.LENGTH_SHORT).show());
    builder.show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_GPS) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mPresenter.onStartTrackingClicked();
      } else {
        Toast.makeText(this, "Access to device's location is required", Toast.LENGTH_LONG).show();
      }
    }
  }

  private class FitActivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      FitActivity usersActivity = intent.getParcelableExtra(LocationUpdatesService.EXTRA_FIT_ACTIVITY);
      Location newLocation = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
      mPresenter.onBroadcastReceived(usersActivity, newLocation);
    }
  }

  private void initViews() {
    buttonRequestLocationUpdates = findViewById(R.id.button_start_tracking);
    buttonRequestLocationUpdates.setOnClickListener(this);
    buttonRemoveLocationUpdates = findViewById(R.id.button_stop_tracking);
    buttonRemoveLocationUpdates.setOnClickListener(this);
    userPositionIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_user_position);
    tvDistance = findViewById(R.id.cr_act_distance);
    chronometer = findViewById(R.id.cr_act_time);
  }

  @Override
  protected void onDestroy() {
    mPresenter.unBind();
    super.onDestroy();
  }
}