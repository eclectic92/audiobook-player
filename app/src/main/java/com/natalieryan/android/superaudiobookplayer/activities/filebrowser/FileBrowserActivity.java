package com.natalieryan.android.superaudiobookplayer.activities.filebrowser;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.activities.main.BaseActivity;
import com.natalieryan.android.superaudiobookplayer.model.FileItem;
import com.natalieryan.android.superaudiobookplayer.ui.custom.CustomIconTabLayout;
import com.natalieryan.android.superaudiobookplayer.utils.filesystem.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class FileBrowserActivity extends BaseActivity
		implements FileBrowserFragment.OnSDCardNotMountedListener
{
	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private boolean mSdCardIsMounted;
	private TabLayout mTabLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_file_browser);

		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar actionBar=this.getSupportActionBar();
		if (actionBar!=null)
		{
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		mSdCardIsMounted=FileUtils.sdCardIsMounted();
		mViewPager=(ViewPager)findViewById(R.id.browser_pager_container);
		setupViewPager(mViewPager);
		mViewPager.setAdapter(mViewPagerAdapter);

		//setup the tab layout
		mTabLayout=(TabLayout)findViewById(R.id.tabs);
		if (mSdCardIsMounted)
		{
			mTabLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			mTabLayout.setVisibility(View.GONE);
		}

		//add the icons if it's visible
		mTabLayout.setupWithViewPager(mViewPager);
		if (mTabLayout.getVisibility()==View.VISIBLE)
		{
			setupTabIcons();
		}

		FloatingActionButton selectFileFab=(FloatingActionButton)findViewById(R.id.fab_select_file);
		selectFileFab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				returnSelectedFile();
			}
		});
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
			if(intent.hasExtra(FileBrowserFragment.SHOW_FOLDERS_ONLY))
			{
				showFoldersOnly = intent.getBooleanExtra(FileBrowserFragment.SHOW_FOLDERS_ONLY, false);
			}
			if(intent.hasExtra(FileBrowserFragment.ALLOW_FILE_SELECTION))
			{
				allowFileSelection = intent.getBooleanExtra(FileBrowserFragment.ALLOW_FILE_SELECTION, true);
			}
		}

		//prep the bundle for passing on our flags
		Bundle deviceBrowserArgs = new Bundle();
		deviceBrowserArgs.putBoolean(FileBrowserFragment.SHOW_FOLDERS_ONLY, showFoldersOnly);
		deviceBrowserArgs.putBoolean(FileBrowserFragment.ALLOW_FILE_SELECTION, allowFileSelection);
		deviceBrowserArgs.putString(FileBrowserFragment.BROWSER_ROOT_PATH, FileUtils.getDeviceRootStoragePath());

		//set up the broswer tab for device root storage
		FileBrowserFragment deviceBrowser = new FileBrowserFragment();
		deviceBrowser.setArguments(deviceBrowserArgs);
		mViewPagerAdapter.addFragment(deviceBrowser, getString(R.string.device_tab_title));

		//if there's an SD card inserted, add a tab for it now
		if(mSdCardIsMounted)
		{
			Bundle sdBrowserArgs = new Bundle();
			sdBrowserArgs.putBoolean(FileBrowserFragment.SHOW_FOLDERS_ONLY, showFoldersOnly);
			sdBrowserArgs.putBoolean(FileBrowserFragment.ALLOW_FILE_SELECTION, allowFileSelection);
			sdBrowserArgs.putString(FileBrowserFragment.BROWSER_ROOT_PATH, FileUtils.getSdCardPath());
			FileBrowserFragment sdBrowser = new FileBrowserFragment();
			sdBrowser.setArguments(sdBrowserArgs);
			mViewPagerAdapter.addFragment(sdBrowser, getString(R.string.sd_card_tab_title));
		}
		viewPager.setAdapter(mViewPagerAdapter);
	}

	private void setupTabIcons() {

		TabLayout.Tab deviceTab = mTabLayout.getTabAt(0);
		if(deviceTab != null)
		{
			CustomIconTabLayout deviceTabLayout = new CustomIconTabLayout(
					this, getString(R.string.device_tab_title), R.drawable.ic_phone_android_light_24dp);
			deviceTab.setCustomView(deviceTabLayout);
		}

		TabLayout.Tab sdTab = mTabLayout.getTabAt(1);
		if(sdTab != null)
		{
			CustomIconTabLayout sdTabLayout = new CustomIconTabLayout(
					this, getString(R.string.sd_card_tab_title), R.drawable.ic_sd_card_light_24dp);
			sdTab.setCustomView(sdTabLayout);
		}
	}

	@Override
	public void onSDCardUnmounted()
	{
		mViewPager.setCurrentItem(0);
		TabLayout.Tab sdTab = mTabLayout.getTabAt(1);
		if(sdTab != null) mTabLayout.removeTab(sdTab);
		mTabLayout.setVisibility(View.GONE);
		Toast.makeText(this, R.string.sd_card_unmounted, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		getVisibleFragment().onBackPressed();
	}

	private void returnSelectedFile()
	{
		FileItem selectedFile = getVisibleFragment().getSelectedFile();
		Intent returnIntent = new Intent();
		returnIntent.putExtra(FileBrowserFragment.EXTRA_FILE_PATH, selectedFile.getPath());
		returnIntent.putExtra(FileBrowserFragment.EXTRA_FILE_IS_ON_SD_CARD,
				FileUtils.fileIsOnMountedSdCard(selectedFile.getPath()));
		this.setResult(Activity.RESULT_OK, returnIntent);
		this.finish();
	}

	private FileBrowserFragment getVisibleFragment()
	{
		return (FileBrowserFragment) getSupportFragmentManager()
				.findFragmentByTag("android:switcher:" + R.id.browser_pager_container + ":"
						+ mViewPager.getCurrentItem());
	}

	private class ViewPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		ViewPagerAdapter(FragmentManager manager) {
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

		void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}
}
