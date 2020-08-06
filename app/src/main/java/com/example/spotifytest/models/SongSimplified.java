package com.example.spotifytest.models;

import java.util.ArrayList;

public class SongSimplified {

  private String id;
  private String name;
  private String uri;
  private ArrayList<Artist> artists;
  private int duration_ms;

  public SongSimplified() {
  }

  public SongSimplified(String id, String name, int duration_ms, String uri, ArrayList<Artist> artists) {
    this.name = name;
    this.id = id;
    this.duration_ms = duration_ms;
    this.uri = uri;
    this.artists = artists;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getDuration_ms() {
    return duration_ms;
  }

  public void setDuration_ms(int duration_ms) {
    this.duration_ms = duration_ms;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public ArrayList<Artist> getArtists() {
    return artists;
  }

  public void setArtists(ArrayList<Artist> artists) {
    this.artists = artists;
  }

}
