package com.example.spotifytest.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotifytest.Models.Playlist;
import com.example.spotifytest.Models.SongFull;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistService {

    private final static String Tag = "PlaylistService";
    private RelativeLayout relativeLayout;
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private String playlistID;
    private String playlistExternalLink;
    private String playlistURI;

    public PlaylistService(Context context, RelativeLayout relativeLayout) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        this.relativeLayout = relativeLayout;
    }

    public void addPlaylist(String playlistTitle, ArrayList<SongFull> songSimplifieds,
                            int time, Place origin, Place destination) {
        JSONObject payload = preparePutPayloadPlaylistPost(playlistTitle);
        JsonObjectRequest jsonObjectRequest =
                playlistPost(payload, songSimplifieds, time, origin, destination);
        queue.add(jsonObjectRequest);
    }

    public void addSong(ArrayList<SongFull> songFulls) {
        int fullSearches = songFulls.size() / 100;
        int songsRemaining = songFulls.size();
        int i = 0;
        int startOfSublist = 0;
        int endOfSublist = 100;
        while (i < fullSearches) {
            List<SongFull> postList = songFulls.subList(startOfSublist, endOfSublist);
            JSONObject payload = preparePutPayloadSongPost((postList));
            JsonObjectRequest jsonObjectRequest = SongPost(payload);
            queue.add(jsonObjectRequest);
            startOfSublist += 100;
            endOfSublist += 100;
            i++;
            songsRemaining = songsRemaining - 100;
        }
        List<SongFull> postList = songFulls.subList(startOfSublist, startOfSublist + songsRemaining);
        JSONObject payload = preparePutPayloadSongPost(postList);
        JsonObjectRequest jsonObjectRequest = SongPost(payload);
    }

    private JSONObject preparePutPayloadPlaylistPost(String song) {
        JSONObject playlist = new JSONObject();
        try {
            playlist.put("name", song);

        } catch (JSONException e){
            e.printStackTrace();
        }

        return playlist;
    }

    private JSONObject preparePutPayloadSongPost(List<SongFull> songFulls) {
        JSONObject request = new JSONObject();
        JSONArray uri = new JSONArray();
        for(SongFull songFull : songFulls){
            uri.put(songFull.getUri());
        }
        try {
            request.put("uris",uri);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return request;
    }

    private JsonObjectRequest playlistPost(JSONObject payload, ArrayList<SongFull> songSimplifieds,
                                           int time, Place origin, Place destination) {
        return new JsonObjectRequest(Request.Method.POST, getPostPlaylistURL(), payload, response -> {
            try {
                onSuccPlaylist(response, songSimplifieds, time, origin, destination);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Snackbar.make(relativeLayout, "Playlist Post!", Snackbar.LENGTH_SHORT).show();
        }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders() {
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
        return new JsonObjectRequest(Request.Method.POST,
                String.format("https://api.spotify.com/v1/playlists/%s/tracks",playlistID),
                payload, response -> {
            try {
                onSuccSong(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.i(Tag,"song not posted");
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
    }

    public void onSuccSong(JSONObject response) throws JSONException {
        Log.i(Tag,"song posted");
    }

    public void onSuccPlaylist(JSONObject response, ArrayList<SongFull> songSimplifieds,
                               int time, Place origin, Place destination) throws JSONException {
        playlistID = response.getString("id");
        JSONObject external_urls = response.getJSONObject("external_urls");
        playlistExternalLink = external_urls.getString("spotify");
        playlistURI = response.getString("uri");
        addSong(songSimplifieds);
        ArrayList<SongFull> newSongFull = new ArrayList<>();
        int i = 0;
        int sum = 0;
        while(sum < time && i < songSimplifieds.size()) {
            newSongFull.add(songSimplifieds.get(i));
            sum += songSimplifieds.get(i).getDuration_ms();
            i++;
        }
        addSong(newSongFull);
        playlistParsePost(origin, destination, time);
    }

    private void playlistParsePost(Place origin, Place destination, int time) {
        Playlist playlist = new Playlist();
        playlist.setKeyOriginId(origin.getId());
        playlist.setKeyDestinationId(destination.getId());
        playlist.setKeyPlaylistId(playlistID);
        playlist.setKeyRedirectLink(playlistExternalLink);
        playlist.setTimeTo(String.valueOf(time));
        playlist.setUser(ParseUser.getCurrentUser());
        playlist.setKeyTitle(origin.getName() + " to " + destination.getName());
        playlist.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(Tag, "post failed", e);
                }
                Log.i(Tag, "posted playlist");
            }
        });
        return;
    }

    public String getPostPlaylistURL() {
        String endpoint = String.format("https://api.spotify.com/v1/users/%s/playlists",
                sharedPreferences.getString("userid", "No SpotifyUser"));
        Log.i(Tag,endpoint);
        return endpoint;
    }

    public String getPlaylistExternalLink() {
        return playlistExternalLink;
    }

    public String getPlaylistId() {
        return playlistID;
    }

    public String getPlaylistURI() {
        return playlistURI;
    }
}
