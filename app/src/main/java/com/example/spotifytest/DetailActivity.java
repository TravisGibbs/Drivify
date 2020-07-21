package com.example.spotifytest;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
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
  SupportMapFragment mapFrag;
  private Playlist playlist;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    time = findViewById(R.id.timeAmountDetail);
    title = findViewById(R.id.playlistTitleDetail);
    goToPlaylist = findViewById(R.id.openLinkButtonDetail);
    playlist = Parcels.unwrap(getIntent().getParcelableExtra("playlist"));
    mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapDetail);
    mapFrag.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
      }
    });
    getDistance(playlist.getKeyOriginId(), playlist.getKeyDestinationId());
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

  public void getDirections(){
    StringBuilder url = new StringBuilder();
    url.append("https://maps.googleapis.com/maps/api/directions/json?origin=place_id:");
    url.append(playlist.getKeyOriginId());
    url.append("&destination=place_id:");
    url.append(playlist.getKeyDestinationId());
    url.append("&key=");
    url.append(apiKey);
    AsyncHttpClient client = new AsyncHttpClient();
    client.get(url.toString(), new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Headers headers, JSON json) {
        Log.i(Tag,"search for directions succ");
        JSONObject jsonObject = json.jsonObject;
        try {
          JSONArray routes = (JSONArray) jsonObject.get("routes");
          jsonObject = (JSONObject) routes.get(0);
          jsonObject = (JSONObject) jsonObject.getJSONObject("overview_polyline");
          String polyline = jsonObject.getString("points");
          List<LatLng> latLngs = PolyUtil.decode(polyline);
          Polyline polyline1 = map.addPolyline(new PolylineOptions()
                  .clickable(true)
                  .color(Color.parseColor("#1ED760"))
                  .addAll(latLngs));
          MapSettings mapSettings = new MapSettings();
          int zoomLevel = mapSettings.getZoom(Integer.parseInt((playlist.getKeyTimeTo())));
          try {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(latLngs.size()/2), zoomLevel));
            map.addMarker(new MarkerOptions()
                    .position(latLngs.get(0))
                    .title("Start"));
            map.addMarker(new MarkerOptions()
                    .position(latLngs.get(latLngs.size()-1))
                    .title("End"));
          } catch (Exception e) {
            Log.e(Tag, "Error setting bounds", e);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
        Log.e(Tag,"search for directions failed", throwable);
      }
    });
    Log.i(Tag, url.toString());
  }

  public void getDistance(String OriginID, String DestinationID){
    getDirections();
    String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=place_id:"
            + OriginID + "&destinations=place_id:"
            + DestinationID + "&key=AIzaSyDmCIZvAzyQ5iO3s4Qw2GMJxu_vDjOXWCk";
    AsyncHttpClient client = new AsyncHttpClient();
    client.get(url, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Headers headers, JSON json) {
        Log.d(Tag, "Time between places found.");
        JSONObject jsonObject = json.jsonObject;
        try {
          JSONObject rows = (JSONObject) jsonObject.getJSONArray("rows").get(0);
          JSONObject elements = (JSONObject) rows.getJSONArray("elements").get(0);
          String temp = elements.getJSONObject("duration").getString("text");
          Log.i(Tag,"time to destination " + temp);
          time.setText("time to destination " + temp);
          title.setText(playlist.getTitle());
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
        Log.d(Tag, "fail");
      }
    });
  }
}