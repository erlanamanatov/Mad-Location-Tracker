package com.erkprog.madlocationtracker.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.erkprog.madlocationtracker.AppApplication;
import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.Utils;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.ui.CreateFitActivity;
import com.erkprog.madlocationtracker.ui.detailedFitActivity.DetailedFitActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View, SharedPreferences.OnSharedPreferenceChangeListener {

  private static final String TAG = "MainActivity";

  private RecyclerView mRecyclerView;
  private FitActivityAdapter mAdapter;
  private MainContract.Presenter mPresenter;
  private FloatingActionButton trackFitActivity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    trackFitActivity = findViewById(R.id.floatingActionButton);
    trackFitActivity.setOnClickListener(v -> {
      Intent newFitActivity = new Intent(MainActivity.this, CreateFitActivity.class);
      startActivity(newFitActivity);
    });

    mPresenter = new MainPresenter(AppApplication.getInstance().getRepository());
    mPresenter.bind(this);
    mRecyclerView = findViewById(R.id.rcv_activities);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
  }

  @Override
  protected void onStart() {
    super.onStart();
    mPresenter.loadFitActivities();
    PreferenceManager.getDefaultSharedPreferences(this)
        .registerOnSharedPreferenceChangeListener(this);
    setFloatingButtonState(Utils.requestingLocationUpdates(this));
  }

  private void onFitActivityClicked(FitActivity fitActivity) {
    mPresenter.onFitActivityClicked(fitActivity);
  }

  @Override
  public void startDetailedFitActivity(FitActivity fitActivity) {
    startActivity(DetailedFitActivity.getIntent(this, fitActivity));
  }

  @Override
  public void displayActivities(List<FitActivity> fitActivities) {
    mAdapter = new FitActivityAdapter(fitActivities, MainActivity.this::onFitActivityClicked);
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
      setFloatingButtonState(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES, false));
    }
  }

  private void setFloatingButtonState(boolean requestingLocationUpdates) {
    if (requestingLocationUpdates) {
      trackFitActivity.setImageDrawable(getDrawable(R.drawable.ic_active_tracking));
    } else {
      trackFitActivity.setImageDrawable(getDrawable(R.drawable.ic_start_fit_activity));
    }
  }

  @Override
  protected void onStop() {
    PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    mPresenter.unBind();
    super.onDestroy();
  }

}