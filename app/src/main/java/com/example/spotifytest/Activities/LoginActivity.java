package com.example.spotifytest.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.spotifytest.Models._User;
import com.example.spotifytest.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String Tag = "LoginActivity";
    private TextView userText;
    private EditText passTextEdit;
    private Button loginButton;
    private Button registerButton;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        _User user = new _User();
        if(user.getCurrentUser()!=null){
            Log.i(Tag,"logged in");
            goToMainActivity();
        }

        userText = findViewById(R.id.usernameText);
        passTextEdit = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        linearLayout = findViewById(R.id.LiLayout);
        SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
        final String username = sharedPreferences.getString("user_name", "defauly");
        userText.setText(username);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(Tag, "login tapped");
                String userText = username;
                String passText = String.valueOf(passTextEdit.getText());
                loginUser(userText, passText);
            }
        });
    }



    private void loginUser(String userText, String passText){
        ParseUser.logInInBackground(userText, passText, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Log.i(Tag,"user logged in");
                    goToMainActivity();
                } else {
                    Log.e(Tag,"user login failed",e);
                    Snackbar.make(linearLayout, "login failed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToMainActivity(){
        goToActivity(MainActivity.class);
    }

    private void goToRegister(){
        goToActivity(RegisterActivity.class);
    }

    private void goToActivity(Class newActivity){
        Intent i = new Intent(this, newActivity);
        startActivity(i);
    }
}
