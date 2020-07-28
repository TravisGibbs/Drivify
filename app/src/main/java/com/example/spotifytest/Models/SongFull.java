package com.example.spotifytest.Models;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class SongFull extends SongSimplified implements Serializable {

    private Album album;
    private float dance;
    private float energy;
    private float loudness;
    private float tempo;

    public SongFull(){
        super();
    }

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

    public static Comparator<SongFull> SongDanceComparator = new Comparator<SongFull>() {

        @Override
        public int compare(SongFull songFull, SongFull t1) {
            float temp = songFull.getDance() - t1.getDance();
            if (temp < 0) {
                return -1;
            }
            if (temp > 0) {
                return 1;
            }
            return 0;
        }
        };

    public static Comparator<SongFull> SongEnergyComparator = new Comparator<SongFull>() {

    @Override
    public int compare(SongFull songFull, SongFull t1) {
        float temp = t1.getEnergy() - songFull.getEnergy();
        if (temp < 0) {
            return -1;
        }
        if (temp > 0) {
            return 1;
        }
        return 0;
        }
    };

}
