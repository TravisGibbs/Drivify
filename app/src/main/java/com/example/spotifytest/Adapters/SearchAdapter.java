package com.example.spotifytest.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotifytest.Models.SearchObject;
import com.example.spotifytest.R;
import com.example.spotifytest.SearchActivity;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

  public interface OnClickListener {
    void onItemClicked(int position, String id, Boolean isSong);
  }

  private static final String Tag = "SearchAdapter";
  private List<SearchObject> searchObjectsList;
  private Context context;
  private OnClickListener ClickListener;

  public SearchAdapter(List<SearchObject> searchObjects, Context context, OnClickListener onClickListener){
    this.searchObjectsList = searchObjects;
    this.context = context;
    this.ClickListener = onClickListener;
  }

  @NonNull
  @Override
  public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
    return new SearchAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
    SearchObject searchObject = searchObjectsList.get(position);
    if(getItemCount() > 0) {
      holder.bind(searchObject);
    }
  }

  @Override
  public int getItemCount() {
    return searchObjectsList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {

    private TextView topText;
    private TextView bottomText;
    private ImageView albumImage;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      topText = itemView.findViewById(R.id.songName);
      bottomText = itemView.findViewById(R.id.artistName);
      albumImage = itemView.findViewById(R.id.albumArt);
    }

    public void bind(SearchObject searchObject){
      if (searchObject.getArtist() != null){
        topText.setText(searchObject.getArtist().getName());
        bottomText.setVisibility(View.GONE);
        if(searchObject.getArtist().getImages().size() > 0) {
          Glide.with(context).load(searchObject.getArtist().getImages().get(0).getUrl()).into(albumImage);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            ClickListener.onItemClicked(getAdapterPosition(), searchObject.getArtist().getId(), false);
          }
        });
      }
      if (searchObject.getSong() != null){
        topText.setText(searchObject.getSong().getName());
        bottomText.setText(searchObject.getSong().getArtists().get(0).getName());
        bottomText.setVisibility(View.VISIBLE);
        if(searchObject.getSong().getAlbum().getImages().size() > 0) {
          Glide.with(context).load(searchObject.getSong().getAlbum().getImages().get(0).getUrl()).into(albumImage);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            ClickListener.onItemClicked(getAdapterPosition(),searchObject.getSong().getArtists().get(0).getId(),true);
          }
        });
      }
    }
  }


}
