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
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.spotifytest.MainActivity;
import com.example.spotifytest.Services.MapService;
import com.example.spotifytest.Models.SongFull;
import com.example.spotifytest.OnSwipeTouchListener;
import com.example.spotifytest.R;
import com.example.spotifytest.SearchActivity;
import com.example.spotifytest.Services.PlaylistService;
import com.example.spotifytest.Services.SongService;
import com.example.spotifytest.SongsViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.PolyUtil;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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
    private TextView danceLabel;
    private TextView energyLabel;
    private TextView valenceLabel;
    private EditText destText;
    private EditText originText;
    private Button goToPlaylistButton;
    private Button makePlaylistButton;
    private Button getSongsButton;
    private Button searchButton;
    private RadioGroup radioGroup;
    private RadioButton radioIncrease;
    private RadioButton radioDecrease;
    private RadioButton radioDance;
    private Slider sliderDance;
    private Slider sliderEnergy;
    private Slider sliderValence;
    private RelativeLayout relativeLayout;
    private SongService songService;
    private PlaylistService playlistService;
    private MapService mapService;
    private ArrayList<SongFull> allTracks;
    private SongsViewModel viewModel;
    private GoogleMap map;
    private Place origin;
    private Place destination;
    private int time = 0;
    private int amountSongs;
    private boolean clickFromOriginText = true;
    private ArrayList<String> customIdSongs;
    private ArrayList<String> customIdArtists;
    private StringBuilder currentSearchedObjects;
    private SupportMapFragment mapFrag;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Places.initialize(view.getContext(), apiKey);
        relativeLayout = view.findViewById(R.id.generateFragmentLayout);
        playlistService = new PlaylistService(view.getContext(), relativeLayout);
        songService = new SongService(view.getContext(), relativeLayout);
        destText = view.findViewById(R.id.destinationTest);
        originText = view.findViewById(R.id.originText);
        getSongsButton = view.findViewById(R.id.distanceButton);
        timeView = view.findViewById(R.id.timeText);
        searchResults = view.findViewById(R.id.searchObjects);
        goToPlaylistButton = view.findViewById(R.id.goToButton);
        makePlaylistButton = view.findViewById(R.id.makePlaylistButton);
        searchButton = view.findViewById(R.id.searchButton);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioIncrease = view.findViewById(R.id.radioIncrease);
        radioDecrease = view.findViewById(R.id.radioDecrease);
        radioDance = view.findViewById(R.id.radioDance);
        sliderDance = view.findViewById(R.id.danceSlider);
        sliderEnergy = view.findViewById(R.id.energySlider);
        sliderValence = view.findViewById(R.id.valenceSlider);
        danceLabel = view.findViewById(R.id.danceLabel);
        energyLabel = view.findViewById(R.id.energyLabel);
        valenceLabel = view.findViewById(R.id.valenceLabel);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("SPOTIFY", 0);
        viewModel = ViewModelProviders.of(this.getActivity()).get(SongsViewModel.class);
        customIdArtists = new ArrayList<>();
        customIdSongs = new ArrayList<>();
        currentSearchedObjects = new StringBuilder();
        currentSearchedObjects.append("Searched Artists and Songs:");
        mapService = new MapService();
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

        getSongsButton.setOnClickListener(new View.OnClickListener() {
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
        Float danceValue = sliderDance.getValue();
        Float energyValue = sliderEnergy.getValue();
        Float loudnessValue = sliderValence.getValue();
        if (customIdSongs.isEmpty() && customIdArtists.isEmpty()) {
            songService.getRecentlyPlayedTracks(() -> {
                allTracks = songService.getSongFulls();
            }, amount);
        } else {
            songService.getSeedTracks(() -> {
                allTracks = songService.getSongFulls();
            }, customIdSongs, customIdArtists, amount, danceValue, energyValue, loudnessValue);
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
            playlistService.addPlaylist(origin.getName() + " to " + destination.getName(), allTracks, time, origin, destination);
            startButtonSwap(goToPlaylistButton, makePlaylistButton);
        }
        else{
            Snackbar.make(relativeLayout, "Please choose a route!", Snackbar.LENGTH_SHORT).show();
            return;
        }
    }

    public ArrayList<SongFull> chooseSongs(ArrayList<SongFull> songList){
        Set<SongFull> set = new HashSet<>(songList);
        songList.clear();
        songList.addAll(set);
        ArrayList<SongFull> newSongList = new ArrayList<>();
        if(radioButtonSelected.equals("Dance")) {
            Collections.sort(songList, SongFull.SongDanceComparator);
            return(addKSongsFromList(newSongList, songList, amountSongs));
        }
        if(radioButtonSelected.equals("Increase")) {
            Collections.sort(songList, SongFull.SongEnergyComparator);
            return(addKSongsFromList(newSongList, songList, amountSongs));
        }
        if(radioButtonSelected.equals("Increase")) {
            Collections.sort(songList, SongFull.SongEnergyComparator);
            Collections.reverse(songList);
            return(addKSongsFromList(newSongList, songList, amountSongs));
        }
        return(addKSongsFromList(newSongList, songList, amountSongs));
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

    public void getDistance(Place a, Place b){
        if (customIdArtists.size() + customIdSongs.size() < 1){
            Snackbar.make(relativeLayout, "Select an artist or song!", Snackbar.LENGTH_SHORT).show();
            return;
        }
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
                    String temp = help2.getJSONObject("duration").getString("text");
                    Log.i(Tag,"time to destination " + temp);
                    int minutes = mapService.getMinutes(temp);
                    time = minutes * 60000;
                    int zoomLevel = mapService.getZoom(time);
                    amountSongs = time / 100000;
                    amountSongs = amountSongs + 2;
                    if (amountSongs > 100) {
                        Snackbar.make(relativeLayout, "Drive too long for full playlists!", Snackbar.LENGTH_SHORT).show();
                        amountSongs = 100;
                    }
                    allTracks = getTracks(amountSongs);
                    startButtonSwap(makePlaylistButton, getSongsButton);
                    timeView.setText(help2.getJSONObject("duration").getString("text"));
                    timeView.setVisibility(View.VISIBLE);

                    mapService.getRoute(origin.getId(), destination.getId(), new MapService.MyCallback() {
                        @Override
                        public void onDataGotRoute() {
                            try {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapService.getFocusPointLatLng(), zoomLevel));
                                map.addMarker(
                                        new MarkerOptions()
                                                .position(mapService.getOriginLatLng())
                                                .title("Start"));
                                map.addMarker(
                                        new MarkerOptions()
                                                .position(mapService.getDestinationLatLng())
                                                .title("End"));
                                map.addPolyline(mapService.getPolylineOptions());
                            } catch (Exception e) {
                                Log.e(Tag, "Error setting bounds", e);
                            }
                        }
                    });
                    mapFrag.getView().setVisibility(View.VISIBLE);
                    searchResults.setVisibility(View.GONE);
                    originText.setVisibility(View.GONE);
                    destText.setVisibility(View.GONE);
                    danceLabel.setVisibility(View.GONE);
                    energyLabel.setVisibility(View.GONE);
                    valenceLabel.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(Tag, "failed getting directions");
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
