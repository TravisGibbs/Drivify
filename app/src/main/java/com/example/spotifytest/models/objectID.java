package com.example.spotifytest.models;

public class objectID {
  private boolean isSong;
  private String id;

  public objectID(boolean isSong, String id) {
    this.isSong = isSong;
    this.id = id;
  }

  public boolean isSong() {
    return isSong;
  }

  public void setSong(boolean song) {
    isSong = song;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
