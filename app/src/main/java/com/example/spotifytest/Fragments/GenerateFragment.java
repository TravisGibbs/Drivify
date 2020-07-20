package com.example.spotifytest.Fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.spotifytest.MainActivity;
import com.example.spotifytest.Models.Playlist;
import com.example.spotifytest.Models.SongFull;
import com.example.spotifytest.Models.SongSimplified;
import com.example.spotifytest.OnSwipeTouchListener;
import com.example.spotifytest.R;
import com.example.spotifytest.SearchActivity;
import com.example.spotifytest.Services.PlaylistService;
import com.example.spotifytest.Services.SongService;
import com.example.spotifytest.SongsViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Headers;


public class GenerateFragment extends Fragment {

    private static final int RESULT_OK = -1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int RESULT_CANCELED = 0;
    private static final String apiKey = "AIzaSyDmCIZvAzyQ5iO3s4Qw2GMJxu_vDjOXWCk";
    private static final String Tag = "generateFrag";
    private String radioButtonSelected = "";
    private TextView timeView;
    private TextView searchResults;
    private EditText destText;
    private EditText originText;
    private Button goToPlaylistButton;
    private Button makePlaylistButton;
    private Button findDistanceButton;
    private Button searchButton;
    private RadioGroup radioGroup;
    private RadioButton radioIncrease;
    private RadioButton radioDecrease;
    private RadioButton radioDance;
    private RelativeLayout relativeLayout;
    private SongService songService;
    private PlaylistService playlistService;
    private ArrayList<SongFull> allTracks;
    private SongsViewModel viewModel;
    private GoogleMap map;
    private Place origin;
    private Place destination;
    private int time = 0;
    private boolean clickFromOriginText = true;
    private ArrayList<String> customIdSongs;
    private ArrayList<String> customIdArtists;
    private StringBuilder currentSearchedObjects;
    SupportMapFragment mapFrag;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Places.initialize(view.getContext(), apiKey);
        PlacesClient placesClient = Places.createClient(view.getContext());
        relativeLayout = view.findViewById(R.id.generateFragmentLayout);
        playlistService = new PlaylistService(view.getContext(), relativeLayout);
        songService = new SongService(view.getContext(), relativeLayout);
        destText = view.findViewById(R.id.destinationTest);
        originText = view.findViewById(R.id.originText);
        LatLng originLatLng;
        LatLng destinationLatLng;
        findDistanceButton = view.findViewById(R.id.distanceButton);
        timeView = view.findViewById(R.id.timeText);
        searchResults = view.findViewById(R.id.searchObjects);
        goToPlaylistButton = view.findViewById(R.id.goToButton);
        makePlaylistButton = view.findViewById(R.id.makePlaylistButton);
        searchButton = view.findViewById(R.id.searchButton);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioIncrease = view.findViewById(R.id.radioIncrease);
        radioDecrease = view.findViewById(R.id.radioDecrease);
        radioDance = view.findViewById(R.id.radioDance);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("SPOTIFY", 0);
        viewModel = ViewModelProviders.of(this.getActivity()).get(SongsViewModel.class);
        customIdArtists = new ArrayList<>();
        customIdSongs = new ArrayList<>();
        currentSearchedObjects = new StringBuilder();
        currentSearchedObjects.append("Searched Artists and Songs:");
        
        mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getView().setVisibility(View.GONE);
        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
            }
        });

        goToPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPlaylist();
            }
        });

        makePlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postPlaylist();
            }
        });

        destText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFromOriginText = false;
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(view.getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        originText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFromOriginText = true;
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(view.getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        findDistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(origin !=null && destination !=null) {
                    getDistance(origin, destination);
                }
                else{
                    Snackbar.make(relativeLayout, "Select a route!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchActivity();
            }
        });

        radioIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        radioDance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        radioDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(view.getContext()) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                MainActivity main = (MainActivity) getActivity();
                main.getBottomNavigationView().setSelectedItemId(R.id.playlistAction);
            }
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                MainActivity main = (MainActivity) getActivity();
                main.getBottomNavigationView().setSelectedItemId(R.id.profileAction);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generate, container, false);
    }

    private ArrayList<SongFull> getTracks(int amount) {
        if (customIdSongs.isEmpty() && customIdArtists.isEmpty()) {
            songService.getRecentlyPlayedTracks(() -> {
                allTracks = songService.getSongFulls();
            }, amount);
        } else {
            songService.getSeedTracks(() -> {
                allTracks = songService.getSongFulls();
            }, customIdSongs,customIdArtists, amount);
        }
        return allTracks;
    }

    private void goToPlaylist() {
        String URL = playlistService.getPlaylistExternalLink();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(URL));
        startActivity(intent);
    }

    private void postPlaylist() {
        if(time > 0){
            radioGroup.setVisibility(View.GONE);
            allTracks = songService.getSongFulls();
            allTracks = chooseSongs(allTracks);
            viewModel.setSongList(allTracks);
            viewModel.setPlaylistService(playlistService);
            playlistService.addPlaylist(origin.getName() + " to " + destination.getName(), allTracks, time);
            startButtonSwap(goToPlaylistButton, makePlaylistButton);
        }
        else{
            Snackbar.make(relativeLayout, "Please choose a route!", Snackbar.LENGTH_SHORT).show();
            return;
        }
    }

    private void playlistParsePost() {
        Playlist playlist = new Playlist();
        playlist.setKeyOriginId(origin.getId());
        playlist.setKeyDestinationId(destination.getId());
        playlist.setKeyPlaylistId(playlistService.getPlaylistId());
        playlist.setKeyRedirectLink(playlistService.getPlaylistExternalLink());
        playlist.setUser(ParseUser.getCurrentUser());
        playlist.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(Tag, "post failed", e);
                }
                Log.i(Tag, "posted playlist");
            }
        });
        return;
    }

    public ArrayList<SongFull> chooseSongs(ArrayList<SongFull> songList){
        Set<SongFull> set = new HashSet<>(songList);
        songList.clear();
        songList.addAll(set);
        ArrayList<SongFull> newSongList = new ArrayList<>();
        int amountOfSongs = time / 100000;
        amountOfSongs = amountOfSongs + 2;
        if(amountOfSongs > 50){
            Snackbar.make(relativeLayout, "Drive too long for full playlists!", Snackbar.LENGTH_SHORT).show();
            amountOfSongs = 30;
        }
        if(radioButtonSelected.equals("Dance")) {
            Collections.sort(songList, SongFull.SongDanceComparator);
            return(addKSongsFromList(newSongList, songList, amountOfSongs));
        }
        if(radioButtonSelected.equals("Increase")) {
            Collections.sort(songList, SongFull.SongEnergyComparator);
            return(addKSongsFromList(newSongList, songList, amountOfSongs));
        }
        if(radioButtonSelected.equals("Increase")) {
            Collections.sort(songList, SongFull.SongEnergyComparator);
            Collections.reverse(songList);
            return(addKSongsFromList(newSongList, songList, amountOfSongs));
        }
        return(addKSongsFromList(newSongList, songList, amountOfSongs));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(Tag, "Place: " + place.getName() + ", " + place.getId());
                if (clickFromOriginText){
                    origin = place;
                    originText.setText(place.getName());
                } else {
                    destination = place;
                    destText.setText(place.getName());
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        if (requestCode == 2) {
            if (resultCode == 0) {
                return;
            }
            setSearchResults(data);
            if ((customIdArtists.size() + customIdSongs.size()) > 4) {
                if (!customIdArtists.isEmpty()) {
                    customIdArtists.remove(0);

                } else {
                    customIdSongs.remove(0);
                }
            }
            if (data.getBooleanExtra("isSong", false)) {
                customIdSongs.add(data.getStringExtra("id"));
            } else {
                customIdArtists.add(data.getStringExtra("id"));
            }
            for (int i = 0; i < customIdArtists.size(); i++) {
                Log.i(Tag,customIdArtists.get(i) + " ");
            }
            for (int i = 0; i < customIdSongs.size();i++) {
                Log.i(Tag,customIdSongs.get(i) + " ");
            }
        }
    }

    public ArrayList<SongFull> addKSongsFromList(ArrayList<SongFull> newSongList, ArrayList<SongFull> songList, int k){
        for(int i = 0; i < k - 1; i++){
            newSongList.add(songList.get(i));
        }
        return newSongList;
    }

    public void setSearchResults(Intent data) {
        if (customIdArtists.size() + customIdSongs.size() > 4) {
            int endOfLastObjectIndex = 0;
            for (int i = 0; i < currentSearchedObjects.length(); i++) {
                if (currentSearchedObjects.charAt(i) == ',') {
                    endOfLastObjectIndex = i;
                    break;
                }
            }
            currentSearchedObjects.replace(28, endOfLastObjectIndex, data.getStringExtra("name") + " ");
        } else {
            if (customIdArtists.size() + customIdSongs.size() < 1) {
                currentSearchedObjects.append(" ").append(data.getStringExtra("name")).append(" ");
            } else {
                currentSearchedObjects.append(", ").append(data.getStringExtra("name")).append(" ");
            }
        }
        searchResults.setText(currentSearchedObjects.toString());
    }

    public void getDirections(){
        StringBuilder url = new StringBuilder();
        url.append("https://maps.googleapis.com/maps/api/directions/json?origin=place_id:");
        url.append(origin.getId());
        url.append("&destination=place_id:");
        url.append(destination.getId());
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

    public void getDistance(Place a, Place b){
        getDirections();
        moveButtonOffScreenRight(searchButton);
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=place_id:"
                + a.getId() + "&destinations=place_id:"
                + b.getId() + "&key=AIzaSyDmCIZvAzyQ5iO3s4Qw2GMJxu_vDjOXWCk";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(Tag, "Time between places found.");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONObject help = (JSONObject) jsonObject.getJSONArray("rows").get(0);
                    JSONObject help2 = (JSONObject) help.getJSONArray("elements").get(0);
                    int minutes = 0;
                    String temp = help2.getJSONObject("duration").getString("text");
                    Log.i(Tag,"time to destination " + temp);
                    if (temp.contains("d")){
                        minutes += 1440;
                        temp = temp.substring(6);
                    }
                    if (temp.contains("h")) {
                        for (int i = 0; i < temp.length(); i++) {
                            if (temp.charAt(i) == 'h') {
                                minutes += Integer.parseInt(temp.substring(0, i - 1)) * 60;
                            }
                            if (temp.charAt(i) == 'r'){
                                temp = temp.substring(i+2);
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
                    int zoomLevel;
                    if (15 < minutes && minutes < 30) {
                        zoomLevel = 9;
                    }
                    else if (minutes < 120) {
                        zoomLevel = 7;
                    }
                    else if (minutes < 240) {
                        zoomLevel = 6;
                    } else {
                        zoomLevel = 3;
                    }
                    Log.i(Tag, "total minutes " + minutes);
                    time = minutes*60000;
                    allTracks = getTracks(30);
                    startButtonSwap(makePlaylistButton, findDistanceButton);
                    timeView.setText(help2.getJSONObject("duration").getString("text"));
                    timeView.setVisibility(View.VISIBLE);
                    try {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(origin.getLatLng(), zoomLevel));
                    } catch (Exception e) {
                        Log.e(Tag, "Error setting bounds", e);
                    }
                    mapFrag.getView().setVisibility(View.VISIBLE);
                    map.addMarker(new MarkerOptions()
                            .position(origin.getLatLng())
                            .title("Start"));
                    map.addMarker(new MarkerOptions()
                            .position(destination.getLatLng())
                            .title("End"));
                    searchResults.setVisibility(View.GONE);
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

    public void startButtonSwap(Button goUpButton, Button goRightButton) {
        ObjectAnimator animationButtonUp = ObjectAnimator.ofFloat(goUpButton, "translationY", -170f);
        animationButtonUp.setDuration(2000);
        moveButtonOffScreenRight(goRightButton);
        animationButtonUp.start();
    }

    public void moveButtonOffScreenRight(Button button){
        ObjectAnimator animationButtonRight = ObjectAnimator.ofFloat(button, "translationX", 1800f);
        animationButtonRight.setDuration(1500);
        animationButtonRight.start();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioDance:
                if (checked) {
                    radioButtonSelected = "Dance";
                }
                break;
            case R.id.radioIncrease:
                if (checked) {
                    radioButtonSelected = "Increase";
                }
                break;
            case R.id.radioDecrease:
                if (checked) {
                    radioButtonSelected = "Decrease";
                }
                break;
        }
        Log.i(Tag, "Radio button selected: " + radioButtonSelected);
    }

    public void startSearchActivity() {
        Intent intent = new Intent(getContext(), SearchActivity.class);
        startActivityForResult(intent, 2);
    }
}
