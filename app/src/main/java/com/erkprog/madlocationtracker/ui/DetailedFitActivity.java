package com.erkprog.madlocationtracker.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.erkprog.madlocationtracker.AppApplication;
import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.Utils;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DetailedFitActivity extends FragmentActivity implements OnMapReadyCallback {
  private static final String TAG = "DetailedFitActivity";

  private static final String KEY_FIT_ACTIVITY = "detailedFitActivity.fitactivjity";
  private static final int ZOOM = 11;
  private static final float WIDTH_OF_ROUTE = 25;

  private GoogleMap mMap;
  private LocalRepository mRepository;
  private FitActivity mFitActivity;
  private List<LocationItem> mLocationItems;
  private TextView tvDistance;
  private TextView tvDuration;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mRepository = AppApplication.getInstance().getRepository();
    setContentView(R.layout.activity_detailed_fit);
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    mFitActivity = getIntent().getParcelableExtra(KEY_FIT_ACTIVITY);
    Log.d(TAG, "onCreate: " + mFitActivity.toString());

    tvDistance = findViewById(R.id.act_detail_distance);
    tvDuration = findViewById(R.id.act_detail_duration);

    tvDistance.setText(Utils.getFormattedDistance(mFitActivity.getDistance()));
    tvDuration.setText(Utils.getFormattedTime(mFitActivity.getEndTime().getTime() - mFitActivity.getStartTime().getTime()));
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.getUiSettings().setRotateGesturesEnabled(false);
    mMap.getUiSettings().setTiltGesturesEnabled(false);
    mMap.setOnMarkerClickListener(marker -> true);
    getLocations();
  }

  private void getLocations() {
    mRepository.getDatabase().locationDao()
        .getLocationsByActivity(mFitActivity.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<List<LocationItem>>() {
          @Override
          public void onSuccess(List<LocationItem> locationItems) {
            Log.d(TAG, "onSuccess: " + locationItems.size() + " locations in db");
            mLocationItems = locationItems;
            displayLocations();
          }

          @Override
          public void onError(Throwable e) {
            Log.d(TAG, "Error getting locations for this activity");
          }
        });
  }

  private void displayLocations() {
    List<LatLng> routePoints = new ArrayList<>();
    for (LocationItem item : mLocationItems) {
      routePoints.add(new LatLng(item.getLatitude(), item.getLongitude()));
    }
    Polyline route = mMap.addPolyline(new PolylineOptions()
        .width(WIDTH_OF_ROUTE)
        .color(Color.parseColor("#801B60FE"))
        .geodesic(true));
    route.setPoints(routePoints);

    displayFirstLocation(mLocationItems.get(0));
    displayLastLocation(mLocationItems.get(mLocationItems.size() - 1));
  }

  private void displayLastLocation(LocationItem locationItem) {
    LatLng lastPosition = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
    mMap.addMarker(new MarkerOptions().position(lastPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_icon)));
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, ZOOM));
  }

  private void displayFirstLocation(LocationItem locationItem) {
    LatLng firstLocation = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
    mMap.addMarker(new MarkerOptions().position(firstLocation)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_icon))
        .anchor(0.5f, 0.5f));
  }

  public static Intent getIntent(Context context, FitActivity fitActivity) {
    Intent intent = new Intent(context, DetailedFitActivity.class);
    intent.putExtra(KEY_FIT_ACTIVITY, fitActivity);
    return intent;
  }
}

