/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.codenauts.hockeyapp.util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.codenauts.hockeyapp.MainActivity;
import de.codenauts.hockeyapp.R;

/**
 * A class that handles some common activity-related functionality in the app, such as setting up
 * the action bar. This class provides functionality useful for both phones and tablets, and does
 * not require any Android 3.0-specific features.
 */
public class ActivityHelper {
  protected Activity activity;

  /**
   * Factory method for creating {@link ActivityHelper} objects for a given activity. Depending
   * on which device the app is running, either a basic helper or Honeycomb-specific helper will
   * be returned.
   */
  public static ActivityHelper createInstance(Activity activity) {
    return UIUtils.isHoneycomb() ?
        new ActivityHelperHoneycomb(activity) :
          new ActivityHelper(activity);
  }

  protected ActivityHelper(Activity activity) {
    this.activity = activity;
  }

  public void onPostCreate(Bundle savedInstanceState) {
    // Create the action bar
    SimpleMenu menu = new SimpleMenu(activity);
    activity.onCreatePanelMenu(Window.FEATURE_OPTIONS_PANEL, menu);
    // TODO: call onPreparePanelMenu here as well
    for (int i = 0; i < menu.size(); i++) {
      MenuItem item = menu.getItem(i);
      addActionButtonCompatFromMenuItem(item);
    }
  }

  /**
   * Method, to be called in <code>onPostCreate</code>, that sets up this activity as the
   * home activity for the app.
   */
  public void setupHomeActivity() {
  }

  /**
   * Method, to be called in <code>onPostCreate</code>, that sets up this activity as a
   * sub-activity in the app.
   */
  public void setupSubActivity() {
  }

  /**
   * Invoke "home" action, returning to {@link com.google.android.apps.iosched.ui.HomeActivity}.
   */
  public void goHome() {
    if (activity instanceof MainActivity) {
      return;
    }

    final Intent intent = new Intent(activity, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    activity.startActivity(intent);

    if (!UIUtils.isHoneycomb()) {
      activity.overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
    }
  }

  /**
   * Invoke "search" action, triggering a default search.
   */
  public void goSearch() {
    activity.startSearch(null, false, Bundle.EMPTY, false);
  }

  /**
   * Sets up the action bar with the given title and accent color. If title is null, then
   * the app logo will be shown instead of a title. Otherwise, a home button and title are
   * visible. If color is null, then the default colorstrip is visible.
   */
  public void setupActionBar(CharSequence title, int color) {
    final ViewGroup actionBarCompat = getActionBarCompat();
    if (actionBarCompat == null) {
      return;
    }

    LinearLayout.LayoutParams springLayoutParams = new LinearLayout.LayoutParams(0,
        ViewGroup.LayoutParams.MATCH_PARENT);
    springLayoutParams.weight = 1;

    View.OnClickListener homeClickListener = new View.OnClickListener() {
      public void onClick(View view) {
        goHome();
      }
    };

    // Add Home button
    addActionButtonCompat(R.drawable.icon, R.string.app_name, homeClickListener, true);

    if (title != null) {
      // Add title text
      TextView titleText = new TextView(activity, null, R.attr.actionbarCompatTextStyle);
      titleText.setLayoutParams(springLayoutParams);
      titleText.setText(title);
      actionBarCompat.addView(titleText);

    }

    setActionBarColor(color);
  }
  
  public void setupHomeAsUp() {
    if (UIUtils.isHoneycomb()) {
      ActionBar actionBar = activity.getActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setDisplayShowTitleEnabled(false);
    }
  }
  
  public void hideTitle() {
    if (UIUtils.isHoneycomb()) {
      ActionBar actionBar = activity.getActionBar();
      actionBar.setDisplayShowTitleEnabled(false);
    }
  }

  /**
   * Sets the action bar color to the given color.
   */
  public void setActionBarColor(int color) {
    if (color == 0) {
      return;
    }

    final View colorstrip = activity.findViewById(R.id.colorstrip);
    if (colorstrip == null) {
      return;
    }

    colorstrip.setBackgroundColor(color);
  }

  /**
   * Sets the action bar title to the given string.
   */
  public void setActionBarTitle(CharSequence title) {
    ViewGroup actionBar = getActionBarCompat();
    if (actionBar == null) {
      return;
    }

    TextView titleText = (TextView) actionBar.findViewById(R.id.actionbar_compat_text);
    if (titleText != null) {
      titleText.setText(title);
    }
  }

  /**
   * Returns the {@link ViewGroup} for the action bar on phones (compatibility action bar).
   * Can return null, and will return null on Honeycomb.
   */
  public ViewGroup getActionBarCompat() {
    return (ViewGroup) activity.findViewById(R.id.actionbar_compat);
  }

  /**
   * Adds an action bar button to the compatibility action bar (on phones).
   */
  private View addActionButtonCompat(int iconResId, int textResId,
      View.OnClickListener clickListener, boolean separatorAfter) {
    final ViewGroup actionBar = getActionBarCompat();
    if (actionBar == null) {
      return null;
    }

    // Create the separator
    ImageView separator = new ImageView(activity, null, R.attr.actionbarCompatSeparatorStyle);
    separator.setLayoutParams(
        new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.MATCH_PARENT));

    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    layoutParams.setMargins(10, 0, 0, 0);

    // Create the button
    ImageButton actionButton = new ImageButton(activity, null,
        R.attr.actionbarCompatButtonStyle);
    actionButton.setLayoutParams(layoutParams);
    actionButton.setImageResource(iconResId);
    actionButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
    actionButton.setContentDescription(activity.getResources().getString(textResId));
    actionButton.setOnClickListener(clickListener);

    actionBar.addView(actionButton);

    return actionButton;
  }

  /**
   * Adds an action button to the compatibility action bar, using menu information from a
   * {@link MenuItem}. If the menu item ID is <code>menu_refresh</code>, the menu item's state
   * can be changed to show a loading spinner using
   * {@link ActivityHelper#setRefreshActionButtonCompatState(boolean)}.
   */
  private View addActionButtonCompatFromMenuItem(final MenuItem item) {
    final ViewGroup actionBar = getActionBarCompat();
    if (actionBar == null) {
      return null;
    }

    // Create the separator
    ImageView separator = new ImageView(activity, null, R.attr.actionbarCompatSeparatorStyle);
    separator.setLayoutParams(
        new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.MATCH_PARENT));

    // Create the button
    ImageButton actionButton = new ImageButton(activity, null,
        R.attr.actionbarCompatButtonStyle);
    actionButton.setId(item.getItemId());
    actionButton.setLayoutParams(new ViewGroup.LayoutParams(
        (int) activity.getResources().getDimension(R.dimen.actionbar_compat_height),
        ViewGroup.LayoutParams.MATCH_PARENT));
    actionButton.setImageDrawable(item.getIcon());
    actionButton.setScaleType(ImageView.ScaleType.CENTER);
    actionButton.setContentDescription(item.getTitle());
    actionButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        activity.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, item);
      }
    });

    actionBar.addView(separator);
    actionBar.addView(actionButton);

    if (item.getItemId() == R.id.menu_refresh) {
      // Refresh buttons should be stateful, and allow for indeterminate progress indicators,
      // so add those.
      int buttonWidth = activity.getResources()
          .getDimensionPixelSize(R.dimen.actionbar_compat_height);
      int buttonWidthDiv3 = buttonWidth / 3;
      ProgressBar indicator = new ProgressBar(activity, null,
          R.attr.actionbarCompatProgressIndicatorStyle);
      LinearLayout.LayoutParams indicatorLayoutParams = new LinearLayout.LayoutParams(
          buttonWidthDiv3, buttonWidthDiv3);
      indicatorLayoutParams.setMargins(buttonWidthDiv3, buttonWidthDiv3,
          buttonWidth - 2 * buttonWidthDiv3, 0);
      indicator.setLayoutParams(indicatorLayoutParams);
      indicator.setVisibility(View.GONE);
      indicator.setId(R.id.menu_refresh_progress);
      actionBar.addView(indicator);
    }

    return actionButton;
  }

  /**
   * Sets the indeterminate loading state of a refresh button added with
   * {@link ActivityHelper#addActionButtonCompatFromMenuItem(android.view.MenuItem)}
   * (where the item ID was menu_refresh).
   */
  public void setRefreshActionButtonCompatState(boolean refreshing) {
    View refreshButton = activity.findViewById(R.id.menu_refresh);
    View refreshIndicator = activity.findViewById(R.id.menu_refresh_progress);

    if (refreshButton != null) {
      refreshButton.setVisibility(refreshing ? View.GONE : View.VISIBLE);
    }
    if (refreshIndicator != null) {
      refreshIndicator.setVisibility(refreshing ? View.VISIBLE : View.GONE);
    }
  }
}
