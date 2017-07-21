package com.natalieryan.android.superaudiobookplayer.ui.viewholders;

import com.natalieryan.android.superaudiobookplayer.databinding.GenericHeaderItemBinding;

/**
 * Created by natalier258 on 7/21/17.
 *
 */

public class GenericHeaderViewHolder extends GenericRecyclerViewHolder
{
	private final GenericHeaderItemBinding mBinding;

	public GenericHeaderViewHolder(GenericHeaderItemBinding binding)
	{
		super(binding.getRoot());
		this.mBinding = binding;
	}

	@Override
	public void bind(String bindingString)
	{
		mBinding.setHeaderString(bindingString);
		mBinding.executePendingBindings();
	}
}
