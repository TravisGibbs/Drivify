package com.example.spotifytest.Models;

import java.util.ArrayList;

public class SongFull extends SongSimplified {

    public SongFull(String id, String name, int duration_ms, String uri, ArrayList<Artist> artists, Album album) {
        super(id, name, duration_ms, uri, artists);
        this.album = album;
    }

    private Album album;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

}
