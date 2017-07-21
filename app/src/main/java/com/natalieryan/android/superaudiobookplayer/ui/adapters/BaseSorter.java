package com.natalieryan.android.superaudiobookplayer.ui.adapters;

import android.support.annotation.Nullable;

/**
 * Created by natalier258 on 7/21/17.
 *
 */

@SuppressWarnings("unused")
public class BaseSorter implements GenericListSorter<Object>
{

	public BaseSorter(){}

	@Nullable
	public String getHeaderForEntry(Object currentItem)
	{
		return null;
	}

	public long getHeaderIdForEntry(Object currentFolder)
	{
		return 0;
	}
}
