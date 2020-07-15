package com.example.spotifytest;

import androidx.lifecycle.ViewModel;

import com.example.spotifytest.Models.SongFull;

import java.util.ArrayList;

public class SongsViewModel extends ViewModel {

  private ArrayList<SongFull> songList;

  public SongsViewModel(){
    songList = new ArrayList<>();
  }

  public ArrayList<SongFull> getSongList() {
    return songList;
  }

  public void setSongList(ArrayList<SongFull> songList) {
    this.songList = songList;
  }
}
