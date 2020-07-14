package com.example.spotifytest.Models;

import java.util.ArrayList;

public class Playlist {
    private ArrayList<SongFull> songList;
    private String name;

    public Playlist(ArrayList<SongFull> songList, String name) {
        this.songList = songList;
        this.name = name;
    }

    public ArrayList<SongFull> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<SongFull> songList) {
        this.songList = songList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
