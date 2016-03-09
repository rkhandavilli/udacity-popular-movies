package com.rkhandavilli.popularmovies;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * SettingsFragment class to implement the Settings menu.
 * Created by ravi on 02/29/16.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        String key = getString(R.string.pref_sort_order_key);
        Preference preference = findPreference(key);
        if ((sharedPreferences.getString(key,"")).equals(getString(R.string.pref_sort_order_most_popular))) {
            preference.setSummary("Most popular");
        } else if ((sharedPreferences.getString(key, "")).equals(getString(R.string.pref_sort_order_highest_rated))) {
            preference.setSummary("Highest rated");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.pref_sort_order_key))) {
            Preference preference = findPreference(key);
            if ((sharedPreferences.getString(key,"")).equals(getString(R.string.pref_sort_order_most_popular))) {
                preference.setSummary("Most popular");
            } else if ((sharedPreferences.getString(key, "")).equals(getString(R.string.pref_sort_order_highest_rated))) {
                preference.setSummary("Highest rated");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
