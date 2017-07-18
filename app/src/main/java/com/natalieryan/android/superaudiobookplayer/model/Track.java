package com.natalieryan.android.superaudiobookplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by natalier258 on 7/17/17.
 *
 */

@SuppressWarnings("unused")
public class Track implements Parcelable
{
	private int mId;
	private String mPath;
	private String mName;
	private long mDuration;
	private long mStartTime;
	private long mEndTime;
	private long mFileSize;
	private int mChapterCount;
	private String mAudioCodec;
	private ArrayList<Chapter> mChapters = new ArrayList<>();

	//required empty constructor
	public Track(){}

	public Track(String path, String name, long duration, long startTime, long endTime, long fileSize,
				 int chapterCount, String audioCodec, ArrayList<Chapter> chapters)
	{
		this.mPath = path;
		this.mName = name;
		this.mDuration = duration;
		this.mStartTime = startTime;
		this.mEndTime = endTime;
		this.mFileSize = fileSize;
		this.mChapterCount = chapterCount;
		this.mAudioCodec = audioCodec;
	}

	public Track(int id, String path, String name, long duration, long startTime, long endTime, long fileSize,
				 int chapterCount, String audioCodec, ArrayList<Chapter> chapters)
	{
		this(path, name, duration, startTime, endTime, fileSize, chapterCount, audioCodec, chapters);
		this.mId = id;
	}

	private Track(Parcel in)
	{
		this.mId = in.readInt();
		this.mPath = in.readString();
		this.mName = in.readString();
		this.mDuration = in.readLong();
		this.mStartTime = in.readLong();
		this.mEndTime = in.readLong();
		this.mFileSize = in.readLong();
		this.mChapterCount = in.readInt();
		this.mAudioCodec = in.readString();
		in.readTypedList(mChapters, Chapter.CREATOR);
	}

	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeInt(mId);
		out.writeString(mPath);
		out.writeString(mName);
		out.writeLong(mDuration);
		out.writeLong(mStartTime);
		out.writeLong(mEndTime);
		out.writeLong(mFileSize);
		out.writeInt(mChapterCount);
		out.writeString(mAudioCodec);
		out.writeTypedList(mChapters);
	}

	public int describeContents()
	{
		return 0;
	}

	public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>()
	{
		public Track createFromParcel(Parcel parcel)
		{
			return new Track(parcel);
		}

		public Track[] newArray(int size)
		{
			return new Track[size];
		}
	};

	//setters and getters --------------------------------------------------------------------------------
	public int getId()
	{
		return mId;
	}

	public void setId(int id)
	{
		this.mId = id;
	}

	public String getName()
	{
		return mName;
	}

	public void setName(String name)
	{
		this.mName = name;
	}

	public long getDuration()
	{
		return mDuration;
	}

	public void setDuration(long duration)
	{
		this.mDuration = duration;
	}

	public long getStartTime()
	{
		return mStartTime;
	}

	public void setStartTime(long startTime)
	{
		this.mStartTime = startTime;
	}

	public long getEndTime()
	{
		return mEndTime;
	}

	public void setEndTime(long endTime)
	{
		this.mEndTime = endTime;
	}

	public long getFileSize()
	{
		return mFileSize;
	}

	public void setFileSize(long fileSize)
	{
		this.mFileSize = fileSize;
	}

	public int getChapterCount()
	{
		return mChapterCount;
	}

	public void setChapterCount(int chapterCount)
	{
		this.mChapterCount = chapterCount;
	}

	public String getAudioCodec()
	{
		return mAudioCodec;
	}

	public void setAudioCodec(String audioCodec)
	{
		this.mAudioCodec = audioCodec;
	}

	public ArrayList<Chapter> getChapters()
	{
		return mChapters;
	}

	public void setChapters(ArrayList<Chapter> chapters)
	{
		this.mChapters = chapters;
	}
}
