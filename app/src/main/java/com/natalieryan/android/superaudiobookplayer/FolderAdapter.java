package com.natalieryan.android.superaudiobookplayer;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.natalieryan.android.superaudiobookplayer.databinding.FolderItemBinding;

import java.util.ArrayList;


/**
 * Created by natalier258 on 7/8/17.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder>
{
	private ArrayList<FolderItem> mFolders;
	private FolderItemBinding mBinder;

	//default constructor
	public FolderAdapter(){}

	public class ViewHolder extends RecyclerView.ViewHolder
	{
		private TextView mFolderNameTv;
		public FolderItem folderItem;

		public ViewHolder(View view)
		{
			super(view);
		}
	}

	@Override
	public FolderAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		Context context=viewGroup.getContext();
		int layoutIdForListItem=R.layout.folder_item;
		LayoutInflater inflater=LayoutInflater.from(context);
		mBinder=DataBindingUtil.inflate(inflater, layoutIdForListItem, viewGroup, false);
		View view=mBinder.getRoot();
		return new FolderAdapter.ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(FolderAdapter.ViewHolder viewHolder, int position)
	{
		FolderItem folderItem = mFolders.get(position);
		mBinder.setFolder(folderItem);
	}

	@Override
	public int getItemCount()
	{
		if (null == mFolders)
		{
			return 0;
		}
		return mFolders.size();
	}

	FolderItem getItem (int position){
		if(mFolders != null)
		{
			return mFolders.get(position);
		}else
		{
			return new FolderItem();
		}
	}

	public ArrayList<FolderItem> getFolderList()
	{
		return mFolders;
	}

	void setFolderList(ArrayList<FolderItem> folderList)
	{
		mFolders = folderList;
		notifyDataSetChanged();
	}
}
