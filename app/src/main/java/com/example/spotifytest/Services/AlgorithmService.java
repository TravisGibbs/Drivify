package com.example.spotifytest.Services;

import android.util.Log;

import com.example.spotifytest.Models.DataPoint;

import java.util.ArrayList;

public class AlgorithmService {

  private final static String TAG = "AlgorithmService";
  private ArrayList<DataPoint> values;
  private double threshold;
  private double influence;
  private int lag;
  private double std;
  private double mean;

  public AlgorithmService(double threshold, double influence, int lag) {
    this.threshold = threshold;
    this.influence = influence;
    this.lag = lag;
    this.values = new ArrayList<>();
  }

  public double getThreshold() {
    return threshold;
  }

  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }

  /*
    This algorithm tests for zscores using a rolling average and standard deviation.
    Each time there is a new data point the previous points are used in calcstats to determine
    these values. If a point is outside of desired range than it is indicated via a response code:

    0: there aren't enough values to fill the specified lag parameter or the point is not
    outside the threshold
    1: the value is outside the threshold and above it
    2: the value is outside the threshold and below it

    Lag -
    The algorithm stores a set number of values as determined by the lag parameter.
    Each time a value is added the oldest value is removed.

    Threshold -
    The threshold is used to be threshold*one standard deviation to determine if the point is
    past the threshold.

    Upper/Lower threshold - These are values that are passed into the add function that
     represent an option to adjust the bounds for different directions of breaking the bounds.
      This is important because in th main algorithm it shows whether
       or not the user is slowing or speeding up.

    Influence -
    If the point is outside the threshold than it will have less influence
    on the overall average as determined by the influence parameter

    Credit:
https://stackoverflow.com/questions/22583391/peak-signal-detection-in-realtime-timeseries-data/56174275#56174275
    */
  public int add(int speed, double thresholdUpper, double thresholdDown) {
    if (values.size() < lag) {
      DataPoint dataPoint = new DataPoint(speed, false);
      values.add(dataPoint);
      Log.i(TAG, "datapoint with speed: " + dataPoint.getSpeed() + " is a signal: " + dataPoint.isPastThreshold());
      return 0;
    }
    DataPoint dataPoint;
    boolean upperSignal = false;
    calcStats();
    if ((speed - mean) > (std * thresholdUpper)) {
      dataPoint = new DataPoint(speed, true);
      upperSignal = true;
    } else if ((speed - mean) < (std * thresholdDown)) {
      dataPoint = new DataPoint(speed, true);
    } else {
      dataPoint = new DataPoint(speed, false);
    }
    values.remove(0);
    values.add(dataPoint);
    Log.i(TAG,"datapoint with speed: " + dataPoint.getSpeed() + " is a signal: " + dataPoint.isPastThreshold());
    if (!dataPoint.isPastThreshold()) {
      return 0;
    } else if (upperSignal) {
      return 1;
    } else {
      return 2;
    }
  }

  /* Calc stats is called each time a data point is added to calculate a rolling average and
   a rolling standard deviation. The varible signal count allows for the influence to
   be properly proportioned by the influence factor.*/
  private void calcStats () {
    double sum = 0;
    int signalCount = 0;
    for (int i = 0; i < values.size(); i++) {
      if (values.get(i).isPastThreshold()) {
        sum += ((double) values.get(i).getSpeed() / values.size()) * influence;
        signalCount += 1;
      }
    }
    double factorForNotSignals = 1 - (signalCount * ((double) 1 / values.size() * influence));
    for (int i = 0; i < values.size(); i++) {
      if (!values.get(i).isPastThreshold()) {
        sum += (double) values.get(i).getSpeed() / values.size() * factorForNotSignals;
      }
    }
    this.mean = sum;
    double stdDevSum = 0;
    for (int i = 0; i < values.size(); i++) {
      if (values.get(i).isPastThreshold()) {
        stdDevSum += Math.pow((values.get(i).getSpeed() - mean), 2) * influence;
      } else {
        stdDevSum += Math.pow((values.get(i).getSpeed() - mean), 2) / factorForNotSignals;
      }
    }
    this.std = Math.pow(stdDevSum / values.size(), .5);
    Log.i(TAG,"signal count: " + signalCount +
            ", factor for non signals: " + factorForNotSignals +
            ", values size: " + values.size() +
            ", mean: " + sum +
            ", std: " + std);
  }
}
