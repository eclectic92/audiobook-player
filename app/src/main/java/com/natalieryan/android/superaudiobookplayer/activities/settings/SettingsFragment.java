package com.natalieryan.android.superaudiobookplayer.activities.settings;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

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

		//SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		PreferenceScreen prefScreen = getPreferenceScreen();

		int count = prefScreen.getPreferenceCount();

		// Go through all of the preferences, and set up their preference summary.
		for (int i = 0; i < count; i++) {
			Preference p = prefScreen.getPreference(i);

			if(p.hasKey()){
				if(p.getKey().equalsIgnoreCase(getString(R.string.pref_manage_folders_key)))
				{
					p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
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
			/*
			if (!(p instanceof CheckBoxPreference)) {
				String value = sharedPreferences.getString(p.getKey(), "");
				setPreferenceSummary(p, value);
			}
			*/
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Figure out which preference was changed
		Preference preference = findPreference(key);
		if (null != preference) {
			if(preference.getKey().equalsIgnoreCase(getContext().getString(R.string.pref_night_mode_on_key)))
			{
				int nightMode= ((SwitchPreference)preference).isChecked()
						? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;

					mSetNightMode.onNightModeSelected(nightMode);
			}
		}
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
				listPreference.setSummary(listPreference.getEntries()[prefIndex]);
			}
		} else if (preference instanceof EditTextPreference) {
			// For EditTextPreferences, set the summary to the value's simple string representation.
			preference.setSummary(value);
		}
	}


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue)
	{
		// In this context, we're using the onPreferenceChange listener for checking whether the
		// size setting was set to a valid value.

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