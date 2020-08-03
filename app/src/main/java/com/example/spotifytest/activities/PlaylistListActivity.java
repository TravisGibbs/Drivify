package com.example.spotifytest.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.spotifytest.adapters.ListAdapter;
import com.example.spotifytest.models.Playlist;
import com.example.spotifytest.R;
import com.example.spotifytest.services.NavigatorService;
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
  private NavigatorService navigatorService;
  private ProgressBar progressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_playlist_list);
    progressBar = findViewById(R.id.pbLoadingPlaylistList);
    progressBar.setVisibility(View.VISIBLE);
    playlistList = new ArrayList<>();
    drives = findViewById(R.id.rvDrives);
    navigatorService = new NavigatorService();
    ListAdapter.OnClickListener onClickListener = new ListAdapter.OnClickListener() {
      @Override
      public void onItemClicked(String url) {
        startActivity(navigatorService.openPlaylist(url));
      }
    };
    adapter = new ListAdapter(playlistList, this, onClickListener);
    relativeLayout = findViewById(R.id.listLayout);
    layoutManager = new LinearLayoutManager(this);
    drives.setAdapter(adapter);
    drives.setLayoutManager(layoutManager);
    queryPosts();
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);
    return true;
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
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
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
      }
    });
  }
}