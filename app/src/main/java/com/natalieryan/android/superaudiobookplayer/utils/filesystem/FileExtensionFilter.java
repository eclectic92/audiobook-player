package com.natalieryan.android.superaudiobookplayer.utils.filesystem;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by natalier258 on 7/17/17.
 *
 */

public class FileExtensionFilter implements FileFilter
{

	private final String[] mValidExtensions;
	private final boolean mIncludeDirectories;

	@SuppressWarnings("SameParameterValue")
	public FileExtensionFilter(boolean includeDirectories, String... validExtensions)
	{
		this.mValidExtensions = validExtensions;
		this.mIncludeDirectories = includeDirectories;
	}

	public boolean accept(File pathname)
	{
		if (pathname.isDirectory() && !mIncludeDirectories)
		{
			return false;
		}

		String name = pathname.getName().toLowerCase();

		for (String ext : mValidExtensions) {
			if(name.endsWith(ext))
			{
				return true;
			}
		}
		return false;
	}
}
