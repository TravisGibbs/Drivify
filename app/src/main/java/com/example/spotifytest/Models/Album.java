package com.example.spotifytest.Models;

import java.util.ArrayList;

public class Album {

    private ArrayList<SpotifyImages> images;

    public ArrayList<SpotifyImages> getImages() {
        return images;
    }

    public void setImages(ArrayList<SpotifyImages> spotifyImages) {
        this.images = images;
    }

    public Album(ArrayList<SpotifyImages> images){
        this.images = images;
    }
}
