package com.erkprog.madlocationtracker.ui.btScanActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.erkprog.madlocationtracker.R;

public class BtScanActivity extends AppCompatActivity implements BtDevicesAdapter.OnDeviceClickListener {
  private static final String TAG = "BtScanActivity";

  private static final int REQUEST_ENABLE_BT = 1;

  private BluetoothAdapter mBluetoothAdapter;
  private boolean mScanning;
  private Handler mHandler;

  private RecyclerView mRecyclerView;
  private BtDevicesAdapter mBtDevicesAdapter;

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
      }, SCAN_PERIOD);

      mScanning = true;
      mBluetoothAdapter.startLeScan(mLeScanCallback);
    } else {
      mScanning = false;
      mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
  }

  private BluetoothAdapter.LeScanCallback mLeScanCallback =
      (device, rssi, scanRecord) -> runOnUiThread(() -> {
        if (device.getName() != null && device.getName().toLowerCase().contains("mi band 2")) {
          mBtDevicesAdapter.addDevice(device);
          mBtDevicesAdapter.notifyDataSetChanged();
        }
      });

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
      scanLeDevice(true);
    }
  }

  @Override
  public void onDeviceClicked(BluetoothDevice device) {
    Intent intent = new Intent();
    intent.putExtra("device", device);
    setResult(RESULT_OK, intent);
    finish();
  }
}
