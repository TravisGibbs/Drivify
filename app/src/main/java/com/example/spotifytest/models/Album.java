package com.example.spotifytest.models;

import java.util.ArrayList;

public class Album {

  private ArrayList<SpotifyImages> images;

  public Album(ArrayList<SpotifyImages> images) {
    this.images = images;
  }

  public ArrayList<SpotifyImages> getImages() {
    return images;
  }

  public void setImages(ArrayList<SpotifyImages> spotifyImages) {
    this.images = images;
  }
}
