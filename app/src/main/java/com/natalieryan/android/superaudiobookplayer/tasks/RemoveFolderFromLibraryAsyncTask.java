package com.natalieryan.android.superaudiobookplayer.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.natalieryan.android.superaudiobookplayer.data.LibraryContract;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;

/**
 * Created by natalier258 on 7/15/17.
 *
 */

public class RemoveFolderFromLibraryAsyncTask extends AsyncTask<LibraryFolder, Void, String>
{
	private final Context mContext;
	private final RemoveFolderListener mFolderListener;


	public RemoveFolderFromLibraryAsyncTask(Context context, RemoveFolderListener removeFavoriteListener)
	{
		this.mContext=context;
		this.mFolderListener=removeFavoriteListener;
	}


	/**
	 * Listener for async task completion
	 * Must be implemented by hosting class
	 */
	@SuppressWarnings("UnusedParameters")
	public interface RemoveFolderListener
	{
		void onFolderRemoved(String folderFriendlyPath);
	}


	@Override
	protected String doInBackground(LibraryFolder... params)
	{

		if (params.length==0)
		{
			return null;
		}

		LibraryFolder libraryFolder=params[0];

		if (mContext!=null && mContext.getContentResolver()!=null)
		{
			mContext.getContentResolver().delete(
					LibraryContract.FolderEntry.CONTENT_URI,
					LibraryContract.FolderEntry._ID+" = "+libraryFolder.getId(),
					null
			);

		}
		return libraryFolder.getFriendlyPath();
	}


	@Override
	protected void onPostExecute(String friendlyPath)
	{
		mFolderListener.onFolderRemoved(friendlyPath);
	}
}

