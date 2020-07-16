package com.example.spotifytest.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifytest.Adapters.PlaylistAdapter;
import com.example.spotifytest.MainActivity;
import com.example.spotifytest.Models.SongFull;
import com.example.spotifytest.OnSwipeTouchListener;
import com.example.spotifytest.R;
import com.example.spotifytest.SongsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class PlaylistFragment extends Fragment {

  private static final String Tag = "PlaylistFragment";
  private ArrayList<SongFull> allSongs = new ArrayList<>();
  private SongsViewModel viewModel;
  private RecyclerView rvPlaylist;
  private PlaylistAdapter playlistAdapter;
  private RelativeLayout relativeLayout;
  private LinearLayoutManager linearLayoutManager;
  //private BottomNavigationView bottomNavigationView;
  private TextView errorText;

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    rvPlaylist = view.findViewById(R.id.rvSongs);
    errorText = view.findViewById(R.id.errorText);
    relativeLayout = view.findViewById(R.id.playlistLayout);
    //bottomNavigationView = view.findViewById(R.id.bottomNavigation);
    SongsViewModel viewModel = ViewModelProviders.of(this.getActivity()).get(SongsViewModel.class);
    allSongs = viewModel.getSongList();
    if(allSongs.size() > 0) {
      errorText.setVisibility(View.GONE);
      linearLayoutManager = new LinearLayoutManager(view.getContext());
      playlistAdapter = new PlaylistAdapter(allSongs, view.getContext());
      rvPlaylist.setAdapter(playlistAdapter);
      rvPlaylist.setLayoutManager(linearLayoutManager);
      playlistAdapter.notifyDataSetChanged();
    }

    relativeLayout.setOnTouchListener(new OnSwipeTouchListener(view.getContext()) {
      @Override
      public void onSwipeRight() {
        super.onSwipeRight();
        MainActivity main = (MainActivity) getActivity();
        main.bottomNavigationView.setSelectedItemId(R.id.generateAction);
      }
    });
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_list, container, false);
  }

}
