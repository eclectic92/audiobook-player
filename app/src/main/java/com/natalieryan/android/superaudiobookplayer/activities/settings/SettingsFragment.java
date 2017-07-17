package com.natalieryan.android.superaudiobookplayer.activities.settings;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.activities.foldermanager.FolderManagerActivity;

@SuppressWarnings("unused")
public class SettingsFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener,
																		  Preference.OnPreferenceChangeListener
{

	private SetNightMode mSetNightMode;

	interface SetNightMode
	{
		void onNightModeSelected(int nightMode);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mSetNightMode = (SetNightMode) context;
		} catch (ClassCastException castException) {
			//nothing to do here yet
		}
	}

	@Override
	public void onCreatePreferences(Bundle bundle, String s) {

		addPreferencesFromResource(R.xml.pref_library);

		//final SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		//PreferenceScreen prefScreen = getPreferenceScreen();


		final ListPreference nightMode = (ListPreference) findPreference(getString(R.string.pref_night_mode_menu_key));
		if(nightMode != null){
			nightMode.setOnPreferenceChangeListener(this);
			setPreferenceSummary(nightMode, nightMode.getValue());
		}


		Preference folderManager = findPreference(getString(R.string.pref_manage_folders_key));
		if(folderManager != null)
		{
			folderManager.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
			{
				@Override
				public boolean onPreferenceClick(Preference preference)
				{
					Intent intent = new Intent(getActivity(), FolderManagerActivity.class);
					startActivity(intent);
					return true;
				}
			});
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Figure out which preference was changed
	}

	/**
	 * Updates the summary for the preference
	 *
	 * @param preference The preference to be updated
	 * @param value      The value that the preference was updated to
	 */
	private void setPreferenceSummary(Preference preference, String value) {
		if (preference instanceof ListPreference) {
			// For list preferences, figure out the label of the selected value
			ListPreference listPreference = (ListPreference) preference;
			int prefIndex = listPreference.findIndexOfValue(value);
			if (prefIndex >= 0) {
				// Set the summary to that label
				listPreference.setTitle(getString(R.string.pref_night_mode_menu_label_formatted,
						listPreference.getEntries()[prefIndex]));
			}
		}
	}


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue)
	{
		if(preference.hasKey())
		{
			if(preference.getKey().equalsIgnoreCase(getString(R.string.pref_night_mode_menu_key)))
			{
				mSetNightMode.onNightModeSelected(Integer.valueOf(newValue.toString()));
			}
		}
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}
}