package com.example.spotifytest.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifytest.R;
import com.example.spotifytest.activities.DetailActivity;
import com.example.spotifytest.models.Playlist;

import org.parceler.Parcels;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

  public interface OnClickListener {
    void onItemClicked(String url);
  }

  private static final String Tag = "ListAdapter";
  private List<Playlist> playlists;
  private Context context;
  private OnClickListener onClickListener;

  public ListAdapter(List<Playlist> playlists, Context context, OnClickListener onClickListener) {
    this.playlists = playlists;
    this.context = context;
    this.onClickListener = onClickListener;
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
    private Button goToButton;
    private TextView time;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      title = itemView.findViewById(R.id.playlistTitle);
      goToButton = itemView.findViewById(R.id.openLinkButton);
      time = itemView.findViewById(R.id.timeAmount);
    }

    public void bind(Playlist playlist) {
      title.setText(playlist.getTitle());
      String timeText = String.valueOf(Integer.parseInt(playlist.getKeyTimeTo())/60000);
      time.setText(timeText + " minutes");
      goToButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          onClickListener.onItemClicked(playlist.getKeyRedirectLink());
        }
      });
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(view.getContext(), DetailActivity.class);
          intent.putExtra("playlist", Parcels.wrap(playlist));
          context.startActivity(intent);
        }
      });
    }
  }
}
