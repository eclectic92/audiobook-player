package com.natalieryan.android.superaudiobookplayer;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.natalieryan.android.superaudiobookplayer.databinding.FragmentFolderBrowserBinding;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class FolderBrowserFragment extends Fragment implements FolderAdapter.FolderClickListener
{

	private FolderAdapter mFolderAdapter;
	private FragmentFolderBrowserBinding mBinder;
	private String mCurrentFolder;
	private String mRootDirPath;
	private ArrayList<File> mFolders;
	private ArrayList<String> mSdCardPaths = new ArrayList<>();


	//default constructor
	public FolderBrowserFragment() {}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mRootDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		mSdCardPaths = getSdCardPaths(mRootDirPath);

		mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_folder_browser, container, false);

		View rootView = mBinder.getRoot();

		LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
		mFolderAdapter = new FolderAdapter();
		mFolderAdapter.setClickListener(this);
		mBinder.folderListRv.setAdapter(mFolderAdapter);
		mBinder.folderListRv.setLayoutManager(layoutManager);

		loadFolderList(mCurrentFolder);

		return rootView;
	}

	private void loadFolderList(@Nullable String currentFolder)
	{
		if (currentFolder == null || currentFolder.isEmpty())
		{
			currentFolder = mRootDirPath;
		}

		File singleFolder = new File(currentFolder);
		File[] folders = singleFolder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		mFolders = new ArrayList<>(Arrays.asList(folders));
		if (!mFolders.isEmpty())
		{
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
		if(folder != null){
			loadFolderList(folder.getAbsolutePath());
		}
	}
}
