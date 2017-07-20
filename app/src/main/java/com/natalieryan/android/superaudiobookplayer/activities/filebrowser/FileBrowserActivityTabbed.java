package com.natalieryan.android.superaudiobookplayer.activities.filebrowser;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.utils.filesystem.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class FileBrowserActivityTabbed extends AppCompatActivity
{
	private ViewPagerAdapter mViewPagerAdapter;
	private boolean mSdCardIsMounted;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_browser_activity_tabbed);

		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar actionBar = this.getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		mSdCardIsMounted = FileUtils.sdCardIsMounted();

		TabLayout tabLayout=(TabLayout)findViewById(R.id.tabs);
		ViewPager viewPager = (ViewPager)findViewById(R.id.browser_pager_container);
		setupViewPager(viewPager);
		viewPager.setAdapter(mViewPagerAdapter);
		tabLayout.setupWithViewPager(viewPager);
		// Set up the ViewPager with the sections adapter.
		if(mSdCardIsMounted)
		{
			tabLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			tabLayout.setVisibility(View.GONE);
		}
		tabLayout.setupWithViewPager(viewPager);
	}

	private void setupViewPager(ViewPager viewPager) {

		//instantiate the adapter
		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

		//default flags to pass to the browser fragments
		boolean showFoldersOnly = false;
		boolean allowFileSelection = true;

		//see if any flags came in with the intent
		Intent intent = getIntent();
		if(intent != null)
		{
			if(intent.hasExtra(FileBrowserFragmentTabbed.SHOW_FOLDERS_ONLY))
			{
				showFoldersOnly = intent.getBooleanExtra(FileBrowserFragmentTabbed.SHOW_FOLDERS_ONLY, false);
			}
			if(intent.hasExtra(FileBrowserFragmentTabbed.ALLOW_FILE_SELECTION))
			{
				allowFileSelection = intent.getBooleanExtra(FileBrowserFragmentTabbed.ALLOW_FILE_SELECTION, true);
			}
		}

		//prep the bundle for passing on our flags
		Bundle deviceBrowserArgs = new Bundle();
		deviceBrowserArgs.putBoolean(FileBrowserFragmentTabbed.SHOW_FOLDERS_ONLY, showFoldersOnly);
		deviceBrowserArgs.putBoolean(FileBrowserFragmentTabbed.ALLOW_FILE_SELECTION, allowFileSelection);
		deviceBrowserArgs.putString(FileBrowserFragmentTabbed.BROWSER_ROOT_PATH, FileUtils.getDeviceRootStoragePath());

		//set up the broswer tab for device root storage
		FileBrowserFragmentTabbed deviceBrowser = new FileBrowserFragmentTabbed();
		deviceBrowser.setArguments(deviceBrowserArgs);
		mViewPagerAdapter.addFragment(deviceBrowser, getString(R.string.device_tab_title));

		//if there's an SD card inserted, add a tab for it now
		if(mSdCardIsMounted)
		{
			Bundle sdBrowserArgs = new Bundle();
			sdBrowserArgs.putBoolean(FileBrowserFragmentTabbed.SHOW_FOLDERS_ONLY, showFoldersOnly);
			sdBrowserArgs.putBoolean(FileBrowserFragmentTabbed.ALLOW_FILE_SELECTION, allowFileSelection);
			sdBrowserArgs.putString(FileBrowserFragmentTabbed.BROWSER_ROOT_PATH, FileUtils.getSdCardPath());
			FileBrowserFragmentTabbed sdBrowser = new FileBrowserFragmentTabbed();
			sdBrowser.setArguments(sdBrowserArgs);

			mViewPagerAdapter.addFragment(sdBrowser, getString(R.string.sd_card_tab_title));

		}
		viewPager.setAdapter(mViewPagerAdapter);
	}


	class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public ViewPagerAdapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}
}
