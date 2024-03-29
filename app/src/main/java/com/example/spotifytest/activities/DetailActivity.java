package com.example.spotifytest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifytest.R;
import com.example.spotifytest.models.Const;
import com.example.spotifytest.models.Playlist;
import com.example.spotifytest.models.SongsViewModel;
import com.example.spotifytest.services.MapService;
import com.example.spotifytest.services.NavigatorService;
import com.example.spotifytest.services.PlaylistService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

  private static final String apiKey = "AIzaSyDmCIZvAzyQ5iO3s4Qw2GMJxu_vDjOXWCk";
  private static final String Tag = "detailActivity";
  private TextView title;
  private Button goToPlaylist;
  private Button goToDrivify;
  private PlaylistService playlistService;
  private TextView time;
  private GoogleMap map;
  private SupportMapFragment mapFrag;
  private Playlist playlist;
  private MapService mapService;
  private NavigatorService navigatorService;
  private RelativeLayout relativeLayout;
  private SongsViewModel viewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_detail);
    time = findViewById(R.id.timeAmountDetail);
    title = findViewById(R.id.playlistTitleDetail);
    goToPlaylist = findViewById(R.id.openLinkButtonDetail);
    goToDrivify = findViewById(R.id.drivifyButton);
    relativeLayout = findViewById(R.id.listLayout);
    mapService = new MapService();
    playlistService = new PlaylistService(this, relativeLayout);
    navigatorService = new NavigatorService();
    playlist = Parcels.unwrap(getIntent().getParcelableExtra("playlist"));
    mapService.getRoute(playlist.getKeyOriginId(),
            playlist.getKeyDestinationId(),
            new MapService.mapServiceCallback() {
              @Override
              public void onDataGotRoute() {
                getDirections();
              }

              @Override
              public void onDataGotDistance() {
              }
            });
    mapService.getDistance(playlist.getKeyOriginId(),
            playlist.getKeyDestinationId(),
            new MapService.mapServiceCallback() {
              @Override
              public void onDataGotRoute() {
              }

              @Override
              public void onDataGotDistance() {
                time.setText("time to destination " + mapService.getTimeString());
              }
            });
    title.setText(playlist.getTitle());
    mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDetail);
    mapFrag.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
      }
    });
    goToPlaylist.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(navigatorService.openPlaylist(playlist.getKeyRedirectLink()));
      }
    });
    goToDrivify.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        intent.putExtra(Const.goToPlaylistKey, true);
        intent.putExtra(Const.playlistIDKey, playlist.getKeyPlaylistId());
        intent.putExtra(Const.playlistURIKey, playlist.getKeyUri());
        startActivity(intent);
      }
    });
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

  public void getDirections() {
    int zoomLevel = mapService.getZoom(Integer.parseInt((playlist.getKeyTimeTo())));
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapService.getFocusPointLatLng(), zoomLevel));
    map.addMarker(new MarkerOptions()
            .position(mapService.getOriginLatLng())
            .title("Start"));
    map.addMarker(new MarkerOptions()
            .position(mapService.getDestinationLatLng())
            .title("End"));
    map.addPolyline(mapService.getPolylineOptions());
  }
}