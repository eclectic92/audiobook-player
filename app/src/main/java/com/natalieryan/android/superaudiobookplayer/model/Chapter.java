package com.natalieryan.android.superaudiobookplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by natalier258 on 7/17/17.
 *
 */

public class Chapter implements Parcelable
{
	private int mId;
	private String mTitle;
	private long mDuration;
	private long mStartTime;
	private long mEndTime;

	//empty constructor
	public Chapter(){}

	//baseline constructor
	public Chapter(String title, long duration, long startTime, long endTime)
	{
		this.mTitle = title;
		this.mDuration = duration;
		this.mStartTime = startTime;
		this.mEndTime = endTime;
	}

	public Chapter(int id, String title, long duration, long startTime, long endTime)
	{
		this(title, duration, startTime, endTime);
		this.mId = id;
	}

	//constructor for parceler
	private Chapter(Parcel in)
	{
		this.mId = in.readInt();
		this.mTitle = in.readString();
		this.mDuration = in.readLong();
		this.mStartTime = in.readLong();
		this.mEndTime = in.readLong();
	}

	@Override
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeInt(mId);
		out.writeString(mTitle);
		out.writeLong(mDuration);
		out.writeLong(mStartTime);
		out.writeLong(mEndTime);
	}

	public static final Parcelable.Creator<Chapter> CREATOR = new Parcelable.Creator<Chapter>()
	{
		public Chapter createFromParcel(Parcel parcel)
		{
			return new Chapter(parcel);
		}

		public Chapter[] newArray(int size)
		{
			return new Chapter[size];
		}
	};

	//setters and getters --------------------------------------------------------
	public int getId()
	{
		return mId;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}


	public void setId(int id)
	{
		this.mId=id;
	}

	public String getTitle()
	{
		return mTitle;
	}

	public void setTitle(String title)
	{
		this.mTitle = title;
	}

}
