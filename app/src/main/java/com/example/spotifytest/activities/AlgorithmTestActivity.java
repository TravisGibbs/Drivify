package com.example.spotifytest.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.example.spotifytest.R;
import com.example.spotifytest.services.AlgorithmService;
import com.google.android.material.snackbar.Snackbar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlgorithmTestActivity extends AppCompatActivity {
  private static final String Tag = "AlgorithmTestActivity";
  private RelativeLayout relativeLayout;
  private ArrayList<Integer> testVals = new ArrayList<>();
  private LineGraphSeries<DataPoint> rawData;
  private LineGraphSeries<DataPoint> outlierData;

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_algorithm_test);
    relativeLayout = findViewById(R.id.testLayout);
    GraphView graph = findViewById(R.id.graph);
    GraphView graphOutlier = findViewById(R.id.graph2);
    rawData = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 0),
    });
    outlierData = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 0),
    });
    outlierData.setColor(getResources().getColor(R.color.colorPrimary));
    graph.addSeries(rawData);
    graphOutlier.addSeries(outlierData);
    runTest(90, true);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void runTest(int amountOfVals, boolean isRandom) {
    if (isRandom) {
      generateTestVals(amountOfVals);
    } // add possible loading for list of chosen values
    AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
    int maxLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxLevel/2, 0);
    AlgorithmService algorithmService = new AlgorithmService(2, .5, 5);
    int speed = 0;
    for (int i = 0; i < amountOfVals; i++) {
      speed = testVals.get(i);
      rawData.appendData(new DataPoint(i,speed), false, 500, false);
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
        Log.i(Tag, "volume increased with speed: " + speed);
        Snackbar.make(relativeLayout,
                "volume increased!",
                Snackbar.LENGTH_SHORT).show();
        outlierData.appendData(new DataPoint(i,1), false,500,false);
      } else if (result == 2) {
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        Log.i(Tag, "volume lowered with speed: " + speed);
        Snackbar.make(relativeLayout,
                "volume decreased!",
                Snackbar.LENGTH_SHORT).show();
        outlierData.appendData(new DataPoint(i,-1), false,500,false);
      } else {
        outlierData.appendData(new DataPoint(i,0), false,500,false);
      }
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void generateTestVals(int amountOfVals) {
    Random r = new Random();
    Stream<Integer> stream = r.ints(amountOfVals/3, 40, 60).boxed();
    List<Integer> tempList = stream.collect(Collectors.toList());
    testVals.addAll(tempList);
    stream = r.ints(amountOfVals/3, 70, 90).boxed();
    tempList = stream.collect(Collectors.toList());
    testVals.addAll(tempList);
    stream = r.ints(amountOfVals/3, 110, 130).boxed();
    tempList = stream.collect(Collectors.toList());
    testVals.addAll(tempList);
  }
}