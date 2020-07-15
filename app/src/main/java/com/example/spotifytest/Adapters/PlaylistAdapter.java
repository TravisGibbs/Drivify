package com.example.spotifytest.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifytest.Models.SongFull;
import com.example.spotifytest.R;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

  private static final String Tag = "playlistAdapter";
  private List<SongFull> songList;
  private Context context;

  public PlaylistAdapter(List<SongFull> songs, Context context){
    this.songList = songs;
    this.context = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      SongFull song = songList.get(position);
      holder.bind(song);
  }

  @Override
  public int getItemCount() {
    return songList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    private TextView songName;
    private TextView artistName;
    private ImageView albumImage;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      songName = itemView.findViewById(R.id.songName);
      artistName = itemView.findViewById(R.id.artistName);
      albumImage = itemView.findViewById(R.id.albumArt);
    }

    public void bind(SongFull song){
      songName.setText(song.getName());
      artistName.setText(song.getArtists().get(0).getName());
      if(song.getAlbum().getImages().get(0).getUrl()!=null) {
        Log.i(Tag,song.getAlbum().getImages().get(0).getUrl());
        Glide.with(context).load(song.getAlbum().getImages().get(0).getUrl()).into(albumImage);
      }
    }
  }
}
