package com.natalieryan.android.superaudiobookplayer.activities.filebrowser;


import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;

import android.os.Bundle;

import com.natalieryan.android.superaudiobookplayer.R;

public class FileBrowserActivity extends AppCompatActivity
{

	private FileBrowserFragment mBrowserFragment;
	private static final String SHOW_FOLDERS_ONLY = "show_folders_only";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
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
	public void onBackPressed() {
		if(mBrowserFragment.isAtTopLevel()){
			super.onBackPressed();
		}
		else{
			mBrowserFragment.navigateBack();
		}
	}
}
