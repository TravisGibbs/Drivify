package com.example.spotifytest.services;

import android.content.Intent;
import android.net.Uri;

public class NavigatorService {

  public Intent openPlaylist(String URL) {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    intent.addCategory(Intent.CATEGORY_BROWSABLE);
    intent.setData(Uri.parse(URL));
    return intent;
  }
}
