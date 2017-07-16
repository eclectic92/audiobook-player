package com.natalieryan.android.superaudiobookplayer.utils.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextPaint;
import android.view.View;

@SuppressWarnings("unused")
public abstract class SwipeHelper extends ItemTouchHelper.SimpleCallback {

	private Drawable mBackground;
	private Drawable mSwipeLeftIcon;
	private Drawable mSwipeRightIcon;

	//defaults
	private int mSwipeLeftIconId = -1;
	private int mSwipeRightIconId = -1;
	private int mSwipeLeftColorCode= Color.RED;
	private int mSwipeRightColorCode= Color.DKGRAY;
	private int mSwipeLeftIconColorCode = Color.WHITE;
	private int mSwipeRightIconColorCode = Color.WHITE;
	private int mIconMargin = 16;
	private int mTextMargin = 16;
	private int mIconVerticalMargin = 16;
	private int mTextSize = 16;
	private String mSwipeLeftLabel;
	private String mSwipeRightLabel;

	private boolean mIsInitialized;
	private final Context mContext;

	public RecyclerView.ViewHolder viewHolderToSwipe;

	public SwipeHelper(Context context) {
		super(0,0);
		this.mContext = context;
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return false;
	}

	@Override
	public abstract void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
							float dX, float dY, int actionState, boolean isCurrentlyActive) {

		View itemView = viewHolder.itemView;
		if (!mIsInitialized) {
			mBackground= new ColorDrawable();
			mIsInitialized=true;
		}

		int horizontalBounds;
		int swipeColor;
		int itemHeight = itemView.getBottom() - itemView.getTop();
		Drawable swipeIcon = null;

		if (dX > 0)
		{ // swiping right
			horizontalBounds = itemView.getLeft();
			swipeColor =mSwipeRightColorCode;
			if(mSwipeRightIconId  != -1)
			{
				swipeIcon = mSwipeRightIcon;
			}
		}
		else
		{ // swiping left
			horizontalBounds = itemView.getRight();
			swipeColor =mSwipeLeftColorCode;
			if(mSwipeLeftIconId != -1)
			{
				swipeIcon = mSwipeLeftIcon;
			}
		}

		//draw the color background
		((ColorDrawable)mBackground).setColor(swipeColor);
		mBackground.setBounds(horizontalBounds + (int) dX, itemView.getTop(), horizontalBounds, itemView.getBottom());
		mBackground.draw(c);

		//render the icon if there is one
		int iconLeft = 0;
		int iconRight = 0;
		if(null != swipeIcon)
		{
			int iconIntrinsicWidth = swipeIcon.getIntrinsicWidth();
			int iconIntrinsicHeight = swipeIcon.getIntrinsicHeight();
			swipeIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
			if (dX > 0)
			{ // swiping right
				iconLeft = horizontalBounds + mIconMargin;
			}else
			{ //swiping left
				iconLeft = horizontalBounds - mIconMargin - iconIntrinsicWidth;
			}
			iconRight = iconLeft + iconIntrinsicWidth;
			int iconTop = itemView.getTop()  + (itemHeight - iconIntrinsicHeight) / 2;
			int iconBottom = iconTop + iconIntrinsicHeight;
			swipeIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
			swipeIcon.draw(c);
		}

		//draw the swipe text
		TextPaint textPaint = getLabelPaint();
		int textTop = ((itemView.getTop() + itemView.getBottom()) / 2) + mTextSize ;

		if (dX > 0)
		{ // swiping right
			if(mSwipeRightLabel!= null && !mSwipeRightLabel.isEmpty())
			{
				int left = swipeIcon == null ?  itemView.getLeft() + mTextMargin : iconRight;
				c.drawText(mSwipeRightLabel, left + mTextMargin, textTop , textPaint);
			}

		}else
		{ //swiping left
			if(mSwipeLeftLabel!= null && !mSwipeLeftLabel.isEmpty())
			{
				int width = (int) textPaint.measureText(mSwipeLeftLabel);
				int left = swipeIcon == null ?  itemView.getRight() - mTextMargin : iconLeft;
				c.drawText(mSwipeLeftLabel, (left - width) - mTextMargin, textTop , textPaint);
			}
		}
		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
	}

	private TextPaint getLabelPaint()
	{
		TextPaint textPaint = new TextPaint();
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(mTextSize * mContext.getResources().getDisplayMetrics().density);
		textPaint.setColor(Color.WHITE);
		return textPaint;
	}

	public void setIconMargin(int margin)
	{
		this.mIconMargin = margin;
	}

	public void setTextMargin(int margin)
	{
		this.mTextMargin = margin;
	}

	public void setTextSize(int textSize)
	{
		this.mTextSize = textSize;
	}

	public void setLabelTextSize(int textSize)
	{
		this.mTextSize = textSize;
	}

	//setters for left swipe properties
	public void setSwipeLeftLabel(String leftSwipeLabel)
	{
		this.mSwipeLeftLabel=leftSwipeLabel;
	}

	public void setSwipeLeftColor(int leftcolorCode)
	{
		this.mSwipeLeftColorCode=leftcolorCode;
	}

	public void setSwipeLeftIconId(int iconId)
	{
		this.mSwipeLeftIconId = iconId;
		this.mSwipeLeftIcon = ContextCompat.getDrawable(mContext, mSwipeLeftIconId);
	}

	public void setSwipeLeftIconColor(int iconColorCode)
	{
		this.mSwipeLeftIconColorCode = iconColorCode;
	}

	//setters for right swipe properties
	public void setSwipeRightLabel(String rightSwipeLabel)
	{
		this.mSwipeRightLabel=rightSwipeLabel;
	}

	public void setSwipeRightColor(int rightColorCode)
	{
		this.mSwipeRightColorCode=rightColorCode;
	}

	public void setSwipeRightIconId(int iconId)
	{
		this.mSwipeRightIconId = iconId;
		this.mSwipeRightIcon = ContextCompat.getDrawable(mContext, mSwipeRightIconId);
	}

	public void setSwipeRightIconColor(int iconColorCode)
	{
		this.mSwipeRightIconColorCode = iconColorCode;
	}
}