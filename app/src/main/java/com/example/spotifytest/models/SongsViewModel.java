package com.example.spotifytest.models;

import androidx.lifecycle.ViewModel;

import com.example.spotifytest.services.PlaylistService;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class SongsViewModel extends ViewModel {

  private ArrayList<SongFull> songList;
  private PlaylistService playlistService;

  public SongsViewModel() {
    songList = new ArrayList<>();
  }

  public ArrayList<SongFull> getSongList() {
    return songList;
  }

  public void setSongList(ArrayList<SongFull> songList) {
    this.songList = songList;
  }

  @Nullable
  public PlaylistService getPlaylistService() {
    return playlistService;
  }

  public void setPlaylistService(PlaylistService playlistService) {
    this.playlistService = playlistService;
  }
}
