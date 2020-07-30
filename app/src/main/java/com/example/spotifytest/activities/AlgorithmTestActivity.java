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
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlgorithmTestActivity extends AppCompatActivity {
  private static final String Tag = "AlgorithmTestActivity";
  private static final int lag =  5;
  private static final int sizeOfTest = 100;
  private ArrayList<Integer> testVals = new ArrayList<>();
  private LineGraphSeries<DataPoint> rawData;
  private BarGraphSeries<DataPoint> outlierData;
  private LineGraphSeries<DataPoint> upperData;
  private LineGraphSeries<DataPoint> downerData;
  private BarGraphSeries<DataPoint> volumeData;
  private GraphView graph;
  private GraphView graphOutlier;
  private GraphView graphVolume;
  private AudioManager audioManager;
  private int maxLevel;

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_algorithm_test);
    audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
    maxLevel = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    GraphView graph = findViewById(R.id.graph1);
    GraphView graphOutlier = findViewById(R.id.graph2);
    GraphView graphVolume = findViewById(R.id.graph3);
    rawData = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 0),
    });
    outlierData = new BarGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 0),
    });
    upperData = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 0),
    });
    downerData = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 0),
    });
    volumeData = new BarGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)),
    });
    graph.addSeries(upperData);
    graph.addSeries(downerData);
    upperData.setBackgroundColor(getResources().getColor(R.color.paleGreen));
    upperData.setDrawBackground(true);
    downerData.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    downerData.setDrawBackground(true);
    rawData.setColor(getResources().getColor(R.color.spotifyDarkGrey));
    volumeData.setColor(getResources().getColor(R.color.spotifyLightGrey));
    outlierData.setColor(getResources().getColor(R.color.colorPrimary));
    upperData.setColor(getResources().getColor(R.color.colorPrimary));
    downerData.setColor(getResources().getColor(R.color.colorPrimary));
    graph.addSeries(rawData);
    graphOutlier.addSeries(outlierData);
    graphVolume.addSeries(volumeData);
    graph.getViewport().setYAxisBoundsManual(true);
    graph.getViewport().setMaxY(200);
    graph.getViewport().setMinY(0);
    graphVolume.getViewport().setYAxisBoundsManual(true);
    graphVolume.getViewport().setMinY(0);
    graphVolume.getViewport().setMaxY(maxLevel);
    setGraphBounds(graph);
    setGraphBounds(graphOutlier);
    setGraphBounds(graphVolume);
    graph.setTitle("mock speed data graph");
    graphOutlier.setTitle("outlier graph");
    graphVolume.setTitle("volume graph");
    runTest(sizeOfTest, true);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void runTest(int amountOfVals, boolean isRandom) {
    if (isRandom) {
      generateTestVals(amountOfVals);
    } // add possible loading for list of chosen values
    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxLevel/2, 0);
    AlgorithmService algorithmService = new AlgorithmService(2, .5, lag);
    int speed = 0;
    for (int i = 0; i < amountOfVals; i++) {
      speed = testVals.get(i);
      rawData.appendData(new DataPoint(i,speed), true, 500, false);
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
      if (i > lag) {
        Log.i(Tag, String.valueOf(algorithmService.getMean() + algorithmService.getStd() * upper));
        Log.i(Tag, String.valueOf(algorithmService.getMean() + algorithmService.getStd() * upper));
        upperData.appendData(new DataPoint(i, algorithmService.getMean() + algorithmService.getStd() * upper), true, 500, false);
        downerData.appendData(new DataPoint(i, algorithmService.getMean() + algorithmService.getStd() * downer), true, 500, false);
      } else {
        upperData.appendData(new DataPoint(i, 0), true, 500, false);
        downerData.appendData(new DataPoint(i, 0), true, 500, false);

      }
      Log.i(Tag, "upper: " + upper + " downer: " + downer + " offset: " + offset);
      int result = algorithmService.add(speed, upper, downer);
      if (result == 1) {
        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        Log.i(Tag, "volume increased with speed: " + speed);
        outlierData.appendData(new DataPoint(i,1), true, 500, false);
      } else if (result == 2) {
        audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        Log.i(Tag, "volume lowered with speed: " + speed);
        outlierData.appendData(new DataPoint(i,-1), true, 500, false);
      } else {
        outlierData.appendData(new DataPoint(i,0), true, 500, false);
      }
      volumeData.appendData(new DataPoint(i, audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)), true, 500, false);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void generateTestVals(int amountOfVals) {
    Random r = new Random();
    Stream<Integer> stream = r.ints(amountOfVals/5, 40, 60).boxed();
    List<Integer> tempList = stream.collect(Collectors.toList());
    testVals.addAll(tempList);
    stream = r.ints(amountOfVals/5, 70, 90).boxed();
    tempList = stream.collect(Collectors.toList());
    testVals.addAll(tempList);
    stream = r.ints(amountOfVals/5, 110, 130).boxed();
    tempList = stream.collect(Collectors.toList());
    testVals.addAll(tempList);
    stream = r.ints(amountOfVals/5, 70, 90).boxed();
    tempList = stream.collect(Collectors.toList());
    testVals.addAll(tempList);
    stream = r.ints(amountOfVals/5, 40, 60).boxed();
    tempList = stream.collect(Collectors.toList());
    testVals.addAll(tempList);
  }

  public void setGraphBounds(GraphView graph) {
    graph.getViewport().setXAxisBoundsManual(true);
    graph.getViewport().setMaxX(sizeOfTest);
    graph.getViewport().setMinX(lag);
  }
}