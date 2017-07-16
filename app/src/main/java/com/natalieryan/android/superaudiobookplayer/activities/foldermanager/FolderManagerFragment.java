package com.natalieryan.android.superaudiobookplayer.activities.foldermanager;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.data.LibraryContract;
import com.natalieryan.android.superaudiobookplayer.data.async.AddFolderToLibraryAsyncTask;
import com.natalieryan.android.superaudiobookplayer.data.async.RemoveFolderFromLibraryAsyncTask;
import com.natalieryan.android.superaudiobookplayer.databinding.FragmentFolderManagerBinding;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;
import com.natalieryan.android.superaudiobookplayer.utils.ui.FabScrollListener;
import com.natalieryan.android.superaudiobookplayer.utils.ui.SwipeHelper;
import com.natalieryan.android.superaudiobookplayer.activities.filebrowser.FileBrowserActivity;
import com.natalieryan.android.superaudiobookplayer.activities.filebrowser.FileBrowserFragment;
import com.natalieryan.android.superaudiobookplayer.utils.media.MediaScanner;
import com.natalieryan.android.superaudiobookplayer.utils.filesystem.PathUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderManagerFragment extends Fragment implements AddFolderToLibraryAsyncTask.AddFolderListener,
															   LoaderManager.LoaderCallbacks<Cursor>,
															   FabScrollListener.FabPositionListener,
															   RemoveFolderFromLibraryAsyncTask.RemoveFolderListener
{

	private static final int SELECT_FOLDER_RESULT_CODE = 1;
	private static final int PERMISSION_REQUEST_CODE = 200;
	private static final int LIBRARY_FOLDER_LOADER = 100;

	private static final String FOLDER_SORT_ORDER = LibraryContract.FolderEntry.COLUMN_ROOT_PATH + " DESC, "+
			LibraryContract.FolderEntry.COLUMN_PATH + " ASC";

	private FloatingActionsMenu mFam;
	private boolean mEachFileIsBook = false;
	private boolean mFabIsVisible;

	private LibraryFolder mDeletedFolder = null;
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

		//create the floating action button menu
		setupFabMenu(rootView);

		return rootView;
	}


	@Override
	public void onResume()
	{
		super.onResume();
		//setup the recyclerview
		LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
		mAdapter = new FolderManagerAdapter(getContext(), true);
		mBinder.LibraryFolderListRv.setAdapter(mAdapter);
		mBinder.LibraryFolderListRv.setLayoutManager(layoutManager);

		//load up our data
		getActivity().getSupportLoaderManager().initLoader(LIBRARY_FOLDER_LOADER, null, this);

		//attach the fab scroll listener
		mBinder.LibraryFolderListRv.addOnScrollListener(new FabScrollListener(this));

		//create and attach the swipe helper for deleting items
		setSwipeForRecyclerView();
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
					LibraryFolder folderToAdd =
							new LibraryFolder(filePath, rootPath, isOnSDCard, mEachFileIsBook, bookCount);
					addFolderToLibrary(folderToAdd);
				}
			}
		}
	}

	private void launchFolderBrowser()
	{
		mFam.collapseImmediately();
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



	//
	//utility functions
	//

	private void addFolderToLibrary(LibraryFolder folderToAdd)
	{
		AddFolderToLibraryAsyncTask addLibraryFolder = new AddFolderToLibraryAsyncTask(getContext(), this);
		addLibraryFolder.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, folderToAdd);
	}

	@Override
	public void onFolderAdded()
	{
		this.mDeletedFolder = null;
	}


	private void removeFolderFromLibrary(LibraryFolder folderToRemove)
	{
		this.mDeletedFolder = folderToRemove;
		RemoveFolderFromLibraryAsyncTask removeFolderFromLibraryAsyncTask =
				new RemoveFolderFromLibraryAsyncTask(getContext(), this);
		removeFolderFromLibraryAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, folderToRemove);
	}

	@Override
	public void onFolderRemoved(String folderFriendlyPath)
	{
		Snackbar bar = Snackbar.make(getActivity().findViewById( R.id.folder_manager_layout),
				getString(R.string.folder_deleted_message, mDeletedFolder.getFriendlyPath()),
				getResources().getInteger(R.integer.snackbar_delete_length))
				.setAction(getString(R.string.folder_delete_undo), new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mDeletedFolder != null)
						{
							addFolderToLibrary(mDeletedFolder);
						}
					}
				});
		bar.show();
	}

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

	private void setSwipeForRecyclerView()
	{

		SwipeHelper swipeHelper=new SwipeHelper(getContext())
		{
			@Override
			public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
			{
				int swipedPosition=viewHolder.getAdapterPosition();
				removeFolderFromLibrary(mAdapter.getItem(swipedPosition));
			}

			@Override
			public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
				final int dragFlags = 0;
				final int swipeFlags = ItemTouchHelper.START;
				return viewHolder.getItemViewType() == FolderManagerAdapter.VIEW_TYPE_HEADER
						? 0 : makeMovementFlags(dragFlags, swipeFlags);
			}
		};

		ItemTouchHelper mItemTouchHelper=new ItemTouchHelper(swipeHelper);
		mItemTouchHelper.attachToRecyclerView(mBinder.LibraryFolderListRv);

		swipeHelper.setSwipeLeftLabel(getContext().getString( R.string.delete_folder));
		swipeHelper.setSwipeLeftIconColor(ContextCompat.getColor(getActivity(), R.color.colorIcons));
		swipeHelper.setSwipeLeftColor(ContextCompat.getColor(getActivity(), R.color.colorCancelButton));
		swipeHelper.setSwipeLeftIconId(R.drawable.ic_delete_black_24dp);
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
		if (data!=null && data.getCount() > 0)
		{
			mAdapter.setFolderList(data);
			mBinder.libraryFolderManagerScrollView.setVisibility(View.VISIBLE);
			mBinder.libraryFolderManagerZeroState.setVisibility(View.GONE);
		}
		else
		{
			mBinder.libraryFolderManagerScrollView.setVisibility(View.GONE);
			mBinder.libraryFolderManagerZeroState.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		//nothing to do here yet
	}
}
