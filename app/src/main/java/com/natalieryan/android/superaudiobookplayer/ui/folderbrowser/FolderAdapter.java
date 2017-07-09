package com.natalieryan.android.superaudiobookplayer.ui.folderbrowser;

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
	private boolean mShowParent;
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

		public void bind(File folder, boolean showAsParent)
		{
			mBinding.setVariable(BR.folder, folder);
			if(showAsParent){
				mBinding.folderNameTv.setText("..");
			}else
			{
				mBinding.folderNameTv.setText(folder.getName());
			}
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
		boolean showAsParent = false;
		if(position == 0 && mShowParent)
		{
			showAsParent = true;
		}
		viewHolder.bind(folderItem, showAsParent);
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

	void setFolderList(ArrayList<File> folderList, boolean showParent)
	{
		if(mFolders != null && !mFolders.isEmpty())
		{
			mFolders.clear();
		}
		this.mShowParent = showParent;
		mFolders = folderList;
		notifyDataSetChanged();
	}

	public void setClickListener(FolderClickListener folderClickListener)
	{
		this.clickListener=folderClickListener;
	}
}
