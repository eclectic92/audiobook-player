package com.natalieryan.android.superaudiobookplayer.ui.adapters;

/**
 * Created by natalier258 on 7/21/17.
 *
 */

public interface GenericListSorter<T> {
	String getHeaderForEntry(T currentEntry);
	long getHeaderIdForEntry(T currentEntry);
	void setSortOption(Enum sortByOption);
}
