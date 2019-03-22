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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LocationServiceTest {


  @Rule
  public final ServiceTestRule mServiceRule = new ServiceTestRule();

  @Test
  public void testWithBoundService() throws TimeoutException, InterruptedException {
    // Create the service Intent.
    Intent serviceIntent =
        new Intent(getApplicationContext(), LocationUpdatesService.class);

//    // Data can be passed to the service via the Intent.
//    serviceIntent.putExtra(LocalService.SEED_KEY, 42L);

    // Bind the service and grab a reference to the binder.
    IBinder binder = mServiceRule.bindService(serviceIntent);

    // Get the reference to the service, or you can call public methods on the binder directly.
    LocationUpdatesService service = ((LocationUpdatesService.LocalBinder) binder).getService();

    assertTrue(service.isServiceStarted());
    service.startTracking();
    Thread.sleep(500);
    Location location = new Location("test Location");
    location.setLatitude(42.42);
    location.setLongitude(74.24);
    service.locationChanged(location);
    System.out.println("testingshit " + String.valueOf(FitActivity.STATUS_TRACKING));
    String s = service.getCurrentFitActivity() != null ? "not null" : "null";
    System.out.println("testingshit " + s);
    service.stopTracking(100L);
    service.onDestroy();
    assertFalse(service.isServiceStarted());
  }
}
