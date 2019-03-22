package com.erkprog.madlocationtracker.ui.btScanActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.erkprog.madlocationtracker.R;

import static android.view.View.GONE;

public class BtScanActivity extends AppCompatActivity implements BtDevicesAdapter.OnDeviceClickListener {
  private static final String TAG = "BtScanActivity";

  public static String EXTRA_DEVICE_ADDRESS = "device_address";

  private static final int REQUEST_ENABLE_BT = 1;

  private BluetoothAdapter mBluetoothAdapter;
  private boolean mScanning;
  private Handler mHandler;

  private RecyclerView mRecyclerView;
  private BtDevicesAdapter mBtDevicesAdapter;
  private ProgressBar mProgressBar;
  private Button btRefresh;

  // Stops scanning after 10 seconds.
  private static final long SCAN_PERIOD = 10000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bt_scan);

    mRecyclerView = findViewById(R.id.bt_scan_rcv);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    mBtDevicesAdapter = new BtDevicesAdapter(this);
    mRecyclerView.setAdapter(mBtDevicesAdapter);

    mProgressBar = findViewById(R.id.bt_scan_prb);
    mProgressBar.setVisibility(GONE);
    btRefresh = findViewById(R.id.bt_scan_refresh);
    btRefresh.setOnClickListener(v ->
        {
          mBtDevicesAdapter.clearData();
          scanLeDevice(true);
        }
    );

    final BluetoothManager bluetoothManager =
        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    mBluetoothAdapter = bluetoothManager.getAdapter();
    mHandler = new Handler();

    if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    } else {
      scanLeDevice(true);
    }

  }

  private void scanLeDevice(final boolean enable) {
    if (enable) {
      // Stops scanning after a pre-defined scan period.
      mHandler.postDelayed(() -> {
        mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        setViewState(mScanning);
      }, SCAN_PERIOD);

      mScanning = true;
      mBluetoothAdapter.startLeScan(mLeScanCallback);
    } else {
      mScanning = false;
      mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
    setViewState(mScanning);
  }

  private BluetoothAdapter.LeScanCallback mLeScanCallback =
      (device, rssi, scanRecord) -> runOnUiThread(() -> {
        if (device.getName() != null && device.getName().toLowerCase().contains("mi band 2")) {
          mBtDevicesAdapter.addDevice(device);
          mBtDevicesAdapter.notifyDataSetChanged();
        }
      });

  private void setViewState(boolean isScanning) {
    if (isScanning) {
      mProgressBar.setVisibility(View.VISIBLE);
      btRefresh.setVisibility(GONE);
    } else {
      mProgressBar.setVisibility(View.INVISIBLE);
      btRefresh.setVisibility(View.VISIBLE);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == REQUEST_ENABLE_BT) {
      if (resultCode == RESULT_OK) {
        scanLeDevice(true);
      } else {
        finish();
      }
    }
  }

  @Override
  public void onDeviceClicked(String deviceAddress) {
    Intent intent = new Intent();
    intent.putExtra(EXTRA_DEVICE_ADDRESS, deviceAddress);
    setResult(RESULT_OK, intent);
    finish();
  }

  @Override
  protected void onStop() {
    if (mScanning) {
      mBluetoothAdapter.stopLeScan(mLeScanCallback);
      mHandler.removeCallbacksAndMessages(null);
    }
    super.onStop();
  }
}
