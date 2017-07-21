package com.natalieryan.android.superaudiobookplayer.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.databinding.FileItemBinding;
import com.natalieryan.android.superaudiobookplayer.model.FileItem;
import com.natalieryan.android.superaudiobookplayer.ui.viewholders.FileItemViewHolder;
import com.natalieryan.android.superaudiobookplayer.ui.viewholders.GenericRecyclerViewHolder;

import java.util.ArrayList;

/**
 * Created by natalier258 on 7/10/17.
 *
 */

@SuppressWarnings("unused")
public class FileItemAdapter extends GenericArrayListAdapter<FileItem, BaseSorter>
{
	private GenericRecyclerViewHolder.OnViewHolderClickListener mViewHolderClickListener;
	private FileItemBinding mBinder;

	//default constructor
	public FileItemAdapter(){}

	@Override
	public FileItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		Context context=viewGroup.getContext();
		int layoutIdForListItem = R.layout.file_item;
		LayoutInflater inflater = LayoutInflater.from(context);
		FileItemBinding fileItemBinding = DataBindingUtil.inflate(inflater, layoutIdForListItem, viewGroup, false);
		return new FileItemViewHolder(fileItemBinding, mViewHolderClickListener);
	}

	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
	{
		GenericRecyclerViewHolder holder = (GenericRecyclerViewHolder) viewHolder;
		final FileItem fileItem = getItem(position);
		holder.bind(fileItem);
	}

	public void setData(@NonNull ArrayList<FileItem> fileItems, boolean showHeaders)
	{
		super.setData(fileItems, showHeaders);
		notifyDataSetChanged();
	}

	public void setClickListener(GenericRecyclerViewHolder.OnViewHolderClickListener itemClickListener)
	{
		this.mViewHolderClickListener=itemClickListener;
	}
}