package com.erkprog.madlocationtracker.ui.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.erkprog.madlocationtracker.AppApplication;
import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.ui.CreateFitActivity;
import com.erkprog.madlocationtracker.ui.detailedFitActivity.DetailedFitActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View {

  private static final String TAG = "MainActivity";

  private RecyclerView mRecyclerView;
  private FitActivityAdapter mAdapter;
  private MainContract.Presenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FloatingActionButton btnNewFitActivity = findViewById(R.id.floatingActionButton);
    btnNewFitActivity.setOnClickListener(v -> {
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
  protected void onDestroy() {
    mPresenter.unBind();
    super.onDestroy();
  }
}