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

  public AlgorithmService(int threshold, int influence, int lag) {
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

  public Boolean add(int speed) {
    DataPoint dataPoint;
    calcStats();
    if (Math.abs(speed - mean) > (std * threshold)) {
      dataPoint = new DataPoint(speed, true);
    } else {
      dataPoint = new DataPoint(speed, false);
    }
    values.remove(0);
    values.add(dataPoint);
    Log.i(TAG,"datapoint with speed: " + dataPoint.getSpeed() + " is a signal: " + dataPoint.isSignal());
    return dataPoint.isSignal();
  }

  private void calcStats () {
    double sum = 0;
    double totalFraction = 1;
    for (int i = 0; i < values.size(); i++) {
      if (values.get(i).isSignal()) {
        sum += (values.get(i).getSpeed() / (values.size() * influence));
        totalFraction = totalFraction - (double) (values.size()*influence);
      }
    }
    double factorForNotSignals = totalFraction /values.size();
    for (int i = 0; i < values.size(); i++) {
      if (!values.get(i).isSignal()) {
        sum += (double) values.get(i).getSpeed() / values.size()
                + (values.get(i).getSpeed() / factorForNotSignals);
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
    Log.i(TAG,"total fraction after signals: " + totalFraction +
            ", factor for non signals: " + factorForNotSignals +
            ", values size: " + values.size() +
            ", mean: " + sum +
            ", std: " + std);
  }
}
