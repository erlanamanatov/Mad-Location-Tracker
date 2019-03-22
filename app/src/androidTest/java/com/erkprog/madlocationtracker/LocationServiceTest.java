package com.erkprog.madlocationtracker;

import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.erkprog.madlocationtracker.data.entity.FitActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ServiceTestRule;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LocationServiceTest {

  @Rule
  public final ServiceTestRule mServiceRule = new ServiceTestRule();

  @Test
  public void testLocationService() throws TimeoutException, InterruptedException {
    Intent serviceIntent =
        new Intent(getApplicationContext(), LocationUpdatesService.class);

    IBinder binder = mServiceRule.bindService(serviceIntent);

    LocationUpdatesService service = ((LocationUpdatesService.LocalBinder) binder).getService();

    assertTrue(service.isServiceStarted());

    // start tracking, new FitActivity created here
    service.startTracking();

    Thread.sleep(500);
    Location location = getTestLocation();
    // test with fake location
    service.locationChanged(location);

    // check locationList, size of list should be greater than 0
    assertThat(service.getLocationsList().size(), greaterThan(0));
    // check id of fit activity, if id > 0 activity has been created in DB
    assertThat((int) service.getCurrentFitActivity().getId(), greaterThan(0));
    service.stopTracking(5000L);

    Thread.sleep(500);
    service.onDestroy();
    assertFalse(service.isServiceStarted());
  }

  private Location getTestLocation() {
    Location location = new Location("test Location");
    location.setLatitude(42.42);
    location.setLongitude(74.24);
    return location;
  }
}
