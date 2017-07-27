package com.natalieryan.android.superaudiobookplayer.utils.filesystem;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.natalieryan.android.superaudiobookplayer.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by natalier258 on 7/14/17.
 *
 */

public class FileUtils
{

	private static final String BASE_STORAGE_PATH="/storage/";


	public static String getDeviceRootStoragePath()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}


	public static String getSdCardPath()
	{
		File storageRoots[]=new File(BASE_STORAGE_PATH).listFiles();
		ArrayList<String> sdCardPaths=new ArrayList<>();

		for (File singleRoot : storageRoots)
		{
			if (!singleRoot.getAbsolutePath().equalsIgnoreCase(getDeviceRootStoragePath())
					&& singleRoot.isDirectory()
					&& singleRoot.canRead())
			{
				sdCardPaths.add(singleRoot.getAbsolutePath());
			}
		}

		if (sdCardPaths.isEmpty())
		{
			return null;
		}
		else
		{
			return sdCardPaths.get(0);
		}
	}


	public static boolean sdCardIsMounted()
	{
		String sdCardPath=getSdCardPath();
		return sdCardPath!=null && sdCardIsMounted(sdCardPath);

	}

	public static boolean sdCardIsMounted(String sdCardPath)
	{
		if (sdCardPath==null || sdCardPath.isEmpty())
		{
			return false;
		}

		File testFile=new File(sdCardPath);
		return testFile.exists();
	}


	public static String getFriendlyPath(String path, String rootPath)
	{
		return path.replace(rootPath, "");
	}


	public static String getFriendlySdCardName(String path)
	{
		return path.replace(BASE_STORAGE_PATH, "");
	}


	public static boolean fileIsOnMountedSdCard(String path)
	{
		return getSdCardPath() != null && !getSdCardPath().isEmpty() && path.contains(getSdCardPath());
	}

	public static String getFileNameWithoutExtension(@NonNull String fileName)
	{
		if(!fileName.contains("."))
		{
			return fileName;
		}
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

	@Nullable
	private static String getFileExtension(@NonNull String fileName)
	{
		if(!fileName.contains("."))
		{
			return fileName;
		}

		if(fileName.lastIndexOf('.') == fileName.length()-1)
		{
			return null;
		}

		return fileName.substring(fileName.lastIndexOf('.')+1);
	}

	public static boolean isChildOfFolder(String parentPath, String childPath)
	{
		return childPath.contains(parentPath);
	}

	public static int getIconIdForFile(String path)
	{
		return getIconIdForFile(path, false);
	}

	public static int getIconIdForFile(String path, boolean isDisabled)
	{
		String ext = getFileExtension(path);

		if(ext == null || ext.isEmpty())
		{
			return isDisabled ? R.drawable.ic_insert_drive_file_disabled_24dp
					: R.drawable.ic_insert_drive_file_black_24dp;
		}

		switch (ext.toLowerCase())
		{
			//audio
			case "mp3":
			case "m4a":
			case "ogg":
			case "oga":
			case "aac":
			case "flac":
				return isDisabled ? R.drawable.ic_audiotrack_disabled_24dp:  R.drawable.ic_audiotrack_black_24dp;
			//audiobook
			case "m4b":
				return isDisabled ? R.drawable.ic_book_disabled_24dp : R.drawable.ic_book_black_24dp;
			//video
			case "m4v":
			case "mp4":
			case "mkv":
			case "flv":
			case "ogv":
				return isDisabled ? R.drawable.ic_local_movies_disabled_24dp : R.drawable.ic_local_movies_black_24dp;
			//image
			case "gif":
			case "jpg":
			case "jpeg":
			case "jfif":
			case "png":
			case "bmp":
				return isDisabled ? R.drawable.ic_image_disabled_24dp : R.drawable.ic_image_black_24dp;
			default:
				return isDisabled ? R.drawable.ic_insert_drive_file_disabled_24dp
						: R.drawable.ic_insert_drive_file_black_24dp;
		}
	}
}
