package com.example.spotifytest.Models;

import java.util.ArrayList;

public class Artist {

    private String id;
    private String name;
    private String uri;
    private ArrayList<SpotifyImages> images;

    public Artist(String id, String name,  String uri, ArrayList<SpotifyImages> images){
        this.id =id;
        this.name=name;
        this.uri =uri;
        this.images = images;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public ArrayList<SpotifyImages> getImages() {
        return images;
    }

    public void setImages(ArrayList<SpotifyImages> images) {
        this.images = images;
    }
}
