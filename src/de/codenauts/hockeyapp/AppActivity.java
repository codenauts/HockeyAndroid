package de.codenauts.hockeyapp;

import net.hockeyapp.android.UpdateActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AppActivity extends UpdateActivity {
  private ImageLoader imageLoader;

  public void onCreate(Bundle savedInstanceState) {
    this.imageLoader = new ImageLoader(getApplicationContext());

    super.onCreate(savedInstanceState);

    setTitle("App Info");
  }

  protected void configureView() {
    super.configureView();
    
    ImageView iconView = (ImageView)findViewById(resources.getItemId("icon_view"));
    try {
      String identifier = getIntent().getStringExtra("identifier");
      String url = "https://rink.hockeyapp.net/api/2/apps/" + identifier + "?format=png";
      imageLoader.displayImage(url, this, iconView);
    }
    catch (Exception e) {
      iconView.setImageBitmap(null);
    }

    TextView titleLabel = (TextView)findViewById(resources.getItemId("name_label"));
    titleLabel.setText(getIntent().getStringExtra("title"));
    
    ImageButton downloadButton = (ImageButton)findViewById(resources.getItemId("hockey_update_button"));
    downloadButton.setImageDrawable(getResources().getDrawable(R.drawable.button_download));
  }
}
