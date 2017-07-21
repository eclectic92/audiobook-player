package com.natalieryan.android.superaudiobookplayer.ui.viewholders;

import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by natalier258 on 7/21/17.
 *
 */

public class GenericRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

	private OnViewHolderClickListener clickListener;

	public interface OnViewHolderClickListener
	{
		void onViewHolderClick(View view, int position);
	}

	public GenericRecyclerViewHolder(@NonNull View itemView, @Nullable OnViewHolderClickListener clickListener)
	{
		super(itemView);
		if(clickListener != null)
		{
			this.clickListener = clickListener;
			itemView.setOnClickListener(this);
		}
	}

	public GenericRecyclerViewHolder(@NonNull View itemView)
	{
		super(itemView);
	}

	public void bind(Object binding){}

	public void bind(String boundString){}

	@Override
	public void onClick(View view) {
		if (clickListener != null)
			clickListener.onViewHolderClick(view, getAdapterPosition());
	}
}