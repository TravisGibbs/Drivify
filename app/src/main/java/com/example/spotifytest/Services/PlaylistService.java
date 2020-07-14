package com.example.spotifytest.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotifytest.Models.SongFull;
import com.example.spotifytest.Models.SongSimplified;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistService {
    private final RelativeLayout relativeLayout;
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    public final static String Tag = "PlaylistService";
    public String playlistID;
    public String playlistExternalLink;

    public PlaylistService(Context context, RelativeLayout relativeLayout) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        this.relativeLayout = relativeLayout;
    }

    public void addPlaylist(String playlistTitle, ArrayList<SongFull> songSimplifieds, int time) {
        JSONObject payload = preparePutPayloadPlaylistPost(playlistTitle);
        JsonObjectRequest jsonObjectRequest = PlaylistPost(payload, songSimplifieds, time);
        queue.add(jsonObjectRequest);
    }

    public void addSong(ArrayList<SongFull> songSimplifieds){
        JSONObject payload = preparePutPayloadSongPost(songSimplifieds);
        JsonObjectRequest jsonObjectRequest = SongPost(payload);
        queue.add(jsonObjectRequest);
    }

    private JSONObject preparePutPayloadPlaylistPost(String song){
        JSONObject playlist = new JSONObject();
        try {
            playlist.put("name", song);

        } catch (JSONException e){
            e.printStackTrace();
        }

        return playlist;
    }

    private JSONObject preparePutPayloadSongPost(ArrayList<SongFull> songSimplifieds) {
        JSONObject request = new JSONObject();
        JSONArray uri = new JSONArray();
        for(SongFull songSimplified : songSimplifieds){
            uri.put(songSimplified.getUri());
        }
        try {
            request.put("uris",uri);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return request;
    }

    private JsonObjectRequest PlaylistPost(JSONObject payload, ArrayList<SongFull> songSimplifieds, int time) {
        return new JsonObjectRequest(Request.Method.POST, PostPlaylistEnd(), payload, response -> {
            try {
                onSuccPlaylist(response, songSimplifieds, time);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Snackbar.make(relativeLayout, "Playlist Post!", Snackbar.LENGTH_SHORT).show();
        }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Accept","application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
    }

    private JsonObjectRequest SongPost(JSONObject payload) {
        return new JsonObjectRequest(Request.Method.POST, String.format("https://api.spotify.com/v1/playlists/%s/tracks",playlistID), payload, response -> {
            try {
                onSuccSong(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.i(Tag,"help");
        }) {

            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
    }

    public void onSuccSong(JSONObject response) throws JSONException{
        Log.i(Tag,"song posted");
    }

    public void onSuccPlaylist(JSONObject response, ArrayList<SongFull> songSimplifieds, int time) throws JSONException {
        playlistID = response.getString("id");
        JSONObject external_urls = response.getJSONObject("external_urls");
        playlistExternalLink = external_urls.getString("spotify");
        ArrayList<SongFull> newSongFull = new ArrayList<>();
        int i = 0;
        int sum = 0;
        while(sum<time){
            newSongFull.add(songSimplifieds.get(i));
            sum += songSimplifieds.get(i).getDuration_ms();
            i++;
        }
        addSong(newSongFull);
    }

    public String PostPlaylistEnd() {
        String endpoint = String.format("https://api.spotify.com/v1/users/%s/playlists", sharedPreferences.getString("userid", "No SpotifyUser"));
        Log.i(Tag,endpoint);
        return endpoint;
    }
}
