package com.erkprog.madlocationtracker.ui;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.erkprog.madlocationtracker.AppApplication;
import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

  private GoogleMap mMap;
  private LocalRepository mRepository;
  private FitActivity mFitActivity;
  private List<LocationItem> mLocationItems;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mRepository = AppApplication.getInstance().getRepository();
    setContentView(R.layout.activity_detailed_fit);
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    mFitActivity = getIntent().getParcelableExtra("fact");
    Log.d(TAG, "onCreate: " + mFitActivity.toString());
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;


    mRepository.getDatabase().locationDao()
        .getLocationsByActivity(mFitActivity.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<List<LocationItem>>() {
          @Override
          public void onSuccess(List<LocationItem> locationItems) {
            Toast.makeText(DetailedFitActivity.this, "success " + locationItems.size(), Toast.LENGTH_SHORT).show();
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
        .width(24)
        .color(Color.parseColor("#801B60FE"))
        .geodesic(true));
    route.setPoints(routePoints);

    LocationItem last = mLocationItems.get(mLocationItems.size() - 1);
    LatLng lastPosition = new LatLng(last.getLatitude(), last.getLongitude());
    mMap.addMarker(new MarkerOptions().position(lastPosition).title("Last location"));
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 11));
  }
}

