package com.example.spotifytest;

public class MapSettings {

  public MapSettings () {

  }

  public int getZoom(int timeTo){
    int minutes = timeTo/60000;
    if (minutes< 5) {
      return 12;
    } else if (minutes <= 15) {
      return 11;
    } else if (15 < minutes && minutes <= 30) {
      return 9;
    } else if (minutes < 60){
      return 8;
    } else if (minutes < 120) {
      return  7;
    } else if (minutes < 240) {
      return  6;
    } else {
      return  3;
    }
  }
}
