package com.natalieryan.android.superaudiobookplayer.activities.foldermanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.natalieryan.android.superaudiobookplayer.R;

public class FolderManagerActivity extends AppCompatActivity
{

	private FolderManagerFragment mFolderMgrFragment;
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
		setContentView(R.layout.activity_folder_manager);
		final FragmentManager fragmentManager=getSupportFragmentManager();

		ActionBar actionBar = this.getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		if(savedInstanceState == null)
		{
			mFolderMgrFragment = new FolderManagerFragment();
			fragmentManager.beginTransaction()
					.add(R.id.library_folders_container, mFolderMgrFragment).commit();
		}
		else{
			mFolderMgrFragment = (FolderManagerFragment) fragmentManager
					.findFragmentById(R.id.library_folders_container);
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
		FloatingActionsMenu fam = mFolderMgrFragment.getFam();

		if (fam.isExpanded())
		{
			fam.collapse();
		}
		else
		{
			super.onBackPressed();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				FloatingActionsMenu fam = mFolderMgrFragment.getFam();
				if (fam.isExpanded())
				{
					fam.collapse();
				}
				else
				{
					NavUtils.navigateUpFromSameTask(this);
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
