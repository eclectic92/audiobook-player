package com.natalieryan.android.superaudiobookplayer.utils.media;

import java.util.Random;

/**
 * Created by natalier258 on 7/14/17.
 *
 * total dummy class - just need some data back before real scanner is implemented
 */

public class MediaScanner
{

	public static int getBookCount()
	{
		Random rand = new Random();
		return rand.nextInt(26);
	}
}
