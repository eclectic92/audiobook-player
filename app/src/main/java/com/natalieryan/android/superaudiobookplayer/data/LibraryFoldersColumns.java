package com.natalieryan.android.superaudiobookplayer.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface LibraryFoldersColumns
{
	@DataType(INTEGER)
	@PrimaryKey
	@AutoIncrement
	String ID = "_id";

	@DataType(TEXT)
	@NotNull
	String FOLDER_PATH = "folder_path";

	@DataType(INTEGER)
	@NotNull
	String CONTAINS_MULTIPLE_BOOKS = "contains_multiple_books";

	@DataType(INTEGER)
	@NotNull
	String IS_ON_SD_CARD = "is_on_sd_card";
}