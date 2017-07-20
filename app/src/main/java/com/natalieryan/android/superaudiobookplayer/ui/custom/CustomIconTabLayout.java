package com.natalieryan.android.superaudiobookplayer.ui.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.natalieryan.android.superaudiobookplayer.R;

/**
 * Created by natalier258 on 7/20/17.
 *
 */

public class CustomIconTabLayout extends ConstraintLayout
{

	private String mTitle;
	private int mIconId;

	public CustomIconTabLayout(@NonNull Context context)
	{
		super(context);
	}

	public CustomIconTabLayout(@NonNull Context context, String title, int iconId)
	{
		super(context);
		this.mTitle = title;
		this.mIconId = iconId;
		initialize(context);
	}

	private void initialize(@NonNull final Context context)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.custom_icon_tab, this);
		TextView tvTitle = (TextView) findViewById(R.id.tab_text_view);
		tvTitle.setText(mTitle);
		ImageView ivIcon = (ImageView) findViewById(R.id.tab_image_view);
		ivIcon.setImageResource(mIconId);
	}
}
