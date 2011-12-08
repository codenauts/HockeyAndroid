package de.codenauts.hockeyapp;

import de.codenauts.hockeyapp.util.ActivityHelper;
import android.graphics.Color;
import android.os.Bundle;
import net.hockeyapp.android.R;
import net.hockeyapp.android.UpdateActivity;

public class CustomUpdateActivity extends UpdateActivity {
  final ActivityHelper activityHelper = ActivityHelper.createInstance(this);

  public void onCreate(Bundle savedInstanceState) {
    iconDrawableId = R.drawable.icon;
    
    super.onCreate(savedInstanceState);

    setTitle("Version Info");
    activityHelper.setupActionBar(getTitle(), Color.BLACK);
  }
  
  public int getLayout() {
    return R.layout.custom_update_view;
  }
}
