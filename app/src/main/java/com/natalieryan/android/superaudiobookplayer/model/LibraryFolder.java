package com.natalieryan.android.superaudiobookplayer.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import net.simonvt.schematic.annotation.NotNull;

/**
 * Created by natalier258 on 7/12/17.
 *
 */

public class LibraryFolder implements Parcelable
{
	private String mPath;
	private boolean mIsSdCardFolder = false;
	private boolean mContainsMultipleBooks = false;

	public LibraryFolder(){}

	public LibraryFolder(String path, boolean isSdCardFolder, boolean containsMultipleBooks)
	{
		this.mPath = path;
		this.mIsSdCardFolder = isSdCardFolder;
		this.mContainsMultipleBooks = containsMultipleBooks;
	}

	//constructor for parceler
	private LibraryFolder(Parcel in)
	{
		this.mPath = in.readString();
		this.mIsSdCardFolder = in.readInt() == 1;
		this.mContainsMultipleBooks = in.readInt() == 1;
	}

	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(mPath);
		out.writeInt(mIsSdCardFolder ? 1 : 0);
		out.writeInt(mContainsMultipleBooks ? 1 : 0);
	}

	public static final Parcelable.Creator<LibraryFolder> CREATOR = new Parcelable.Creator<LibraryFolder>()
	{
		public LibraryFolder createFromParcel(Parcel parcel)
		{
			return new LibraryFolder(parcel);
		}

		public LibraryFolder[] newArray(int size)
		{
			return new LibraryFolder[size];
		}
	};

	@Override
	public int describeContents()
	{
		return 0;
	}

	public String getPath()
	{
		return mPath;
	}

	public void setPath(@NonNull String path)
	{
		this.mPath = path;
	}

	public boolean getIsSdCardFolder()
	{
		return mIsSdCardFolder;
	}

	public void setIsSdCardFolder(boolean isSdCardFolder)
	{
		this.mIsSdCardFolder = isSdCardFolder;
	}

	public boolean getContainsMultipleBooks()
	{
		return mContainsMultipleBooks;
	}

	public void setContainsMultipleBooks(boolean containsMultipleBooks)
	{
		this.mContainsMultipleBooks = containsMultipleBooks;
	}
}
