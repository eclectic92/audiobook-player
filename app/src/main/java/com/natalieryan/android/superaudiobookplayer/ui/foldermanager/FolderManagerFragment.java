package com.natalieryan.android.superaudiobookplayer.ui.foldermanager;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.data.async.AddFolderToLibraryAsyncTask;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;
import com.natalieryan.android.superaudiobookplayer.ui.filebrowser.FileBrowserActivity;
import com.natalieryan.android.superaudiobookplayer.ui.filebrowser.FileBrowserFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderManagerFragment extends Fragment implements AddFolderToLibraryAsyncTask.AddFolderListener
{

	private static final int SELECT_FOLDER_RESULT_CODE = 1;
	private static final int PERMISSION_REQUEST_CODE = 200;

	private FloatingActionsMenu mFam;
	private LibraryFolder mLibraryFolder = null;
	private boolean mEachFileIsBook = false;

	public FolderManagerFragment()
	{
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{

		View rootView = inflater.inflate(R.layout.fragment_folder_manager, container, false);
		mFam = (FloatingActionsMenu) rootView.findViewById(R.id.fab_folder_menu);
		final View overlay = rootView.findViewById(R.id.shadow_overlay);

		FloatingActionsMenu.OnFloatingActionsMenuUpdateListener listener
				= new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
			@Override
			public void onMenuExpanded()
			{
				overlay.setVisibility(View.VISIBLE);
			}

			@Override
			public void onMenuCollapsed() {
				overlay.setVisibility(View.GONE);
			}
		};

		FloatingActionButton fabAddFolderMulitBook =
				(FloatingActionButton) rootView.findViewById(R.id.action_folder_multifile_books);

		fabAddFolderMulitBook.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				mEachFileIsBook = false;
				launchFolderBrowser();
			}
		});

		FloatingActionButton fabAddFolderSingleFilePerBook =
				(FloatingActionButton) rootView.findViewById(R.id.action_folder_single_file_per_book);


		fabAddFolderSingleFilePerBook.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				mEachFileIsBook = true;
				launchFolderBrowser();
			}
		});


		mFam.setOnFloatingActionsMenuUpdateListener(listener);

		return rootView;
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
					mLibraryFolder = new LibraryFolder(filePath, isOnSDCard, mEachFileIsBook);
					AddFolderToLibraryAsyncTask addLibraryFolder = new AddFolderToLibraryAsyncTask(getContext(), this);
					addLibraryFolder.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mLibraryFolder);
				}
			}
		}
	}

	private void launchFolderBrowser()
	{
		mFam.collapse();
		int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
		if (permissionCheck != PackageManager.PERMISSION_GRANTED)
		{
			requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
		}
		else
		{
			startFolderBrowserActivity();
		}
	}

	private void startFolderBrowserActivity()
	{
		Intent intent = new Intent(getContext(), FileBrowserActivity.class);
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
		Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
		mLibraryFolder = null;
	}
	public FloatingActionsMenu getFam(){
		return this.mFam;
	}
}
