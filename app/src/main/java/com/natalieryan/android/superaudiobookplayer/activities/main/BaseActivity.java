package com.natalieryan.android.superaudiobookplayer.activities.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

import com.natalieryan.android.superaudiobookplayer.R;

/**
 * Created by natalier258 on 7/24/17.
 *
 */

@SuppressWarnings("unused")
public abstract class BaseActivity extends AppCompatActivity
{
	private final static String TAG=BaseActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		final SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		int nightMode=Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_night_mode_menu_key),
				getString(R.string.pref_night_mode_value_off)));

		getDelegate().setLocalNightMode(nightMode);
		super.onCreate(savedInstanceState);
	}
}
