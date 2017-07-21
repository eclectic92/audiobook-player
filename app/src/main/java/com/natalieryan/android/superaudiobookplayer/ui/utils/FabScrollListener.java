package com.natalieryan.android.superaudiobookplayer.ui.utils;


import android.support.v7.widget.RecyclerView;

/**
 * Created by natalieryan on 7/13/17.
 *
 * An object that listens to the scroll event of a recycler view and hides/shows
 * a fab when the user scrolls up or down on the recyclerview list
 */

public class FabScrollListener extends RecyclerView.OnScrollListener
{

	private final FabPositionListener positionListener;


	public interface FabPositionListener
	{
		void showFab();

		void hideFab();
	}


	public FabScrollListener(FabPositionListener positionListener)
	{
		this.positionListener=positionListener;
	}


	@Override
	public final void onScrolled(RecyclerView recyclerView, int dx, int dy)
	{
		super.onScrolled(recyclerView, dx, dy);
		if (!recyclerView.canScrollVertically(-1))
		{
			positionListener.showFab();
		}
		else if (!recyclerView.canScrollVertically(1))
		{
			positionListener.showFab();
		}
		else if (dy<0)
		{
			positionListener.hideFab();
		}
		else if (dy>0)
		{
			positionListener.hideFab();
		}
	}
}