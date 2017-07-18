package com.natalieryan.android.superaudiobookplayer.utils.filesystem;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by natalier258 on 7/17/17.
 *
 */

public class FileExtensionFilter implements FileFilter
{

	private final String[] validExtensions;

	public FileExtensionFilter(String... validExtensions)
	{
		this.validExtensions = validExtensions;
	}

	public boolean accept(File pathname)
	{
		if (pathname.isDirectory())
		{
			return false;
		}

		String name = pathname.getName().toLowerCase();

		for (String ext : validExtensions) {
			if(name.endsWith(ext))
			{
				return true;
			}
		}
		return false;
	}
}
