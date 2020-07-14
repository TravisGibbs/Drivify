package com.example.spotifytest.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifytest.Models.SongFull;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
  private static final String Tag = "playlistAdapter";
  private List<SongFull> songList;
  private Context context;

  public PlaylistAdapter(List<SongFull> songs, Context context){
    
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return null;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

  }

  @Override
  public int getItemCount() {
    return 0;
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
    }



  }
}
