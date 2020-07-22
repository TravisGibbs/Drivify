package com.example.spotifytest.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.spotifytest.Adapters.PlaylistAdapter;
import com.example.spotifytest.Activities.MainActivity;
import com.example.spotifytest.Models.SongFull;
import com.example.spotifytest.OnSwipeTouchListener;
import com.example.spotifytest.R;
import com.example.spotifytest.Models.SongsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;


public class PlaylistFragment extends Fragment {

  private static final String Tag = "PlaylistFragment";
  private static final String CLIENT_ID = "16b8f7e96bbb4d12b021825527475319";
  private static final String REDIRECT_URI = "https://developer.spotify.com/dashboard";
  private SpotifyAppRemote mSpotifyAppRemote;
  private ArrayList<SongFull> allSongs = new ArrayList<>();
  private SongsViewModel viewModel;
  private RecyclerView rvPlaylist;
  private PlaylistAdapter playlistAdapter;
  private RelativeLayout relativeLayout;
  private TextView trackText;
  private LinearLayoutManager linearLayoutManager;
  private FloatingActionButton floatingActionButton;
  private TextView errorText;
  private Boolean playing = false;
  private int speed;
  private int pastSpeed;
  private AudioManager audioManager;
  private Track currentTrack;

  @SuppressLint("RestrictedApi")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    LocationManager lm = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
    audioManager = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);
    if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(view.getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
      Log.i(Tag, "Permission check");
    }
    LocationProvider provider = lm.getProvider(LocationManager.GPS_PROVIDER);
    errorText = view.findViewById(R.id.errorText);
    trackText = view.findViewById(R.id.SongText);
    trackText.setVisibility(View.GONE);
    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            10000, // 10 second interval
            10, // 10 meters
            new LocationListener() {
              @Override
              public void onLocationChanged(Location location) {
                if (location==null){
                  // if you can't get speed because reasons :)
                  errorText.setText("00 km/h");
                } else {
                  //int speed=(int) ((location.getSpeed()) is the standard which returns meters per second. In this example i converted it to kilometers per hour
                  speed = (int) ((location.getSpeed()*3600)/1000);
                  if (pastSpeed + 5 < speed) {
                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                  }
                  if (pastSpeed - 5 > speed) {
                    audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                  }
                  errorText.setText(speed+" km/h");
                  pastSpeed = speed;
                }
              }

              @Override
              public void onStatusChanged(String s, int i, Bundle bundle) {

              }

              @Override
              public void onProviderEnabled(String s) {

              }

              @Override
              public void onProviderDisabled(String s) {

              }
            });
    errorText = view.findViewById(R.id.errorText);
    rvPlaylist = view.findViewById(R.id.rvSongs);
    relativeLayout = view.findViewById(R.id.playlistLayout);
    viewModel = ViewModelProviders.of(this.getActivity()).get(SongsViewModel.class);
    floatingActionButton = view.findViewById(R.id.playButton);
    allSongs = viewModel.getSongList();
    if (allSongs.size() > 0) {
      setUpSpotifyRemote(view);
      floatingActionButton.setVisibility(View.VISIBLE);
      trackText.setVisibility(View.VISIBLE);
      errorText.setVisibility(View.GONE);
      linearLayoutManager = new LinearLayoutManager(view.getContext());
      playlistAdapter = new PlaylistAdapter(allSongs, view.getContext());
      rvPlaylist.setAdapter(playlistAdapter);
      rvPlaylist.setLayoutManager(linearLayoutManager);
      playlistAdapter.notifyDataSetChanged();
      floatingActionButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          playOrPause();
        }
      });
    }
    relativeLayout.setOnTouchListener(new OnSwipeTouchListener(view.getContext()) {
      @Override
      public void onSwipeRight() {
        super.onSwipeRight();
        MainActivity main = (MainActivity) getActivity();
        main.getBottomNavigationView().setSelectedItemId(R.id.generateAction);
      }
    });
  }

  private void playOrPause() {
    if (playing) {
      mSpotifyAppRemote.getPlayerApi().pause();
      floatingActionButton.setImageResource(R.drawable.play_button);
      playing = false;
    } else {
      mSpotifyAppRemote.getPlayerApi().resume();
      floatingActionButton.setImageResource(R.drawable.pause_icon);
      playing = true;
    }
  }

  private void setUpSpotifyRemote(View view) {
    ConnectionParams connectionParams =
            new ConnectionParams.Builder(CLIENT_ID)
                    .setRedirectUri(REDIRECT_URI)
                    .showAuthView(true)
                    .build();
    SpotifyAppRemote.connect(view.getContext(), connectionParams,
            new Connector.ConnectionListener() {

              @Override
              public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                mSpotifyAppRemote.getPlayerApi().play(viewModel.getPlaylistService().getPlaylistURI());
                floatingActionButton.setImageResource(R.drawable.pause_icon);
                playing = true;
                mSpotifyAppRemote.getPlayerApi()
                        .subscribeToPlayerState()
                        .setEventCallback(playerState -> {
                          final Track track = playerState.track;
                          if (track != null) {
                            trackText.setText(track.name + " by " + track.artist.name);
                          }
                        });
              }

              @Override
              public void onFailure(Throwable throwable) {
                Log.e("MainActivity", throwable.getMessage(), throwable);
                // Something went wrong when attempting to connect! Handle errors here
              }
            });


  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_list, container, false);
  }
}
