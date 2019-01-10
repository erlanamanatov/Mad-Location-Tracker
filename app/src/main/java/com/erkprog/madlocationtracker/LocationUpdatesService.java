package com.erkprog.madlocationtracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.erkprog.madlocationtracker.data.db.FitActivity;
import com.erkprog.madlocationtracker.data.db.LocationItem;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

public class LocationUpdatesService extends Service {

  private static final String TAG = LocationUpdatesService.class.getSimpleName();

  private final IBinder mBinder = new LocalBinder();

  private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

  private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
      UPDATE_INTERVAL_IN_MILLISECONDS / 2;

  private static final String CHANNEL_ID = "channel 1";
  private static final int NOTIFICATION_ID = 123;

  HandlerThread handlerThread;
  private Handler mServiceHandler;

  private FitActivity mCurrentFitActivity;
  private long mFitActivityId = -1;
  private LocationRequest mLocationRequest;
  private NotificationManager mNotificationManager;
  private FusedLocationProviderClient mFusedLocationClient;
  private LocationCallback mLocationCallback;
  private Location mLocation;
  private LocalRepository mRepository;

  public LocationUpdatesService() {
  }

  @Override
  public void onCreate() {
    Utils.logd(TAG, "Service on create");
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    mLocationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        super.onLocationResult(locationResult);
        onNewLocation(locationResult.getLastLocation());
      }
    };

    createLocationRequest();

    handlerThread = new HandlerThread(TAG);
    handlerThread.start();
    mServiceHandler = new Handler(handlerThread.getLooper());
    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.app_name);
      NotificationChannel mChannel =
          new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
      mNotificationManager.createNotificationChannel(mChannel);
    }

    mRepository = AppApplication.getInstance().getRepository();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Utils.logd(TAG, "Service started");
    return START_NOT_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    Utils.logd(TAG, "in onBind()");
    stopForeground(true);
    return mBinder;
  }

  @Override
  public void onRebind(Intent intent) {
    Utils.logd(TAG, "in onRebind()");
    stopForeground(true);
    super.onRebind(intent);
  }

  @Override
  public boolean onUnbind(Intent intent) {
    Utils.logd(TAG, "Last client unbound from service");
    if (Utils.requestingLocationUpdates(this)) {
      Utils.logd(TAG, "Starting foreground service");
      startForeground(NOTIFICATION_ID, getNotification());
    }
    return true;
  }

  @Override
  public void onDestroy() {
    Utils.logd(TAG, "Service on destroy");
    handlerThread.quitSafely();
//    mServiceHandler.removeCallbacksAndMessages(null);
  }

  private Notification getNotification() {
    CharSequence text = "Getting location updates";
    PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, MainActivity.class), 0);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
        .setContentText(text)
        .setContentTitle("Mad Location Tracker")
        .setContentIntent(activityPendingIntent)
        .setOngoing(true)
        .setVibrate(null)
        .setPriority(Notification.PRIORITY_HIGH)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setTicker(text)
        .setWhen(System.currentTimeMillis());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      builder.setChannelId(CHANNEL_ID); // Channel ID
    }

    return builder.build();
  }

  private void onNewLocation(Location location) {
    if (mLocation != null) {
      mCurrentFitActivity.addDistance(location.distanceTo(mLocation));
    }
    mLocation = location;
    Utils.logd(TAG, "onNewLocation: lat " + mLocation.getLatitude() + ", long " + mLocation.getLongitude() + ", activity id = " + mFitActivityId);
    Utils.logd(TAG, "onNewLocation: total distance = " + mCurrentFitActivity.getDistance());
    if (mFitActivityId != -1) {
      mServiceHandler.post(() -> mRepository.getDatabase().locationDao()
          .addLocation(new LocationItem(location, mFitActivityId)));
    }
  }

  private void createLocationRequest() {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
    mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }


  public void requestLocationUpdates() {
    Utils.logd(TAG, "Requesting location updates");
    Utils.setRequestingLocationUpdates(this, true);
    mServiceHandler.post(() -> {
      mFitActivityId = mRepository.getDatabase()
          .acitivityDao().addActivity(new FitActivity());
      mCurrentFitActivity = new FitActivity(mFitActivityId, Calendar.getInstance().getTime());
      Utils.logd(TAG, "New FitActivity started, id = " + mFitActivityId);
    });
    startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
    try {
      mFusedLocationClient.requestLocationUpdates(mLocationRequest,
          mLocationCallback, Looper.myLooper());
    } catch (SecurityException unlikely) {
      Utils.setRequestingLocationUpdates(this, false);
      Utils.logd(TAG, "Lost location permission. Could not request updates. " + unlikely);
    }
  }

  public void removeLocationUpdates() {
    Utils.logd(TAG, "Removing location updates");
    try {
      Utils.setRequestingLocationUpdates(this, false);
      mFusedLocationClient.removeLocationUpdates(mLocationCallback);
      mLocation = null;
      mFitActivityId = -1;
      mCurrentFitActivity = null;
      stopSelf();
    } catch (SecurityException unlikely) {
      Utils.setRequestingLocationUpdates(this, true);
      Utils.logd(TAG, "Lost location permission. Could not remove updates. " + unlikely);
    }
  }

  public class LocalBinder extends Binder {
    LocationUpdatesService getService() {
      return LocationUpdatesService.this;
    }
  }
}
