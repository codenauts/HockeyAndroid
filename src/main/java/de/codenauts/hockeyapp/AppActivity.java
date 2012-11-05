package de.codenauts.hockeyapp;

import net.hockeyapp.android.UpdateActivity;
import net.hockeyapp.android.internal.DownloadFileListener;
import net.hockeyapp.android.internal.UpdateView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.codenauts.hockeyapp.util.ActivityHelper;

public class AppActivity extends UpdateActivity {
  private final static int RESTORE_DIALOG = 0;

  private final ActivityHelper activityHelper = ActivityHelper.createInstance(this);

  private ImageLoader imageLoader;
  private String versionURL;

  public void onCreate(Bundle savedInstanceState) {
    imageLoader = new ImageLoader(getApplicationContext());

    super.onCreate(savedInstanceState);
    setTitle("App Details");

    activityHelper.setupActionBar(getTitle(), Color.BLACK);
    activityHelper.setupHomeAsUp();
  }

  @SuppressWarnings("deprecation")
  @Override
  protected Dialog onCreateDialog(int id) {
    if (id == RESTORE_DIALOG) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      AlertDialog alert = builder.create();
      builder.setTitle("Restore Old Version").setMessage("Please note that installing an old version might lead to data loss or other side effects. Are you sure?").setPositiveButton("Restore", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          startDownloadTask(versionURL);
        }
      }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
        }
      });
      alert = builder.create();
      return alert;
    }
    else {
      return super.onCreateDialog(id);
    }
  }

  protected void configureView() {
    super.configureView();

    final String identifier = getIntent().getStringExtra("identifier");
    final String apiURL = "https://rink.hockeyapp.net/api/2/apps/" + identifier;

    ImageView iconView = (ImageView)findViewById(AppView.ICON_VIEW_ID);
    try {
      imageLoader.displayImage(apiURL + "?format=png", this, iconView);
    }
    catch (Exception e) {
      iconView.setImageBitmap(null);
    }

    TextView titleLabel = (TextView)findViewById(UpdateView.NAME_LABEL_ID);
    titleLabel.setText(getIntent().getStringExtra("title"));

    Button downloadButton = (Button)findViewById(UpdateView.UPDATE_BUTTON_ID);
    downloadButton.setText("Download");
    
    WebView webView = (WebView)findViewById(UpdateView.WEB_VIEW_ID);
    webView.setWebViewClient(new WebViewClient() {
      @SuppressWarnings("deprecation")
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url != null) {
          if (url.startsWith("restore:")) {
            versionURL = apiURL + "/app_versions/" + url.replace("restore:", "") + "?format=apk"; 
            showDialog(RESTORE_DIALOG);
            return true;
          }
        }
        
        return false; 
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
      Intent intent = new Intent(this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  public int getCurrentVersionCode() {
    return -1;
  }

  protected String getReleaseNotes() {
    return versionHelper.getReleaseNotes(true);
  }

  public ViewGroup getLayoutView() {
    return (ViewGroup)getLayoutInflater().inflate(R.layout.app_view, null);
  }

  protected void createDownloadTask(String url, DownloadFileListener listener) {
    downloadTask = new AppDownloadTask(this, url, getIntent().getStringExtra("token"), listener);
  }
}
