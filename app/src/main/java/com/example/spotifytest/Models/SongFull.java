package com.example.spotifytest.Models;

import java.util.ArrayList;

public class SongFull extends SongSimplified {

    private Album album;
    private float dance;
    private float energy;
    private float loudness;
    private float tempo;

    public SongFull(String id, String name, int duration_ms, String uri, ArrayList<Artist> artists, Album album) {
        super(id, name, duration_ms, uri, artists);
        this.album = album;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public float getDance() {
        return this.dance;
    }

    public void setDance(float dance) {
        this.dance = dance;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float getLoudness() {
        return loudness;
    }

    public void setLoudness(float loudness) {
        this.loudness = loudness;
    }

    public float getTempo() {
        return tempo;
    }

    public void setTempo(float tempo) {
        this.tempo = tempo;
    }

}
