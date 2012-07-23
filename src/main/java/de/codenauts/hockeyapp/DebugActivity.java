package de.codenauts.hockeyapp;

import java.util.List;

import net.hockeyapp.android.R;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.WebView;

public class DebugActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.debug_view);
    setTitle(null);
    
    loadListOfApps();
  }

  private void loadListOfApps() {
    StringBuilder builder = new StringBuilder();
    builder.append("<html><head><style type='text/css'>h2 { font-size: 110% };</style></head></body>");
    
    PackageManager packageManager = getPackageManager();
    List<PackageInfo> packages = packageManager.getInstalledPackages(0);
    for (PackageInfo packageInfo : packages) {
      builder.append("<h2>" + packageInfo.packageName + "</h2>");
      builder.append("<dl>");
      builder.append("<dt>Version Name:</dt>");
      builder.append("<dd>" + packageInfo.versionName + "</dd>");
      builder.append("<dt>Version Code:</dt>");
      builder.append("<dd>" + packageInfo.versionCode + "</dd>");
      builder.append("</dl>");
    }

    builder.append("</body></html>");
    
    WebView webView = (WebView)findViewById(R.id.web_view);
    webView.loadData(builder.toString(), "text/html", "UTF-8");
  }
}
