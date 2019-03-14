package com.erkprog.madlocationtracker.ui.detailedFitActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.erkprog.madlocationtracker.AppApplication;
import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.utils.HourAxisValueFormatter;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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

import java.util.List;

public class DetailedFitActivity extends FragmentActivity implements OnMapReadyCallback, DetailedFitActivityContract.View {
  private static final String TAG = "DetailedFitActivity";

  private static final String KEY_FIT_ACTIVITY = "detailedFitActivity.fitactivjity";
  private static final float WIDTH_OF_ROUTE = 25;

  private TextView tvDistance;
  private TextView tvDuration;
  private TextView tvAvgSpeed;
  private TextView tvTrackingTime;
  private LineChart mLineChart;

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

    tvDistance = findViewById(R.id.act_detail_distance);
    tvDuration = findViewById(R.id.act_detail_duration);
    tvAvgSpeed = findViewById(R.id.act_detail_speed);
    tvTrackingTime = findViewById(R.id.act_detail_tracking_time);

    FitActivity fitActivity = getIntent().getParcelableExtra(KEY_FIT_ACTIVITY);

    mPresenter = new DetailedActivityPresenter(AppApplication.getInstance().getRepository(), fitActivity.getId());
    mPresenter.bind(this);
    mPresenter.processFitActivity(fitActivity);
    mPresenter.getHeartRate();
    mLineChart = findViewById(R.id.chart);
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
  public void hideGraph() {
    mLineChart.setVisibility(View.GONE);
  }

  @Override
  public void plotGraph(List<Entry> entries, long referenceTimestamp) {
    mLineChart.setVisibility(View.VISIBLE);
    IAxisValueFormatter xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
    XAxis xAxis = mLineChart.getXAxis();
    xAxis.setValueFormatter(xAxisFormatter);
    LineDataSet dataSet = new LineDataSet(entries, "Heart rate");
    dataSet.setColor(R.color.colorAccent);
    LineData lineData = new LineData(dataSet);
    mLineChart.setData(lineData);
    mLineChart.invalidate();
  }

  @Override
  public void showStartOfRoute(LocationItem locationItem) {
    LatLng firstLocation = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
    mMap.addMarker(new MarkerOptions().position(firstLocation)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_icon))
        .anchor(0.5f, 0.5f));
  }

  @Override
  public void showEndOfRoute(LocationItem locationItem) {
    LatLng lastPosition = new LatLng(locationItem.getLatitude(), locationItem.getLongitude());
    mMap.addMarker(new MarkerOptions()
        .position(lastPosition)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_icon)));
  }

  @Override
  public void showRoute(List<LatLng> routePoints, LatLngBounds bounds) {
    Polyline route = mMap.addPolyline(new PolylineOptions()
        .width(WIDTH_OF_ROUTE)
        .color(Color.parseColor("#801B60FE"))
        .geodesic(true));
    route.setPoints(routePoints);
    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING);
    mMap.moveCamera(cu);
  }

  @Override
  public void showDuration(String totalDuration) {
    tvDuration.setText(totalDuration);
  }

  @Override
  public void showDistance(String distance) {
    tvDistance.setText(distance);
  }

  @Override
  public void showAvgSpeed(String formattedSpeed) {
    tvAvgSpeed.setText(formattedSpeed);
  }

  @Override
  public void showTrackingTime(String trackingTime) {
    tvTrackingTime.setText(trackingTime);
  }

  @Override
  public void showMessage(int resId) {
    Snackbar.make(findViewById(R.id.act_detail_duration), getText(resId), Snackbar.LENGTH_LONG).show();
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