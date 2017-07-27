package com.natalieryan.android.superaudiobookplayer.activities.foldermanager;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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
import com.natalieryan.android.superaudiobookplayer.activities.filebrowser.FileBrowserActivity;
import com.natalieryan.android.superaudiobookplayer.activities.filebrowser.FileBrowserFragment;
import com.natalieryan.android.superaudiobookplayer.data.LibraryContract;
import com.natalieryan.android.superaudiobookplayer.tasks.AddFolderToLibraryAsyncTask;
import com.natalieryan.android.superaudiobookplayer.tasks.RemoveFolderFromLibraryAsyncTask;
import com.natalieryan.android.superaudiobookplayer.databinding.FragmentFolderManagerBinding;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;
import com.natalieryan.android.superaudiobookplayer.ui.adapters.FolderManagerAdapter;
import com.natalieryan.android.superaudiobookplayer.tasks.ScanFolderAsyncTask;
import com.natalieryan.android.superaudiobookplayer.ui.adapters.GenericArrayListAdapter;
import com.natalieryan.android.superaudiobookplayer.ui.utils.FabScrollListener;
import com.natalieryan.android.superaudiobookplayer.ui.utils.SwipeHelper;
import com.natalieryan.android.superaudiobookplayer.utils.filesystem.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderManagerFragment extends Fragment implements AddFolderToLibraryAsyncTask.AddFolderListener,
															   LoaderManager.LoaderCallbacks<Cursor>,
															   FabScrollListener.FabPositionListener,
															   RemoveFolderFromLibraryAsyncTask.RemoveFolderListener,
															   ScanFolderAsyncTask.ScanFolderListener
{
	private static final int SELECT_FOLDER_RESULT_CODE = 1;
	private static final int PERMISSION_REQUEST_CODE = 200;
	private static final int LIBRARY_FOLDER_LOADER = 100;
	private static final String EXTRA_OVRLAY_VISIBLE = "overlay_visible";
	private static final String FOLDER_SORT_ORDER = LibraryContract.FolderEntry.COLUMN_ROOT_PATH + " DESC, "+
			LibraryContract.FolderEntry.COLUMN_PATH + " ASC";

	private FloatingActionsMenu mFam;
	private boolean mEachFileIsBook = false;
	private boolean mFabIsVisible;

	private LibraryFolder mDeletedFolder = null;
	private LibraryFolder mFolderToAdd = null;
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

		if(savedInstanceState != null)
		{
			if(savedInstanceState.containsKey(EXTRA_OVRLAY_VISIBLE))
			{
				if(savedInstanceState.getBoolean(EXTRA_OVRLAY_VISIBLE))
				{
					mBinder.shadowOverlay.setVisibility(View.VISIBLE);
				}
			}
		}
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
		mAdapter = new FolderManagerAdapter(getContext());
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
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putBoolean(EXTRA_OVRLAY_VISIBLE, mBinder.shadowOverlay.getVisibility() == View.VISIBLE);
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
				String rootPath = isOnSDCard ? FileUtils.getSdCardPath() : FileUtils.getDeviceRootStoragePath();
				if(filePath != null && !filePath.isEmpty() && rootPath != null)
				{
					mFolderToAdd = new LibraryFolder(filePath, rootPath, isOnSDCard, mEachFileIsBook, 0);

					//see if selected folder is the child of an existing folder and disallow if it is
					int parentFolderId = getParentFolderId(mFolderToAdd);
					if(parentFolderId > -1){
						LibraryFolder parentFolder = mAdapter.getDataItem(parentFolderId);
						if(parentFolder != null){
							if(mFolderToAdd.getFriendlyPath().equalsIgnoreCase(parentFolder.getFriendlyPath()))
							{
								showFolderNotAllowedAlert(getString(
										R.string.alert_body_folder_already_added,
										mFolderToAdd.getFriendlyPath()),
										getString(R.string.alert_title_folder_already_added));
							}
							else
							{
								showFolderNotAllowedAlert(getString(
										R.string.alert_body_parent_folder_already_added,
										mFolderToAdd.getFriendlyPath(),
										parentFolder.getFriendlyPath()),
										getString(R.string.alert_title_folder_already_added));
							}
						}
						mFolderToAdd = null;
					}
					else
					{
						//if it's not a child, see if it's that parent of any existing folders and disallow
						ArrayList<String> childFolderNames = getChildFolderNames(mFolderToAdd);
						if(childFolderNames.size() > 0)
						{
							StringBuilder sb = new StringBuilder();
							for(int i=0; i < childFolderNames.size(); i++)
							{
									sb.append("\n");
									sb.append(childFolderNames.get(i));
							}

							showFolderNotAllowedAlert(getString(
									R.string.alert_body_folder_has_children, sb.toString()),
									getString(R.string.alert_title_folder_has_children));
						}
						else{
							scanLibraryFolder(mFolderToAdd);
						}
					}
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
		intent.putExtra(FileBrowserFragment.SHOW_FOLDERS_ONLY, false);
		intent.putExtra(FileBrowserFragment.ALLOW_FILE_SELECTION, false);
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
	private void scanLibraryFolder(LibraryFolder folderToScan)
	{
		ScanFolderAsyncTask scanFolderAsyncTask = new ScanFolderAsyncTask(getContext(), this);
		scanFolderAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, folderToScan);
	}

	private int getParentFolderId(LibraryFolder selectedFolder)
	{
		int parentFolderId = -1;
		ArrayList<LibraryFolder> existingFolders = mAdapter.getData();
		for(int i = 0; i < existingFolders.size(); i++)
		{
			if(FileUtils.isChildOfFolder(existingFolders.get(i).getPath(), selectedFolder.getPath()))
			{
				parentFolderId = i;
				break;
			}
		}
		return parentFolderId;
	}

	private ArrayList<String> getChildFolderNames(LibraryFolder selectedFolder)
	{
		ArrayList<String> childFolders = new ArrayList<>();
		ArrayList<LibraryFolder> existingFolders = mAdapter.getData();
		for(int i = 0; i < existingFolders.size(); i++)
		{
			LibraryFolder childFolder = existingFolders.get(i);
			if(childFolder != null)
			{
				if(FileUtils.isChildOfFolder(selectedFolder.getPath(), childFolder.getPath()) &&
						selectedFolder.getIsSdCardFolder() == childFolder.getIsSdCardFolder())
				{
					childFolders.add(childFolder.getFriendlyPath());
				}
			}
		}
		return childFolders;
	}

	@Override
	public void onFolderScanned(int bookCount)
	{
		if (mFolderToAdd != null)
		{
			mFolderToAdd.setBookCount(bookCount);
			addFolderToLibrary(mFolderToAdd);
		}

	}

	private void addFolderToLibrary(LibraryFolder folderToAdd)
	{
		AddFolderToLibraryAsyncTask addLibraryFolder = new AddFolderToLibraryAsyncTask(getContext(), this);
		addLibraryFolder.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, folderToAdd);
	}

	@Override
	public void onFolderAdded()
	{
		this.mDeletedFolder = null;
		this.mFolderToAdd = null;
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

		mBinder.shadowOverlay.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				mFam.collapse();
				mBinder.shadowOverlay.setVisibility(View.GONE);
			}
		});
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
				return viewHolder.getItemViewType() ==GenericArrayListAdapter.ITEM_VIEW_TYPE_HEADER
						? 0 : makeMovementFlags(dragFlags, swipeFlags);
			}
		};

		ItemTouchHelper mItemTouchHelper=new ItemTouchHelper(swipeHelper);
		mItemTouchHelper.attachToRecyclerView(mBinder.LibraryFolderListRv);

		swipeHelper.setSwipeLeftLabel(getContext().getString( R.string.delete_folder));
		swipeHelper.setTextSize(getResources().getInteger(R.integer.swiper_text_size));
		swipeHelper.setSwipeLeftIconColor(ContextCompat.getColor(getContext(), R.color.colorIcons));
		swipeHelper.setSwipeLeftColor(ContextCompat.getColor(getContext(), R.color.colorCancelButton));
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

	private void showFolderNotAllowedAlert(String body, String title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(title);
		builder.setMessage(body);
		builder.setIcon(R.drawable.ic_warning_black_24dp);
		builder.setCancelable(true);
		builder.setPositiveButton(
				getString(R.string.button_ok_text),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
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
			mAdapter.setData(data, true);
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
