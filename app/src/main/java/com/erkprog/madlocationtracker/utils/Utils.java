package com.erkprog.madlocationtracker.utils;

import android.content.Context;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
  private static final String TAG = "Utils";

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
    try {
      SimpleDateFormat dayMonth = new SimpleDateFormat("EEEE, MMM d", new Locale("en"));
      SimpleDateFormat hourMinute = new SimpleDateFormat("hh:mm a", new Locale("en"));
      String dm = dayMonth.format(resourceDate);
      String hm = hourMinute.format(resourceDate);
      return String.format("%s at %s", dm, hm);
    } catch (Exception e) {
      return "?";
    }
  }

  public static String getFormattedDuration(long diff) {
    try {
      long diffSeconds = diff / 1000 % 60;
      long diffMinutes = diff / (60 * 1000) % 60;
      long diffHours = diff / (60 * 60 * 1000);
      return String.format(Locale.UK, "%dh %dm %ds", diffHours, diffMinutes, diffSeconds);
    } catch (Exception e) {
      return "?";
    }
  }

  public static String getFormattedSpeed(float avgSpeed) {
    try {
      String unit = "km/h";
      String formattedSpeed = String.format(Locale.UK, "%.2f %s", avgSpeed, unit);
      return formattedSpeed;
    } catch (Exception e) {
      return "?";
    }

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

  public static String getFormattedDistance(float distance) {
    try {
      String unit;
      if (distance > 1000) {
        unit = "km";
        distance = distance / 1000;
      } else {
        unit = "m";
      }
      return String.format(Locale.UK, "%.2f %s", distance, unit);
    } catch (Exception e) {
      return "?";
    }
  }

}
