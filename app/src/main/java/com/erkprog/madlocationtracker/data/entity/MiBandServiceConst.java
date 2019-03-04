package com.erkprog.madlocationtracker.data.entity;

import java.util.UUID;

public class MiBandServiceConst {

  public static class HeartRate {
    public static UUID service = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    public static UUID measurementCharacteristic = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    public static UUID descriptor = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static UUID controlCharacteristic = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");
  }

  public static class Basic {
    public static UUID service = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb");
    public static UUID batteryCharacteristic = UUID.fromString("00000006-0000-3512-2118-0009af100700");
    public static UUID stepsCharacteristic = UUID.fromString("00000007-0000-3512-2118-0009af100700");
  }
}
