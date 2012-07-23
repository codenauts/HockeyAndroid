package de.codenauts.hockeyapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import net.hockeyapp.android.internal.DownloadFileListener;
import net.hockeyapp.android.internal.DownloadFileTask;

public class AppDownloadTask extends DownloadFileTask {
  private String token;
  
  public AppDownloadTask(Context context, String urlString, String token, DownloadFileListener listener) {
    super(context, urlString, listener);
    this.token = token;
  }

  protected URLConnection createConnection(URL url) throws IOException {
    HttpURLConnection connection = (HttpURLConnection)super.createConnection(url);
    addCredentialsToConnection(connection);
    return connection;
  }

  private void addCredentialsToConnection(HttpURLConnection connection) {
    connection.addRequestProperty("User-Agent", "Hockey/Android");
    connection.addRequestProperty("X-HockeyAppToken", this.token);
  }
}
