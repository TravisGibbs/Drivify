package com.example.spotifytest.Models;

public class DataPoint {

  private int speed;
  private boolean isPastThreshold = false;

  public DataPoint(int speed, boolean isPastThreshold){
    this.speed = speed;
    this.isPastThreshold = isPastThreshold;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public boolean isPastThreshold() {
    return isPastThreshold;
  }

  public void setPastThreshold(boolean pastThreshold) {
    isPastThreshold = pastThreshold;
  }
}
