package com.erkprog.madlocationtracker;

import android.content.Context;
import android.preference.PreferenceManager;

public class Utils {

  static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";

  static boolean requestingLocationUpdates(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
  }

  static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
    PreferenceManager.getDefaultSharedPreferences(context)
        .edit()
        .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
        .apply();
  }
}
