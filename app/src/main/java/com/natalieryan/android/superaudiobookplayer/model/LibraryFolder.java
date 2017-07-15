package com.natalieryan.android.superaudiobookplayer.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.natalieryan.android.superaudiobookplayer.utils.filesystem.PathUtils;

/**
 * Created by natalier258 on 7/12/17.
 *
 */

@SuppressWarnings("unused")
public class LibraryFolder implements Parcelable
{
	private long mId;
	private String mPath;
	private String mRootPath;
	private boolean mIsSdCardFolder=false;
	private boolean mEachFileIsABook=false;
	private int mBookCount;


	public LibraryFolder()
	{
	}


	public LibraryFolder(long id, @NonNull String path, @NonNull String rootPath,
						 boolean isSdCardFolder, boolean containsMultipleBooks, int bookCount)
	{
		this(path, rootPath, isSdCardFolder, containsMultipleBooks, bookCount);
		this.mId=id;
	}


	public LibraryFolder(@NonNull String path, @NonNull String rootPath,
						 boolean isSdCardFolder, boolean containsMultipleBooks, int bookCount)
	{
		this.mPath=path;
		this.mRootPath = rootPath;
		this.mIsSdCardFolder=isSdCardFolder;
		this.mEachFileIsABook=containsMultipleBooks;
		this.mBookCount = bookCount;
	}


	//constructor for parceler
	private LibraryFolder(Parcel in)
	{
		this.mId=in.readLong();
		this.mPath=in.readString();
		this.mRootPath = in.readString();
		this.mIsSdCardFolder=in.readInt()==1;
		this.mEachFileIsABook=in.readInt()==1;
		this.mBookCount=in.readInt();
	}


	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeLong(mId);
		out.writeString(mPath);
		out	.writeString(mRootPath);
		out.writeInt(mIsSdCardFolder ? 1 : 0);
		out.writeInt(mEachFileIsABook ? 1 : 0);
		out.writeInt(mBookCount);
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

	public String getRootPath()
	{
		return mRootPath;
	}

	public void setRootPath(String rootPath)
	{
		this.mRootPath = rootPath;
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

	public int getBookCount()
	{
		return mBookCount;
	}

	public void setBookCount(int bookCount)
	{
		this.mBookCount = bookCount;
	}

	public String getFriendlyPath()
	{
		return PathUtils.getFriendlyPath(mPath, mRootPath);
	}
}
