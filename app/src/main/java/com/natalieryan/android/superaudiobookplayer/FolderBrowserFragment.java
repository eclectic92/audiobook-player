package com.natalieryan.android.superaudiobookplayer;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.natalieryan.android.superaudiobookplayer.databinding.FragmentFolderBrowserBinding;

import java.util.ArrayList;


public class FolderBrowserFragment extends Fragment
{

	private ArrayList<FolderItem> mFolders;
	private FolderAdapter mFolderAdapter;
	private FragmentFolderBrowserBinding mBinder;

	//default constructor
	public FolderBrowserFragment() {}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_folder_browser, container, false);
		View rootView = mBinder.getRoot();

		loadFolderList();
		renderFolderList();
		return rootView;
	}

	private void loadFolderList()
	{
		mFolders = new ArrayList<FolderItem>();
		FolderItem test1 = new FolderItem();
		FolderItem test2 = new FolderItem();

		test1.setName("Folder 1");
		test2.setName("Folder 2");

		mFolders.add(test1);
		mFolders.add(test2);
	}
	private void renderFolderList()
	{
		if (mFolders!=null && !mFolders.isEmpty())
		{
			LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
			mFolderAdapter=new FolderAdapter();
			mBinder.folderListRv.setLayoutManager(layoutManager);
			mBinder.folderListRv.setAdapter(mFolderAdapter);
			mFolderAdapter.setFolderList(mFolders);
		}
	}
}
