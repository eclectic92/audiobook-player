package com.natalieryan.android.superaudiobookplayer.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

/**
 * Created by natalier258 on 7/14/17.
 *
 */

@SuppressWarnings("unused")
public class FolderManagerAdapter extends GenericListAdapter<LibraryFolder, FolderManagerSorter>
{
	private LibraryFolderItemBinding mBinder;


	public FolderManagerAdapter(Context context)
	{
		this.setSorter(new FolderManagerSorter(context));
	}

	@Override
	public GenericRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		Context context=viewGroup.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);

		switch (viewType)
		{
			case ITEM_VIEW_TYPE_DATA_ITEM:
			{
				LibraryFolderItemBinding libraryFolderItemBinding =
						DataBindingUtil.inflate(inflater, R.layout.library_folder_item, viewGroup, false);
				return new LibraryFolderViewHolder(libraryFolderItemBinding);
			}
			case ITEM_VIEW_TYPE_HEADER:
			{
				GenericHeaderItemBinding headerItemBinding =
						DataBindingUtil.inflate(inflater, R.layout.generic_header_item, viewGroup, false);
				return new GenericHeaderViewHolder(headerItemBinding);
			}
		}
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
	{
		GenericRecyclerViewHolder holder = (GenericRecyclerViewHolder) viewHolder;
		if(holder instanceof LibraryFolderViewHolder)
		{
			final LibraryFolder folderItem = getItem(position);
			holder.bind(folderItem);
		}
		else if(holder instanceof GenericHeaderViewHolder)
		{
			LibraryFolder folderItem;
			if(getNumHeadersOnOrAbove() != null)
			{
				folderItem = getData().get(position-getNumHeadersOnOrAbove().get(position)+1);
				holder.bind(getSorter().getHeaderForEntry(folderItem));
			}
		}
	}


	public void setData(@NonNull ArrayList<LibraryFolder> folderList, boolean showHeaders)
	{
		super.setData(folderList, showHeaders);
		notifyDataSetChanged();
	}

	public void setData(@NonNull Cursor cursor, boolean showHeaders)
	{
		super.setData(cursor ,showHeaders);
		if (cursor.moveToFirst())
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
				  add(singleFolder);
			} while (cursor.moveToNext());
		}
		notifyDataSetChanged();
	}
}
