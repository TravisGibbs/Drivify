package com.example.spotifytest.Services;

import android.graphics.Color;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Headers;

public class MapService {

  public interface MyCallback {
    void onDataGotRoute();
    void onDataGotDistance();
  }

  private static final String apiKey = "AIzaSyDmCIZvAzyQ5iO3s4Qw2GMJxu_vDjOXWCk";
  private static final String Tag = "MapService";
  private PolylineOptions polylineOptions;
  private LatLng originLatLng;
  private LatLng destinationLatLng;
  private LatLng focusPointLatLng;

  public void getRoute(String originId, String destinationId, MyCallback callback) {
    StringBuilder url = new StringBuilder();
    url.append("https://maps.googleapis.com/maps/api/directions/json?origin=place_id:");
    url.append(originId);
    url.append("&destination=place_id:");
    url.append(destinationId);
    url.append("&key=");
    url.append(apiKey);
    AsyncHttpClient client = new AsyncHttpClient();
    client.get(url.toString(), new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Headers headers, JSON json) {
        Log.i(Tag, "search for directions succ");
        JSONObject jsonObject = json.jsonObject;
        try {
          JSONArray routes = (JSONArray) jsonObject.get("routes");
          jsonObject = (JSONObject) routes.get(0);
          jsonObject = (JSONObject) jsonObject.getJSONObject("overview_polyline");
          String polyline = jsonObject.getString("points");
          List<LatLng> latLngs = PolyUtil.decode(polyline);
          polylineOptions = new PolylineOptions()
                          .clickable(true)
                          .color(Color.parseColor("#1ED760"))
                          .addAll(latLngs);
          originLatLng = latLngs.get(0);
          focusPointLatLng = latLngs.get(latLngs.size()/2);
          destinationLatLng = latLngs.get(latLngs.size()-1);
          callback.onDataGotRoute();
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
        Log.e(Tag,"search for directions failed", throwable);
      }
    });
  }

  public LatLng getOriginLatLng() {
    return originLatLng;
  }

  public LatLng getDestinationLatLng() {
    return destinationLatLng;
  }

  public LatLng getFocusPointLatLng() {
    return focusPointLatLng;
  }

  public PolylineOptions getPolylineOptions() {
    return polylineOptions;
  }

  public int getMinutes(String temp) {
    int minutes = 0;
    if (temp.contains("d")){
      minutes += 1440;
      temp = temp.substring(6);
    }
    if (temp.contains("h")) {
      for (int i = 0; i < temp.length(); i++) {
        if (temp.charAt(i) == 'h') {
          minutes += Integer.parseInt(temp.substring(0, i - 1)) * 60;
        }
        if (temp.charAt(i) == 'r' && temp.charAt(i+1)!= 's'){
          temp = temp.substring(i+2);
          break;
        }
        if (temp.charAt(i) == 'r' && temp.charAt(i+1) == 's') {
          try {
            temp = temp.substring(i + 3);
          } catch (Exception e) {
            break;
          }
          break;
        }
      }
    }
    if (temp.contains("m")) {
      for (int i =0; i < temp.length(); i++) {
        if (temp.charAt(i) == 'm') {
          minutes += Integer.parseInt(temp.substring(0,i-1));
          break;
        }
      }
    }
    return minutes;
  }

  public int getZoom(int timeTo){
    int minutes = timeTo / 60000;
    if (minutes < 5) {
      return 12;
    } else if (minutes <= 15) {
      return 11;
    } else if (15 < minutes && minutes <= 30) {
      return 9;
    } else if (minutes < 60){
      return 8;
    } else if (minutes < 120) {
      return  7;
    } else if (minutes < 240) {
      return  6;
    } else {
      return  3;
    }
  }
}
