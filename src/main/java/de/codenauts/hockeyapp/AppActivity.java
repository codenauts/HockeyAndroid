package de.codenauts.hockeyapp;

import net.hockeyapp.android.UpdateActivity;
import net.hockeyapp.android.internal.UpdateView;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.codenauts.hockeyapp.util.ActivityHelper;
import de.codenauts.hockeyapp.util.UIUtils;

public class AppActivity extends UpdateActivity {
  final ActivityHelper activityHelper = ActivityHelper.createInstance(this);
  
  private ImageLoader imageLoader;

  public void onCreate(Bundle savedInstanceState) {
    imageLoader = new ImageLoader(getApplicationContext());

    super.onCreate(savedInstanceState);

    setTitle(null);
    activityHelper.setupActionBar(null, Color.BLACK);
    
    if (UIUtils.isHoneycomb()) {
      ActionBar actionBar = getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowTitleEnabled(false);
    }
  }

  protected void configureView() {
    super.configureView();
    
    ImageView iconView = (ImageView)findViewById(AppView.ICON_VIEW_ID);
    try {
      String identifier = getIntent().getStringExtra("identifier");
      String url = "https://rink.hockeyapp.net/api/2/apps/" + identifier + "?format=png";
      imageLoader.displayImage(url, this, iconView);
    }
    catch (Exception e) {
      iconView.setImageBitmap(null);
    }

    TextView titleLabel = (TextView)findViewById(UpdateView.NAME_LABEL_ID);
    titleLabel.setText(getIntent().getStringExtra("title"));
    
    Button downloadButton = (Button)findViewById(UpdateView.UPDATE_BUTTON_ID);
    downloadButton.setText("Download");
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

  public ViewGroup getLayoutView() {
    return (ViewGroup)getLayoutInflater().inflate(R.layout.app_view, null);
  }
}
