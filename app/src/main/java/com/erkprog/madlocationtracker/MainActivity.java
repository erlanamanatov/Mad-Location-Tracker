package com.erkprog.madlocationtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

  Button buttonRequestLocationUpdates, buttonRemoveLocationUpdates;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init();
  }

  private void init() {
    buttonRequestLocationUpdates= findViewById(R.id.button_request_location);
    buttonRequestLocationUpdates.setOnClickListener(this);
    buttonRemoveLocationUpdates = findViewById(R.id.button_remove_locations);
    buttonRemoveLocationUpdates.setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_request_location:
        //TODO: on request location updates
        break;

      case R.id.button_remove_locations:
        //TODO: on remove location updates
        break;
    }
  }
}
