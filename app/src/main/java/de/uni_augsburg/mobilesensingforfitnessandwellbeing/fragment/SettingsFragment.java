package de.uni_augsburg.mobilesensingforfitnessandwellbeing.fragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;

public class SettingsFragment extends PreferenceFragment implements
        OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(sharedPreferences, "pref_height");
        onSharedPreferenceChanged(sharedPreferences, "pref_media_directory");
    }

    private void updateHeightPreference(Preference preference) {
        String height = ((EditTextPreference) preference).getText();
        preference.setSummary("Your height: " + height + " centimeter");
    }


    private void updateMediaDirectoryPreference(Preference preference) {
        preference.setSummary(((EditTextPreference) preference).getText());
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        switch (key) {
            case "pref_height":
                updateHeightPreference(preference);
                break;
            case "pref_media_directory":
                updateMediaDirectoryPreference(preference);
                break;
        }
    }

}
