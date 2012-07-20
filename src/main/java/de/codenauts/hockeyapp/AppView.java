package de.codenauts.hockeyapp;

import net.hockeyapp.android.internal.UpdateView;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class AppView extends UpdateView {
  public final static int ICON_VIEW_ID = 0x2001;

  public AppView(Context context) {
    this(context, null);
  }

  public AppView(Context context, AttributeSet attrs) {
    this(context, true, false);
  }

  public AppView(Context context, boolean allowHorizontalLayout, boolean limitHeight) {
    super(context, allowHorizontalLayout, limitHeight);

    loadIconView(headerView, context);
    changeParamsForTitleLabel(headerView);
    changeParamsForVersionLabel(headerView);
    changeParamsForUpdateButton(headerView);
  }

  private void loadIconView(RelativeLayout headerView, Context context) {
    ImageView iconView = new ImageView(context); 
    iconView.setId(ICON_VIEW_ID);

    int size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)48.0, getResources().getDisplayMetrics());
    LayoutParams params = new LayoutParams(size, size);

    int margin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)20.0, getResources().getDisplayMetrics());
    params.setMargins(margin, margin, margin, 0);
    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
    if (layoutHorizontally) {
      params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
    }
    else {
      params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
    }
    iconView.setLayoutParams(params);
    
    headerView.addView(iconView);
  }
  
  private void changeParamsForTitleLabel(RelativeLayout headerView) {
    TextView titleLabel = (TextView)headerView.findViewById(NAME_LABEL_ID);

    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    int margin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)20.0, getResources().getDisplayMetrics());
    params.setMargins(margin, margin, 0, 0);
    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
    if (layoutHorizontally) {
      params.addRule(RelativeLayout.BELOW, ICON_VIEW_ID);
      titleLabel.setLines(0);
      titleLabel.setSingleLine(false);
    }
    else {
      params.addRule(RelativeLayout.LEFT_OF, ICON_VIEW_ID);
    }
    titleLabel.setLayoutParams(params);
  }

  private void changeParamsForVersionLabel(RelativeLayout headerView) {
    TextView versionLabel = (TextView)headerView.findViewById(VERSION_LABEL_ID);

    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    int marginSide = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)20.0, getResources().getDisplayMetrics());
    int marginTop = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)10.0, getResources().getDisplayMetrics());
    params.setMargins(marginSide, marginTop, marginSide, 0);
    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
    params.addRule(RelativeLayout.BELOW, NAME_LABEL_ID);
    if (!layoutHorizontally) {
      params.addRule(RelativeLayout.LEFT_OF, ICON_VIEW_ID);
    }
    versionLabel.setLayoutParams(params);
  }
  
  private void changeParamsForUpdateButton(RelativeLayout headerView) {
    if (layoutHorizontally) {
      TextView updateButton = (TextView)headerView.findViewById(UPDATE_BUTTON_ID);
  
      int margin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)20.0, getResources().getDisplayMetrics());
      int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)120.0, getResources().getDisplayMetrics());
      
      LayoutParams params = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
      params.setMargins(margin, margin, margin, margin);
      params.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
      params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
      updateButton.setLayoutParams(params);
    }
  }
}
