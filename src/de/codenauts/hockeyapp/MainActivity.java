package de.codenauts.hockeyapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
  final static int DIALOG_LOGIN = 1;

  private AlertDialog alert;
  private AppsTask appsTask;
  private LoginTask loginTask;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_view);
    moveViewBelowOrBesideHeader(this, R.id.content_view, R.id.header_view, 5);

    System.setProperty("http.keepAlive", "false");
    
    loadApps();
  }

  private void loadApps() {
    String token = getAPIToken();
    if (token == null) {
      showDialog(DIALOG_LOGIN);
    }
    else {
      getApps(token);
    }
  }

  private void getApps(String token) {
    appsTask = new AppsTask(this, token);
    appsTask.execute();
    
    setStatus("Searching for apps…");
  }

  protected Dialog onCreateDialog(int id) {
    Dialog dialog = null;
    switch (id) {
    case DIALOG_LOGIN:
      dialog = createLoginDialog();
      break;
    }

    return dialog;
  }
  
  @Override
  public void onResume() {
    super.onResume();
    
    loginTask = (LoginTask)getLastNonConfigurationInstance();
    if (loginTask != null) {
      loginTask.attach(this);
    }
  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    if (loginTask != null) {
      loginTask.detach();
      return loginTask;
    }
    else {
      return null;
    }
  }

  private static void moveViewBelowOrBesideHeader(Activity activity, int viewID, int headerID, float offset) {
    ViewGroup headerView = (ViewGroup)activity.findViewById(headerID); 
    View view = (View)activity.findViewById(viewID);
    float density = activity.getResources().getDisplayMetrics().density; 
    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, activity.getWindowManager().getDefaultDisplay().getHeight() - headerView.getHeight() + (int)(offset * density));
    if (((String)view.getTag()).equalsIgnoreCase("right")) {
      layoutParams.addRule(RelativeLayout.RIGHT_OF, headerID);
      layoutParams.setMargins(-(int)(offset * density), 0, 0, (int)(10 * density));
    }
    else {
      layoutParams.addRule(RelativeLayout.BELOW, headerID);
      layoutParams.setMargins(0, -(int)(offset * density), 0, (int)(10 * density));
    }
    view.setLayoutParams(layoutParams);
  }

  private Dialog createLoginDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Sign In");

    builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        String email = ((EditText)alert.findViewById(R.id.email_field)).getText().toString();
        String password = ((EditText)alert.findViewById(R.id.password_field)).getText().toString();

        loginTask = new LoginTask(MainActivity.this, email, password);
        loginTask.execute();

        setStatus("Signing in…");
      }
    });

    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
      }
    });

    alert = builder.create();

    LayoutInflater inflater = alert.getLayoutInflater();
    View view = inflater.inflate(R.layout.login_view, null, false);
    
    alert.setView(view);
    
    return alert;
  }
  
  private String getAPIToken() {
    SharedPreferences preferences = getSharedPreferences("HockeyApp", Context.MODE_PRIVATE);
    return preferences.getString("APIToken", null);
  }
  
  private void setAPIToken(String token) {
    SharedPreferences preferences = getSharedPreferences("HockeyApp", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString("APIToken", token);
    editor.commit();
  }
  
  private void setStatus(String status) {
    TextView statusLabel = (TextView)findViewById(R.id.status_label);
    statusLabel.setText(status);
  }

  public void loginWasSuccesful(String token) {
    loginTask = null;
    setAPIToken(token);
    getApps(token);
  }

  public void loginFailed() {
    loginTask = null;
    Toast.makeText(this, R.string.login_view_failed_toast, Toast.LENGTH_LONG).show();
    showDialog(DIALOG_LOGIN);
    setStatus(getResources().getString(R.string.main_view_signed_out_label));
  }

  public void didFailToReceiveApps() {
    setStatus("Connection failed. Please try again or renew your credentials.");
  }

  public void didReceiveApps(JSONArray apps) {
    if (apps.length() == 0) {
      setStatus("No apps found.");
    }
    else {
      int count = 0;
      for (int index = 0; index < apps.length(); index++) {
        try {
          JSONObject app = apps.getJSONObject(index);
          if ((app.has("platform")) && (app.getString("platform").equals("Android"))) {
            count++;
          }
        }
        catch (JSONException e) {
        }
      }

      if (count == 0) {
        setStatus("No apps found.");
      }
      else {
        setStatus(count + " app(s) found.");
      }
    }
  }
}