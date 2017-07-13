package com.natalieryan.android.superaudiobookplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.natalieryan.android.superaudiobookplayer.ui.filebrowser.FileBrowerStandaloneActivity;
import com.natalieryan.android.superaudiobookplayer.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener
{
	public static final String TAG = MainActivity.class.getSimpleName();

	private static final int PERMISSION_REQUEST_CODE = 101;
	private static final int SELECT_FOLDER_RESULT_CODE = 1;
	private static final String SELECTED_FILE = "selected_file";
	private MenuItem mMenuItemWaiting;
	private DrawerLayout drawer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		overridePendingTransition(R.anim.swap_in_bottom, R.anim.swap_out_bottom);
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		drawer=(DrawerLayout)findViewById(R.id.drawer_layout);

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		{
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if(mMenuItemWaiting != null)
				{
					onNavigationItemSelected(mMenuItemWaiting);
				}
			}
		};
		drawer.addDrawerListener(toggle);
		toggle.syncState();


		NavigationView navigationView=(NavigationView)findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		//launch tag reader
		Button tagButton = (Button) findViewById(R.id.tag_reader_btn);
		tagButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				launchTagReader();
			}
		});

		//launch file browser
		Button fileButton = (Button) findViewById(R.id.file_button);
		fileButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				launchFileBrowser();
			}
		});
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
			startSettingsActivity.putExtra(Intent.EXTRA_REFERRER, TAG);
			startActivity(startSettingsActivity);
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item)
	{

		mMenuItemWaiting = null;
		if(drawer.isDrawerOpen(GravityCompat.START)) {
			mMenuItemWaiting = item;
			drawer.closeDrawers();
			return false;
		};

		// Handle navigation view item clicks here.
		int id=item.getItemId();

		if (id == R.id.nav_library)
		{
			// Handle the camera action
		}
		else if (id == R.id.nav_settings)
		{
			Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
			startSettingsActivity.putExtra(Intent.EXTRA_REFERRER, TAG);
			startActivity(startSettingsActivity);
		}


		DrawerLayout drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void launchTagReader()
	{
		Intent intent = new Intent(this, TagReader.class);
		startActivity(intent);

	}

	private void launchFileBrowser()
	{
		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		if (permissionCheck != PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
		}
		else
		{
			Intent intent = new Intent(this, FileBrowerStandaloneActivity.class);
			startActivityForResult(intent, SELECT_FOLDER_RESULT_CODE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == PERMISSION_REQUEST_CODE)
		{
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				Intent intent = new Intent(this, FileBrowerStandaloneActivity.class);
				startActivityForResult(intent, SELECT_FOLDER_RESULT_CODE);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// check that it is the SecondActivity with an OK result
		if (requestCode == SELECT_FOLDER_RESULT_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				// get String data from Intent
				String returnString = data.getStringExtra(SELECTED_FILE);
				Toast.makeText(this, returnString, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
