package com.natalieryan.android.superaudiobookplayer.ui.adapters;

/**
 * Created by natalier258 on 7/21/17.
 *
 */

@SuppressWarnings("unused")
interface GenericListSorter<T> {
	String getHeaderForEntry(T currentEntry);
	long getHeaderIdForEntry(T currentEntry);
}
