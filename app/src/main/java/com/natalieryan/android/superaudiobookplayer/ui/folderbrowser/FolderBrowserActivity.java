package com.natalieryan.android.superaudiobookplayer.ui.folderbrowser;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;

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
		FragmentManager fragmentManager=getSupportFragmentManager();

		if(savedInstanceState == null)
		{
			mBrowserFragment = new FolderBrowserFragment();
			fragmentManager.beginTransaction()
					.add(R.id.folder_list_container, mBrowserFragment).commit();
		}
		else
		{
			mBrowserFragment = (FolderBrowserFragment) fragmentManager.findFragmentById(R.id.folder_list_container);
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
