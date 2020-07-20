package com.example.spotifytest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.example.spotifytest.Adapters.ListAdapter;
import com.example.spotifytest.Adapters.SearchAdapter;
import com.example.spotifytest.Models.Playlist;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
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
    ListAdapter.OnClickListener onClickListener = new ListAdapter.OnClickListener() {
      @Override
      public void onItemClicked(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
      }
    };
    adapter = new ListAdapter(playlistList, this, onClickListener);
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