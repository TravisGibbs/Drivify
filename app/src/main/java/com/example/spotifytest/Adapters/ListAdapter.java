package com.example.spotifytest.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifytest.Models.Playlist;
import com.example.spotifytest.Models.SongFull;
import com.example.spotifytest.R;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

  private static final String Tag = "ListAdapter";
  private List<Playlist> playlists;
  private Context context;

  public ListAdapter(List<Playlist> playlists, Context context) {
    this.playlists = playlists;
    this.context = context;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_playlist, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Playlist playlist = playlists.get(position);
    holder.bind(playlist);
  }

  @Override
  public int getItemCount() {
    return playlists.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView date;
    private TextView time;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      title = itemView.findViewById(R.id.playlistTitle);
      date = itemView.findViewById(R.id.dateText);
      time = itemView.findViewById(R.id.timeAmount);
    }

    public void bind(Playlist playlist) {
      title.setText(playlist.getTitle());
      time.setText(playlist.getKeyTimeTo());
      date.setText(playlist.getKeyRedirectLink());
    }
  }
}
