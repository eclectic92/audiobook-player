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
	public static String getSdCardPath(String baseStoragePath)
	{
		File storageRoots[] = new File("/storage/").listFiles();
		ArrayList<String> sdCardPaths = new ArrayList<>();

		for(File singleRoot : storageRoots)
		{
			if(!singleRoot.getAbsolutePath().equalsIgnoreCase(baseStoragePath)
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

	public static String getFriendlyPath(String pathName, boolean isOnSdCard)
	{
		String deviceBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		if(isOnSdCard)
		{
			String sdCardBasePath = getSdCardPath(deviceBasePath);
			if(sdCardBasePath != null){
				pathName = pathName.replace(sdCardBasePath, "");
			}
		}
		else
		{
			pathName = pathName.replace(deviceBasePath, "");
		}
		return pathName + "/";
	}
}
