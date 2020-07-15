package com.example.spotifytest.Models;

public class SearchObject {

  private SongFull song;
  private Artist artist;

  public SearchObject(SongFull song, Artist artist) {
    this.song = song;
    this.artist = artist;
  }

  public SongFull getSong() {
    return song;
  }

  public void setSong(SongFull song) {
    this.song = song;
  }

  public Artist getArtist() {
    return artist;
  }

  public void setArtist(Artist artist) {
    this.artist = artist;
  }
}
