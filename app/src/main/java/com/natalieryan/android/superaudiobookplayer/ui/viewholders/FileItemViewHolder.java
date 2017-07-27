package com.natalieryan.android.superaudiobookplayer.ui.viewholders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.databinding.FileItemBinding;
import com.natalieryan.android.superaudiobookplayer.model.FileItem;

/**
 * Created by natalier258 on 7/21/17.
 *
 */

public class FileItemViewHolder extends GenericRecyclerViewHolder
{
	private final FileItemBinding mBinding;
	private final Context mContext;

	public FileItemViewHolder(FileItemBinding binding, Context context, OnViewHolderClickListener clickListener)
	{
		super(binding.getRoot(), clickListener);
		this.mContext = context;
		this.mBinding = binding;

	}

	@Override
	public void bind(Object fileItemObject)
	{
		FileItem fileItem = (FileItem) fileItemObject;
		mBinding.setFileItem(fileItem);
		int textColor = fileItem.getIsDisabled() ? ContextCompat.getColor(mContext, R.color.textColorPrimaryDisabled) :
				ContextCompat.getColor(mContext, R.color.textColorPrimary);
		mBinding.folderNameTv.setTextColor(textColor);
		mBinding.executePendingBindings();
	}
}
