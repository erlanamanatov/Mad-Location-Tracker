package com.erkprog.madlocationtracker;

import android.content.Context;
import android.os.Handler;

import com.erkprog.madlocationtracker.data.entity.MiBandServiceConst;
import com.erkprog.madlocationtracker.utils.Utils;
import com.jakewharton.rx.ReplayingShare;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

class BluetoothDeviceManager {
  private static final String TAG = "BluetoothDeviceManager";

  private RxBleDevice mBleDevice;
//  private BluetoothGatt mBluetoothGatt;
  private Handler mHrHandler;
  private BluetoothResultListener mListener;

  private RxBleClient mBleClient;
  private Observable<RxBleConnection> connectionObservable;
  private final CompositeDisposable compositeDisposable = new CompositeDisposable();
  private PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();


  private static final int HEART_RATE_UPDATE_INTERVAL = 30 * 1000;

  interface BluetoothResultListener {
    void onHeartRateRead(int heartRateValue);
  }

  BluetoothDeviceManager(Context context, String deviceAddress) {
    Utils.logd(TAG, "constructor");
    mBleClient = RxBleClient.create(context);
    mBleDevice = mBleClient.getBleDevice(deviceAddress);
    mHrHandler = new Handler();
    connectionObservable = prepareConnectionObservable();
  }

  private Observable<RxBleConnection> prepareConnectionObservable() {
    return mBleDevice
        .establishConnection(false)
        .takeUntil(disconnectTriggerSubject)
        .compose(ReplayingShare.instance());
  }

  void setListener(BluetoothResultListener listener) {
    mListener = listener;
  }

  void start() {
    Utils.logd(TAG, "start");

    final Disposable connectionDisposable = connectionObservable
        .flatMapSingle(RxBleConnection::discoverServices)
//        .flatMapSingle(rxBleDeviceServices -> rxBleDeviceServices.getCharacteristic(MiBandServiceConst.Basic.stepsCharacteristic))
        .flatMapSingle(rxBleDeviceServices -> rxBleDeviceServices.getCharacteristic(MiBandServiceConst.HeartRate.measurementCharacteristic))
        .observeOn(AndroidSchedulers.mainThread())
//        .doOnSubscribe(disposable -> connectButton.setText(R.string.connecting))
        .subscribe(
            characteristic -> {
              Utils.logd(TAG, "Start, heartRate connection has been established!");
              setHeartRateNotification();
            },
            this::onConnectionFailure,
            this::onConnectionFinished
        );

    compositeDisposable.add(connectionDisposable);

//    final Disposable rConDisp = connectionObservable
//        .flatMapSingle(RxBleConnection::discoverServices)
//        .flatMapSingle(rxBleDeviceServices -> rxBleDeviceServices.getCharacteristic(MiBandServiceConst.Basic.stepsCharacteristic))
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(
//            characteristic -> {
//              Utils.logd(TAG, "Start, steps connection has been established!");
//            },
//            this::onConnectionFailure,
//            this::onConnectionFinished
//        );
//    compositeDisposable.add(rConDisp);

  }

  private void onConnectionFailure(Throwable throwable) {
    Utils.logd(TAG, "onConnectionFailure, " + throwable);
  }

  private void onConnectionFinished() {
    Utils.logd(TAG, "OnConnectionFinished");
  }

  private boolean isConnected() {
    return mBleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
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

//  private void stateConnected() {
//    mBluetoothGatt.discoverServices();
//  }

  private void getStepsCount() {
    Utils.logd(TAG, "getting steps count");
    if (isConnected()) {
      final Disposable disposable = connectionObservable
          .firstOrError()
          .flatMap(rxBleConnection -> rxBleConnection.readCharacteristic(MiBandServiceConst.Basic.stepsCharacteristic))
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(bytes -> {
            Utils.logd(TAG, "steps : " + Arrays.toString(bytes));
          }, this::onReadFailure);

      compositeDisposable.add(disposable);
    }
  }

  private void onReadFailure(Throwable throwable) {
    Utils.loge(TAG, "Read error: " + throwable);
  }


//  private void stateDisconnected() {
//    mBluetoothGatt.disconnect();
//  }

  void startScanHeartRate() {

    mHeartTask.run();

//    getStepsCount();
//    getHeartRate();
  }

  void stopScanHeartRate() {
    Utils.logd(TAG, "stopScanHeartRate");
    mHrHandler.removeCallbacks(mHeartTask);
    compositeDisposable.clear();
  }

  private void getHeartRate() {
    Utils.logd(TAG, "requesting heart rate");

    if (isConnected()) {
      final Disposable disposable = connectionObservable
          .firstOrError()
          .flatMap(rxBleConnection -> rxBleConnection.writeCharacteristic(MiBandServiceConst.HeartRate.controlCharacteristic, new byte[]{21, 2, 1}))
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              bytes -> onWriteSuccess(),
              this::onWriteFailure
          );

      compositeDisposable.add(disposable);
    } else {
      Utils.loge(TAG, "device not connected");
      compositeDisposable.clear();
      start();
    }
  }

  private void onWriteSuccess() {
    Utils.logd(TAG, "onWriteSuccess");
  }

  private void onWriteFailure(Throwable throwable) {
    Utils.logd(TAG, "onWriteFailure, " + throwable);
  }


  private void setHeartRateNotification() {

    Utils.logd(TAG, "setHeartRateNotification");
    if (isConnected()) {
      final Disposable disposable = connectionObservable
          .flatMap(rxBleConnection -> rxBleConnection.setupNotification(MiBandServiceConst.HeartRate.measurementCharacteristic))
          .doOnNext(notificationObservable -> Utils.logd(TAG, "notification has been set up"))
          .flatMap(notificationObservable -> notificationObservable)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(this::onNotificationReceived, this::onNotificationSetupFailure);

      compositeDisposable.add(disposable);
    }
  }

  private void onNotificationReceived(byte[] bytes) {
    try {
      Utils.logd(TAG, "onNotificationReceived " + Arrays.toString(bytes));
      handleHeartrate(bytes);
    } catch (Exception e) {
      Utils.loge(TAG, "onNotificationReceived, : " + e.getMessage());
    }
  }

  private void onNotificationSetupFailure(Throwable throwable) {
    Utils.loge(TAG, "onNotificationSetupFailure: " + throwable);
  }

  void stop() {
    Utils.logd(TAG, "Stop");
    stopScanHeartRate();
    compositeDisposable.clear();
  }

  private Runnable mHeartTask = new Runnable() {
    @Override
    public void run() {
      getHeartRate();
      mHrHandler.postDelayed(mHeartTask, HEART_RATE_UPDATE_INTERVAL);
    }
  };
}
