package com.natalieryan.android.superaudiobookplayer.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.activities.foldermanager.FolderManagerActivity;
import com.natalieryan.android.superaudiobookplayer.activities.settings.SettingsActivity;

public class MainActivity extends BaseActivity
		implements NavigationView.OnNavigationItemSelectedListener
{

	//TODO: Day-night should be moved to a static in app context

	@SuppressWarnings("unused")
	private static final String TAG=MainActivity.class.getSimpleName();
	private MenuItem mMenuItemWaiting;
	private DrawerLayout drawer;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		overridePendingTransition(R.anim.swap_in_bottom, R.anim.swap_out_bottom);
		Toolbar toolbar=(Toolbar)findViewById(R.id.main_activity_toolbar);
		setSupportActionBar(toolbar);

		drawer=(DrawerLayout)findViewById(R.id.drawer_layout);

		ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		{
			@Override
			public void onDrawerClosed(View drawerView)
			{
				super.onDrawerClosed(drawerView);
				if (mMenuItemWaiting!=null)
				{
					onNavigationItemSelected(mMenuItemWaiting);
				}
			}
		};
		drawer.addDrawerListener(toggle);
		toggle.syncState();


		NavigationView navigationView=(NavigationView)findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
	}


	@Override
	public void onBackPressed()
	{
		DrawerLayout drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START))
		{
			drawer.closeDrawer(GravityCompat.START);
		}
		else
		{
			super.onBackPressed();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id=item.getItemId();

		switch (id)
		{
			case R.id.action_settings:
			{
				launchSettings();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);

	}


	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item)
	{

		mMenuItemWaiting=null;
		if (drawer.isDrawerOpen(GravityCompat.START))
		{
			mMenuItemWaiting=item;
			drawer.closeDrawers();
			return false;
		}

		// Handle navigation view item clicks here.
		int id=item.getItemId();

		switch (id)
		{
			case R.id.nav_library:
			{
				break;
			}
			case R.id.nav_settings:
			{
				launchSettings();
				break;
			}
			case R.id.nav_folders:
			{
				launchFolderManager();
				break;
			}
		}

		DrawerLayout drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}


	private void launchFolderManager()
	{
		Intent intent=new Intent(this, FolderManagerActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.swap_in_bottom, R.anim.swap_out_bottom);
	}


	private void launchSettings()
	{
		Intent startSettingsActivity=new Intent(this, SettingsActivity.class);
		startActivity(startSettingsActivity);
		overridePendingTransition(R.anim.swap_in_bottom, R.anim.swap_out_bottom);
	}
}
