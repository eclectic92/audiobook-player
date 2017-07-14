package com.natalieryan.android.superaudiobookplayer.ui.foldermanager;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.data.LibraryContract;
import com.natalieryan.android.superaudiobookplayer.databinding.LibraryFolderItemBinding;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;
import com.natalieryan.android.superaudiobookplayer.utils.PathUtils;

import java.util.ArrayList;

/**
 * Created by natalier258 on 7/14/17.
 *
 */

@SuppressWarnings("unused")
public class FolderManagerAdapter extends RecyclerView.Adapter<FolderManagerAdapter.ViewHolder>
{
	private final ArrayList<LibraryFolder> mFolders = new ArrayList<>();
	private LibraryFolderItemBinding mBinder;

	FolderManagerAdapter(){}

	class ViewHolder extends RecyclerView.ViewHolder
	{

		private final LibraryFolderItemBinding mBinding;

		ViewHolder(LibraryFolderItemBinding binding)
		{
			super(binding.getRoot());
			this.mBinding = binding;
		}

		void bind(LibraryFolder libraryFolder)
		{
			mBinding.setLibraryFolderItem(libraryFolder);
			mBinding.executePendingBindings();
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
	{
		Context context=viewGroup.getContext();
		int layoutIdForListItem = R.layout.library_folder_item;
		LayoutInflater inflater = LayoutInflater.from(context);
		LibraryFolderItemBinding libraryFolderItemBinding =
				DataBindingUtil.inflate(inflater, layoutIdForListItem, viewGroup, false);
		return new ViewHolder(libraryFolderItemBinding);
	}

	@Override
	public void onBindViewHolder(FolderManagerAdapter.ViewHolder viewHolder, int position)
	{
		final LibraryFolder fileItem = mFolders.get(position);
		viewHolder.bind(fileItem);
	}

	@Override
	public int getItemCount()
	{
		return mFolders.size();
	}

	@Nullable
	LibraryFolder getItem (int position){
		return mFolders.get(position);
	}

	public ArrayList<LibraryFolder> getFileList()
	{
		return mFolders;
	}

	void setFolderList(ArrayList<LibraryFolder> folderList)
	{

		mFolders.clear();

		if(folderList != null  && !folderList.isEmpty())
		{
			this.mFolders.addAll(folderList);
		}
		notifyDataSetChanged();
	}

	void setFolderList(Cursor cursor)
	{
		mFolders.clear();
		if (cursor!=null && cursor.moveToFirst())
		{
			do
			{
				long id=cursor.getLong(LibraryContract.FolderEntry.INDEX_LIBRARY_FOLDER_ID);
				String path=cursor.getString(LibraryContract.FolderEntry.INDEX_LIBRARY_FOLDER_PATH);
				boolean isOnSd=cursor.getInt(LibraryContract.FolderEntry.INDEX_LIBRARY_FOLDER_IS_SD_FOLDER) == 1;
				boolean eachFileIsBook=
						cursor.getInt(LibraryContract.FolderEntry.INDEX_LIBRARY_FOLDER_EACH_FILE_IS_A_BOOK) == 1;
				String friendlyPath=PathUtils.getFriendlyPath(path,isOnSd);
				LibraryFolder singleFolder = new LibraryFolder(id, path, friendlyPath, eachFileIsBook, isOnSd);
				mFolders.add(singleFolder);
			} while (cursor.moveToNext());
		}
		notifyDataSetChanged();
	}
}
