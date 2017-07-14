package com.natalieryan.android.superaudiobookplayer.ui.foldermanager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.natalieryan.android.superaudiobookplayer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FolderManagerFragment extends Fragment
{
	public FloatingActionsMenu fam;

	public FolderManagerFragment()
	{
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{

		View rootView = inflater.inflate(R.layout.fragment_folder_manager, container, false);
		fam = (FloatingActionsMenu) rootView.findViewById(R.id.fab_folder_menu);
		final View overlay = rootView.findViewById(R.id.shadow_overlay);
		FloatingActionsMenu.OnFloatingActionsMenuUpdateListener listener
				= new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
			@Override
			public void onMenuExpanded()
			{
				overlay.setVisibility(View.VISIBLE);
			}

			@Override
			public void onMenuCollapsed() {
				overlay.setVisibility(View.GONE);
			}
		};

		fam.setOnFloatingActionsMenuUpdateListener(listener);

		return rootView;
	}

	public FloatingActionsMenu getFam(){
		return this.fam;
	}
}
