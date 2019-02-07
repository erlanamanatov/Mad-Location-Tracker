package com.erkprog.madlocationtracker.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

  public static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";

  public static boolean requestingLocationUpdates(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
  }

  public static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
        .apply();
  }

  public static String getFormattedDate(Date resourceDate) {
    SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM d", new Locale("en"));
    return formatter.format(resourceDate);
  }

  @SuppressLint("DefaultLocale")
  public static String getTotalDuration(long diff) {
    long diffSeconds = diff / 1000 % 60;
    long diffMinutes = diff / (60 * 1000) % 60;
    long diffHours = diff / (60 * 60 * 1000);
    return String.format("%dh %dm %ds", diffHours, diffMinutes, diffSeconds);
  }

  public static long getBaseForChronometer(Date startTimeOfActivity) {
    return SystemClock.elapsedRealtime() - (System.currentTimeMillis() - startTimeOfActivity.getTime());
  }

  public static void logd(String tag, String message) {
    Log.d(tag, String.format("%s [%s]", message, Thread.currentThread().getName()));
  }

  public static void loge(String tag, String message) {
    Log.e(tag, String.format("%s [%s]", message, Thread.currentThread().getName()));
  }

  @SuppressLint("DefaultLocale")
  public static String getFormattedDistance(float distance) {
    String unit = "";
    if (distance > 1000) {
      unit = "km";
      distance = distance / 1000;
    } else {
      unit = "m";
    }
    return String.format("%.2f %s", distance, unit);
  }
}
