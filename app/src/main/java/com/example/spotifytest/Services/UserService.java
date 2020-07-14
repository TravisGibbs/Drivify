package com.example.spotifytest.Services;

import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.spotifytest.Models._User;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class UserService {

    public interface VolleyCallBack {
        void onSuccess();
    }

    private static final String ENDPOINT = "https://api.spotify.com/v1/me";
    private SharedPreferences msharedPreferences;
    private RequestQueue mqueue;
    private _User spotifyUser;

    public UserService(RequestQueue queue, SharedPreferences sharedPreferences) {
        mqueue = queue;
        msharedPreferences = sharedPreferences;
    }

    public _User getSpotifyUser() {
        return spotifyUser;
    }

    public void get(final VolleyCallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(ENDPOINT, null, response -> {
            spotifyUser = new _User();
            try {
                spotifyUser.setUsername(response.getString("display_name"));
                spotifyUser.setKeySpotifyid(response.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callBack.onSuccess();
        }, error -> get(() -> {

        })) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = msharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        mqueue.add(jsonObjectRequest);
    }

}
