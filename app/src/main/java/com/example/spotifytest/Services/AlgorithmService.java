package com.example.spotifytest.Services;

import android.util.Log;

import com.example.spotifytest.Models.DataPoint;

import java.util.ArrayList;
import java.util.List;

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

  public int add(int speed, double thresholdUpper, double thresholdDown) {
    if (values.size() < lag) {
      DataPoint dataPoint = new DataPoint(speed, false);
      values.add(dataPoint);
      Log.i(TAG,"datapoint with speed: " + dataPoint.getSpeed() + " is a signal: " + dataPoint.isSignal());
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
    Log.i(TAG,"datapoint with speed: " + dataPoint.getSpeed() + " is a signal: " + dataPoint.isSignal());
    if (!dataPoint.isSignal()) {
      return 0;
    } else if (upperSignal) {
      return 1;
    } else {
      return 2;
    }
  }

  private void calcStats () {
    double sum = 0;
    int signalCount = 0;
    for (int i = 0; i < values.size(); i++) {
      if (values.get(i).isSignal()) {
        sum += ((double) values.get(i).getSpeed() / values.size()) * influence;
        signalCount += 1;
      }
    }
    double factorForNotSignals = 1 - (signalCount * ((double) 1 / values.size() * influence));
    for (int i = 0; i < values.size(); i++) {
      if (!values.get(i).isSignal()) {
        sum += (double) values.get(i).getSpeed() / values.size() * factorForNotSignals;
      }
    }
    this.mean = sum;
    double stdDevSum = 0;
    for (int i = 0; i < values.size(); i++) {
      if (values.get(i).isSignal()) {
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
