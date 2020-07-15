package com.example.spotifytest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotifytest.Adapters.PlaylistAdapter;
import com.example.spotifytest.Adapters.SearchAdapter;
import com.example.spotifytest.Models.Artist;
import com.example.spotifytest.Models.SearchObject;
import com.example.spotifytest.Models.SongFull;
import com.example.spotifytest.Models.SongSimplified;
import com.example.spotifytest.Models.SpotifyImages;
import com.example.spotifytest.Services.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

  private static final String Tag = "SearchActivity";
  private RelativeLayout relativeLayout;
  private SharedPreferences sharedPreferences;
  private RequestQueue queue;
  private RecyclerView rvSearch;
  private EditText searchText;
  private SearchAdapter searchAdapter;
  private LinearLayoutManager linearLayoutManager;
  private ArrayList<SearchObject> searchObjects;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
    queue = Volley.newRequestQueue(this);
    searchObjects = new ArrayList<>();
    rvSearch = findViewById(R.id.rvSearch);
    searchText = findViewById(R.id.searchBar);
    SearchAdapter.OnClickListener onClickListener = new SearchAdapter.OnClickListener() {
      @Override
      public void onItemClicked(int position, String id, Boolean isSong) {
        Intent intent=new Intent();
        intent.putExtra("id",id);
        intent.putExtra("isSong", isSong);
        setResult(2,intent);
        finish();//finishing activity
      }
    };
    searchAdapter = new SearchAdapter(searchObjects, this, onClickListener);
    linearLayoutManager = new LinearLayoutManager(this);
    rvSearch.setAdapter(searchAdapter);
    rvSearch.setLayoutManager(linearLayoutManager);

    searchText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        getSearchObjects(new UserService.VolleyCallBack() {
          @Override
          public void onSuccess() {
            Log.i(Tag, "Text searched");
          }
        },searchText.getText().toString());
      }

      @Override
      public void afterTextChanged(Editable editable) {
      }
    });

  }

  public void getSearchObjects(final UserService.VolleyCallBack callBack, String q) {
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, getUrl(q), null, response -> {
              Log.i(Tag,"searched " + q);
              Gson gson = new Gson();
              searchObjects.clear();
              JSONArray artists = new JSONArray();
              JSONArray songs = new JSONArray();
              try {
                artists = response.getJSONObject("artists").getJSONArray("items");
                for(int i = 0; i < artists.length();i++){
                  JSONObject jsonObject = (JSONObject) artists.get(i);
                  Artist artist = gson.fromJson(jsonObject.toString(), Artist.class);
                  SearchObject searchObject = new SearchObject(null, artist);
                  searchObjects.add(searchObject);
                }
                songs = response.getJSONObject("tracks").getJSONArray("items");
                for(int i = 0; i < songs.length();i++){
                  JSONObject jsonObject = (JSONObject) songs.get(i);
                  SongFull song = gson.fromJson(jsonObject.toString(), SongFull.class);
                  SearchObject searchObject = new SearchObject(song, null);
                  searchObjects.add(searchObject);
                }
              } catch (JSONException e) {
                e.printStackTrace();
              }
              searchAdapter.notifyDataSetChanged();
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
  }

  private String getUrl(String q){
    String url = "https://api.spotify.com/v1/search?q=";
    q.replace(" ", "%20");
    url += q;
    url += "&type=artist,track";
    url += "&limit=10";
    //TODO possible offset offset=(0,2000) for infinite scrolling
    return(url);
  }
}
