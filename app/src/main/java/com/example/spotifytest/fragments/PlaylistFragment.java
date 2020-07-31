package com.example.spotifytest.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifytest.adapters.PlaylistAdapter;
import com.example.spotifytest.activities.MainActivity;
import com.example.spotifytest.models.Const;
import com.example.spotifytest.models.SongFull;
import com.example.spotifytest.OnSwipeTouchListener;
import com.example.spotifytest.R;
import com.example.spotifytest.models.SongsViewModel;
import com.example.spotifytest.services.AlgorithmService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class PlaylistFragment extends Fragment {

  private static final String Tag = "PlaylistFragment";
  private static final double DEFAULT_THRESHOLD = 1;
  private static final double DEFAULT_INFLUENCE = .5;
  private static final int DEFAULT_LAG = 10;
  private static final int DEFAULT_METERS_BETWEEN_SEARCH = 100;
  private static final int DEFAULT_MS_BETWEEN_SEARCH = 5000;
  private Boolean attach = false;
  private SpotifyAppRemote mSpotifyAppRemote;
  private ArrayList<SongFull> allSongs = new ArrayList<>();
  private SongsViewModel viewModel;
  private RecyclerView rvPlaylist;
  private PlaylistAdapter playlistAdapter;
  private RelativeLayout relativeLayout;
  private TextView trackText;
  private LinearLayoutManager linearLayoutManager;
  private FloatingActionButton floatingActionButton;
  private ProgressBar progressBar;
  private TextView errorText;
  private Boolean playing = false;
  private int speed;
  private AudioManager audioManager;
  private Track currentTrack;
  private AlgorithmService algorithmService;
  private String trackName;
  private String playlistURI;
  private Long songLength;
  private int songProgress;
  private View view;
  private Button saveDataButton;
  private EditText editText;
  private ArrayList<String> speedData = new ArrayList<>();
  private LocationManager lm;

  @SuppressLint("RestrictedApi")
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.view = view;
    lm = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
    audioManager = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);
    int maxLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxLevel/2, 0);
    relativeLayout = view.findViewById(R.id.playlistLayout);
    if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(view.getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
      Log.i(Tag, "Permission check");
    }
    trackName = "";
    Activity MainActivity = getActivity();
    algorithmService = new AlgorithmService(DEFAULT_THRESHOLD, DEFAULT_INFLUENCE, DEFAULT_LAG);
    errorText = view.findViewById(R.id.errorText);
    trackText = view.findViewById(R.id.SongText);
    progressBar = view.findViewById(R.id.progressBarSong);
    saveDataButton = view.findViewById(R.id.saveData);
    editText = view.findViewById(R.id.editTextFileName);
    trackText.setVisibility(View.GONE);
    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            DEFAULT_MS_BETWEEN_SEARCH,
            DEFAULT_METERS_BETWEEN_SEARCH,
            new LocationListener()  {
              @Override
              public void onLocationChanged(Location location) {
                if (location==null){
                  ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("00 km/h");
                } else {
                  speed = (int) ((location.getSpeed()*3600)/1000);
                  speedData.add(String.valueOf(speed));
                  int currentLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                  int offset = currentLevel - maxLevel / 2;
                  double upper = 1.5;
                  double downer = -1.5;
                  if (offset < 0) {
                    upper = upper / Math.sqrt(Math.abs(offset));
                    downer = downer * Math.sqrt(Math.abs(offset));
                  } else if (offset > 0) {
                    upper = upper * Math.sqrt(Math.abs(offset));
                    downer = downer / Math.sqrt(Math.abs(offset));
                  }
                  Log.i(Tag, "upper: " + upper + " downer: " + downer + " offset: " + offset);
                  int result = algorithmService.add(speed, upper, downer);
                  if (result == 1) {
                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                    Snackbar.make(relativeLayout,
                            "volume increased!",
                            Snackbar.LENGTH_SHORT).show();
                  } else if (result == 2) {
                    audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                    Snackbar.make(relativeLayout,
                            "volume decreased!",
                            Snackbar.LENGTH_SHORT).show();
                  }
                  errorText.setText("current speed= " + speed+" km/h");
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
    viewModel = ViewModelProviders.of(this.getActivity()).get(SongsViewModel.class);
    floatingActionButton = view.findViewById(R.id.playButton);
    try {
      allSongs = viewModel.getSongList();
      playlistURI = viewModel.getPlaylistService().getPlaylistURI();
    } catch (Exception e) {
      Log.i(Tag, "View model empty, checking main activity");
    }
    if (allSongs.size() < 1) {
      MainActivity activity = (MainActivity) getActivity();
      allSongs = activity.getAllTracks();
      playlistURI = activity.getPlaylistURI();
    }
    if (allSongs.size() > 0) {
      saveDataButton.setVisibility(View.GONE);
      editText.setVisibility(View.GONE);
      setUpSpotifyRemote(view);
      trackText.setVisibility(View.VISIBLE);
      progressBar.setVisibility(View.VISIBLE);
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
    saveDataButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        for (String speed : speedData) {
          Log.i(Tag, "speed saved: " + speed);
        }
        if (permissionWriteCheck()) {
          try {
            writeToFile(speedData.toString());
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
        } else {
          Log.i(Tag, "Permission Error");
        }
      }
    });
    //Using this executor to update song progress every 2 seconds
    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    exec.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        if (playing) {
          updateProgressbar();
        }
      }
    }, 0, 2, TimeUnit.SECONDS);
  }

  private void updateProgressbar () {
    songProgress += 2000; // every 2 seconds this thread is triggered so 2000 ms are added to progress
    progressBar.setProgress(songProgress);
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
            new ConnectionParams.Builder(Const.getSpotifyClientId())
                    .setRedirectUri(Const.getSpotifyRedirectLink())
                    .showAuthView(true)
                    .build();
    SpotifyAppRemote.connect(view.getContext(), connectionParams,
            new Connector.ConnectionListener() {
              @Override
              public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                mSpotifyAppRemote.getPlayerApi().setShuffle(false);
                mSpotifyAppRemote.getPlayerApi().play(playlistURI);
                floatingActionButton.setVisibility(View.VISIBLE);
                floatingActionButton.setImageResource(R.drawable.pause_icon);
                playing = true;
                mSpotifyAppRemote.getPlayerApi()
                        .subscribeToPlayerState()
                        .setEventCallback(playerState -> {
                          final Track track = playerState.track;
                          if (track != null) {
                            if (trackName.isEmpty() || !trackName.equals(track.name)) {
                              trackName = track.name;
                              songLength = track.duration;
                              songProgress = 0;
                              progressBar.setMax((int) track.duration);
                              trackText.setText(track.name + " by " + track.artist.name);
                            }
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
  public void onDestroyView() {
    super.onDestroyView();
    if (floatingActionButton != null) {
      floatingActionButton.setVisibility(View.GONE);
    }
    if (mSpotifyAppRemote != null) {
      mSpotifyAppRemote.getPlayerApi().pause();
      SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_list, container, false);
  }

  private void writeToFile(String data) throws FileNotFoundException {
    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), editText.getText().toString());
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    try {
      fileOutputStream.write(data.getBytes());
      fileOutputStream.close();
      Toast.makeText(view.getContext(), "File saved", Toast.LENGTH_LONG).show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  private boolean permissionWriteCheck() {
    int check = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
    if (check < 0) {
      ActivityCompat.requestPermissions((Activity) view.getContext(),
              new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
              100);
    }
    return (check == PackageManager.PERMISSION_GRANTED);
  }
}
