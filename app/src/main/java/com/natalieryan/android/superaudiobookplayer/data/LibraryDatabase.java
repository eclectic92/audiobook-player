package com.natalieryan.android.superaudiobookplayer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.ExecOnCreate;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by natalier258 on 7/11/17.
 *
 */

@Database(version = LibraryDatabase.VERSION)
public final class LibraryDatabase
{
	public static final int VERSION = 1;

	@Table(LibraryFoldersColumns.class)
	public static final String FOLDERS = "folders";

	@OnCreate
	public static void onCreate(Context context, SQLiteDatabase db) {
	}

	@OnUpgrade
	public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
								 int newVersion)
	{
	}

	@OnConfigure
	public static void onConfigure(SQLiteDatabase db)
	{
	}

	@ExecOnCreate
	public static final String EXEC_ON_CREATE = "SELECT * FROM " + FOLDERS;

}
