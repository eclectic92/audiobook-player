package com.natalieryan.android.superaudiobookplayer.activities.filebrowser;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;

import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;

import com.natalieryan.android.superaudiobookplayer.R;

public class FileBrowserActivity extends AppCompatActivity
{

	private FileBrowserFragment mBrowserFragment;
	private static final String SHOW_FOLDERS_ONLY = "show_folders_only";
	private int mNightMode;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if(savedInstanceState == null){
			final SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			mNightMode = Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_night_mode_menu_key),
					getString(R.string.pref_night_mode_value_off)));
		}
		else
		{
			if(savedInstanceState.containsKey(getString(R.string.pref_night_mode_menu_key)))
			{
				mNightMode = savedInstanceState.getInt(getString(R.string.pref_night_mode_menu_key),
						Integer.valueOf(getString(R.string.pref_night_mode_value_off)));
			}
		}

		getDelegate().setLocalNightMode(mNightMode);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder_browser);
		FragmentManager fragmentManager=getSupportFragmentManager();
		ActionBar actionBar = this.getSupportActionBar();

		// Set the action bar back button to look like an up button
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		if(savedInstanceState == null)
		{
			mBrowserFragment = new FileBrowserFragment();
			Intent intent = getIntent();
			if(intent != null)
			{
				if (intent.hasExtra(SHOW_FOLDERS_ONLY))
				{
					Bundle args = new Bundle();
					args.putInt(SHOW_FOLDERS_ONLY, intent.getIntExtra(SHOW_FOLDERS_ONLY, 1));
					mBrowserFragment.setArguments(args);
				}
			}


			fragmentManager.beginTransaction()
					.add(R.id.folder_list_container, mBrowserFragment).commit();
		}
		else
		{
			mBrowserFragment = (FileBrowserFragment) fragmentManager.findFragmentById(R.id.folder_list_container);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(getString(R.string.pref_night_mode_menu_key), mNightMode);
	}

	@Override
	public void onBackPressed() {
		if(mBrowserFragment.isAtTopLevel()){
			super.onBackPressed();
		}
		else{
			mBrowserFragment.navigateBack();
		}
	}
}
