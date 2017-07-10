package com.natalieryan.android.superaudiobookplayer.ui.filebrowser;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.natalieryan.android.superaudiobookplayer.BR;
import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.databinding.FolderItemBinding;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by natalier258 on 7/8/17.
 *
 */

@SuppressWarnings("unused")
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder>
{
	private ArrayList<File> mFolders;
	private FolderClickListener clickListener;

	//default constructor
	public FolderAdapter(){}

	public interface FolderClickListener
	{
		void onFolderClick(View view, int position);
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{
		private final FolderItemBinding mBinding;

		public ViewHolder(FolderItemBinding binding)
		{
			super(binding.getRoot());
			binding.getRoot().setOnClickListener(this);
			this.mBinding = binding;
		}

		public void bind(File folder)
		{
			mBinding.setVariable(BR.folder, folder);
			mBinding.executePendingBindings();
		}

		@Override
		public void onClick(View view)
		{
			if (clickListener!=null)
			{
				clickListener.onFolderClick(view, getAdapterPosition());
			}
		}
	}

	@Override
	public FolderAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		Context context=viewGroup.getContext();
		int layoutIdForListItem=R.layout.folder_item;
		LayoutInflater inflater=LayoutInflater.from(context);
		FolderItemBinding binding =DataBindingUtil.inflate(inflater, layoutIdForListItem, viewGroup, false);
		return new FolderAdapter.ViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(FolderAdapter.ViewHolder viewHolder, int position)
	{
		File folderItem = mFolders.get(position);
		viewHolder.bind(folderItem);
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

	@Nullable
	File getItem (int position){
		if(mFolders != null)
		{
			return mFolders.get(position);
		}else
		{
			return null;
		}
	}

	public ArrayList<File> getFolderList()
	{
		return mFolders;
	}

	void setFolderList(ArrayList<File> folderList)
	{
		if(mFolders != null && !mFolders.isEmpty())
		{
			mFolders.clear();
		}
		mFolders = folderList;
		notifyDataSetChanged();
	}

	public void setClickListener(FolderClickListener folderClickListener)
	{
		this.clickListener=folderClickListener;
	}
}
