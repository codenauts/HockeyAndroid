package de.codenauts.hockeyapp;

import java.util.ArrayList;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.R;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.UpdateManagerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import de.codenauts.hockeyapp.util.ActivityHelper;
import de.codenauts.hockeyapp.util.UIUtils;

public class MainActivity extends Activity implements OnItemClickListener {
  final static int DIALOG_LOGIN = 1;

  final ActivityHelper activityHelper = ActivityHelper.createInstance(this);

  private AlertDialog alert;
  private AppsAdapter appsAdapter;
  private AppsTask appsTask;
  private AppTask appTask;
  private JSONArray apps;
  private LoginTask loginTask;
  private int selectedAppIndex;
  private View selectedAppView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main_view);
    setTitle(null);
    activityHelper.setupActionBar(null, Color.BLACK);
    
    if (UIUtils.isHoneycomb()) {
      ActionBar actionBar = getActionBar();
      actionBar.setDisplayShowTitleEnabled(false);
    }
    
    System.setProperty("http.keepAlive", "false");
    if (savedInstanceState == null) {
      checkForUpdates(false);
    }

    loadApps(savedInstanceState);
  }

  private void checkForUpdates(final Boolean notify) {
    UpdateManager.register(this, "0873e2b98ad046a92c170a243a8515f6", new UpdateManagerListener() {
      @Override
      public Class<?> getUpdateActivityClass() {
        return CustomUpdateActivity.class;
      }
      
      @Override
      public void onNoUpdateAvailable() {
        if ((!isFinishing()) && (notify)) {
          Toast.makeText(MainActivity.this, R.string.main_view_no_update_label, Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  @Override 
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    
    MenuInflater inflater = getMenuInflater(); 
    inflater.inflate(R.menu.main_menu, menu);
    
    return true; 
  } 
  
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem refreshItem = menu.getItem(1);
    MenuItem logoutItem = menu.getItem(2);
    
    if (getAPIToken() == null) {
      refreshItem.setEnabled(false);
      logoutItem.setTitle("Sign in");
    }
    else {
      refreshItem.setEnabled(true);
      logoutItem.setTitle("Sign out");
    }
    
    return true;
  }

  @Override 
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_update) {
      checkForUpdates(true);
    }
    else {
      stopRunningTasks();
      
      if (item.getItemId() == R.id.menu_logout) {
        setAPIToken(null);
        setStatus(getResources().getString(R.string.main_view_signed_out_label));
      }
  
      View appsView = (View)findViewById(R.id.apps_view);
      appsView.setVisibility(View.INVISIBLE);
      
      this.apps = null;
      loadApps(null);
    }
    
    return true;
  }

  private void stopRunningTasks() {
    if (appsTask != null) {
      appsTask.cancel(true);
      appsTask.detach();
      appsTask = null;
    }
    
    if (appTask != null) {
      appTask.cancel(true);
      appTask.detach();
      appTask = null;
    }
  }

  private void loadApps(Bundle savedInstanceState) {
    
    if (savedInstanceState != null) {
      String json = savedInstanceState.getString("apps");
      try {
        this.apps = new JSONArray(json);
        didReceiveApps(this.apps);
      }
      catch (JSONException e) {
      }
      catch (NullPointerException e) {
      }
    }
    
    if (this.apps == null) {
      String token = getAPIToken();
      if (token == null) {
        if (savedInstanceState == null) {
          showDialog(DIALOG_LOGIN);
        }
      }
      else {
        getApps(token);
      }
    }
  }

  private void getApps(String token) {
    appsTask = new AppsTask(this, token);
    appsTask.execute();

    setStatus(R.string.main_view_searching_apps_label);
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

    checkForCrashes();

    Object instance = getLastNonConfigurationInstance();
    if (instance instanceof LoginTask) {
      loginTask = (LoginTask)instance;
      if (loginTask != null) {
        loginTask.attach(this);
      }
    }
    else if (instance instanceof AppsTask) {
      appsTask = (AppsTask)instance;
      if (appsTask != null) {
        appsTask.attach(this);
      }
    }
    else if (instance instanceof AppTask) {
      appTask = (AppTask)instance;
      if (appTask != null) {
        appTask.attach(this);
      }
    }
  }

  private void checkForCrashes() {
    CrashManager.register(this, "0873e2b98ad046a92c170a243a8515f6");
  }

  @Override
  protected void onSaveInstanceState (Bundle outState) {
    if (this.apps != null) {
      outState.putString("apps", this.apps.toString());
    }
;  }

  @Override
  public Object onRetainNonConfigurationInstance() {
    if (loginTask != null) {
      loginTask.detach();
      return loginTask;
    }
    else if (appsTask != null) {
      appsTask.detach();
      return appsTask;
    }
    else if (appTask != null) {
      appTask.detach();
      return appTask;
    }
    else {
      return null;
    }
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

        setStatus(R.string.main_view_signing_in_label);
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

  private void setStatus(int stringID) {
    setStatus(getResources().getString(stringID));
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
    setStatus("Connection failed. Please try again or check your credentials.");
  }

  @SuppressWarnings("unchecked")
  public void didReceiveApps(JSONArray apps) {
    this.apps = apps;
    
    if (apps.length() == 0) {
      setStatus("No apps found.");
    }
    else {
      ArrayList<JSONObject> androidApps = new ArrayList<JSONObject>();

      int count = 0;
      for (int index = 0; index < apps.length(); index++) {
        try {
          JSONObject app = apps.getJSONObject(index);
          if (((app.has("platform")) && (app.getString("platform").equals("Android"))) &&
              ((app.has("release_type")) && (app.getInt("release_type") == 0))) {
            count++;

            androidApps.add(app);
          }
        }
        catch (JSONException e) {
        }
      }

      @SuppressWarnings("rawtypes")
      AdapterView listView = (AdapterView)findViewById(R.id.apps_view);
      if (count == 0) {
        listView.setVisibility(View.INVISIBLE);
        setStatus("No apps found.");
      }
      else {
        appsAdapter = new AppsAdapter(this, androidApps);

        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(appsAdapter);
        listView.setOnItemClickListener(this);
        setStatus("");
      }
    }
  }

  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (selectedAppView != null) {
      ProgressBar progressBar = (ProgressBar)selectedAppView.findViewById(R.id.progress_bar);
      progressBar.setVisibility(View.INVISIBLE);
    }

    if (appTask != null) {
      appTask.cancel(true);
      appTask = null;
    }

    selectedAppIndex = position;
    selectedAppView = view;
    
    ProgressBar progressBar = (ProgressBar)selectedAppView.findViewById(R.id.progress_bar);
    progressBar.setVisibility(View.VISIBLE);

    JSONObject app = (JSONObject)appsAdapter.getItem(position);
    try {
      String identifier = app.getString("public_identifier");
      appTask = new AppTask(this, "https://rink.hockeyapp.net/", identifier);
      appTask.execute();
    }
    catch (JSONException e) {
      progressBar.setVisibility(View.INVISIBLE);
    }
  }

  public void didReceiveAppInfo(JSONArray updateInfo, String apkURL) {
    if (selectedAppView != null) {
      ProgressBar progressBar = (ProgressBar)selectedAppView.findViewById(R.id.progress_bar);
      progressBar.setVisibility(View.INVISIBLE);
    }

    if (updateInfo != null) {
      JSONObject app = (JSONObject)appsAdapter.getItem(selectedAppIndex);
      try {
        String identifier = app.getString("public_identifier");
        String title = app.getString("title");

        Intent intent = new Intent(this, AppActivity.class);
        intent.putExtra("identifier", identifier);
        intent.putExtra("title", title);
        intent.putExtra("json", updateInfo.toString());
        intent.putExtra("url", apkURL);
        startActivity(intent);
      }
      catch (JSONException e) {
      }
    }
  }
}