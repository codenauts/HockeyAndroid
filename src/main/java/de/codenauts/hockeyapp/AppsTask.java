package de.codenauts.hockeyapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class AppsTask extends AsyncTask<String, String, JSONArray> {
  private boolean finished;
  private int status = OnlineHelper.STATUS_UNKNOWN_ERROR;
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
      return getApps();
    }
    catch (IOException e) {
      status = OnlineHelper.STATUS_NETWORK_ERROR;
    }
    catch (Exception e) {
    }
    return null;
  }
  
  private JSONArray getApps() throws IOException, JSONException {
    URL url = new URL(OnlineHelper.BASE_URL + OnlineHelper.APPS_ACTION);
    HttpURLConnection connection = (HttpURLConnection)url.openConnection();

    addCredentialsToConnection(connection);
    connection.connect();

    if (connection.getResponseCode() == 200) {
      String jsonString = OnlineHelper.getStringFromConnection(connection);
      return parseJSONFromString(jsonString);
    }
    else {
      status = OnlineHelper.STATUS_LOGIN_ERROR;
      return null;
    }
  }

  private JSONArray parseJSONFromString(String jsonString) throws JSONException {
    JSONObject json = new JSONObject(jsonString);
    if ((json.has("status")) && (json.get("status").equals("success"))) {
      return (JSONArray)json.get("apps");
    }
    else {
      status = OnlineHelper.STATUS_LOGIN_ERROR;
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
      activity.didFailToReceiveApps(status);
    }
    else {
      activity.didReceiveApps(this.apps);
    }
  }
}
