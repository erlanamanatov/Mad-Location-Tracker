package com.erkprog.madlocationtracker.ui.detailedFitActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.erkprog.madlocationtracker.AppApplication;
import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.utils.Utils;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class DetailedFitActivity extends FragmentActivity implements OnMapReadyCallback, DetailedFitActivityContract.View {
  private static final String TAG = "DetailedFitActivity";

  private static final String KEY_FIT_ACTIVITY = "detailedFitActivity.fitactivjity";
  private static final float WIDTH_OF_ROUTE = 25;

  private GoogleMap mMap;
  private static final int MAP_PADDING = 80;

  private DetailedFitActivityContract.Presenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detailed_fit);
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    FitActivity fitActivity = getIntent().getParcelableExtra(KEY_FIT_ACTIVITY);
    ((TextView) findViewById(R.id.act_detail_distance))
        .setText(Utils.getFormattedDistance(fitActivity.getDistance()));
    ((TextView) findViewById(R.id.act_detail_duration))
        .setText(Utils.getTotalDuration(fitActivity.getEndTime().getTime() - fitActivity.getStartTime().getTime()));

    mPresenter = new DetailedActivityPresenter(AppApplication.getInstance().getRepository(), fitActivity);
    mPresenter.bind(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.getUiSettings().setRotateGesturesEnabled(false);
    mMap.getUiSettings().setTiltGesturesEnabled(false);
    mMap.setOnMarkerClickListener(marker -> true);
    mPresenter.getLocations();
  }

  @Override
  public void showMessage(int resId) {
    Snackbar.make(findViewById(R.id.act_detail_duration), getText(resId), Snackbar.LENGTH_LONG).show();
  }

  @Override
  public void showTrack(List<LocationItem> locationItems) {
    List<LatLng> routePoints = new ArrayList<>();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    for (LocationItem item : locationItems) {
      routePoints.add(item.getLatLng());
      builder.include(item.getLatLng());
    }
    Polyline route = mMap.addPolyline(new PolylineOptions()
        .width(WIDTH_OF_ROUTE)
        .color(Color.parseColor("#801B60FE"))
        .geodesic(true));
    route.setPoints(routePoints);

    displayFirstLocation(locationItems.get(0));
    displayLastLocation(locationItems.get(locationItems.size() - 1));

    LatLngBounds bounds = builder.build();
    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING);
    mMap.moveCamera(cu);
  }

  private void displayLastLocation(LocationItem locationItem) {
    LatLng lastPosition = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
    mMap.addMarker(new MarkerOptions().position(lastPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_icon)));
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mPresenter.unBind();
  }
}

