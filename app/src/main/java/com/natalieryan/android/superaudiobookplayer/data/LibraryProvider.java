package com.natalieryan.android.superaudiobookplayer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyBulkInsert;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.NotifyUpdate;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by natalier258 on 7/11/17.
 *
 */

@ContentProvider(authority = LibraryProvider.AUTHORITY, database = LibraryDatabase.class)
public final class LibraryProvider
{

	private LibraryProvider()
	{
	}

	public static final String AUTHORITY = "com.natalieryan.android.superaudiobookplayer";
	static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	interface Path
	{
		String FOLDERS = "folders";
	}

	private static Uri buildUri(String... paths) {
		Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
		for (String path : paths)
		{
			builder.appendPath(path);
		}
		return builder.build();
	}

	@TableEndpoint(table = LibraryDatabase.FOLDERS) public static class Folders
	{

		@ContentUri(
				path = Path.FOLDERS,
				type = "vnd.android.cursor.dir/folder")
		public static final Uri CONTENT_URI = buildUri(Path.FOLDERS);

		@InexactContentUri(
				name = "FOLDER_ID",
				path = Path.FOLDERS + "/#",
				type = "vnd.android.cursor.item/folder",
				whereColumn = LibraryFoldersColumns.ID,
				pathSegment = 1)
		public static Uri withId(long id)
		{
			return buildUri(Path.FOLDERS, String.valueOf(id));
		}


		@NotifyInsert(paths = Path.FOLDERS)
		public static Uri[] onInsert(ContentValues values)
		{
			final long folderId = values.getAsLong(LibraryFoldersColumns.ID);
			return new Uri[] {
					Folders.withId(folderId)
			};
		}

		@NotifyBulkInsert(paths = Path.FOLDERS)
		public static Uri[] onBulkInsert(Context context, Uri uri, ContentValues[] values, long[] ids)
		{
			return new Uri[] {
					uri,
			};
		}

		@NotifyUpdate(paths = Path.FOLDERS + "/#")
		public static Uri[] onUpdate(Context context, Uri uri, String where, String[] whereArgs)
		{
			final long noteId = Long.valueOf(uri.getPathSegments().get(1));
			Cursor c = context.getContentResolver().query(uri, new String[] {
					LibraryFoldersColumns.ID,
			}, null, null, null);
			c.moveToFirst();
			c.close();

			return new Uri[] {
					withId(noteId)
			};
		}

		@NotifyDelete(paths = Path.FOLDERS + "/#")
		public static Uri[] onDelete(Context context, Uri uri)
		{
			final long folderId = Long.valueOf(uri.getPathSegments().get(1));
			Cursor c = context.getContentResolver().query(uri, null, null, null, null);
			c.moveToFirst();
			c.close();

			return new Uri[] {
					withId(folderId)
			};
		}
	}
}
