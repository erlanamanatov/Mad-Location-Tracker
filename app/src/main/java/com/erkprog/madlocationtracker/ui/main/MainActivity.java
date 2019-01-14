package com.erkprog.madlocationtracker.ui.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.erkprog.madlocationtracker.AppApplication;
import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;
import com.erkprog.madlocationtracker.ui.CreateFitActivity;
import com.erkprog.madlocationtracker.ui.DetailedFitActivity;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  private RecyclerView mRecyclerView;
  private FitActivityAdapter mAdapter;

  private LocalRepository mRepository;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FloatingActionButton btnNewFitActivity = findViewById(R.id.floatingActionButton);
    btnNewFitActivity.setOnClickListener(v -> {
      Intent newFitActivity = new Intent(MainActivity.this, CreateFitActivity.class);
      startActivity(newFitActivity);
    });

    mRecyclerView = findViewById(R.id.rcv_activities);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    mRepository = AppApplication.getInstance().getRepository();

    loadData();

  }

  private void loadData() {
    mRepository.getDatabase().acitivityDao()
        .getAllActivities()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableMaybeObserver<List<FitActivity>>() {
          @Override
          public void onSuccess(List<FitActivity> fitActivities) {
            mAdapter = new FitActivityAdapter(fitActivities, MainActivity.this::onFitActivityClicked);
            mRecyclerView.setAdapter(mAdapter);
          }

          @Override
          public void onError(Throwable e) {
            Toast.makeText(MainActivity.this, "Error loading activities", Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onComplete() {
            Toast.makeText(MainActivity.this, "No activities in db", Toast.LENGTH_SHORT).show();
          }
        });
  }

  private void onFitActivityClicked(FitActivity fitActivity) {
    Intent intent = new Intent(this, DetailedFitActivity.class);
    intent.putExtra("fact", fitActivity);
    startActivity(intent);
  }
}
