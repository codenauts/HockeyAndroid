package de.codenauts.hockeyapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import net.hockeyapp.android.internal.CheckUpdateTask;

import org.json.JSONArray;

public class AppInfoTask extends CheckUpdateTask {
  private MainActivity activity;
  private String token;
  
  public AppInfoTask(MainActivity activity, String urlString, String appIdentifier, String token) {
    super(activity, urlString, appIdentifier);
    
    this.activity = activity;
    this.token = token;
  }

  protected int getVersionCode() {
    return 0;
  }
  
  @Override
  protected void onPostExecute(JSONArray updateInfo) {
    activity.didReceiveAppInfo(updateInfo, getURLString("apk"));
  }
  
  protected String getURLString(String format) {
    StringBuilder builder = new StringBuilder();
    builder.append(this.urlString);
    builder.append("api/2/apps/");
    builder.append((this.appIdentifier != null ? this.appIdentifier : this.activity.getPackageName()));
    builder.append("?format=" + format);
    
    return builder.toString();
  }

  protected boolean getCachingEnabled() {
    return false;
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
