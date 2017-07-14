package com.natalieryan.android.superaudiobookplayer.data.async;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.natalieryan.android.superaudiobookplayer.data.LibraryContract;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;

/**
 * Created by natalier258 on 7/12/17.
 *
 */

public class AddFolderToLibraryAsyncTask extends AsyncTask<LibraryFolder, Void, Integer>
{
	private final Context mContext;
	private final AddFolderListener mAddFolderListener;


	public AddFolderToLibraryAsyncTask(Context context, AddFolderListener addFolderListener)
	{
		this.mContext=context;
		this.mAddFolderListener=addFolderListener;
	}


	/**
	 * Listener for async task completion
	 * Must be implemented by hosting class
	 */
	public interface AddFolderListener
	{
		void onFolderAdded(int addedRowId);
	}


	@Override
	protected Integer doInBackground(LibraryFolder... params)
	{
		if (params.length==0)
		{
			return null;
		}

		int addedRowId = -1;
		LibraryFolder libraryFolder=params[0];

		ContentValues libaryFolderValues=new ContentValues();

		libaryFolderValues.put(LibraryContract.FolderEntry.COLUMN_PATH, libraryFolder.getPath());
		libaryFolderValues.put(LibraryContract.FolderEntry.COLUMN_IS_SD_FOLDER,
				libraryFolder.getIsSdCardFolder() ? 1 : 0);
		libaryFolderValues.put(LibraryContract.FolderEntry.COLUMN_EACH_FILE_IS_A_BOOK,
				libraryFolder.getEachFileIsABook() ? 1 : 0);

		if (mContext!=null && mContext.getContentResolver()!=null)
		{
			Uri retVal = mContext.getContentResolver()
					.insert(LibraryContract.FolderEntry.CONTENT_URI,libaryFolderValues);
			if(retVal != null)
			{
				addedRowId = Integer.parseInt(retVal.getLastPathSegment());
			}
		}
		return addedRowId;
	}

	@Override
	protected void onPostExecute(Integer addedRowId)
	{
		mAddFolderListener.onFolderAdded(addedRowId);
	}
}
