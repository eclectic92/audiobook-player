package com.natalieryan.android.superaudiobookplayer.ui.viewholders;

import android.databinding.ViewDataBinding;

import com.natalieryan.android.superaudiobookplayer.BR;

/**
 * Created by natalier258 on 7/21/17.
 *
 */

public class GenericHeaderViewHolder extends GenericRecyclerViewHolder
{
	private final ViewDataBinding mBinding;

	public GenericHeaderViewHolder(ViewDataBinding binding)
	{
		super(binding.getRoot());
		this.mBinding = binding;
	}

	@Override
	public void bind(String bindingString)
	{
		mBinding.setVariable(BR.headerString, bindingString);
		mBinding.executePendingBindings();
	}
}
