package com.erkprog.madlocationtracker;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

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

  public static void logd(String tag, String message) {
    Log.d(tag, String.format("%s [%s]", message, Thread.currentThread().getName()));
  }
}
