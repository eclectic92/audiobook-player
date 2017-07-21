package com.natalieryan.android.superaudiobookplayer.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;
import com.natalieryan.android.superaudiobookplayer.utils.filesystem.FileUtils;

/**
 * Created by natalier258 on 7/21/17.
 *
 */

public class LibraryFolderSorter implements GenericListSorter<LibraryFolder>
{
	private final Context mContext;


	public LibraryFolderSorter(Context context)
	{
		this.mContext=context;
	}


	@Nullable
	public String getHeaderForEntry(LibraryFolder currentFolder)
	{
		String rootPath = currentFolder.getRootPath();
		if(rootPath.equalsIgnoreCase(FileUtils.getDeviceRootStoragePath()))
		{

			return mContext.getString(R.string.header_for_device_folders);
		}
		else
		{
			String sdState = FileUtils.sdCardIsMounted(rootPath)
					? mContext.getString(R.string.header_sd_inserted)
					: mContext.getString(R.string.header_sd_not_inserted);
			return mContext.getString(R.string.header_for_sd_card_folders,
					FileUtils.getFriendlySdCardName(rootPath),sdState);
		}
	}

	public long getHeaderIdForEntry(LibraryFolder currentFolder)
	{
		return currentFolder.getRootPath().hashCode();
	}
}
