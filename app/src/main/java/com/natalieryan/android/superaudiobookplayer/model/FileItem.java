package com.natalieryan.android.superaudiobookplayer.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.natalieryan.android.superaudiobookplayer.BR;

/**
 * Created by natalier258 on 7/10/17.
 *
 */

@SuppressWarnings("unused")
public class FileItem extends BaseObservable implements Parcelable, Comparable<FileItem>
{

	@Bindable
	private String mName;
	private String mPath;
	private String mParentPath;
	private long mSize;
	@Bindable
	private int mIcon;
	private boolean mIsDirectory;
	private boolean mIsTopLevel;
	private boolean mHasChildren;

	//empty constructor
	public FileItem(){}

	//baseline constructor
	public FileItem(String name, String path, String parentPath, long size, int icon, boolean mIsDirectory,
					boolean mIsTopLevel, boolean mHasChildren)
	{
		this.mName = name;
		this.mPath = path;
		this.mParentPath = parentPath;
		this.mSize = size;
		this.mIcon = icon;
		this.mIsDirectory=mIsDirectory;
		this.mIsTopLevel=mIsTopLevel;
		this.mHasChildren=mHasChildren;
	}

	//constructor for parceler
	private FileItem(Parcel in)
	{
		this.mName = in.readString();
		this.mPath = in.readString();
		this.mParentPath = in.readString();
		this.mSize = in.readLong();
		this.mIcon = in.readInt();
		this.mIsDirectory = in.readInt() == 1;
		this.mIsTopLevel = in.readInt() == 1;
		this.mHasChildren = in.readInt() == 1;
	}

	@Override
	public int compareTo(@NonNull FileItem fileItem) {
		return this.mName.compareTo(fileItem.getName());
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(mName);
		out.writeString(mPath);
		out.writeString(mParentPath);
		out.writeLong(mSize);
		out.writeInt(mIcon);
		out.writeInt(mIsDirectory ? 1 : 0);
		out.writeInt(mIsTopLevel ? 1 : 0);
		out.writeInt(mHasChildren ? 1 : 0);
	}

	public static final Parcelable.Creator<FileItem> CREATOR = new Parcelable.Creator<FileItem>()
	{
		public FileItem createFromParcel(Parcel parcel)
		{
			return new FileItem(parcel);
		}

		public FileItem[] newArray(int size)
		{
			return new FileItem[size];
		}
	};

	//setters and getters --------------------------------------------------------------------------------
	public String getName()
	{
		return mName;
	}

	public void setName(String name)
	{
		this.mName = name;
		notifyPropertyChanged(BR.name);
	}

	public String getPath()
	{
		return mPath;
	}

	public void setPath(String path)
	{
		this.mPath = path;
	}

	@Nullable
	public String getParentPath()
	{
		return mParentPath;
	}

	public void setParentPath(@Nullable String parentPath)
	{
		this.mParentPath = parentPath;
	}

	public long getSize()
	{
		return mSize;
	}

	public void setSize(long size)
	{
		this.mSize = size;
	}

	public int getIcon()
	{
		return mIcon;
	}

	@Bindable
	public void setIcon(int icon)
	{
		this.mIcon = icon;
		notifyPropertyChanged(BR.icon);
	}

	public boolean getIsDirectory()
	{
		return mIsDirectory;
	}

	public void setIsDirectory(boolean isDirectory)
	{
		this.mIsDirectory = isDirectory;
	}

	public boolean getIsTopLevel()
	{
		return mIsTopLevel;
	}

	public void setIsTopLevel(boolean isTopLevel)
	{
		this.mIsTopLevel = isTopLevel;
	}

	public boolean getHasChildren()
	{
		return mHasChildren;
	}

	public void setHasChildren(boolean hasChildren)
	{
		this.mHasChildren = hasChildren;
	}
}
