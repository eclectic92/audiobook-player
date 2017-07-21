package com.natalieryan.android.superaudiobookplayer.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.data.LibraryContract;
import com.natalieryan.android.superaudiobookplayer.databinding.GenericHeaderItemBinding;
import com.natalieryan.android.superaudiobookplayer.databinding.LibraryFolderItemBinding;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;
import com.natalieryan.android.superaudiobookplayer.ui.viewholders.GenericHeaderViewHolder;
import com.natalieryan.android.superaudiobookplayer.ui.viewholders.GenericRecyclerViewHolder;
import com.natalieryan.android.superaudiobookplayer.ui.viewholders.LibraryFolderViewHolder;
import com.natalieryan.android.superaudiobookplayer.utils.filesystem.FileUtils;

import java.util.ArrayList;

import java.util.List;

/**
 * Created by natalier258 on 7/14/17.
 *
 */

@SuppressWarnings("unused")
public class FolderManagerAdapter extends RecyclerView.Adapter<GenericRecyclerViewHolder>
{
	public static final int VIEW_TYPE_HEADER=1;
	public static final int VIEW_TYPE_FOLDER=2;

	private final ArrayList<LibraryFolder> mFolders = new ArrayList<>();
	private boolean mShowHeaders = false;
	private List<Integer> mNumberOfHeadersOnOrAbove;
	private LibraryFolderItemBinding mBinder;
	private final Context mContext;


	public FolderManagerAdapter(Context context, boolean showHeaders)
	{
		this.mContext = context;
		this.mShowHeaders = showHeaders;
	}

	@Override
	public GenericRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		Context context=viewGroup.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);

		switch (viewType)
		{
			case VIEW_TYPE_FOLDER:
			{
				LibraryFolderItemBinding libraryFolderItemBinding =
						DataBindingUtil.inflate(inflater, R.layout.library_folder_item, viewGroup, false);
				return new LibraryFolderViewHolder(libraryFolderItemBinding);
			}
			case VIEW_TYPE_HEADER:
			{
				GenericHeaderItemBinding headerItemBinding =
						DataBindingUtil.inflate(inflater, R.layout.generic_header_item, viewGroup, false);
				return new GenericHeaderViewHolder(headerItemBinding);
			}
		}
		return null;
	}

	@Override
	public void onBindViewHolder(GenericRecyclerViewHolder viewHolder, int position)
	{
		if(viewHolder instanceof LibraryFolderViewHolder)
		{
			int itemPosition = position;
			if(mShowHeaders)
			{
				itemPosition = position-mNumberOfHeadersOnOrAbove.get(position);
			}
			final LibraryFolder folderItem = mFolders.get(itemPosition);
			viewHolder.bind(folderItem);
		}
		else if(viewHolder instanceof GenericHeaderViewHolder)
		{
			LibraryFolder folderItem = mFolders.get(position-mNumberOfHeadersOnOrAbove.get(position)+1);
			viewHolder.bind(getHeaderForFolderGroup(folderItem));
		}
	}

	@Nullable
	public LibraryFolder getItem (int position){

		if(!mShowHeaders) return mFolders.get(position);

		if (!mFolders.isEmpty())
		{
			return mFolders.get(position-mNumberOfHeadersOnOrAbove.get(position));
		}
		else
		{
			return new LibraryFolder();
		}
	}

	public ArrayList<LibraryFolder> getFileList()
	{
		return mFolders;
	}

	@Override
	public int getItemViewType(int position)
	{
		if(!mShowHeaders) return VIEW_TYPE_FOLDER;

		if (position == 0 && mFolders.size() > 0)
		{
			return VIEW_TYPE_HEADER;
		}

		// check if we have more than 1 item, so we can check item(position) and item(position+1)
		int numHeadersForPreviousEntry=mNumberOfHeadersOnOrAbove.get(position-1);
		int numHeadersForCurrentEntry=mNumberOfHeadersOnOrAbove.get(position);

		if (numHeadersForCurrentEntry!=numHeadersForPreviousEntry)
		{
			return VIEW_TYPE_HEADER;
		}
		else
		{
			return VIEW_TYPE_FOLDER;
		}
	}

	public void setFolderList(ArrayList<LibraryFolder> folderList)
	{
		mFolders.clear();
		mNumberOfHeadersOnOrAbove=null;
		if(folderList != null  && !folderList.isEmpty())
		{
			this.mFolders.addAll(folderList);
		}
		notifyDataSetChanged();
	}

	public void setFolderList(Cursor cursor)
	{
		mFolders.clear();
		mNumberOfHeadersOnOrAbove=null;
		if (cursor!=null && cursor.moveToFirst())
		{
			do
			{
				long id=cursor.getLong(LibraryContract.FolderEntry.INDEX_LIBRARY_FOLDER_ID);
				String path=cursor.getString(LibraryContract.FolderEntry.INDEX_LIBRARY_FOLDER_PATH);
				String rootPath=cursor.getString(LibraryContract.FolderEntry.INDEX_LIBRARY_FOLDER_ROOT_PATH);
				boolean isOnSd=cursor.getInt(LibraryContract.FolderEntry.INDEX_LIBRARY_FOLDER_IS_SD_FOLDER) == 1;
				int bookCount=cursor.getInt(LibraryContract.FolderEntry.INDEX_LIBRARY_FOLDER_BOOK_COUNT);
				boolean eachFileIsBook=
						cursor.getInt(LibraryContract.FolderEntry.INDEX_LIBRARY_FOLDER_EACH_FILE_IS_A_BOOK) == 1;
				LibraryFolder singleFolder = new LibraryFolder(id, path, rootPath, isOnSd, eachFileIsBook, bookCount);
				mFolders.add(singleFolder);
			} while (cursor.moveToNext());
		}
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount()
	{
		if(!mShowHeaders)
		{
			return mFolders.size();
		}

		// if the count has already been calculated, simply return it, otherwise, calculate and return
		if (mNumberOfHeadersOnOrAbove!=null)
		{
			return mNumberOfHeadersOnOrAbove.size();
		}

		mNumberOfHeadersOnOrAbove=new ArrayList<>();

		int totalItemCount=0;

		if (!mFolders.isEmpty())
		{
			int numItemsInList = mFolders.size();
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
					LibraryFolder libraryFolderCurrentPosition = mFolders.get(i);
					LibraryFolder libraryFolderPreviousPosition = mFolders.get(i-1);

					if(!libraryFolderCurrentPosition.getRootPath()
							.equalsIgnoreCase(libraryFolderPreviousPosition.getRootPath()))
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
			totalItemCount=mNumberOfHeadersOnOrAbove.size();
		}

		return totalItemCount;
	}


	public String getHeaderForFolderGroup(LibraryFolder currentFolder)
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
}
