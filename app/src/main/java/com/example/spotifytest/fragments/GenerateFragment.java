package com.example.spotifytest.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.example.spotifytest.activities.MainActivity;
import com.example.spotifytest.services.NavigatorService;
import com.example.spotifytest.services.MapService;
import com.example.spotifytest.models.SongFull;
import com.example.spotifytest.OnSwipeTouchListener;
import com.example.spotifytest.R;
import com.example.spotifytest.activities.SearchActivity;
import com.example.spotifytest.services.PlaylistService;
import com.example.spotifytest.services.SongService;
import com.example.spotifytest.models.SongsViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenerateFragment extends Fragment {

    private static final int RESULT_OK = -1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int RESULT_CANCELED = 0;
    private static final String apiKey = "AIzaSyDmCIZvAzyQ5iO3s4Qw2GMJxu_vDjOXWCk";
    private static final String Tag = "generateFrag";
    private String radioButtonSelected = "";
    private TextView timeView;
    private ChipGroup searchResults;
    private Chip chip0;
    private Chip chip1;
    private Chip chip2;
    private Chip chip3;
    private Chip chip4;
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
    private NavigatorService navigatorService;
    private MapService mapService;
    private ArrayList<SongFull> allTracks;
    private SongsViewModel viewModel;
    private GoogleMap map;
    private Place origin;
    private Place destination;
    private int time = 0;
    private int amountSongs;
    private String timeString;
    private boolean clickFromOriginText = true;
    private ArrayList<String> customIdSongs;
    private ArrayList<String> customIdArtists;
    private ArrayList<String> currentChipId = new ArrayList<>();
    private StringBuilder currentSearchedObjects;
    private SupportMapFragment mapFrag;
    private SwipeRefreshLayout swipeContainer;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModel = ViewModelProviders.of(this.getActivity()).get(SongsViewModel.class);
    }

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
        goToPlaylistButton = view.findViewById(R.id.goToButton);
        makePlaylistButton = view.findViewById(R.id.makePlaylistButton);
        searchButton = view.findViewById(R.id.searchButton);
        searchResults = view.findViewById(R.id.searchObjects);
        chip0 = view.findViewById(R.id.chip0);
        chip1 = view.findViewById(R.id.chip1);
        chip2 = view.findViewById(R.id.chip2);
        chip3 = view.findViewById(R.id.chip3);
        chip4 = view.findViewById(R.id.chip4);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioIncrease = view.findViewById(R.id.radioIncrease);
        radioDecrease = view.findViewById(R.id.radioDecrease);
        radioDance = view.findViewById(R.id.radioDance);
        sliderDance = view.findViewById(R.id.danceSlider);
        sliderEnergy = view.findViewById(R.id.energySlider);
        sliderValence = view.findViewById(R.id.valenceSlider);
        sliderDance.setValue(0.5f);
        sliderEnergy.setValue(0.5f);
        sliderValence.setValue(0.5f);
        danceLabel = view.findViewById(R.id.danceLabel);
        energyLabel = view.findViewById(R.id.energyLabel);
        valenceLabel = view.findViewById(R.id.valenceLabel);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("SPOTIFY", 0);
        customIdArtists = new ArrayList<>();
        customIdSongs = new ArrayList<>();
        currentSearchedObjects = new StringBuilder();
        currentSearchedObjects.append("Searched Artists and Songs:");
        navigatorService = new NavigatorService();
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
                if (customIdSongs.size() + customIdArtists.size() >= 5) {
                    Snackbar.make(relativeLayout, "Remove an artist and try again", Snackbar.LENGTH_SHORT).show();
                } else {
                    startSearchActivity();
                }
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

        for (int i = 0; i < 5; i++) {
            currentChipId.add(i, "");
        }

        chip0.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chip0.setVisibility(View.GONE);
                chip0.setText("");
                if (customIdArtists.contains(currentChipId.get(0))) {
                    for (int i = 0; i < customIdArtists.size(); i++) {
                        if (customIdArtists.get(i).equals(currentChipId.get(0))) {
                            customIdArtists.remove(i);
                            currentChipId.set(0, "");
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < customIdSongs.size(); i++) {
                        if (customIdSongs.get(i).equals(currentChipId.get(0))) {
                            customIdSongs.remove(i);
                            currentChipId.set(0, "");
                            break;
                        }
                    }
                }
            }
        });

        chip1.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chip1.setVisibility(View.GONE);
                chip1.setText("");
                if (customIdArtists.contains(currentChipId.get(1))) {
                    for (int i = 0; i < customIdArtists.size(); i++) {
                        if (customIdArtists.get(i).equals(currentChipId.get(1))) {
                            customIdArtists.remove(i);
                            currentChipId.set(1, "");
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < customIdSongs.size(); i++) {
                        if (customIdSongs.get(i).equals(currentChipId.get(1))) {
                            customIdSongs.remove(i);
                            currentChipId.set(1, "");
                            break;
                        }
                    }
                }
            }
        });

        chip2.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chip2.setVisibility(View.GONE);
                chip2.setText("");
                if (customIdArtists.contains(currentChipId.get(2))) {
                    for (int i = 0; i < customIdArtists.size(); i++) {
                        if (customIdArtists.get(i).equals(currentChipId.get(2))) {
                            customIdArtists.remove(i);
                            currentChipId.set(2, "");
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < customIdSongs.size(); i++) {
                        if (customIdSongs.get(i).equals(currentChipId.get(2))) {
                            customIdSongs.remove(i);
                            currentChipId.set(2, "");
                            break;
                        }
                    }
                }
            }
        });

        chip3.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chip3.setVisibility(View.GONE);
                chip3.setText("");
                if (customIdArtists.contains(currentChipId.get(3))) {
                    for (int i = 0; i < customIdArtists.size(); i++) {
                        if (customIdArtists.get(i).equals(currentChipId.get(3))) {
                            customIdArtists.remove(i);
                            currentChipId.set(3, "");
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < customIdSongs.size(); i++) {
                        if (customIdSongs.get(i).equals(currentChipId.get(3))) {
                            customIdSongs.remove(i);
                            currentChipId.set(3, "");
                            break;
                        }
                    }
                }
            }
        });

        chip4.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chip4.setVisibility(View.GONE);
                chip4.setText("");
                if (customIdArtists.contains(currentChipId.get(4))) {
                    for (int i = 0; i < customIdArtists.size(); i++) {
                        if (customIdArtists.get(i).equals(currentChipId.get(4))) {
                            customIdArtists.remove(i);
                            currentChipId.set(4, "");
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < customIdSongs.size(); i++) {
                        if (customIdSongs.get(i).equals(currentChipId.get(4))) {
                            customIdSongs.remove(i);
                            currentChipId.set(4, "");
                            break;
                        }
                    }
                }
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
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity main = (MainActivity) getActivity();
                main.getBottomNavigationView().setSelectedItemId(R.id.generateAction);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.colorPrimary);
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
        if (amount < 101) {
            songService.getSeedTracks(() -> {
            }, customIdSongs, customIdArtists, amount, danceValue, energyValue, loudnessValue, new SongService.songServiceCallback() {
                /* this call back is triggered when all songs full version are found allowing
                for the screen to move with the knowledge that all songs are found and that the post
                function is ready
                */
                @Override
                public void onSearchFinish(boolean found) {
                    if (found) {
                        allTracks = songService.getSongFulls();
                        updateScreen();
                    } else {
                        Snackbar.make(relativeLayout,
                                "The filter provided couldn't generate enough songs",
                                Snackbar.LENGTH_SHORT).show();
                        resetView();
                    }
                }
            });
        } else if ((customIdArtists.size() + customIdSongs.size()) * 100 > amount) {
            allTracks = new ArrayList<>();
            int amountPerSearch = amount / (customIdSongs.size() + customIdArtists.size()) + 1;
            for (int i = 0; i < customIdArtists.size(); i++) {
                ArrayList<String> arrayList = new ArrayList<>();
                ArrayList<String> blankList = new ArrayList<>();
                arrayList.add(customIdArtists.get(i));
                songService.getSeedTracks(() -> {
                }, blankList, arrayList, amountPerSearch, danceValue, energyValue, loudnessValue, new SongService.songServiceCallback() {
                    @Override
                    public void onSearchFinish(boolean found) {
                        if (found) {
                            if (songService.getTempCheck().size() >= amount) {
                                updateScreen();
                            }
                        } else {
                            Snackbar.make(relativeLayout,
                                    "The filter provided couldn't generate enough songs",
                                    Snackbar.LENGTH_SHORT).show();
                            resetView();
                        }
                    }
                });
            }
            for (int i = 0; i < customIdSongs.size(); i++) {
                ArrayList<String> arrayList = new ArrayList<>();
                ArrayList<String> blankList = new ArrayList<>();
                arrayList.add(customIdSongs.get(i));
                songService.getSeedTracks(() -> {
                }, arrayList, blankList, amountPerSearch, danceValue, energyValue, loudnessValue, new SongService.songServiceCallback() {
                    @Override
                    public void onSearchFinish(boolean found) {
                        if (found) {
                            if (songService.getTempCheck().size() >= amount) {
                                updateScreen();
                            }
                        } else {
                            Snackbar.make(relativeLayout,
                                    "The filter provided couldn't generate enough songs",
                                    Snackbar.LENGTH_SHORT).show();
                            resetView();
                        }
                    }
                });
            }
        } else if (amount > 500) {
            Snackbar.make(relativeLayout,
                    "Trip too long for app in curent version",
                    Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(relativeLayout,
                    "Add more songs or/and artists and try again",
                    Snackbar.LENGTH_SHORT).show();
        }
        return allTracks;
    }

    private void resetView() {
        customIdArtists.clear();
        customIdSongs.clear();
        chip0.setVisibility(View.GONE);
        chip1.setVisibility(View.GONE);
        chip2.setVisibility(View.GONE);
        chip3.setVisibility(View.GONE);
        chip4.setVisibility(View.GONE);
        chip0.setText("");
        chip1.setText("");
        chip2.setText("");
        chip3.setText("");
        chip4.setText("");
        sliderDance.setValue(.5f);
        sliderEnergy.setValue(.5f);
        sliderValence.setValue(.5f);
    }

    private void goToPlaylist() {
        startActivity(navigatorService.openPlaylist(playlistService.getPlaylistExternalLink()));
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
        for(int i = 0; i < k; i++){
            newSongList.add(songList.get(i));
        }
        return newSongList;
    }

    public void setSearchResults(Intent data) {
        if (chip0.getText().toString().equals("")) {
            chip0.setText(data.getStringExtra("name"));
            chip0.setVisibility(View.VISIBLE);
            currentChipId.set(0, data.getStringExtra("id"));
        } else if (chip1.getText().toString().equals("")) {
            chip1.setText(data.getStringExtra("name"));
            chip1.setVisibility(View.VISIBLE);
            currentChipId.set(1, data.getStringExtra("id"));
        } else if (chip2.getText().toString().equals("")) {
            chip2.setText(data.getStringExtra("name"));
            chip2.setVisibility(View.VISIBLE);
            currentChipId.set(2, data.getStringExtra("id"));
        } else if (chip3.getText().toString().equals("")) {
            chip3.setText(data.getStringExtra("name"));
            chip3.setVisibility(View.VISIBLE);
            currentChipId.set(3, data.getStringExtra("id"));
        } else if (chip4.getText().toString().equals("")) {
            chip4.setText(data.getStringExtra("name"));
            chip4.setVisibility(View.VISIBLE);
            currentChipId.set(4, data.getStringExtra("id"));
        }
    }

    public void getDistance(Place a, Place b){
        if (customIdArtists.size() + customIdSongs.size() < 1){
            Snackbar.make(relativeLayout, "Select an artist or song!", Snackbar.LENGTH_SHORT).show();
            return;
        }
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=place_id:"
                + a.getId() + "&destinations=place_id:"
                + b.getId() + "&key=AIzaSyDmCIZvAzyQ5iO3s4Qw2GMJxu_vDjOXWCk";
        mapService.getDistance(a.getId(), b.getId(),
                new MapService.mapServiceCallback() {
            @Override
            public void onDataGotRoute() {
            }
            @Override
            public void onDataGotDistance() {
                timeString = mapService.getTimeString();
                Log.i(Tag,"time to destination " + timeString);
                int minutes = mapService.getMinutes(timeString);
                time = minutes * 60000;
                amountSongs = time / 210000;
                amountSongs = amountSongs + 2;
                if (amountSongs > 500) {
                    Snackbar.make(relativeLayout, "Drive too long for playlist!", Snackbar.LENGTH_SHORT).show();
                    amountSongs = 500;
                }
                allTracks = getTracks(amountSongs);
            }
        });
    }

    public void updateScreen() {
        startButtonSwap(makePlaylistButton, getSongsButton);
        timeView.setText(timeString);
        timeView.setVisibility(View.VISIBLE);
        int zoomLevel = mapService.getZoom(time);
        mapService.getRoute(origin.getId(), destination.getId(), new MapService.mapServiceCallback() {
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
            @Override
            public void onDataGotDistance() {
            }
        });
        moveButtonOffScreenRight(searchButton);
        mapFrag.getView().setVisibility(View.VISIBLE);
        searchResults.setVisibility(View.GONE);
        originText.setVisibility(View.GONE);
        destText.setVisibility(View.GONE);
        danceLabel.setVisibility(View.GONE);
        energyLabel.setVisibility(View.GONE);
        valenceLabel.setVisibility(View.GONE);
        sliderDance.setVisibility(View.GONE);
        sliderEnergy.setVisibility(View.GONE);
        sliderValence.setVisibility(View.GONE);
        radioGroup.setVisibility(View.VISIBLE);
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
