package com.natalieryan.android.superaudiobookplayer.utils.media;

import android.content.Context;

import android.net.Uri;
import android.os.AsyncTask;

import com.natalieryan.android.superaudiobookplayer.model.Chapter;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;
import com.natalieryan.android.superaudiobookplayer.utils.filesystem.FileExtensionFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;


/**
 * Created by natalier258 on 7/17/17.
 *
 */

public class ScanFolderAsyncTask extends AsyncTask<LibraryFolder, Void, Integer>
{
	private final Context mContext;
	private final ScanFolderListener mScanFolderListener;
	private final ArrayList<String> mFolderPaths = new ArrayList<>();
	//private ArrayList<Chapter> mChapters = new ArrayList<>();
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
		FFmpegMediaMetadataRetriever metaRetriver = new FFmpegMediaMetadataRetriever();
		for (String path : mFolderPaths )
		{
			File singleFolder = new File(path);
			File files[] = singleFolder.listFiles(new FileExtensionFilter(mAllowedExtensions));
			Arrays.sort(files);
			for (File file : files)
			{
				try
				{
					Uri fileUri =Uri.fromFile(file);
					metaRetriver.setDataSource(mContext, fileUri);
					FFmpegMediaMetadataRetriever.Metadata metadata = metaRetriver.getMetadata();
					//metaRetriver.extractMetadata(FFmpegMediaMetadataRetriever.)
					//String album=metaRetriver.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
					//String artist=metaRetriver.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
					String gener=metaRetriver.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_GENRE);

				}
				catch (Exception e)
				{
					//TODO: how to handle this error
				}


			}
		}
		metaRetriver.release();
		/*
		if (libraryFolder.getEachFileIsABook())
		{

		}
		*/
		//stuff

		return bookCount;
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
}





	/*


	public void scanDirectory(LibraryFolder targetFolder) {

		HashMap<String, String> song;
		String mp3Pattern = ".mp3";
		File listFile[] = dir.listFiles();

		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {

				if (listFile[i].isDirectory()) {
					scanDirectory(listFile[i]);
				} else {
					if (listFile[i].getName().endsWith(mp3Pattern)){
						song = new HashMap<String, String>();
						song.put("songTitle", listFile[i].getName().substring(0, (listFile[i].getName().length() - 4)));
						song.put("songPath", listFile[i].getPath());

						// Adding each song to SongList
						Log.d("songs", song.get("songTitle"));

					}
				}
			}
		}
		return;
	}*/

/*
FFmpegMediaMetadataRetriever metaRetriver = new FFmpegMediaMetadataRetriever();\
// or MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();

String path = "audio";

String [] files  = context.getAssets().list(path);

for (int i = 0; i < files.length; i++) {
    String file = path + "/" + files[i];

    AssetFileDescriptor afd = context.getAssets().openFd(file);

    metaRetriver.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

    String album = metaRetriver.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
    String artist = metaRetriver.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
    String gener = metaRetriver.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_GENRE);

    afd.close();
}

metaRetriver.release();
 */