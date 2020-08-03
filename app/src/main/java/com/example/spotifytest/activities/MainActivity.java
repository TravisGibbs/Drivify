package com.example.spotifytest.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.example.spotifytest.fragments.GenerateFragment;
import com.example.spotifytest.fragments.ProfileFragment;
import com.example.spotifytest.fragments.PlaylistFragment;
import com.example.spotifytest.models.Const;
import com.example.spotifytest.models.SongFull;
import com.example.spotifytest.R;
import com.example.spotifytest.services.PlaylistService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final static String Tag = "MainActivity";
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;
    private PlaylistService playlistService;
    private RelativeLayout relativeLayout;
    private ArrayList<SongFull> allTracks = new ArrayList();
    private String playlistURI;
    private String playlistID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        relativeLayout = findViewById(R.id.mainLayout);
        playlistService = new PlaylistService(this, relativeLayout);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.profileAction:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.generateAction:
                        fragment = new GenerateFragment();
                        break;
                    case R.id.playlistAction:
                        fragment = new PlaylistFragment();
                        break;
                    default: return true;
                }
                Fragment myFragment = getSupportFragmentManager().findFragmentByTag("current fragment");
                FragmentTransaction ft = setFragmentManager(myFragment, fragment);
                ft.replace(R.id.flContainer, fragment, "current fragment");
                ft.addToBackStack(null);
                ft.commit();
                return true;
            }
        });
        if (getIntent().getBooleanExtra(Const.goToPlaylistKey, false)) {
            openPlaylist();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.generateAction);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public FragmentTransaction setFragmentManager(Fragment fragmentCurrent, Fragment fragmentNext) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentCurrent == null) {
        } else if (fragmentCurrent instanceof ProfileFragment) {
            ft.setCustomAnimations(R.anim.activity_open_enter_reverse,
                    R.anim.activity_open_exit_reverse,
                    R.anim.activity_close_enter,
                    R.anim.activity_close_exit);
        } else if (fragmentCurrent instanceof GenerateFragment && fragmentNext instanceof PlaylistFragment) {
            ft.setCustomAnimations(R.anim.activity_open_enter_reverse,
                    R.anim.activity_open_exit_reverse,
                    R.anim.activity_close_enter,
                    R.anim.activity_close_exit);
        } else if(fragmentCurrent instanceof GenerateFragment && fragmentNext instanceof GenerateFragment){
        } else {
            ft.setCustomAnimations(R.anim.activity_open_enter,
                    R.anim.activity_open_exit,
                    R.anim.activity_close_enter,
                    R.anim.activity_close_exit);
        }
        return ft;
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public ArrayList<SongFull> getAllTracks() {
        return allTracks;
    }

    public String getPlaylistURI() {
        return playlistURI;
    }

    public String getPlaylistID() {
        return playlistID;
    }

    private void openPlaylist () {
        playlistService.getPlaylistItems(getIntent().getStringExtra(Const.playlistIDKey),
                new PlaylistService.playlistServiceCallback() {
            @Override
            public void onSearchFinish(boolean found) {
                if (found) {
                    allTracks = playlistService.getSongFulls();
                    playlistID = getIntent().getStringExtra(Const.playlistIDKey);
                    playlistURI = getIntent().getStringExtra(Const.playlistURIKey);
                    bottomNavigationView.setSelectedItemId(R.id.playlistAction);
                }
            }
        });
    }
}
