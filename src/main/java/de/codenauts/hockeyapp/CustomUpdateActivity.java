package de.codenauts.hockeyapp;

import de.codenauts.hockeyapp.util.ActivityHelper;
import de.codenauts.hockeyapp.util.UIUtils;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import net.hockeyapp.android.UpdateActivity;

public class CustomUpdateActivity extends UpdateActivity {
  final ActivityHelper activityHelper = ActivityHelper.createInstance(this);

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setTitle(null);
    activityHelper.setupActionBar(null, Color.BLACK);
    
    if (UIUtils.isHoneycomb()) {
      ActionBar actionBar = getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowTitleEnabled(false);
    }
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

  public ViewGroup getLayoutView() {
    return (ViewGroup)getLayoutInflater().inflate(R.layout.custom_update_view, null);
  }
}
