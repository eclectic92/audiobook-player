package com.natalieryan.android.superaudiobookplayer.utils.media;

import android.content.Context;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.model.Book;
import com.natalieryan.android.superaudiobookplayer.model.Chapter;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;
import com.natalieryan.android.superaudiobookplayer.model.Track;
import com.natalieryan.android.superaudiobookplayer.utils.filesystem.FileExtensionFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import wseemann.media.FFmpegMediaMetadataRetriever;


/**
 * Created by natalier258 on 7/17/17.
 *
 */

public class ScanFolderAsyncTask extends AsyncTask<LibraryFolder, Void, Integer>
{

	private static final String TAG = ScanFolderAsyncTask.class.getSimpleName();

	private final Context mContext;
	private final FFmpegMediaMetadataRetriever mMetadataRetriever= new FFmpegMediaMetadataRetriever();
	private final ArrayList<Book> mbooks = new ArrayList<>();
	private final ScanFolderListener mScanFolderListener;
	private final ArrayList<String> mFolderPaths = new ArrayList<>();
	private static final String[] mAllowedExtensions = {
			"mp3", "m4a", "aac", "m4b"
	};


	public ScanFolderAsyncTask(Context context, ScanFolderListener scanFolderListener)
	{
		this.mContext=context;
		this.mScanFolderListener=scanFolderListener;
	}


	/**
	 * Listener for async task completion
	 * Must be implemented by hosting class
	 */
	public interface ScanFolderListener
	{
		void onFolderScanned(int bookCount);
	}

	@Override
	protected Integer doInBackground(LibraryFolder... params)
	{
		if (params.length==0)
		{
			return null;
		}

		int bookCount = 0;
		LibraryFolder topFolder=params[0];

		mFolderPaths.add(topFolder.getPath());
		getFolderPaths(topFolder.getPath());

		for (String path : mFolderPaths )
		{
			File singleFolder = new File(path);
			File files[] = singleFolder.listFiles(new FileExtensionFilter(mAllowedExtensions));
			Arrays.sort(files);
			for (File file : files)
			{
				if(topFolder.getEachFileIsABook())
				{
					Book singleBook = getBook(file);
					Track singleTrack = getTrack(file);
					getChapters(singleTrack.getChapterCount(), 1);
					ArrayList<Track> tracks = new ArrayList<>();
					tracks.add(singleTrack);
					singleBook.setTracks(tracks);
					mbooks.add(singleBook);

				}

			/*
					if(chapterCount == 0)
		{
			ArrayList<Chapter> chapters = new ArrayList<>();
			Chapter chapter = new Chapter(name, duration, 0, duration);
			chapters.add(chapter);
			singleTrack.setChapters(chapters);
		}
		else
		{
			singleTrack.setChapters(getChapters(chapterCount));
		}
			 */

			}
		}
		mMetadataRetriever.release();

		return bookCount;
	}

	private Book getBook(File bookFile)
	{
		Book singleBook = new Book();
		Uri fileUri=Uri.fromFile(bookFile);
		mMetadataRetriever.setDataSource(mContext, fileUri);

		//title
		String title = mMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
		if(title == null || title.isEmpty())
		{
			title = removeFileExtension(bookFile.getName());
		}
		singleBook.setTitle(title);

		//author
		String author = mMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
		if(author == null || author.isEmpty())
		{
			author = mContext.getString(R.string.meta_author_unknown);
		}
		singleBook.setAuthor(author);

		//create author+title key as duplicate check
		String authorTitleKey = author.replaceAll("\\P{L}+", "") + title.replaceAll("\\P{L}+", "");
		singleBook.setAuthorTitleKey(authorTitleKey);

		return singleBook;
	}

	private Track getTrack(File trackFile)
	{
		Track singleTrack = new Track();

		//path
		singleTrack.setPath(trackFile.getAbsolutePath());

		//name
		String name = mMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
		if(name == null || name.isEmpty()){
			name = removeFileExtension(trackFile.getAbsolutePath());
		}
		singleTrack.setName(name);

		//times
		long duration = getLongFromString(mMetadataRetriever
				.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
		singleTrack.setDuration(duration);
		singleTrack.setStartTime(0);
		singleTrack.setEndTime(duration);

		//chapters (the metatata retriever returns string "0" if no chapters
		int chapterCount = Integer.valueOf(mMetadataRetriever
				.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_CHAPTER_COUNT));
		singleTrack.setChapterCount(chapterCount);

		//file info
		singleTrack.setFileSize(getLongFromString(
				mMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FILESIZE)));

		singleTrack.setAudioCodec(mMetadataRetriever
				.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_AUDIO_CODEC));


		return singleTrack;
	}

	private ArrayList<Chapter> getChapters(int chapterCount, int startingIndex)
	{
		ArrayList<Chapter> chapters = new ArrayList<>();

		for(int i = 0; i < chapterCount; i++)
		{
			Chapter singleChapter = new Chapter();
			singleChapter.setTitle(mContext.getString(R.string.meta_author_chapter,
					String.format(Locale.US, "%02d", startingIndex + i)));
		}

		//TODO: duration, start time, end time
		return chapters;
	}
	@Override
	protected void onPostExecute(Integer bookCount)
	{
		mScanFolderListener.onFolderScanned(bookCount);
	}

	private void getFolderPaths(String rootPath)
	{
		File root = new File(rootPath);
		File[] folders = root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		for (File folder : folders) {
			mFolderPaths.add(folder.getAbsolutePath());
			getFolderPaths(folder.getPath());
		}
	}

	private static String removeFileExtension(@NonNull String fileName)
	{
		if(!fileName.contains("."))
		{
			return fileName;
		}
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

	private static long getLongFromString(String stringVal)
	{
		long retVal = 0;
		if(stringVal != null && !stringVal.isEmpty())
		{
			try
			{
				retVal = Long.parseLong(stringVal);
			}
			catch (Exception e)
			{
				retVal = 0;
			}
		}
		return retVal;
	}
}