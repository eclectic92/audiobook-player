package com.natalieryan.android.superaudiobookplayer.ui.foldermanager;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.data.LibraryContract;
import com.natalieryan.android.superaudiobookplayer.data.async.AddFolderToLibraryAsyncTask;
import com.natalieryan.android.superaudiobookplayer.databinding.FragmentFolderManagerBinding;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;
import com.natalieryan.android.superaudiobookplayer.ui.FabScrollListener;
import com.natalieryan.android.superaudiobookplayer.ui.filebrowser.FileBrowserActivity;
import com.natalieryan.android.superaudiobookplayer.ui.filebrowser.FileBrowserFragment;
import com.natalieryan.android.superaudiobookplayer.utils.MediaScanner;
import com.natalieryan.android.superaudiobookplayer.utils.PathUtils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderManagerFragment extends Fragment implements AddFolderToLibraryAsyncTask.AddFolderListener,
															   LoaderManager.LoaderCallbacks<Cursor>,
															   FabScrollListener.FabPositionListener
{

	private static final int SELECT_FOLDER_RESULT_CODE = 1;
	private static final int PERMISSION_REQUEST_CODE = 200;
	private static final int LIBRARY_FOLDER_LOADER = 100;

	private static final String FOLDER_SORT_ORDER = LibraryContract.FolderEntry.COLUMN_ROOT_PATH + " DESC, "+
			LibraryContract.FolderEntry.COLUMN_PATH + " ASC";

	private FloatingActionsMenu mFam;
	private LibraryFolder mLibraryFolder = null;
	private boolean mEachFileIsBook = false;
	private boolean mFabIsVisible;

	private FragmentFolderManagerBinding mBinder;
	private FolderManagerAdapter mAdapter;

	public FolderManagerFragment()
	{
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{

		mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_folder_manager, container, false);

		View rootView = mBinder.getRoot();

		setupFabMenu(rootView);

		//setup the recyclerview
		LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
		mAdapter = new FolderManagerAdapter(getContext(), true);
		mBinder.LibraryFolderListRv.setAdapter(mAdapter);
		mBinder.LibraryFolderListRv.setLayoutManager(layoutManager);

		//load up our data
		getActivity().getSupportLoaderManager().initLoader(LIBRARY_FOLDER_LOADER, null, this);

		//attach the fab scroll listener
		mBinder.LibraryFolderListRv.addOnScrollListener(new FabScrollListener(this));

		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SELECT_FOLDER_RESULT_CODE)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				String filePath = data.getStringExtra(FileBrowserFragment.EXTRA_FILE_PATH);
				boolean isOnSDCard = data.getBooleanExtra(FileBrowserFragment.EXTRA_FILE_IS_ON_SD_CARD, false);
				String rootPath = isOnSDCard ? PathUtils.getSdCardPath() : PathUtils.getDeviceRootStoragePath();
				if(filePath != null && !filePath.isEmpty() && rootPath != null)
				{
					int bookCount = MediaScanner.getBookCount();
					mLibraryFolder = new LibraryFolder(filePath, rootPath, isOnSDCard, mEachFileIsBook, bookCount);
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

	//
	//utility functions
	//
	public FloatingActionsMenu getFam(){
		return this.mFam;
	}

	private void setupFabMenu(View view)
	{
		mFam = (FloatingActionsMenu) view.findViewById(R.id.fab_folder_menu);
		final View overlay = view.findViewById(R.id.shadow_overlay);

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
				(FloatingActionButton) view.findViewById(R.id.action_folder_multifile_books);

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
				(FloatingActionButton) view.findViewById(R.id.action_folder_single_file_per_book);


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
	}

	@Override
	public void hideFab()
	{
		if(mFabIsVisible){
			if (mFam.isExpanded())
			{
				mFam.collapse();
			}
			mFam.animate().setInterpolator(new LinearInterpolator())
					.alpha(0.0f)
					.setDuration(250)
					.start();
			mFabIsVisible = false;
		}

	}

	@Override
	public void showFab()
	{
		if(!mFabIsVisible){
			mFam.animate().setInterpolator(new LinearInterpolator())
					.alpha(1.0f)
					.setDuration(250)
					.start();
			mFabIsVisible = true;
		}

	}

	// cursor loader to handle library folders ---------------------------------
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs)
	{

		switch (loaderId)
		{
			case LIBRARY_FOLDER_LOADER:
				Uri foldersURI=LibraryContract.FolderEntry.CONTENT_URI;
				return new CursorLoader(
						getContext(),
						foldersURI,
						LibraryContract.FolderEntry.FOLDER_COLUMNS,
						null,
						null,
						FOLDER_SORT_ORDER
				);
			default:
				throw new RuntimeException("Loader Not Implemented: "+loaderId);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data)
	{
		if (data!=null)
		{
			mAdapter.setFolderList(data);
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		//nothing to do here
	}
}
