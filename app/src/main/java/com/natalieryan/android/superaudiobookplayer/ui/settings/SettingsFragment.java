package com.natalieryan.android.superaudiobookplayer.ui.settings;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

import com.natalieryan.android.superaudiobookplayer.R;
import com.natalieryan.android.superaudiobookplayer.data.async.AddFolderToLibraryAsyncTask;
import com.natalieryan.android.superaudiobookplayer.model.LibraryFolder;
import com.natalieryan.android.superaudiobookplayer.ui.filebrowser.FileBrowerStandaloneActivity;
import com.natalieryan.android.superaudiobookplayer.ui.filebrowser.FileBrowserActivity;
import com.natalieryan.android.superaudiobookplayer.ui.filebrowser.FileBrowserFragment;
import com.natalieryan.android.superaudiobookplayer.ui.foldermanager.FolderManagerActivity;

public class SettingsFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener,
																		  Preference.OnPreferenceChangeListener
{

	private static final int SELECT_FOLDER_RESULT_CODE = 1;
	private static final int PERMISSION_REQUEST_CODE = 200;

	private LibraryFolder mLibraryFolder;

	@Override
	public void onCreatePreferences(Bundle bundle, String s) {

		addPreferencesFromResource(R.xml.pref_library);

		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
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
			if (!(p instanceof CheckBoxPreference)) {
				String value = sharedPreferences.getString(p.getKey(), "");
				setPreferenceSummary(p, value);
			}
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Figure out which preference was changed
		Preference preference = findPreference(key);
		if (null != preference) {
			// Updates the summary for the preference
			if (!(preference instanceof CheckBoxPreference)) {
				String value = sharedPreferences.getString(preference.getKey(), "");
				setPreferenceSummary(preference, value);
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