package com.example.spotifytest.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.spotifytest.OnSwipeTouchListener;
import com.example.spotifytest.R;
import com.example.spotifytest.activities.AlgorithmTestActivity;
import com.example.spotifytest.activities.LoginActivity;
import com.example.spotifytest.activities.MainActivity;
import com.example.spotifytest.activities.PlaylistListActivity;
import com.example.spotifytest.models.Const;
import com.example.spotifytest.models._User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;


public class ProfileFragment extends Fragment {

  private final static String Tag = "profileFragment";
  private Button logoutButton;
  private Button playlistListButton;
  private Button testAlgoButton;
  private Button testAlgoButtonReal;
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
    testAlgoButton = view.findViewById(R.id.testAlgoButton);
    testAlgoButtonReal = view.findViewById(R.id.realvalsTestButton);
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

    testAlgoButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), AlgorithmTestActivity.class);
        startActivity(intent);
      }
    });

    testAlgoButtonReal.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), AlgorithmTestActivity.class);
        intent.putExtra(Const.dataKey, Const.dataSet1.replace("\n", " "));
        intent.putExtra(Const.msKey, true);
        startActivity(intent);
      }
    });
  }

  public void goToLogin() {
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