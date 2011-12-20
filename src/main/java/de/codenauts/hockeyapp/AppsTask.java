package de.codenauts.hockeyapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class AppsTask extends AsyncTask<String, String, JSONArray> {
  private boolean finished;
  private JSONArray apps;
  private MainActivity activity;
  private String token;
  
  public AppsTask(MainActivity activity, String token) {
    this.activity = activity;
    this.token = token;
    this.finished = false;
    this.apps = null;
  }

  public void attach(MainActivity activity) {
    this.activity = activity;
    
    if (this.finished) {
      this.finished = false;
      handleResult();
    }
  }
  
  public void detach() {
    activity = null;
  }

  @Override
  protected JSONArray doInBackground(String... params) {
    try {
      return getTokens();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
  private JSONArray getTokens() throws IOException, JSONException {
    URL url = new URL(OnlineHelper.BASE_URL + OnlineHelper.APPS_ACTION);
    HttpURLConnection connection = (HttpURLConnection)url.openConnection();

    Log.i("HockeyApp", this.token);

    addCredentialsToConnection(connection);
    connection.connect();

    Log.i("HockeyApp", "" + connection.getResponseCode());
    if (connection.getResponseCode() == 200) {
      String jsonString = OnlineHelper.getStringFromConnection(connection);

      Log.i("HockeyApp", jsonString);
      return parseJSONFromString(jsonString);
    }
    else {
      return null;
    }
  }

  private JSONArray parseJSONFromString(String jsonString) throws JSONException {
    JSONObject json = new JSONObject(jsonString);
    if ((json.has("status")) && (json.get("status").equals("success"))) {
      return (JSONArray)json.get("apps");
    }
    else {
      return null;
    }
  }

  private void addCredentialsToConnection(HttpURLConnection connection) {
    connection.addRequestProperty("User-Agent", "Hockey/Android");
    connection.addRequestProperty("X-HockeyAppToken", this.token);
  }

  @Override
  protected void onPostExecute(JSONArray apps) {
    this.apps = apps;
    if ((activity == null) || (activity.isFinishing())) {
      this.finished = true;
    }
    else {
      handleResult();
    }
  }

  private void handleResult() {
    if (this.apps == null) {
      activity.didFailToReceiveApps();
    }
    else {
      activity.didReceiveApps(this.apps);
    }
  }
}
