package com.miz.misho;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.miz.misho.Enum.Preferences;

public class preferencesActivity extends PreferenceActivity {

    SharedPreferences mSP;
    ListPreference search_type_pref;
    CheckBoxPreference pop_up_romaji;
    CheckBoxPreference cb_usedt;
    EditTextPreference max_results;

    boolean isDark;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        mSP = PreferenceManager.getDefaultSharedPreferences(this);
        isDark = mSP.getBoolean(Preferences.CB_DARKTHEME.toString(), false);
        if (isDark)
            this.setTheme(android.R.style.Theme_DeviceDefault_NoActionBar);
        else
            this.setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        super.onCreate(saveInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mSP = PreferenceManager.getDefaultSharedPreferences(this);
        String search_type = mSP.getString(Preferences.SEARCH_TYPE.toString(), "Offline JMDict");
        search_type_pref = (ListPreference) findPreference(Preferences.SEARCH_TYPE.toString());
        max_results = (EditTextPreference) findPreference(Preferences.MAX_RESULTS.toString());
        search_type_pref.setSummary(search_type);
        max_results.setSummary(mSP.getString(Preferences.MAX_RESULTS.toString(), "100"));
        //pop_up_romaji = (CheckBoxPreference) findPreference(pprp);

        search_type_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                search_type_pref.setSummary(newValue.toString());
                return true;
            }
        });

        max_results.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                max_results.setSummary(newValue.toString());
                return true;
            }
        });

        cb_usedt = (CheckBoxPreference) findPreference(Preferences.CB_DARKTHEME.toString());
        cb_usedt.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                recreate();
                return true;
            }
        });
    }

}
