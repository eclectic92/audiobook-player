package com.natalieryan.android.superaudiobookplayer.ui.folderbrowser;


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
import com.natalieryan.android.superaudiobookplayer.databinding.FragmentFolderBrowserBinding;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class FolderBrowserFragment extends Fragment implements FolderAdapter.FolderClickListener
{
	public static final String TAG = FolderBrowserFragment.class.getSimpleName();

	private static final String EXTRA_CURRENT_FOLDER = "current_folder";
	private static final String EXTRA_PARENT_FOLDER = "parent_folder";
	private static final String EXTRA_SELECTED_FOLDER = "selected_folder";
	private static final String EXTRA_SELECTED_FOLDER_NAME = "selected_folder_name";


	private FolderAdapter mFolderAdapter;
	private FragmentFolderBrowserBinding mBinder;
	private String mCurrentFolderPath;
	private String mParentFolderPath;
	private String mDeviceRootPath;
	private String mSdCardRootPath;
	private String mSelectedFolderPath;
	private String mSelectedFolderName;

	private ArrayList<File> mFolders;

	//default constructor
	public FolderBrowserFragment() {}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(savedInstanceState != null)
		{
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

		}

		mDeviceRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		ArrayList<String> sdCardPaths = getSdCardPaths(mDeviceRootPath);

		if(sdCardPaths != null && !sdCardPaths.isEmpty())
		{
			mSdCardRootPath= sdCardPaths.get(0);
		}

		mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_folder_browser, container, false);

		View rootView = mBinder.getRoot();

		mBinder.backArrowImageView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				navigateBack();
			}
		});

		LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
		mFolderAdapter = new FolderAdapter();
		mFolderAdapter.setClickListener(this);
		mBinder.folderListRv.setAdapter(mFolderAdapter);
		mBinder.folderListRv.setLayoutManager(layoutManager);
		loadFolderList();

		return rootView;
	}


	@Override
	public void onSaveInstanceState(Bundle outState)
	{
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
	}

	private void loadFolderList()
	{

		if (mCurrentFolderPath == null || mCurrentFolderPath.isEmpty())
		{
			mCurrentFolderPath = mDeviceRootPath;
		}

		File singleFolder = new File(mCurrentFolderPath);

		if(mSelectedFolderPath == null){
			mSelectedFolderPath = singleFolder.getAbsolutePath();
			mSelectedFolderName = singleFolder.getName();
		}

		setSelectedDisplay(mSelectedFolderName);

		mParentFolderPath = getParentFolderPath(singleFolder);

		File[] folders = singleFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		mFolders = new ArrayList<>(Arrays.asList(folders));
		if (!mFolders.isEmpty())
		{
			Collections.sort(mFolders);
			mFolderAdapter.setFolderList(mFolders);
		}
	}

	private ArrayList<String> getSdCardPaths(String baseStoragePath)
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
		return sdCardPaths;
	}

	@Override
	public void onFolderClick (View view, int position)
	{
		final File folder = mFolderAdapter.getItem(position);
		if(folder != null)
		{
			mSelectedFolderPath = folder.getAbsolutePath();
			mSelectedFolderName = folder.getName();
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
			mBinder.backArrowImageView.setImageResource(R.drawable.ic_phone_android_black_24dp);
		}
		else if (mSelectedFolderPath.equalsIgnoreCase(mSdCardRootPath))
		{
			folderName =  getResources().getString(R.string.sd_root_folder);
			mBinder.backArrowImageView.setImageResource(R.drawable.ic_phone_android_black_24dp);
		}
		else
		{
			mBinder.backArrowImageView.setImageResource(R.drawable.ic_arrow_back_black_24dp);
		}
		String displayName = getResources().getString(R.string.selected_folder, folderName);
		mBinder.selectedFolderNameTv.setText(displayName);

	}

	public void navigateBack() {
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
		loadFolderList();

		Log.d (TAG, "back pressed");
	}

	public boolean isAtTopLevel(){
		return mParentFolderPath == null;
	}
}
