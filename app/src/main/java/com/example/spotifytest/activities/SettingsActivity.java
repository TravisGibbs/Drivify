package com.example.spotifytest.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifytest.R;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

  private SharedPreferences sharedPreferences;
  private SharedPreferences.Editor editor;
  private RangeSlider volumeSlider;
  private SwitchMaterial dynamicSwitch;

  @RequiresApi(api = Build.VERSION_CODES.P)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
    sharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
    dynamicSwitch = findViewById(R.id.dyanamicSwitch);
    dynamicSwitch.setChecked(sharedPreferences.getBoolean("isDynamicVolume", true));
    volumeSlider = findViewById(R.id.volumeSlider);
    Log.i("settings", String.valueOf(audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)));
    Log.i("settings", String.valueOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
    volumeSlider.setValues((float) sharedPreferences.getInt("minVolume", audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)),
              (float) sharedPreferences.getInt("maxVolume", audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    editor = getSharedPreferences("SPOTIFY", 0).edit();
    editor.putInt("minVolume", Math.round(volumeSlider.getValues().get(0)));
    editor.putInt("maxVolume", Math.round(volumeSlider.getValues().get(1)));
    editor.putBoolean("isDynamicVolume", dynamicSwitch.isChecked());
    editor.commit();
  }
}
