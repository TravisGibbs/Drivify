package com.example.spotifytest.models;

public class Const {
  private static final String goToPlaylistKey = "goToPlaylist";
  private static final String playlistIDKey = "playlistID";
  private static final String playlistURIKey = "playlistURI";
  private static final String objectIDKey = "objectID";
  private static final String objectNameKey = "objectName";
  private static final String isSongKey = "isSong";
  private static final String googleApiKey = "AIzaSyDmCIZvAzyQ5iO3s4Qw2GMJxu_vDjOXWCk";
  private static final String spotifyClientId = "16b8f7e96bbb4d12b021825527475319";
  private static final String spotifyRedirectLink = "https://developer.spotify.com/dashboard";

  public static String getGoogleApiKey() {
    return googleApiKey;
  }

  public static String getSpotifyClientId() {
    return spotifyClientId;
  }

  public static String getSpotifyRedirectLink() {
    return spotifyRedirectLink;
  }

  public static String getGoToPlaylistKey() {
    return goToPlaylistKey;
  }

  public static String getPlaylistIDKey() {
    return playlistIDKey;
  }

  public static String getPlaylistURIKey() {
    return playlistURIKey;
  }

  public static String getobjectIDKey() {
    return objectIDKey;
  }

  public static String getObjectNameKey() {
    return objectNameKey;
  }

  public static String getIsSongKey() {
    return isSongKey;
  }
}
