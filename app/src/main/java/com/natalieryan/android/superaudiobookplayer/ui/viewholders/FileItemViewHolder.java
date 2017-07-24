package com.natalieryan.android.superaudiobookplayer.ui.viewholders;

import com.natalieryan.android.superaudiobookplayer.databinding.FileItemBinding;
import com.natalieryan.android.superaudiobookplayer.model.FileItem;

/**
 * Created by natalier258 on 7/21/17.
 *
 */

public class FileItemViewHolder extends GenericRecyclerViewHolder
{
	private final FileItemBinding mBinding;

	public FileItemViewHolder(FileItemBinding binding, OnViewHolderClickListener clickListener)
	{
		super(binding.getRoot(), clickListener);
		this.mBinding = binding;
	}

	@Override
	public void bind(Object fileItemObject)
	{
		FileItem fileItem = (FileItem) fileItemObject;
		mBinding.setFileItem(fileItem);
		mBinding.executePendingBindings();
	}
}
