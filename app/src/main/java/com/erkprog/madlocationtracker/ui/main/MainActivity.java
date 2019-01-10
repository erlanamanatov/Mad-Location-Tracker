package com.erkprog.madlocationtracker.ui.main;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.ui.CreateFitActivity;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FloatingActionButton btnNewFitActivity = findViewById(R.id.floatingActionButton);
    btnNewFitActivity.setOnClickListener(v -> {
      Intent newFitActivity = new Intent(MainActivity.this, CreateFitActivity.class);
      startActivity(newFitActivity);
    });
  }
}
