package com.erkprog.madlocationtracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;
import com.erkprog.madlocationtracker.ui.trackFitActivity.TrackFitActivity;
import com.erkprog.madlocationtracker.utils.KalmanFilterSettings;
import com.erkprog.madlocationtracker.utils.Utils;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mad.location.manager.lib.Interfaces.LocationServiceInterface;
import mad.location.manager.lib.Loggers.GeohashRTFilter;
import mad.location.manager.lib.Services.KalmanLocationService;
import mad.location.manager.lib.Services.ServicesHelper;


public class LocationUpdatesService extends Service implements LocationServiceInterface {

  private static final String TAG = LocationUpdatesService.class.getSimpleName();

  private static final String PACKAGE_NAME = "com.erkprog.madlocationtracker.locationupdatesservice";
  public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
  public static final String EXTRA_FIT_ACTIVITY = PACKAGE_NAME + ".fitactivity";
  public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
  private static final int GEOHASH_MIN_POINT_COUNT = 1;
  private static final int GEOHASH_HASH_LENGTH = 7;
  private static final String CHANNEL_ID = "channel 1";
  private static final int NOTIFICATION_ID = 123;

  private final IBinder mBinder = new LocalBinder();

  private boolean mChangingConfiguration = false;

  private HandlerThread handlerThread;
  private Handler mServiceHandler;
  private GeohashRTFilter mGeohashRTFilter;
  private FitActivity mCurrentFitActivity;
  private long mFitActivityId = -1;
  private NotificationManager mNotificationManager;
  private Location mLocation;
  private LocalRepository mRepository;
  private ArrayList<Location> listLocations;

  public LocationUpdatesService() {
  }

  @Override
  public void onCreate() {
    Utils.logd(TAG, "Service on create");

    handlerThread = new HandlerThread(TAG);
    handlerThread.start();
    mServiceHandler = new Handler(handlerThread.getLooper());
    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    mGeohashRTFilter = new GeohashRTFilter(GEOHASH_HASH_LENGTH, GEOHASH_MIN_POINT_COUNT);
    listLocations = new ArrayList<>();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.app_name);
      NotificationChannel mChannel =
          new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
      mNotificationManager.createNotificationChannel(mChannel);
    }

    mRepository = AppApplication.getInstance().getRepository();
    ServicesHelper.addLocationServiceInterface(this);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Utils.logd(TAG, "Service started");
    return START_NOT_STICKY;
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mChangingConfiguration = true;
  }

  @Override
  public IBinder onBind(Intent intent) {
    Utils.logd(TAG, "in onBind()");
    stopForeground(true);
    mChangingConfiguration = false;
    Utils.logd(TAG, " changing KalmanFilter parameters to ForegroundSettings");
    requestLocationUpdates(KalmanFilterSettings.getForegroundSettings());
    return mBinder;
  }

  @Override
  public void onRebind(Intent intent) {
    Utils.logd(TAG, "in onRebind()");
    stopForeground(true);
    if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
      Utils.logd(TAG, " changing KalmanFilter parameters to ForegroundSettings");
      requestLocationUpdates(KalmanFilterSettings.getForegroundSettings());
    }
    mChangingConfiguration = false;
    super.onRebind(intent);
  }

  @Override
  public boolean onUnbind(Intent intent) {
    Utils.logd(TAG, "Last client unbound from service");
    if (!Utils.requestingLocationUpdates(this)) {
      ServicesHelper.getLocationService(this, KalmanLocationService::stop);
    } else if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
      // App is getting location updates, need to change settings
      Utils.logd(TAG, " changing KalmanFilter parameters to BackgroundSettings");
      requestLocationUpdates(KalmanFilterSettings.getBackgroundSettings());
      Utils.logd(TAG, "Starting foreground service");
      startForeground(NOTIFICATION_ID, getNotification());
    }
    return true;
  }

  private void onNewLocation(Location location) {
    if (mCurrentFitActivity.getStatus() == FitActivity.STATUS_TRACKING) {
      mGeohashRTFilter.filter(location);
//      mCurrentFitActivity.setDistance((float) mGeohashRTFilter.getDistanceGeoFilteredHP());
      listLocations.add(location);
      if (mLocation != null) {
        mCurrentFitActivity.addDistance(location.distanceTo(mLocation));
      }
    }
    mLocation = location;
    Utils.logd(TAG, "onNewLocation: lat " + mLocation.getLatitude() + ", long " + mLocation.getLongitude() + ", activity id = " + mFitActivityId);
    Utils.logd(TAG, "onNewLocation: total distance = " + mCurrentFitActivity.getDistance());
    sendBroadcast(mCurrentFitActivity, location);
  }

  private void sendBroadcast(FitActivity fitActivity, Location location) {
    // send null fitActivity if user has not started tracking new activity
    Intent intent = new Intent(ACTION_BROADCAST);
    intent.putExtra(EXTRA_FIT_ACTIVITY, fitActivity);
    intent.putExtra(EXTRA_LOCATION, location);
    LocalBroadcastManager.getInstance(AppApplication.getInstance()).sendBroadcast(intent);
  }

  public void startTracking() {
    Utils.logd(TAG, "Requesting location updates");
    startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
    resetGeohashFilter();
    Utils.setRequestingLocationUpdates(this, true);
    mServiceHandler.post(() -> {
      mFitActivityId = mRepository.addActivity(new FitActivity());
      mCurrentFitActivity = new FitActivity(mFitActivityId);
      Utils.logd(TAG, "New FitActivity started, id = " + mFitActivityId);
    });
    requestLocationUpdates(KalmanFilterSettings.getForegroundSettings());
  }

  private void requestLocationUpdates(KalmanLocationService.Settings settings) {
    ServicesHelper.getLocationService(this, value -> {
      if (value.IsRunning()) {
        Utils.logd(TAG, "Value is running");
        return;
      }
      Utils.logd(TAG, "Value is not running");
      value.stop();
      value.reset(settings);
      value.start();
    });
  }

  public void stopTracking() {
    Utils.logd(TAG, "Removing location updates");
    try {
      Utils.setRequestingLocationUpdates(this, false);
      ServicesHelper.getLocationService(this, KalmanLocationService::stop);
      Utils.logd(TAG, " Remove location updates, distanceAsIs " + mGeohashRTFilter.getDistanceAsIs());
      Utils.logd(TAG, " Remove location updates, distanceAsIsHp " + mGeohashRTFilter.getDistanceAsIsHP());
      Utils.logd(TAG, " Remove location updates, distanceGeoFiltered " + mGeohashRTFilter.getDistanceGeoFiltered());
      Utils.logd(TAG, " Remove location updates, distanceGeoFilteredHp " + mGeohashRTFilter.getDistanceGeoFilteredHP());
      Utils.logd(TAG, " Remove location upgates, size of filtered locations list: " + Integer.toString(mGeohashRTFilter.getGeoFilteredTrack().size()));
      mServiceHandler.post(() -> {
        mGeohashRTFilter.stop();
//        mCurrentFitActivity.setDistance((float) mGeohashRTFilter.getDistanceGeoFilteredHP());
        mRepository.saveGeoFilteredTrack(mFitActivityId, mGeohashRTFilter.getGeoFilteredTrack());
        Utils.logd(TAG, " geofiltered locations saved to DB");
        saveFitActivityToDB();
        reset();
        mGeohashRTFilter.reset(null);
        stopSelf();
      });
    } catch (SecurityException unlikely) {
      Utils.setRequestingLocationUpdates(this, true);
      Utils.logd(TAG, "Lost location permission. Could not remove updates. " + unlikely);
    } catch (Exception exception) {
      Utils.loge(TAG, "Removing location updates: " + exception.getMessage());
    }
  }

  private void reset() {
    mLocation = null;
    mFitActivityId = -1;
    mCurrentFitActivity = null;
    listLocations.clear();
  }

  private void resetGeohashFilter() {
    mGeohashRTFilter.stop();
    mGeohashRTFilter.reset(null);
  }

  public Location getCurrentLocation() {
    return mLocation;
  }

  public FitActivity getCurrentFitActivity() {
    return mCurrentFitActivity;
  }

  public List<Location> getLocationsList() {
    return listLocations;
  }

  private void saveFitActivityToDB() {
    mCurrentFitActivity.setEndTime(Calendar.getInstance().getTime());
    mRepository.updateActivity(mCurrentFitActivity);
    Utils.logd(TAG, "User's activity saved to DB: " + mCurrentFitActivity.toString());
  }

  private Notification getNotification() {
    CharSequence text = "Getting location updates";
    PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, TrackFitActivity.class), 0);

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
      builder.setChannelId(CHANNEL_ID);
    }
    return builder.build();
  }

  @Override
  public void locationChanged(Location location) {
    if (Utils.requestingLocationUpdates(this)) {
      // tracking user's activity
      onNewLocation(location);
    } else {
      // display current position, the user has not started tracking activity yet
      // TODO: create new broadcast to avoid null
      sendBroadcast(null, location);
    }
  }

  @Override
  public void onDestroy() {
    Utils.logd(TAG, "Service on destroy");
    handlerThread.quitSafely();
    ServicesHelper.removeLocationServiceInterface(this);
  }

  public void continueTracking() {
    if (mCurrentFitActivity != null) {
      mCurrentFitActivity.setStatus(FitActivity.STATUS_TRACKING);
      if (mLocation != null) {
        mGeohashRTFilter.filter(mLocation);
      }
    }
  }

  public void pauseTracking() {
    if (mCurrentFitActivity != null) {
      mCurrentFitActivity.setStatus(FitActivity.STATUS_PAUSED);
    }
  }

  public class LocalBinder extends Binder {
    public LocationUpdatesService getService() {
      return LocationUpdatesService.this;
    }
  }
}
