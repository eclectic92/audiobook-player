package com.natalieryan.android.superaudiobookplayer.ui.foldermanager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.data.async.AddFolderToLibraryAsyncTask;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;
import com.natalieryan.android.superaudiobookplayer.ui.filebrowser.FileBrowerStandaloneActivity;
import com.natalieryan.android.superaudiobookplayer.ui.filebrowser.FileBrowserActivity;
import com.natalieryan.android.superaudiobookplayer.ui.filebrowser.FileBrowserFragment;

public class FolderManagerActivity extends AppCompatActivity
{

	private static final int SELECT_FOLDER_RESULT_CODE = 1;
	private static final int PERMISSION_REQUEST_CODE = 200;
	private static final String EXTRA_EACH_FILE_IS_A_BOOK = "each_file_is_book";

	private LibraryFolder mLibraryFolder;
	private FolderManagerFragment mFolderMgrFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folder_manager);
		final FragmentManager fragmentManager=getSupportFragmentManager();
		//Toolbar toolbar = (Toolbar) findViewById(R.id.folder_manager_toolbar);
		//setSupportActionBar(toolbar);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// check that it is the SecondActivity with an OK result
		if (requestCode == SELECT_FOLDER_RESULT_CODE)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				// get String data from Intent
				String filePath = data.getStringExtra(FileBrowserFragment.EXTRA_FILE_PATH);
				boolean isOnSDCard = data.getBooleanExtra(FileBrowserFragment.EXTRA_FILE_IS_ON_SD_CARD, false);
				if(filePath != null && !filePath.isEmpty())
				{
					//TODO: use this param for eachFileisABook
					mLibraryFolder = new LibraryFolder(filePath, isOnSDCard, true);
					//AddFolderToLibraryAsyncTask addLibraryFolder = new AddFolderToLibraryAsyncTask(getActivity(), this);
					//addLibraryFolder.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mLibraryFolder);
				}
			}
		}
	}

	private void launchFolderBrowser()
	{
		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		if (permissionCheck != PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
		}
		else
		{
			startFolderBrowserActivity();
		}
	}

	private void startFolderBrowserActivity()
	{
		Intent intent = new Intent(this, FileBrowserActivity.class);
		intent.putExtra(FileBrowserFragment.SHOW_FOLDERS_ONLY, 1);
		startActivityForResult(intent, SELECT_FOLDER_RESULT_CODE);
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
				startFolderBrowserActivity();
			}
		}
	}

	@Override
	public void onBackPressed() {
		FloatingActionsMenu fam = mFolderMgrFragment.getFam();

		if (fam.isExpanded())
		{
			mFolderMgrFragment.fam.collapse();
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
					mFolderMgrFragment.fam.collapse();
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
/*
	@Override
	public void onFolderAdded(int addedRowId)
	{
		String message;

		if(addedRowId == -1){
			message =  "Failed to add folder to library. See logs";
		}
		else
		{
			message =  mLibraryFolder.getPath() + " added as folder " + String.valueOf(addedRowId);
		}
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		mLibraryFolder = null;
	}
	*/
}
