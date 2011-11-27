package de.codenauts.hockeyapp;

import net.hockeyapp.android.CheckUpdateTask;

import org.json.JSONArray;

public class AppTask extends CheckUpdateTask {
  private MainActivity activity;
  
  public AppTask(MainActivity activity, String urlString, String appIdentifier) {
    super(activity, urlString, appIdentifier);
    
    this.activity = activity;
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
}
