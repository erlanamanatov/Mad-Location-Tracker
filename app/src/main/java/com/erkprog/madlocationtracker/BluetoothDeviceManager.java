package com.erkprog.madlocationtracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.erkprog.madlocationtracker.data.entity.MiBandServiceConst;
import com.erkprog.madlocationtracker.utils.Utils;

class BluetoothDeviceManager {
  private static final String TAG = "BluetoothDeviceManager";

  private Context mContext;
  //  private String mDeviceAddress;
  private BluetoothDevice mBluetoothDevice;
  private BluetoothGatt mBluetoothGatt;

  BluetoothDeviceManager(Context context, String deviceAddress) {
    Utils.logd(TAG, "constructor");
    mContext = context;
    BluetoothManager bluetoothManager =
        (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
    mBluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
  }

  void start() {
    Utils.logd(TAG, "start");
    mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, mBluetoothGattCallback);
  }

  private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
      super.onConnectionStateChange(gatt, status, newState);
      if (newState == BluetoothProfile.STATE_CONNECTED) {
        Utils.logd(TAG, "onConnectionStateChanged: connected");
        stateConnected();
      } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
        Utils.logd(TAG, "onConnectionStateChanged: disconnected");
        stateDisconnected();
      }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      super.onServicesDiscovered(gatt, status);
      Utils.logd(TAG, "onServicesDiscovered");
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
      super.onCharacteristicRead(gatt, characteristic, status);
      Utils.logd(TAG, "onCharacteristicRead");
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
      super.onCharacteristicChanged(gatt, characteristic);
      Utils.logd(TAG, "onCharacteristicChanged");
    }
  };

  void stateConnected() {
    mBluetoothGatt.discoverServices();
//    txtState.setText("Connected");
  }

  void stateDisconnected() {
    mBluetoothGatt.disconnect();
//    txtState.setText("Disconnected");
  }

  void setHeartRateNotification() {
    Utils.logd(TAG, "listenHeartRate: starts");
    BluetoothGattCharacteristic btChar = mBluetoothGatt.getService(MiBandServiceConst.HeartRate.service)
        .getCharacteristic(MiBandServiceConst.HeartRate.measurementCharacteristic);
    mBluetoothGatt.setCharacteristicNotification(btChar, true);
    BluetoothGattDescriptor descriptor = btChar.getDescriptor(MiBandServiceConst.HeartRate.descriptor);
    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    mBluetoothGatt.writeDescriptor(descriptor);
  }

  void stop() {
    Utils.logd(TAG, "Stop");
    if (mBluetoothGatt == null) {
      return;
    }
    mBluetoothGatt.close();
    mBluetoothGatt = null;
  }
}
