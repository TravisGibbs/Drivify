package com.example.spotifytest.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.spotifytest.LoginActivity;
import com.example.spotifytest.Models._User;
import com.example.spotifytest.R;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

    private final static String Tag = "profileFragment";
    private Button logoutButton;
    private TextView userView;
    private TextView drivesView;
    private ImageView profileImage;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _User user = (_User) ParseUser.getCurrentUser();
        logoutButton = view.findViewById(R.id.logoutButton);
        drivesView = view.findViewById(R.id.driveView);
        userView = view.findViewById(R.id.usernameProfView);
        profileImage = view.findViewById(R.id.profileView);

        Glide.with(view.getContext()).load(user.getImage().getUrl()).into(profileImage);
        userView.setText(ParseUser.getCurrentUser().getUsername());

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                goToLogin();
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