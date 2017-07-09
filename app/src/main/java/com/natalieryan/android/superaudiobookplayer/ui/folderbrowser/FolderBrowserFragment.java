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

	private FolderAdapter mFolderAdapter;
	private FragmentFolderBrowserBinding mBinder;
	private String mCurrentFolderName;
	private File mParentFolder;
	private String mDeviceRootPath;
	private String mSdCardRootPath;

	private ArrayList<File> mFolders;

	//default constructor
	public FolderBrowserFragment() {}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mDeviceRootPath= Environment.getExternalStorageDirectory().getAbsolutePath();
		ArrayList<String> sdCardPaths = getSdCardPaths(mDeviceRootPath);
		if(sdCardPaths != null && !sdCardPaths.isEmpty())
		{
			mSdCardRootPath= sdCardPaths.get(0);
		}else
		{
			mSdCardRootPath= "";
		}

		mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_folder_browser, container, false);

		View rootView = mBinder.getRoot();

		LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
		mFolderAdapter = new FolderAdapter();
		mFolderAdapter.setClickListener(this);
		mBinder.folderListRv.setAdapter(mFolderAdapter);
		mBinder.folderListRv.setLayoutManager(layoutManager);

		loadFolderList();

		return rootView;
	}

	private void loadFolderList()
	{
		boolean showParent = false;

		if (mCurrentFolderName== null || mCurrentFolderName.isEmpty())
		{
			mCurrentFolderName=mDeviceRootPath;
		}

		File singleFolder = new File(mCurrentFolderName);
		mParentFolder = getParentFolder(singleFolder);

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

			if(mParentFolder != null)
			{
				mFolders.add(0, mParentFolder);
				showParent = true;
			}
			mFolderAdapter.setFolderList(mFolders, showParent);
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
		if(folder != null){
			mCurrentFolderName= folder.getAbsolutePath();
			loadFolderList();
		}
	}

	@Nullable
	private File getParentFolder(File currentFolder)
	{
		String currentFolderPath = currentFolder.getAbsolutePath();
		File parentFolder = null;

		if(!currentFolderPath.equalsIgnoreCase(mDeviceRootPath) && !currentFolderPath.equalsIgnoreCase(mSdCardRootPath))
		{
			parentFolder = currentFolder.getParentFile();
		}
		return parentFolder;
	}

	public void backButtonWasPressed() {
		if(mParentFolder != null){
			mCurrentFolderName = mParentFolder.getAbsolutePath();
			loadFolderList();
		}
		Log.d ("browser", "back pressed");
	}

	public boolean isAtTopLevel(){
		return mParentFolder == null;
	}
}
