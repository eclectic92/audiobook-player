package com.natalieryan.android.superaudiobookplayer.activities.settings;

import android.preference.PreferenceActivity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.activities.main.BaseActivity;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */

public class SettingsActivity extends BaseActivity implements SettingsFragment.SetNightMode {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
		if (id == android.R.id.home) {
			super.onBackPressed();
			overridePendingTransition(R.anim.swap_in_bottom, R.anim.swap_out_bottom);
		}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNightModeSelected(int nightMode)
	{
		getDelegate().setLocalNightMode(nightMode);
		recreate();
	}
}