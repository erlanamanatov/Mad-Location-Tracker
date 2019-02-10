package com.erkprog.madlocationtracker.utils;

import mad.location.manager.lib.Services.KalmanLocationService;

public class KalmanFilterSettings {
  private static final int FOREGROUND_GPS_MIN_DISTANCE = 0;
  private static final int FOREGROUND_GPS_MIN_TIME = 1800;
  private static final int BACKGROUND_GPS_MIN_DISTANCE = 0;
  private static final int BACKGROUND_GPS_MIN_TIME = 4000;


  public static KalmanLocationService.Settings getDefaultSettings() {
    return new KalmanLocationService.Settings(mad.location.manager.lib.Commons.Utils.ACCELEROMETER_DEFAULT_DEVIATION,
        mad.location.manager.lib.Commons.Utils.GPS_MIN_DISTANCE,
        mad.location.manager.lib.Commons.Utils.GPS_MIN_TIME,
        mad.location.manager.lib.Commons.Utils.GEOHASH_DEFAULT_PREC,
        mad.location.manager.lib.Commons.Utils.GEOHASH_DEFAULT_MIN_POINT_COUNT,
        mad.location.manager.lib.Commons.Utils.SENSOR_DEFAULT_FREQ_HZ,
        null, false, mad.location.manager.lib.Commons.Utils.DEFAULT_VEL_FACTOR, mad.location.manager.lib.Commons.Utils.DEFAULT_POS_FACTOR);
  }

  public static KalmanLocationService.Settings getForegroundSettings() {
    return new KalmanLocationService.Settings(mad.location.manager.lib.Commons.Utils.ACCELEROMETER_DEFAULT_DEVIATION,
        FOREGROUND_GPS_MIN_DISTANCE,
        FOREGROUND_GPS_MIN_TIME,
        0,
        0,
        mad.location.manager.lib.Commons.Utils.SENSOR_DEFAULT_FREQ_HZ,
        null, false, mad.location.manager.lib.Commons.Utils.DEFAULT_VEL_FACTOR, mad.location.manager.lib.Commons.Utils.DEFAULT_POS_FACTOR);
  }

  public static KalmanLocationService.Settings getBackgroundSettings() {
    return new KalmanLocationService.Settings(mad.location.manager.lib.Commons.Utils.ACCELEROMETER_DEFAULT_DEVIATION,
        BACKGROUND_GPS_MIN_DISTANCE,
        BACKGROUND_GPS_MIN_TIME,
        0,
        0,
        mad.location.manager.lib.Commons.Utils.SENSOR_DEFAULT_FREQ_HZ,
        null, false, mad.location.manager.lib.Commons.Utils.DEFAULT_VEL_FACTOR, mad.location.manager.lib.Commons.Utils.DEFAULT_POS_FACTOR);
  }
}
