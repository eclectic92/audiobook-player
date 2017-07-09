package com.natalieryan.android.superaudiobookplayer.ui.folderbrowser;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.natalieryan.android.superaudiobookplayer.R;

public class FolderBrowserActivity extends AppCompatActivity
{

	private FolderBrowserFragment mBrowserFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder_browser);
		if(savedInstanceState == null)
		{
			mBrowserFragment = new FolderBrowserFragment();

			getSupportFragmentManager().beginTransaction()
					.add(R.id.folder_list_container, mBrowserFragment).commit();
		}
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		mBrowserFragment.backButtonWasPressed();
	}
}
