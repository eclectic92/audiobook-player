package com.natalieryan.android.superaudiobookplayer.activities.filebrowser;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.databinding.FileItemBinding;
import com.natalieryan.android.superaudiobookplayer.model.FileItem;

import java.util.ArrayList;

/**
 * Created by natalier258 on 7/10/17.
 *
 */

@SuppressWarnings("unused")
public class FileItemAdapter extends RecyclerView.Adapter<FileItemAdapter.ViewHolder>
{
	private final ArrayList<FileItem> mFiles = new ArrayList<>();
	private FileClickListener clickListener;
	private FileItemBinding mBinder;

	//default constructor
	public FileItemAdapter(){}

	public interface FileClickListener
	{
		void onFileClick(View view, int position);
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
	{

		private final FileItemBinding mBinding;

		public ViewHolder(FileItemBinding binding)
		{
			super(binding.getRoot());
			binding.getRoot().setOnClickListener(this);
			this.mBinding = binding;
		}

		public void bind(FileItem fileItem)
		{
			mBinding.setFileItem(fileItem);
			mBinding.executePendingBindings();
		}
		@Override
		public void onClick(View view)
		{
			if (clickListener!=null)
			{
				clickListener.onFileClick(view, getAdapterPosition());
			}
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		Context context=viewGroup.getContext();
		int layoutIdForListItem = R.layout.file_item;
		LayoutInflater inflater = LayoutInflater.from(context);
		FileItemBinding fileItemBinding = DataBindingUtil.inflate(inflater, layoutIdForListItem, viewGroup, false);
		return new ViewHolder(fileItemBinding);
	}

	@Override
	public void onBindViewHolder(FileItemAdapter.ViewHolder viewHolder, int position)
	{
		final FileItem fileItem = mFiles.get(position);
		viewHolder.bind(fileItem);
	}


	@Override
	public int getItemCount()
	{
		return mFiles.size();
	}

	@Nullable
	FileItem getItem (int position){
		return mFiles.get(position);
	}

	public ArrayList<FileItem> getFileList()
	{
		return mFiles;
	}

	void setFileList(ArrayList<FileItem> fileList)
	{

		mFiles.clear();

		if(fileList != null  && !fileList.isEmpty())
		{
			this.mFiles.addAll(fileList);
		}
		notifyDataSetChanged();
	}

	public void setClickListener(FileClickListener fileClickListener)
	{
		this.clickListener=fileClickListener;
	}
}