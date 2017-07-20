package com.natalieryan.android.superaudiobookplayer.activities.filebrowser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
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
import com.natalieryan.android.superaudiobookplayer.ui.custom.CustomIconTabLayout;
import com.natalieryan.android.superaudiobookplayer.utils.filesystem.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class FileBrowserActivityTabbed extends AppCompatActivity
		implements FileBrowserFragmentTabbed.OnSDCardNotMountedListener,
				   FileBrowserFragmentTabbed.OnFileSelectedListener
{
	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;
	private boolean mSdCardIsMounted;
	private TabLayout mTabLayout;
	private int mNightMode;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//setup night mode flags
		if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey(getString(R.string.pref_night_mode_menu_key)))
			{
				mNightMode = savedInstanceState.getInt(getString(R.string.pref_night_mode_menu_key),
						Integer.valueOf(getString(R.string.pref_night_mode_value_off)));
			}
		}
		else
		{
			final SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			mNightMode = Integer.valueOf(sharedPreferences.getString(getString(R.string.pref_night_mode_menu_key),
					getString(R.string.pref_night_mode_value_off)));
		}

		getDelegate().setLocalNightMode(mNightMode);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_file_browser);

		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar actionBar = this.getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		mSdCardIsMounted = FileUtils.sdCardIsMounted();
		mViewPager = (ViewPager)findViewById(R.id.browser_pager_container);
		setupViewPager(mViewPager);
		mViewPager.setAdapter(mViewPagerAdapter);

		//setup the tab layout
		mTabLayout=(TabLayout)findViewById(R.id.tabs);
		if(mSdCardIsMounted)
		{
			mTabLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			mTabLayout.setVisibility(View.GONE);
		}

		//add the icons if it's visible
		mTabLayout.setupWithViewPager(mViewPager);
		if(mTabLayout.getVisibility() == View.VISIBLE)
		{
			setupTabIcons();
		}
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

	private void setupTabIcons() {

		/*
		TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("ONE");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);
		 */

		TabLayout.Tab deviceTab = mTabLayout.getTabAt(0);
		if(deviceTab != null)
		{
			CustomIconTabLayout deviceTabLayout = new CustomIconTabLayout(
					this, getString(R.string.device_tab_title), R.drawable.ic_phone_android_light_24dp);
			//TextView tvDeviceTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
			//tvDeviceTab.setText(R.string.device_tab_title);
			////tvDeviceTab.setCompoundDrawablesWithIntrinsicBounds(mTabIcons[0], 0, 0, 0);
			deviceTab.setCustomView(deviceTabLayout);
		}

		TabLayout.Tab sdTab = mTabLayout.getTabAt(1);
		if(sdTab != null)
		{
			CustomIconTabLayout sdTabLayout = new CustomIconTabLayout(
					this, getString(R.string.sd_card_tab_title), R.drawable.ic_sd_card_light_24dp);

			//TextView tvSdTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
			//tvSdTab.setText(R.string.sd_card_tab_title);
			//tvSdTab.setCompoundDrawablesWithIntrinsicBounds(mTabIcons[1], 0, 0, 0);
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
		FileBrowserFragmentTabbed currentFragment  = (FileBrowserFragmentTabbed) getSupportFragmentManager()
				.findFragmentByTag("android:switcher:" + R.id.browser_pager_container + ":"
						+ mViewPager.getCurrentItem());
		currentFragment.onBackPressed();
	}

	@Override
	public void onFileSelected(String path, boolean isOnSDCard)
	{
		Intent returnIntent = new Intent();
		returnIntent.putExtra(FileBrowserFragmentTabbed.EXTRA_FILE_PATH, path);
		returnIntent.putExtra(FileBrowserFragmentTabbed.EXTRA_FILE_IS_ON_SD_CARD, isOnSDCard);
		this.setResult(Activity.RESULT_OK, returnIntent);
		this.finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(getString(R.string.pref_night_mode_menu_key), mNightMode);
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
