package com.natalieryan.android.superaudiobookplayer.ui.filebrowser;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.databinding.FragmentFileBrowserBinding;
import com.natalieryan.android.superaudiobookplayer.model.FileItem;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class FileBrowserFragment extends Fragment implements FileItemAdapter.FileClickListener
{
	public static final String TAG = FileBrowserFragment.class.getSimpleName();

	private static final String EXTRA_CURRENT_FOLDER = "current_folder";
	private static final String EXTRA_PARENT_FOLDER = "parent_folder";
	private static final String EXTRA_SELECTED_FOLDER = "selected_folder";
	private static final String EXTRA_SELECTED_FOLDER_NAME = "selected_folder_name";


	private FragmentFileBrowserBinding mBinder;
	private String mDeviceRootPath;
	private String mSdCardRootPath;
	private String mSelectedFolderPath;

	// new sauce
	private String mSessionRootPath;
	private String mCurrentPath;
	private ArrayList<FileItem> mFiles = new ArrayList<>();
	private boolean mShowOnlyFolders = false;
	private FileItemAdapter mFileItemAdapter;

	private ArrayList<File> mFolders;

	//default constructor
	public FileBrowserFragment() {}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		if(savedInstanceState != null)
		{
			/*
			if(savedInstanceState.containsKey(EXTRA_CURRENT_FOLDER))
			{
				mCurrentFolderPath = savedInstanceState.getString(EXTRA_CURRENT_FOLDER);
			}

			if(savedInstanceState.containsKey(EXTRA_PARENT_FOLDER))
			{
				mParentFolderPath = savedInstanceState.getString(EXTRA_PARENT_FOLDER);
			}

			if(savedInstanceState.containsKey(EXTRA_SELECTED_FOLDER))
			{
				mSelectedFolderPath = savedInstanceState.getString(EXTRA_SELECTED_FOLDER);
			}

			if(savedInstanceState.containsKey(EXTRA_SELECTED_FOLDER_NAME))
			{
				mSelectedFolderName = savedInstanceState.getString(EXTRA_SELECTED_FOLDER_NAME);
			}
			*/
		}
		else
		{
			mDeviceRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
			mSdCardRootPath = getSdCardPath(mDeviceRootPath);
			mSessionRootPath = mDeviceRootPath;
			mCurrentPath = mDeviceRootPath;
		}

		mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_file_browser, container, false);

		View rootView = mBinder.getRoot();

		/*
		mBinder.backArrowImageView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				navigateBack();
			}
		});
		*/

		LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
		mFileItemAdapter = new FileItemAdapter();
		mFileItemAdapter.setClickListener(this);
		mBinder.fileListRv.setAdapter(mFileItemAdapter);
		mBinder.fileListRv.setLayoutManager(layoutManager);

		loadFileList(mCurrentPath);
		return rootView;
	}


	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		/*
		if (mCurrentFolderPath!=null)
		{
			outState.putString(EXTRA_CURRENT_FOLDER, mCurrentFolderPath);
		}

		if (mParentFolderPath != null)
		{
			outState.putString(EXTRA_PARENT_FOLDER, mParentFolderPath);
		}

		if (mSelectedFolderPath != null)
		{
			outState.putString(EXTRA_SELECTED_FOLDER, mSelectedFolderPath);
		}

		if (mSelectedFolderName != null)
		{
			outState.putString(EXTRA_SELECTED_FOLDER_NAME, mSelectedFolderName);
		}
		*/
	}

	private void loadFileList(String currentLocation)
	{
		if(currentLocation != null && !currentLocation.isEmpty())
		{
			mFiles = getFileItems(currentLocation, mShowOnlyFolders);
			mFileItemAdapter.setFileList(mFiles);
		}
	}


	@Nullable
	private String getParentFolderPath(File currentFolder)
	{
		String currentFolderPath = currentFolder.getAbsolutePath();

		if(!currentFolderPath.equalsIgnoreCase(mDeviceRootPath) && !currentFolderPath.equalsIgnoreCase(mSdCardRootPath))
		{
			return currentFolder.getParentFile().getAbsolutePath();
		}
		else
		{
			return null;
		}
	}

	public void setSelectedDisplay(String folderName){
		if(mSelectedFolderPath.equalsIgnoreCase(mDeviceRootPath))
		{
			folderName =  getResources().getString(R.string.device_root_folder);
			mBinder.backArrowImageView.setVisibility(View.INVISIBLE);
		}
		else if (mSelectedFolderPath.equalsIgnoreCase(mSdCardRootPath))
		{
			folderName =  getResources().getString(R.string.sd_root_folder);
			mBinder.backArrowImageView.setVisibility(View.INVISIBLE);
		}
		else
		{
			mBinder.backArrowImageView.setImageResource(R.drawable.ic_arrow_back_black_24dp);
			mBinder.backArrowImageView.setVisibility(View.VISIBLE);
		}
		String displayName = getResources().getString(R.string.selected_folder, folderName);
		mBinder.selectedFileNameTv.setText(displayName);

	}

	public void navigateBack() {
		/*
		if(mParentFolderPath != null)
		{
			mCurrentFolderPath = mParentFolderPath;
		}
		else
		{
			mCurrentFolderPath = mDeviceRootPath;
		}
		mSelectedFolderName = null;
		mSelectedFolderPath = null;
		//loadFolderList();

		Log.d (TAG, "back pressed");
		*/
	}

	public boolean isAtTopLevel(){
		return mSelectedFolderPath.equalsIgnoreCase(mDeviceRootPath)
				|| mSelectedFolderPath.equalsIgnoreCase(mSdCardRootPath);
	}

	// new special sauce ----------------------------------------------------


	@Override
	public void onFileClick (View view, int position)
	{

		final FileItem fileItem = mFileItemAdapter.getItem(position);
		if(fileItem != null){
			if(fileItem.getHasChildren())
			{
				mCurrentPath = fileItem.getPath();
				loadFileList(mCurrentPath);
			}
		}

								/*
		final File folder = mFolderAdapter.getItem(position);
		if(folder != null)
		{
			mSelectedFolderPath = folder.getAbsolutePath();
			mSelectedFolderName = folder.getName();
			mParentFolderPath = getParentFolderPath(folder);
			setSelectedDisplay(mSelectedFolderName);
			File[] subFolders = folder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});

			if(subFolders != null && subFolders.length > 0)
			{
				mCurrentFolderPath = folder.getAbsolutePath();
				loadFolderList();
			}
		}
		*/
	}

	private String getSdCardPath(String baseStoragePath)
	{
		File storageRoots[] = new File("/storage/").listFiles();
		ArrayList<String> sdCardPaths = new ArrayList<>();

		for(File singleRoot : storageRoots)
		{
			if(!singleRoot.getAbsolutePath().equalsIgnoreCase(baseStoragePath)
					&& singleRoot.isDirectory()
					&& singleRoot.canRead())
			{
				sdCardPaths.add(singleRoot.getAbsolutePath());
			}
		}

		if(sdCardPaths.isEmpty())
		{
			return null;
		}
		else
		{
			return sdCardPaths.get(0);
		}
	}

	private ArrayList<FileItem> getFileItems(String filePath, boolean fetchFoldersOnly)
	{
		ArrayList<FileItem> fileAndFolderItems = new ArrayList<>();

		File currentLocation = new File(filePath);

		File[] folders = currentLocation.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		if(folders.length > 0)
		{
			for(File file : folders)
			{
				FileItem fileItem = new FileItem();
				fileItem.setName(file.getName());
				fileItem.setPath(file.getPath());
				fileItem.setIsDirectory(true);
				fileItem.setIcon(R.drawable.ic_folder_black_24dp);
				fileItem.setSize(-1);
				fileItem.setHasChildren(file.listFiles().length > 0);
				if(isTopLevelFolder(file.getAbsolutePath()))
				fileItem.setIsTopLevel(isTopLevelFolder(file.getAbsolutePath()));
				fileItem.setParentPath(filePath);
				fileItem.setIsTopLevel(isTopLevelFolder(file.getAbsolutePath()));
				fileAndFolderItems.add(fileItem);
			}
			Collections.sort(fileAndFolderItems);
		}

		if(!fetchFoldersOnly)
		{
			ArrayList<FileItem> fileItems = new ArrayList<>();
			File[] files = currentLocation.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return !pathname.isDirectory();
				}
			});
			if(files.length > 0)
			{
				for(File file : files)
				{
					FileItem fileItem = new FileItem();
					fileItem.setName(file.getName());
					fileItem.setPath(file.getPath());
					fileItem.setIsDirectory(false);
					fileItem.setIcon(R.drawable.ic_folder_black_24dp);
					fileItem.setSize(file.length());
					fileItem.setHasChildren(false);
					fileItem.setParentPath(filePath);
					fileItem.setIsTopLevel(false);
					fileItems.add(fileItem);
				}
				Collections.sort(fileItems);
				fileAndFolderItems.addAll(fileItems);
			}

		}
		return fileAndFolderItems;
	}

	private boolean isTopLevelFolder(String folderPath)
	{
		boolean isTopLevel = false;

		if(folderPath.equalsIgnoreCase(mDeviceRootPath))
		{
			isTopLevel = true;
		}
		else if (mSdCardRootPath != null && folderPath.equalsIgnoreCase(mSdCardRootPath))
		{
			isTopLevel = true;
		}

		return isTopLevel;
	}
}
