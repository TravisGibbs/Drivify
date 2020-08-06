package com.example.spotifytest.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.spotifytest.R;
import com.example.spotifytest.models._User;
import com.google.android.material.snackbar.Snackbar;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.File;

public class RegisterActivity extends AppCompatActivity {

  public static final String Tag = "RegisterActivity";
  private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
  private String photoFileName = "photo.jpg";
  private Button takePhoto;
  private Button registerButton;
  private ImageView imageTaken;
  private TextView userText;
  private EditText passTextEdit;
  private File photoFile;
  private ProgressBar progressBar;
  private LinearLayout linearLayout;
  private String userID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_register);
    takePhoto = findViewById(R.id.cameraButtonRegister);
    imageTaken = findViewById(R.id.imageTakenRegister);
    registerButton = findViewById(R.id.registerButtonRegister);
    linearLayout = findViewById(R.id.RegisterLayout);
    userText = findViewById(R.id.usernameText);
    passTextEdit = findViewById(R.id.passwordText);
    imageTaken.setVisibility(View.GONE);
    progressBar = (ProgressBar) findViewById(R.id.pbLoadingRegister);
    progressBar.setVisibility(ProgressBar.GONE);
    SharedPreferences sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
    String username = sharedPreferences.getString("user_name", "defauly");
    userID = sharedPreferences.getString("userid", "1");
    userText.setText(username);

    takePhoto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        launchCamera();
      }
    });

    registerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        progressBar.setVisibility(View.VISIBLE);
        registerUser(username, passTextEdit.getText().toString());
      }
    });
  }

  private void registerUser(String userName, String passText) {
    Log.i(Tag, "registering user: " + userName);

    _User user = new _User();
    user.setUsername(userName);
    user.setPassword(passText);
    user.setKeySpotifyid(userID);
    Log.i(Tag, photoFile.getAbsolutePath());
    ParseFile photo = new ParseFile(photoFile);
    try {
      photo.save();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    user.setImage(photo);
    user.signUpInBackground(new SignUpCallback() {
      public void done(ParseException e) {
        if (e == null) {
          Log.i(Tag, "user created");
          loginUser(userText.getText().toString(), passTextEdit.getText().toString());
          goToMainActivity();
          // Hooray! Let them use the app now.
        } else {
          Log.e(Tag, "user register failed", e);
          progressBar.setVisibility(View.GONE);
          Snackbar.make(linearLayout, "Registration failed", Snackbar.LENGTH_SHORT).show();
          // Sign up didn't succeed. Look at the ParseException
          // to figure out what went wrong
        }
      }
    });
  }

  private void launchCamera() {
    Log.i(Tag, "here");
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Create a File reference for future access
    photoFile = getPhotoFileUri(photoFileName);

    // wrap File object into a content provider
    // required for API >= 24
    // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
    Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider.test", photoFile);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

    // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
    // So as long as the result is not null, it's safe to use the intent.
    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    if (intent.resolveActivity(this.getPackageManager()) != null) {
      // Start the image capture intent to take photo
      startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
  }

  public File getPhotoFileUri(String fileName) {
    // Get safe storage directory for photos
    // Use `getExternalFilesDir` on Context to access package-specific directories.
    // This way, we don't need to request external read/write runtime permissions.
    File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), Tag);

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
      Log.d(Tag, "failed to create directory");
    }

    // Return the file target for the photo based on filename
    File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

    return file;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        // by this point we have the camera photo on disk
        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        // RESIZE BITMAP, see section below
        // Load the taken image into a preview
        imageTaken.setImageBitmap(takenImage);
        imageTaken.setVisibility(View.VISIBLE);
      } else { // Result was a failure
        //Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void goToMainActivity() {
    progressBar.setVisibility(View.GONE);
    Intent i = new Intent(this, MainActivity.class);
    startActivity(i);
  }

  private void loginUser(String userText, String passText) {
    ParseUser.logInInBackground(userText, passText, new LogInCallback() {
      public void done(ParseUser user, ParseException e) {
        if (user != null) {
          Log.i(Tag, "user logged in");
          goToMainActivity();
        } else {
          Log.e(Tag, "user login failed", e);
          Snackbar.make(linearLayout, "login failed", Snackbar.LENGTH_SHORT).show();
        }
      }
    });

  }
}