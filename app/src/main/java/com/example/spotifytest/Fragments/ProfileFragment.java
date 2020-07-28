package com.example.spotifytest.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.spotifytest.Activities.LoginActivity;
import com.example.spotifytest.Activities.MainActivity;
import com.example.spotifytest.Models._User;
import com.example.spotifytest.OnSwipeTouchListener;
import com.example.spotifytest.Activities.PlaylistListActivity;
import com.example.spotifytest.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

    private final static String Tag = "profileFragment";
    private Button logoutButton;
    private Button playlistListButton;
    private TextView userView;
    private TextView drivesView;
    private ImageView profileImage;
    private RelativeLayout relativeLayout;
    private BottomNavigationView bottomNavigationView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _User user = (_User) ParseUser.getCurrentUser();
        logoutButton = view.findViewById(R.id.logoutButton);
        userView = view.findViewById(R.id.usernameProfView);
        profileImage = view.findViewById(R.id.profileView);
        relativeLayout = view.findViewById(R.id.profileLayout);
        playlistListButton = view.findViewById(R.id.playlistListButton);
        bottomNavigationView = view.findViewById(R.id.bottomNavigation);

        Glide.with(view.getContext()).load(user.getImage().getUrl()).into(profileImage);
        userView.setText(ParseUser.getCurrentUser().getUsername());

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                goToLogin();
            }
        });

        playlistListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PlaylistListActivity.class);
                startActivity(intent);
            }
        });

        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(view.getContext()) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeRight();
                MainActivity main = (MainActivity) getActivity();
                main.getBottomNavigationView().setSelectedItemId(R.id.generateAction);
            }
        });
    }

    public void goToLogin(){
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}