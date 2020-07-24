package com.example.spotifytest.Models;

public class DataPoint {

  int speed;
  boolean isSignal = false;

  public DataPoint(int speed, boolean isSignal){
    this.speed = speed;
    this.isSignal = isSignal;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public boolean isSignal() {
    return isSignal;
  }

  public void setSignal(boolean signal) {
    isSignal = signal;
  }
}
