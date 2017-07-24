package com.natalieryan.android.superaudiobookplayer.ui.adapters;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by natalier258 on 7/21/17.
 *
 */

@SuppressWarnings("unused")
public abstract class GenericArrayListAdapter<T, S extends GenericListSorter>
		extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	private final ArrayList<T> mData = new ArrayList<>();
	private List<Integer> mNumberOfHeadersOnOrAbove;
	private boolean mShowHeaders;
	private S mSorter;

	// the various item view types that this adapter handles.
	public static final int ITEM_VIEW_TYPE_HEADER= 1;
	public static final int ITEM_VIEW_TYPE_DATA_ITEM= 2;

	public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

	public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

	public ArrayList<T> getData() {
		return mData;
	}

	public void setData(@NonNull ArrayList<T> items, boolean showHeaders) {
		clearData();
		this.mShowHeaders = showHeaders;
		if(!items.isEmpty())
		{
			this.mData.addAll(items);
		}
	}

	public void setData(@NonNull Cursor cursor, boolean showHeaders) {
		clearData();
		this.mShowHeaders = showHeaders;
	}

	public void clearData(){
		mNumberOfHeadersOnOrAbove = null;
		mData.clear();
	}

	public void add(T t) {
		mData.add(t);
	}

	public void remove(T t) {
		mData.remove(t);
	}

	public S getSorter() {
		return mSorter;
	}

	public void setSorter(S obj) {
		this.mSorter  = obj;
	}

	@Nullable
	public List<Integer> getNumHeadersOnOrAbove() {
		return mNumberOfHeadersOnOrAbove;
	}

	public int getCount()
	{
		return mData.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getItemCount()
	{
		if(!mShowHeaders)
		{
			return mData.size();
		}

		// if the count has already been calculated, simply return it, otherwise, calculate and return
		if (mNumberOfHeadersOnOrAbove!=null)
		{
			return mNumberOfHeadersOnOrAbove.size();
		}

		mNumberOfHeadersOnOrAbove=new ArrayList<>();

		int totalItemCount=0;

		if (!mData.isEmpty())
		{
			int numItemsInList = mData.size();
			int offsetFromItemsToListIndex=0;

			for (int i=0; i<numItemsInList; i++)
			{
				if (i==0)
				{
					offsetFromItemsToListIndex++;
					mNumberOfHeadersOnOrAbove.add(offsetFromItemsToListIndex);
					mNumberOfHeadersOnOrAbove.add(offsetFromItemsToListIndex);
				}
				else
				{
					T itemCurrentPosition = mData.get(i);
					T itemPreviousPosition = mData.get(i-1);

					if(mSorter !=null)
					{
						if (mSorter.getHeaderIdForEntry(itemCurrentPosition)
								!=mSorter.getHeaderIdForEntry(itemPreviousPosition))
						{
							offsetFromItemsToListIndex++;
							mNumberOfHeadersOnOrAbove.add(offsetFromItemsToListIndex);
							mNumberOfHeadersOnOrAbove.add(offsetFromItemsToListIndex);
						}
						else
						{
							mNumberOfHeadersOnOrAbove.add(offsetFromItemsToListIndex);
						}
					}

				}
			}
			totalItemCount=mNumberOfHeadersOnOrAbove.size();
		}

		return totalItemCount;
	}


	@Override
	public int getItemViewType(int position)
	{
		if(!mShowHeaders) return ITEM_VIEW_TYPE_DATA_ITEM;

		if (position == 0 && mData.size() > 0)
		{
			return ITEM_VIEW_TYPE_HEADER;
		}

		// check if we have more than 1 item, so we can check item(position) and item(position+1)
		int numHeadersForPreviousEntry=mNumberOfHeadersOnOrAbove.get(position-1);
		int numHeadersForCurrentEntry=mNumberOfHeadersOnOrAbove.get(position);

		if (numHeadersForCurrentEntry!=numHeadersForPreviousEntry)
		{
			return ITEM_VIEW_TYPE_HEADER;
		}
		else
		{
			return ITEM_VIEW_TYPE_DATA_ITEM;
		}
	}

	@Nullable
	public T getItem(int position)
	{
		if (!mData.isEmpty())
		{
			if(!mShowHeaders) return mData.get(position);
			return mData.get(position-mNumberOfHeadersOnOrAbove.get(position));
		}
		else
		{
			return null;
		}
	}

	@Nullable
	public T getDataItem(int position)
	{
		if (!mData.isEmpty())
		{
			return mData.get(position);
		}
		else
		{
			return null;
		}
	}
}
