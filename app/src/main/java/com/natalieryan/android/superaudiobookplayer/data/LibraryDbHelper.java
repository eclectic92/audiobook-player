package com.natalieryan.android.superaudiobookplayer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.natalieryan.android.superaudiobookplayer.data.LibraryContract.FolderEntry;
/**
 * Created by natalier258 on 7/12/17.
 *
 */

/*package-private*/  class LibraryDbHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME="library.db";
	private static final int DATABASE_VERSION=1;

	/*package-private*/  LibraryDbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		addFolderTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
	{
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+FolderEntry.TABLE_NAME);
		onCreate(sqLiteDatabase);
	}

	private void addFolderTable(SQLiteDatabase db)
	{
		final String SQL_CREATE_FOLDERS_TABLE=
				"CREATE TABLE "+ FolderEntry.TABLE_NAME+" ("+
						FolderEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
						FolderEntry.COLUMN_PATH+" TEXT NOT NULL, "+
						FolderEntry.COLUMN_IS_SD_FOLDER+" INTEGER NOT NULL, " +
						FolderEntry.COLUMN_CONTAINS_MULTIPLE_BOOKS+" INTEGER NOT NULL, " +
						" UNIQUE ("+FolderEntry.COLUMN_PATH+") ON CONFLICT REPLACE);";

		db.execSQL(SQL_CREATE_FOLDERS_TABLE);
	}
}
