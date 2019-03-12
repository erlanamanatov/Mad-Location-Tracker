package com.erkprog.madlocationtracker.utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HourAxisValueFormatter implements IAxisValueFormatter {
  private long referenceTimestamp; // minimum timestamp in data set

  public HourAxisValueFormatter(long referenceTimestamp) {
    this.referenceTimestamp = referenceTimestamp;
  }


  /**
   * Called when a value from an axis is to be formatted
   * before being drawn
   *
   * @param value the value to be formatted
   * @param axis  the axis the value belongs to
   * @return
   */
  @Override
  public String getFormattedValue(float value, AxisBase axis) {
    // convertedTimestamp = originalTimestamp - referenceTimestamp
    long convertedTimestamp = (long) value;

    // Retrieve original timestamp
    long originalTimestamp = referenceTimestamp + convertedTimestamp;

    Date date = new Date(originalTimestamp);

    try {
      SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", new Locale("en"));
      return formatter.format(date);
    } catch (Exception e) {
      return "?";
    }
  }
}
