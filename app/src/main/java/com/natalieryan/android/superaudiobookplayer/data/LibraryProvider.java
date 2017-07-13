package com.natalieryan.android.superaudiobookplayer.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by natalier258 on 7/12/17.
 *
 */

public class LibraryProvider extends ContentProvider
{
	private static final int CODE_FOLDERS=100;
	private static final int CODE_FOLDER_BY_ID=101;

	private static final UriMatcher sUriMatcher=buildUriMatcher();
	private LibraryDbHelper mOpenHelper;


	private static UriMatcher buildUriMatcher()
	{

		final UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
		final String authority=LibraryContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, LibraryContract.PATH_LIBRARY_FOLDER, CODE_FOLDERS);
		matcher.addURI(authority, LibraryContract.PATH_LIBRARY_FOLDER+"/#", CODE_FOLDER_BY_ID);

		return matcher;
	}


	@Override
	public boolean onCreate()
	{
		mOpenHelper=new LibraryDbHelper(getContext());
		return true;
	}


	@Nullable
	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder)
	{

		Cursor cursor;

		switch (sUriMatcher.match(uri))
		{
			case CODE_FOLDER_BY_ID:
			{
				String folderId=uri.getLastPathSegment();
				String[] selectionArguments=new String[]{folderId};
				cursor=mOpenHelper.getReadableDatabase().query(
						LibraryContract.FolderEntry.TABLE_NAME,
						projection,
						LibraryContract.FolderEntry._ID+" = ? ",
						selectionArguments,
						null,
						null,
						sortOrder);
				break;
			}
			case CODE_FOLDERS:
			{
				cursor=mOpenHelper.getReadableDatabase().query(
						LibraryContract.FolderEntry.TABLE_NAME,
						projection,
						selection,
						selectionArgs,
						null,
						null,
						sortOrder);
				break;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: "+uri);
		}

		if (getContext()!=null)
		{
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return cursor;
	}


	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values)
	{

		Uri returnUri;

		switch (sUriMatcher.match(uri))
		{
			case CODE_FOLDERS:
			{
				long id=mOpenHelper.getWritableDatabase().insert(
						LibraryContract.FolderEntry.TABLE_NAME,
						null,
						values
				);

				if (id>0)
				{
					returnUri=LibraryContract.FolderEntry.buildFolderUriWithId(id);
				}
				else
				{
					throw new android.database.SQLException("Failed to insert row into "+uri);
				}
				break;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: "+uri);
		}

		if (getContext()!=null)
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return returnUri;
	}


	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs)
	{

		int deletedDrowsCount;

		if (null==selection)
		{
			selection="1";
		}

		switch (sUriMatcher.match(uri))
		{

			case CODE_FOLDERS:
				deletedDrowsCount=mOpenHelper.getWritableDatabase().delete(
						LibraryContract.FolderEntry.TABLE_NAME,
						selection,
						selectionArgs);
				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: "+uri);
		}

		if (deletedDrowsCount!=0)
		{
			if (getContext()!=null)
			{
				getContext().getContentResolver().notifyChange(uri, null);
			}
		}

		return deletedDrowsCount;
	}


	@Override
	public int update(@NonNull Uri uri, ContentValues values,
					  String selection, String[] selectionArgs)
	{

		int updatedRowsCount;

		switch (sUriMatcher.match(uri))
		{
			case CODE_FOLDERS:
			{
				updatedRowsCount=mOpenHelper.getWritableDatabase().update(
						LibraryContract.FolderEntry.TABLE_NAME,
						values,
						selection,
						selectionArgs
				);
				break;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: "+uri);
		}

		if (updatedRowsCount!=0)
		{
			if (getContext()!=null)
			{
				getContext().getContentResolver().notifyChange(uri, null);
			}
		}

		return updatedRowsCount;
	}


	@Nullable
	@Override
	public String getType(@NonNull Uri uri)
	{
		switch (sUriMatcher.match(uri))
		{
			case CODE_FOLDERS:
			{
				return LibraryContract.FolderEntry.CONTENT_TYPE;
			}
			case CODE_FOLDER_BY_ID:
			{
				return LibraryContract.FolderEntry.CONTENT_ITEM_TYPE;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: "+uri);

		}
	}
}
