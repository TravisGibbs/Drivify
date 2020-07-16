package com.example.spotifytest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.spotifytest.Fragments.GenerateFragment;
import com.example.spotifytest.Fragments.ProfileFragment;
import com.example.spotifytest.Fragments.PlaylistFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private final static String Tag = "MainActivity";
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
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
        bottomNavigationView.setSelectedItemId(R.id.generateAction);
    }

    public FragmentTransaction setFragmentManager(Fragment fragmentCurrent, Fragment fragmentNext) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentCurrent == null) {
        }
        else if (fragmentCurrent instanceof ProfileFragment) {
            ft.setCustomAnimations(R.anim.activity_open_enter_reverse,
                    R.anim.activity_open_exit_reverse,
                    R.anim.activity_close_enter,
                    R.anim.activity_close_exit);
        }
        else if (fragmentCurrent instanceof GenerateFragment && fragmentNext instanceof PlaylistFragment) {
            ft.setCustomAnimations(R.anim.activity_open_enter_reverse,
                    R.anim.activity_open_exit_reverse,
                    R.anim.activity_close_enter,
                    R.anim.activity_close_exit);
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
}
