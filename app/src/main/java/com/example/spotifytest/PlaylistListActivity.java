package com.example.spotifytest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.spotifytest.Adapters.ListAdapter;
import com.example.spotifytest.Models.Playlist;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaylistListActivity extends AppCompatActivity {

  private final static String Tag = "PlaylistListActivity";
  private ArrayList<Playlist> playlistList;
  private ListAdapter adapter;
  private LinearLayoutManager layoutManager;
  private RelativeLayout relativeLayout;
  private RecyclerView drives;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_playlist_list);
    playlistList = new ArrayList<>();
    drives = findViewById(R.id.rvDrives);
    adapter = new ListAdapter(playlistList, this);
    relativeLayout = findViewById(R.id.listLayout);
    layoutManager = new LinearLayoutManager(this);
    drives.setAdapter(adapter);
    drives.setLayoutManager(layoutManager);
    queryPosts();
  }

  public void queryPosts() {
    ParseQuery<Playlist> query = ParseQuery.getQuery(Playlist.class);
    query.addDescendingOrder(Playlist.KEY_CREATED_AT);
    query.whereEqualTo(Playlist.KEY_USER, ParseUser.getCurrentUser());
    query.setLimit(30);
    query.findInBackground(new FindCallback<Playlist>() {
      @Override
      public void done(List<Playlist> objects, ParseException e) {
        playlistList.addAll(objects);
        Log.i(Tag, "Query succ");
        adapter.notifyDataSetChanged();
      }
    });
  }
}