package com.natalieryan.android.superaudiobookplayer.utils;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by natalier258 on 7/14/17.
 *
 */

public class PathUtils
{

	private static final String BASE_STORAGE_PATH = "/storage/";

	public static String getDeviceRootStoragePath()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	public static String getSdCardPath()
	{
		File storageRoots[] = new File(BASE_STORAGE_PATH).listFiles();
		ArrayList<String> sdCardPaths = new ArrayList<>();

		for(File singleRoot : storageRoots)
		{
			if(!singleRoot.getAbsolutePath().equalsIgnoreCase(getDeviceRootStoragePath())
					&& singleRoot.isDirectory()
					&& singleRoot.canRead())
			{
				sdCardPaths.add(singleRoot.getAbsolutePath());
			}
		}

		if(sdCardPaths.isEmpty())
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
		String sdCardPath = getSdCardPath();
		if (sdCardPath == null)
		{
			return false;
		}

		return sdCardIsMounted(sdCardPath);
	}

	public static boolean sdCardIsMounted(String sdCardPath)
	{
		if (sdCardPath == null || sdCardPath.isEmpty())
		{
			return false;
		}

		File testFile = new File(sdCardPath);
		return testFile.exists();
	}

	public static String getFriendlyPath(String path, String rootPath)
	{
		return path.replace(rootPath, "");
	}

	public static String getFriendlySdCardName(String path)
	{
		return path.replace(BASE_STORAGE_PATH,"");
	}
}
