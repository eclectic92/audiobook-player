package com.natalieryan.android.superaudiobookplayer.ui.viewholders;

import com.natalieryan.android.superaudiobookplayer.databinding.LibraryFolderItemBinding;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;

/**
 * Created by natalier258 on 7/21/17.
 *
 */

public class LibraryFolderViewHolder extends GenericRecyclerViewHolder
{
	private final LibraryFolderItemBinding mBinding;

	public LibraryFolderViewHolder(LibraryFolderItemBinding binding)
	{
		super(binding.getRoot());
		this.mBinding = binding;
	}

	@Override
	public void bind(Object libraryFolderObject)
	{
		LibraryFolder libraryFolder = (LibraryFolder) libraryFolderObject;
		mBinding.setLibraryFolderItem(libraryFolder);
		mBinding.executePendingBindings();
	}
}
