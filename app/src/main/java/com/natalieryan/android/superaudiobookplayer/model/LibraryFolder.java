package com.natalieryan.android.superaudiobookplayer.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by natalier258 on 7/12/17.
 *
 */

@SuppressWarnings("unused")
public class LibraryFolder implements Parcelable
{
	private long mId;
	private String mPath;
	private String mFriendlyPath;
	private boolean mIsSdCardFolder=false;
	private boolean mEachFileIsABook=false;


	public LibraryFolder()
	{
	}


	public LibraryFolder(long id, @NonNull String path, @NonNull String friendlyPath,
						 boolean isSdCardFolder, boolean containsMultipleBooks)
	{
		this(path, friendlyPath, isSdCardFolder, containsMultipleBooks);
		this.mId=id;
	}


	public LibraryFolder(@NonNull String path, @NonNull String friendlyPath,
						 boolean isSdCardFolder, boolean containsMultipleBooks)
	{
		this.mPath=path;
		this.mFriendlyPath=friendlyPath;
		this.mIsSdCardFolder=isSdCardFolder;
		this.mEachFileIsABook=containsMultipleBooks;
	}


	//constructor for parceler
	private LibraryFolder(Parcel in)
	{
		this.mId=in.readLong();
		this.mPath=in.readString();
		this.mFriendlyPath=in.readString();
		this.mIsSdCardFolder=in.readInt()==1;
		this.mEachFileIsABook=in.readInt()==1;
	}


	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeLong(mId);
		out.writeString(mPath);
		out.writeString(mFriendlyPath);
		out.writeInt(mIsSdCardFolder ? 1 : 0);
		out.writeInt(mEachFileIsABook ? 1 : 0);
	}


	public static final Parcelable.Creator<LibraryFolder> CREATOR=new Parcelable.Creator<LibraryFolder>()
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


	public long getId()
	{
		return mId;
	}


	public void setId(long id)
	{
		this.mId=id;
	}


	public String getPath()
	{
		return mPath;
	}


	public void setPath(@NonNull String path)
	{
		this.mPath=path;
	}


	public String getFriendlyPath()
	{
		return mFriendlyPath;
	}

	public void setFriendlyPath(@NonNull String friendlyPath)
	{
		this.mFriendlyPath = friendlyPath;
	}
	public boolean getIsSdCardFolder()
	{
		return mIsSdCardFolder;
	}

	public void setIsSdCardFolder(boolean isSdCardFolder)
	{
		this.mIsSdCardFolder = isSdCardFolder;
	}

	public boolean getEachFileIsABook()
	{
		return mEachFileIsABook;
	}

	public void setEachFileIsABook(boolean eachFileIsABook)
	{
		this.mEachFileIsABook = eachFileIsABook;
	}


}
