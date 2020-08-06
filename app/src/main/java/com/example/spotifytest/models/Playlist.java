package com.example.spotifytest.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = Playlist.class)
@ParseClassName("Playlist")
public class Playlist extends ParseObject {

  public static final String KEY_PLAYLIST_ID = "playlistId";
  public static final String KEY_ORIGIN_ID = "originId";
  public static final String KEY_USER = "user";
  public static final String KEY_DESTINATION_ID = "destinationId";
  public static final String KEY_REDIRECT_LINK = "redirectLink";
  public static final String KEY_TIME_TO = "timeTo";
  public static final String KEY_TITLE = "title";
  public static final String KEY_URI = "uri";

  public Playlist() {
    super();
  }

  public String getKeyPlaylistId() {
    return getString(KEY_PLAYLIST_ID);
  }

  public void setKeyPlaylistId(String keyPlaylistId) {
    put(KEY_PLAYLIST_ID, keyPlaylistId);
  }

  public String getKeyOriginId() {
    return (getString(KEY_ORIGIN_ID));
  }

  public void setKeyOriginId(String keyOriginId) {
    put(KEY_ORIGIN_ID, keyOriginId);
  }

  public String getKeyDestinationId() {
    return getString(KEY_DESTINATION_ID);
  }

  public void setKeyDestinationId(String keyDestinationId) {
    put(KEY_DESTINATION_ID, keyDestinationId);
  }

  public ParseUser getUser() {
    return (_User) getParseUser(KEY_USER);
  }

  public void setUser(ParseUser parseUser) {
    put(KEY_USER, parseUser);
  }

  public String getKeyRedirectLink() {
    return getString(KEY_REDIRECT_LINK);
  }

  public void setKeyRedirectLink(String keyRedirectLink) {
    put(KEY_REDIRECT_LINK, keyRedirectLink);
  }

  public String getKeyTimeTo() {
    return getString(KEY_TIME_TO);
  }

  public void setTimeTo(String keyTimeTo) {
    put(KEY_TIME_TO, keyTimeTo);
  }

  public String getTitle() {
    return getString(KEY_TITLE);
  }

  public void setKeyTitle(String title) {
    put(KEY_TITLE, title);
  }

  public String getKeyUri() {
    return getString(KEY_URI);
  }

  public void setKeyUri(String keyUri) {
    put(KEY_URI, keyUri);
  }
}
