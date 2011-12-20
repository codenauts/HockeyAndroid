package de.codenauts.hockeyapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class OnlineHelper {
  final static public String BASE_URL = "https://rink.hockeyapp.net/api/2/";
  final static public String AUTH_ACTION = "auth_tokens";
  final static public String APPS_ACTION = "apps?format=json";

  public static String getStringFromConnection(HttpURLConnection connection) throws IOException {
    InputStream inputStream = new BufferedInputStream(connection.getInputStream());
    String jsonString = convertStreamToString(inputStream);
    inputStream.close();
    
    return jsonString;
  }

  private static String convertStreamToString(InputStream inputStream) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 1024);
    StringBuilder stringBuilder = new StringBuilder();

    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line + "\n");
      }
    } 
    catch (IOException e) {
      e.printStackTrace();
    } 
    finally {
      try {
        inputStream.close();
      } 
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return stringBuilder.toString();
  }

}
