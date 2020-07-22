package com.example.spotifytest.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.spotifytest.Models.Playlist;
import com.example.spotifytest.R;
import com.example.spotifytest.Services.MapService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class DetailActivity extends AppCompatActivity {

  private static final String apiKey = "AIzaSyDmCIZvAzyQ5iO3s4Qw2GMJxu_vDjOXWCk";
  private static final String Tag = "detailActivity";
  private TextView title;
  private Button goToPlaylist;
  private TextView time;
  private GoogleMap map;
  private SupportMapFragment mapFrag;
  private Playlist playlist;
  private MapService mapService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    time = findViewById(R.id.timeAmountDetail);
    title = findViewById(R.id.playlistTitleDetail);
    goToPlaylist = findViewById(R.id.openLinkButtonDetail);
    mapService = new MapService();
    playlist = Parcels.unwrap(getIntent().getParcelableExtra("playlist"));
    mapService.getRoute(playlist.getKeyOriginId(), playlist.getKeyDestinationId(), new MapService.MyCallback() {
      @Override
      public void onDataGotRoute() {
        getDirections();
      }

      @Override
      public void onDataGotDistance() {
      }
    });
    mapService.getDistance(playlist.getKeyOriginId(),
            playlist.getKeyDestinationId(), new MapService.MyCallback() {
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
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(playlist.getKeyRedirectLink()));
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