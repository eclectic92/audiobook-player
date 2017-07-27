package com.natalieryan.android.superaudiobookplayer.ui.viewholders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Created by natalier258 on 7/21/17.
 *
 */

@SuppressWarnings("unused")
public class GenericRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

	private OnViewHolderClickListener clickListener;

	public interface OnViewHolderClickListener
	{
		void onViewHolderClick(View view, int position);
	}

	GenericRecyclerViewHolder(@NonNull View itemView, @Nullable OnViewHolderClickListener clickListener)
	{
		super(itemView);
		if(clickListener != null)
		{
			this.clickListener = clickListener;
			itemView.setOnClickListener(this);
		}
	}

	GenericRecyclerViewHolder(@NonNull View itemView)
	{
		super(itemView);
	}

	public void bind(Object bindingObject){}

	public void bind(String bindingString){}

	@Override
	public void onClick(View view) {
		if (clickListener != null)
			clickListener.onViewHolderClick(view, getAdapterPosition());
	}
}