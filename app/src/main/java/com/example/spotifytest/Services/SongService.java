package com.example.spotifytest.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotifytest.Models.SongFull;
import com.example.spotifytest.Models.SongSimplified;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SongService {

    private static final String Tag = "SongService";
    private ArrayList<SongFull> songFulls = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private RelativeLayout relativeLayout;

    public SongService(Context context, RelativeLayout relativeLayout) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        this.relativeLayout = relativeLayout;
    }

    public ArrayList<SongFull> getSongFulls() {
        return songFulls;
    }

    public ArrayList<SongFull> getRecentlyPlayedTracks(final UserService.VolleyCallBack callBack , int amount) {
        ArrayList<SongSimplified> songSimplifieds = new ArrayList<>();
        String endpoint = String.format("https://api.spotify.com/v1/me/player/recently-played?limit=%s",amount );
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("items");
                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);
                            object = object.optJSONObject("track");
                            SongSimplified songSimplified = gson.fromJson(object.toString(), SongSimplified.class);
                            songSimplifieds.add(songSimplified);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    songFulls =getTracks(songSimplifieds, callBack);
                    callBack.onSuccess();
                }, error -> {
                    // TODO: Handle error
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return songFulls;
    }

    public ArrayList<SongFull> getTracks(ArrayList<SongSimplified> songSimplifieds, UserService.VolleyCallBack callBack){
        String url = getURLforTracks(songSimplifieds);
        ArrayList<SongFull> songFulls = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = response.getJSONArray("tracks");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);
                            SongFull songFull = gson.fromJson(object.toString(), SongFull.class);
                            songFulls.add(getTrackDetails(songFull, n, callBack));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    // TODO: Handle error
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return songFulls;
    }

    public SongFull getTrackDetails(SongFull songFull,int position, UserService.VolleyCallBack callBack){
        String url = getURLforTrackDetails(songFull);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    try {
                        songFull.setEnergy(BigDecimal.valueOf(response.getDouble("energy")).floatValue());
                        songFull.setDance(BigDecimal.valueOf(response.getDouble("danceability")).floatValue());
                        songFull.setLoudness(BigDecimal.valueOf(response.getDouble("loudness")).floatValue());
                        songFull.setTempo(BigDecimal.valueOf(response.getDouble("tempo")).floatValue());
                        songFulls.set(position, songFull);
                        callBack.onSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    // TODO: Handle error
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return songFull;
    }



    private String getURLforTracks(ArrayList<SongSimplified> songSimplifieds) {
        String url = "https://api.spotify.com/v1/tracks/?ids=";
        for(SongSimplified songSimplified: songSimplifieds) {
            url += songSimplified.getId();
            url += ",";
        }
        url += songSimplifieds.get(0).getId();
        Log.i(Tag,"URL for get tracks: " + url);
        return url;
    }

    private String getURLforTrackDetails(SongFull songFull) {
        return("https://api.spotify.com/v1/audio-features/" + songFull.getId());
    }

    public void addSongToLibrary(SongSimplified songSimplified) {
        JSONObject payload = preparePutPayload(songSimplified);
        JsonObjectRequest jsonObjectRequest = prepareSongLibraryRequest(payload);
        queue.add(jsonObjectRequest);
    }

    private JsonObjectRequest prepareSongLibraryRequest(JSONObject payload) {
        return new JsonObjectRequest(Request.Method.PUT, "https://api.spotify.com/v1/me/tracks", payload, response -> {
        }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
    }

    private JSONObject preparePutPayload(SongSimplified songSimplified) {
        JSONArray idarray = new JSONArray();
        idarray.put(songSimplified.getId());
        JSONObject ids = new JSONObject();
        try {
            ids.put("ids", idarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ids;
    }

}