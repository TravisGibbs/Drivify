package com.example.spotifytest.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.spotifytest.Models.Playlist;
import com.example.spotifytest.Models.SongsViewModel;
import com.example.spotifytest.R;
import com.example.spotifytest.Services.MapService;
import com.example.spotifytest.Services.NavigatorService;
import com.example.spotifytest.Services.PlaylistService;
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
    playlistService = new PlaylistService(this,relativeLayout);
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
        intent.putExtra("goToPlaylist", true);
        intent.putExtra("playlistID", playlist.getKeyPlaylistId());
        intent.putExtra("playlistURI", playlist.getKeyUri());
        startActivity(intent);
      }
    });
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