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
import android.os.Handler;

import com.erkprog.madlocationtracker.data.entity.MiBandServiceConst;
import com.erkprog.madlocationtracker.utils.Utils;

import java.util.Arrays;

class BluetoothDeviceManager {
  private static final String TAG = "BluetoothDeviceManager";

  private Context mContext;
  private BluetoothDevice mBluetoothDevice;
  private BluetoothGatt mBluetoothGatt;
  private Handler mHrHandler;
  private BluetoothResultListener mListener;

  private static final int HEART_RATE_UPDATE_INTERVAL = 30 * 1000;

  interface BluetoothResultListener {
    void onHeartRateRead(int heartRateValue);
  }

  BluetoothDeviceManager(Context context, String deviceAddress) {
    Utils.logd(TAG, "constructor");
    mContext = context;
    BluetoothManager bluetoothManager =
        (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
    mBluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
    mHrHandler = new Handler();
  }

  void setListener(BluetoothResultListener listener) {
    mListener = listener;
  }

  void start() {
    Utils.logd(TAG, "start");
    mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, true, mBluetoothGattCallback);
  }

  private void handleHeartrate(byte[] value) {
    Utils.logd(TAG, "handleHeartrate: starts");
    try {
      if (value.length == 2 && value[0] == 0) {
        int hrValue = (value[1] & 0xff);
        Utils.logd(TAG, "hr value: " + hrValue);
        if (hrValue != 0) {
          mListener.onHeartRateRead(hrValue);
        }
      }
    } catch (Exception exception) {
      Utils.loge(TAG, "handling heartRate error, value: " + Arrays.toString(value) + ", error:" + exception.getMessage());
    }
  }

  private void handleSteps(byte[] value) {
    Utils.logd(TAG, "handle steps, data : " + Arrays.toString(value));
  }

  private void stateConnected() {
    mBluetoothGatt.discoverServices();
  }

  private void getStepsCount() {
    Utils.logd(TAG, "getting steps count");
    BluetoothGattCharacteristic btChar = mBluetoothGatt.getService(MiBandServiceConst.Basic.service)
        .getCharacteristic(MiBandServiceConst.Basic.stepsCharacteristic);
    if (!mBluetoothGatt.readCharacteristic(btChar)) {
      Utils.logd(TAG, "failed to get steps info");
    }
  }

  private void stateDisconnected() {
    mBluetoothGatt.disconnect();
  }

  void startScanHeartRate() {
    mHeartTask.run();
  }

  void stopScanHeartRate() {
    mHrHandler.removeCallbacks(mHeartTask);
  }

  private void getHeartRate() {
    Utils.logd(TAG, "requesting heart rate");
    BluetoothGattCharacteristic btChar = mBluetoothGatt.getService(MiBandServiceConst.HeartRate.service)
        .getCharacteristic(MiBandServiceConst.HeartRate.controlCharacteristic);
    btChar.setValue(new byte[]{21, 2, 1});
    mBluetoothGatt.writeCharacteristic(btChar);
  }

  private void setHeartRateNotification() {
    Utils.logd(TAG, "listenHeartRate: starts");
    BluetoothGattCharacteristic btChar = mBluetoothGatt.getService(MiBandServiceConst.HeartRate.service)
        .getCharacteristic(MiBandServiceConst.HeartRate.measurementCharacteristic);
    mBluetoothGatt.setCharacteristicNotification(btChar, true);
    BluetoothGattDescriptor descriptor = btChar.getDescriptor(MiBandServiceConst.HeartRate.descriptor);
    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    mBluetoothGatt.writeDescriptor(descriptor);
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
      setHeartRateNotification();
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
      super.onCharacteristicRead(gatt, characteristic, status);
      Utils.logd(TAG, "onCharacteristicRead, UUID: " + String.valueOf(characteristic.getUuid()));
      if (characteristic.getUuid().equals(MiBandServiceConst.Basic.stepsCharacteristic)) {
        handleSteps(characteristic.getValue());
      }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
      super.onCharacteristicChanged(gatt, characteristic);
      Utils.logd(TAG, "onCharacteristicChanged, UUID " + characteristic.getUuid());
      if (characteristic.getUuid().equals(MiBandServiceConst.HeartRate.measurementCharacteristic)) {
        handleHeartrate(characteristic.getValue());
      }
    }
  };

  void stop() {
    Utils.logd(TAG, "Stop");
    stopScanHeartRate();
    if (mBluetoothGatt == null) {
      return;
    }
    mBluetoothGatt.close();
    mBluetoothGatt = null;
  }

  private Runnable mHeartTask = new Runnable() {
    @Override
    public void run() {
      getHeartRate();
      mHrHandler.postDelayed(mHeartTask, HEART_RATE_UPDATE_INTERVAL);
    }
  };
}
