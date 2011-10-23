package de.codenauts.hockeyapp;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppsAdapter extends BaseAdapter {
  public ImageLoader imageLoader;

  private Activity activity;
  private ArrayList<JSONObject> apps;
  private LayoutInflater inflater = null;

  public AppsAdapter(Activity activity, ArrayList<JSONObject> apps) {
    this.activity = activity;
    this.imageLoader = new ImageLoader(activity.getApplicationContext());
    this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.apps = apps;
  }

  public int getCount() {
    return apps.size();
  }

  public Object getItem(int position) {
    return apps.get(position);
  }

  public long getItemId(int position) {
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    View view = convertView;
    if (convertView == null) {
      view = inflater.inflate(R.layout.app_list_item, null);
    }

    JSONObject app = (JSONObject)getItem(position);
    TextView titleLabel = (TextView)view.findViewById(R.id.title_label);
    try {
      if (app.has("title")) {
        titleLabel.setText(app.getString("title"));
      }
      else {
        titleLabel.setText("Unknown");
      }
    }
    catch (Exception e) {
      titleLabel.setText("Unknown");
    }

    TextView ownerLabel = (TextView)view.findViewById(R.id.owner_label);
    try {
      if (app.has("company")) {
        ownerLabel.setText(app.getString("company"));
      }
      else {
        if (app.has("owner")) {
          ownerLabel.setText("owner");
        }
        else {
          ownerLabel.setText("Unknown");
        }
      }
    }
    catch (Exception e) {
      ownerLabel.setText("Unknown");
    }

    ImageView iconView = (ImageView)view.findViewById(R.id.icon_view);
    try {
      if (app.has("public_identifier")) {
        String identifier = app.getString("public_identifier");
        String url = "https://rink.hockeyapp.net/api/2/apps/" + identifier + "?format=png";
        imageLoader.displayImage(url, activity, iconView);
      }
    }
    catch (Exception e) {
      iconView.setImageBitmap(null);
    }

    return view;
  }
}
