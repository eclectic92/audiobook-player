package com.natalieryan.android.superaudiobookplayer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by natalier258 on 7/12/17.
 *
 */

@SuppressWarnings("unused")
public class LibraryContract
{
	public static final String CONTENT_AUTHORITY="com.natalieryan.android.superaudiobookplayer";
	public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
	public static final String PATH_LIBRARY_FOLDER="library_folder";

	public static final class FolderEntry implements BaseColumns
	{

		public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_LIBRARY_FOLDER)
				.build();

		public static final String CONTENT_TYPE=
				ContentResolver.CURSOR_DIR_BASE_TYPE+
						"/"+CONTENT_AUTHORITY+"/"+PATH_LIBRARY_FOLDER;

		public static final String CONTENT_ITEM_TYPE=
				ContentResolver.CURSOR_ITEM_BASE_TYPE+
						"/"+CONTENT_AUTHORITY+"/"+PATH_LIBRARY_FOLDER;

		public static final String TABLE_NAME="folderTable";

		public static final String COLUMN_PATH="folderPath";

		public static final String COLUMN_IS_SD_FOLDER="isSdFolder";

		public static final String COLUMN_EACH_FILE_IS_A_BOOK="eachFileIsABook";


		public static Uri buildFolderUriWithId(long id)
		{
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
}
