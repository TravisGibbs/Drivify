package com.example.spotifytest.Fragments;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.spotifytest.Models.SongFull;
import com.example.spotifytest.Models.SongSimplified;
import com.example.spotifytest.R;
import com.example.spotifytest.Services.PlaylistService;
import com.example.spotifytest.Services.SongService;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Headers;


public class GenerateFragment extends Fragment {

    private static final int RESULT_OK = -1;
    private static final int RESULT_CANCELED = 0;
    private TextView userView;
    private TextView timeView;
    private Button likeSongs;
    private Button findSongs;
    public boolean origin = true;
    private EditText destText;
    private EditText originText;
    private Button goToPlaylistButton;
    private Button makePlaylistButton;
    private Button findDistance;
    private SongSimplified songSimplified;
    private RelativeLayout relativeLayout;
    public int i;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    public Place Start;
    public Place End;
    public int time = 0;


    String Tag = "generateFrag";
    String apiKey = "AIzaSyDmCIZvAzyQ5iO3s4Qw2GMJxu_vDjOXWCk";

    private SongService songService;
    private PlaylistService playlistService;
    private ArrayList<SongFull> allTracks;

    public GenerateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Places.initialize(getContext(), apiKey);
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(getContext());
        i =0;
        relativeLayout = view.findViewById(R.id.generateFragmentLayout);
        playlistService = new PlaylistService(getContext(), relativeLayout);
        songService = new SongService(getContext(), relativeLayout);
        destText = view.findViewById(R.id.destinationTest);
        originText = view.findViewById(R.id.originText);
        findDistance = view.findViewById(R.id.distanceButton);
        timeView = view.findViewById(R.id.timeText);
        //songView = view.findViewById(R.id.songText);
        //likeSongs = view.findViewById(R.id.likeSong);
        //findSongs = view.findViewById(R.id.findSong);
        //likeSongs.setVisibility(View.GONE);
        goToPlaylistButton = view.findViewById(R.id.goToButton);
        makePlaylistButton = view.findViewById(R.id.makePlaylistButton);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SPOTIFY", 0);
        Log.i(Tag, sharedPreferences.getString("user_name", "defauly"));

        //getTracks(20);

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
                origin = false;
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        originText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                origin = true;
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        findDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Start!=null && End!=null) {
                    getDistance(Start, End);
                }
                else{
                    Snackbar.make(relativeLayout, "Select a route!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_generate, container, false);
    }

    private ArrayList<SongFull> getTracks(int amount) {
        songService.getRecentlyPlayedTracks(() -> {
            allTracks = songService.getSongFulls();
            Log.i(Tag, String.valueOf(allTracks.size()));
        },amount);
        return allTracks;
    }
    private void updateSong() {
        if (allTracks.size() > i) {
            //songView.setText(recentlyPlayedTracks.get(i).getName());
            songSimplified = allTracks.get(i);
            likeSongs.setVisibility(View.VISIBLE);
            Snackbar.make(relativeLayout, "SongSimplified Grabbed!", Snackbar.LENGTH_SHORT).show();
            i++;
        }
    }

    private void goToPlaylist(){
        String URL = playlistService.playlistExternalLink;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(URL));
        startActivity(intent);
    }

    private void addSong(){
        songService.addSongToLibrary(allTracks.get(i-1));

    }

    private void postPlaylist(){
        if(time > 0){
            allTracks = songService.getSongFulls();
            playlistService.addPlaylist(Start.getName() + " to " + End.getName(), allTracks, time);
            ObjectAnimator animation = ObjectAnimator.ofFloat(goToPlaylistButton, "translationY", -360f);
            ObjectAnimator animation2 = ObjectAnimator.ofFloat(makePlaylistButton, "translationX",1800f);
            animation.setDuration(2000);
            animation2.setDuration(1500);
            animation2.start();
            animation.start();
        }
        else{
            Snackbar.make(relativeLayout, "Please choose a route!", Snackbar.LENGTH_SHORT).show();
            return;
        }
        //recentlyPlayedTracks = selectSongs(recentlyPlayedTracks);
        Log.i(Tag, String.valueOf(allTracks.size()));
        for (SongSimplified songSimplified : allTracks) {
            System.out.println(songSimplified.getName());
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(Tag, "Place: " + place.getName() + ", " + place.getId());
                if (origin){
                    Start = place;
                    originText.setText(place.getName());
                }
                else {
                    End = place;
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getDistance(Place a, Place b){
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=place_id:" + a.getId() + "&destinations=place_id:"+ b.getId() +"&key=AIzaSyDmCIZvAzyQ5iO3s4Qw2GMJxu_vDjOXWCk";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(Tag, "succ");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONObject help = (JSONObject) jsonObject.getJSONArray("rows").get(0);
                    JSONObject help2 = (JSONObject) help.getJSONArray("elements").get(0);
                    int minutes = 0;
                    String temp = help2.getJSONObject("duration").getString("text");
                    Log.i(Tag,"time to destination " + temp);
                    if(temp.contains("h")){
                        for(int i=0; i<temp.length();i++){
                            if(temp.charAt(i) == ' '){
                                minutes = Integer.parseInt(temp.substring(0, i))*60;
                            }
                            if(temp.charAt(i) == 's'){
                                if(temp.contains("m")){
                                    temp = temp.substring(i + 1);
                                    break;
                                }
                            }
                        }
                    }

                    if(temp.contains("m")){
                        for (int i = 1; i < temp.length(); i++) {
                            if(temp.charAt(i)==' '){
                                minutes = minutes + Integer.parseInt(temp.substring(0,i));
                            }
                        }
                    }
                    Log.i(Tag, "total minutes " + minutes);
                    time = minutes*60000;
                    Log.i(Tag, "totak ms " + String.valueOf(time));
                    int amountOfSongs = time/120000;
                    amountOfSongs = amountOfSongs +5;
                    if(amountOfSongs>50){
                        Snackbar.make(relativeLayout, "Drive too long for full playlists!", Snackbar.LENGTH_SHORT).show();
                        amountOfSongs =50;
                    }
                    allTracks = getTracks(amountOfSongs);
                    ObjectAnimator animation = ObjectAnimator.ofFloat(makePlaylistButton, "translationY", -200f);
                    ObjectAnimator animation2 = ObjectAnimator.ofFloat(findDistance, "translationX",1800f);
                    animation.setDuration(2000);
                    animation2.setDuration(1500);
                    animation2.start();
                    animation.start();
                    timeView.setText(temp);


                    Log.i(Tag, "amount of songs " + String.valueOf(amountOfSongs));
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

    public ArrayList<SongSimplified> selectSongs(ArrayList<SongSimplified> songSimplifieds){
        int n = songSimplifieds.size();
        int k = time;
        int prevDif = 0;
        int currDif = 0;
        int i = 0;
        int j = 0;
        int curSum = 0;
        int[] result = new int[]{-1, -1, k};
        int[] resultTemp = result;
        while(i<=j && j<n){
            curSum += songSimplifieds.get(j).getDuration_ms();
            prevDif = currDif;
            currDif = k - Math.abs(curSum);

            if(currDif <= 0){
                if(Math.abs(currDif) < Math.abs(prevDif)){
                    resultTemp[0] = i;
                    resultTemp[1] = j;
                    resultTemp[2] = currDif;
                }
                else{
                    resultTemp[0] = i;
                    resultTemp[1] = j-1;
                    resultTemp[2] = prevDif;
                }
                curSum = curSum - songSimplifieds.get(i).getDuration_ms() + songSimplifieds.get(j).getDuration_ms();
                i += 1;
            }
            else{
                resultTemp[0] = i;
                resultTemp[1] = j;
                resultTemp[2] = currDif;
                j+=1;
            }
            if(Math.abs(resultTemp[2]) < Math.abs(result[2])) {
                result = resultTemp;
            }
        }

        ArrayList<SongSimplified> pickedTracks = (ArrayList<SongSimplified>) songSimplifieds.subList(result[0],result[1]);


        return pickedTracks;
    }
}