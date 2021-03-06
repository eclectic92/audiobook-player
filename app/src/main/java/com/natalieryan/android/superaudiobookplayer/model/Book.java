package com.natalieryan.android.superaudiobookplayer.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by natalier258 on 7/17/17.
 *
 */

@SuppressWarnings("unused")
public class Book implements Parcelable
{
	private int mId;
	private String mTitle;
	private String mAuthor;
	private String mAuthorTitleKey;
	private String mSDCardPath;
	private long mDuration;
	private String mCurrentFile = null;
	private int mCurrentChapter = -1;
	private long mLastHeardPosition = 0;
	private boolean mIsStarted;
	private boolean mIsFinished;
	private ArrayList<Track> mTracks = new ArrayList<>();

	//required empty constructor
	public Book(){}

	public Book(String title, String author, long duration, ArrayList<Track> tracks)
	{
		this.mTitle = title;
		this.mAuthor = author;
		this.mDuration = duration;
		this.mTracks = tracks;
	}

	public Book(int id, String title, String author, long duration, ArrayList<Track> tracks)
	{
		this(title, author, duration, tracks);
		this.mId = id;
	}

	private Book(Parcel in)
	{
		this.mId = in.readInt();
		this.mTitle = in.readString();
		this.mAuthor = in.readString();
		this.mAuthorTitleKey = in.readString();
		this.mSDCardPath = in.readString();
		this.mDuration = in.readLong();
		this.mCurrentFile = in.readString();
		this.mCurrentChapter = in.readInt();
		this.mLastHeardPosition = in.readLong();
		this.mIsStarted = in.readInt() == 1;
		this.mIsFinished = in.readInt() == 1;
		in.readTypedList(mTracks, Track.CREATOR);
	}

	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeInt(mId);
		out.writeString(mTitle);
		out.writeString(mAuthor);
		out.writeString(mAuthorTitleKey);
		out.writeString(mSDCardPath);
		out.writeLong(mDuration);
		out.writeString(mCurrentFile);
		out.writeInt(mCurrentChapter);
		out.writeLong(mLastHeardPosition);
		out.writeInt(this.mIsStarted ? 1 : 0);
		out.writeInt(this.mIsFinished ? 1 : 0);
		out.writeTypedList(mTracks);
	}

	public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>()
	{
		public Book createFromParcel(Parcel parcel)
		{
			return new Book(parcel);
		}

		public Book[] newArray(int size)
		{
			return new Book[size];
		}
	};

	public int describeContents()
	{
		return 0;
	}

	//setters and getters --------------------------------------------------------------------------------
	public int getId()
	{
		return mId;
	}

	public void setId(int id)
	{
		this.mId = id;
	}

	public String getTitle()
	{
		return mTitle;
	}

	public void setTitle(String title)
	{
		this.mTitle = title;
	}

	@Nullable
	public String getAuthor()
	{
		return mAuthor;
	}

	public void setAuthor(@Nullable String author)
	{
		this.mAuthor = author;
	}

	public String getAuthorTitleKey()
	{
		return mAuthorTitleKey;
	}

	public void setAuthorTitleKey(String authorTitleKey)
	{
		this.mAuthorTitleKey = authorTitleKey;
	}

	@Nullable
	public String getSDCardPath()
	{
		return mSDCardPath;
	}

	public void setSDCardPath(String sdFolderPath)
	{
		this.mSDCardPath = sdFolderPath;
	}

	public long getDuration()
	{
		return mDuration;
	}

	public void setDuration(long duration)
	{
		this.mDuration = duration;
	}

	@Nullable
	public String getCurrentFile()
	{
		return mCurrentFile;
	}

	public void setCurrentFile(@Nullable String currentFile)
	{
		this.mCurrentFile = currentFile;
	}

	public int getCurrentChapter()
	{
		return mCurrentChapter;
	}

	public void setCurrentChapter(int currentChapter)
	{
		this.mCurrentChapter = currentChapter;
	}

	public long getLastHeardPosition()
	{
		return mLastHeardPosition;
	}

	public void setLastHeardPosition(long lastHeardPosition)
	{
		this.mLastHeardPosition = lastHeardPosition;
	}

	public boolean getIsStarted()
	{
		return mIsStarted;
	}

	public void setIsStarted(boolean isStarted)
	{
		this.mIsStarted = isStarted;
	}

	public boolean getIsFinished()
	{
		return mIsFinished;
	}

	public void setIsFinished(boolean isFinished)
	{
		this.mIsFinished = isFinished;
	}

	public ArrayList<Track> getTracks()
	{
		return mTracks;
	}

	public void setTracks(ArrayList<Track> tracks)
	{
		this.mTracks = tracks;
	}

	public boolean isOnSDCard(){
		return mSDCardPath != null;
	}
}
