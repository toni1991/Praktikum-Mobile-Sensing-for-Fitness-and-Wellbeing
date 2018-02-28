package de.uni_augsburg.mobilesensingforfitnessandwellbeing.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.InputType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.AccSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.BTSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.GpsSensor;

public class SettingsFragment extends PreferenceFragment  implements
        OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        this.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        addHeightSettings();
        addMediaSettings();
        addSensorSettings();
    }

    private void addHeightSettings() {
        PreferenceScreen preferenceScreen =  this.getPreferenceScreen();

        PreferenceCategory preferenceCategory = new PreferenceCategory(preferenceScreen.getContext());
        preferenceCategory.setTitle("Misc");
        preferenceScreen.addPreference(preferenceCategory);

        EditTextPreference heightPreference = new EditTextPreference(preferenceScreen.getContext());
        heightPreference.setKey("pref_height");
        heightPreference.setTitle(getResources().getString(R.string.settings_height));
        heightPreference.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        heightPreference.setDefaultValue("175");

        preferenceCategory.addPreference(heightPreference);

        updateHeightPreference(heightPreference);
    }

    private void addMediaSettings() {
        PreferenceScreen preferenceScreen =  this.getPreferenceScreen();

        PreferenceCategory preferenceCategory = new PreferenceCategory(preferenceScreen.getContext());
        preferenceCategory.setTitle("Media");
        preferenceScreen.addPreference(preferenceCategory);

        EditTextPreference mediaDirectoryPreference = new EditTextPreference(preferenceScreen.getContext());
        mediaDirectoryPreference.setKey("pref_media_directory");
        mediaDirectoryPreference.setTitle(getResources().getString(R.string.settings_media_directory));
        mediaDirectoryPreference.setDefaultValue( Environment.getExternalStorageDirectory() + "/Music");

        preferenceCategory.addPreference(mediaDirectoryPreference);

        updateMediaDirectoryPreference(mediaDirectoryPreference);
    }

    private void updateMediaDirectoryPreference(Preference preference) {
        preference.setSummary(((EditTextPreference) preference).getText());
    }

    private void addSensorSettings() {
        PreferenceScreen preferenceScreen =  this.getPreferenceScreen();

        PreferenceCategory preferenceCategory = new PreferenceCategory(preferenceScreen.getContext());
        preferenceCategory.setTitle("Sensors");
        preferenceScreen.addPreference(preferenceCategory);

        String[] sensorNames = new String[]{
                new GpsSensor(preferenceScreen.getContext()).getSensorName(),
                new AccSensor(preferenceScreen.getContext()).getSensorName(),
                new BTSensor(preferenceScreen.getContext()).getSensorName()
        };

        MultiSelectListPreference sensorListPreference = new MultiSelectListPreference(preferenceScreen.getContext());
        sensorListPreference.setKey("pref_sensors");
        sensorListPreference.setTitle(getResources().getString(R.string.settings_sensors));
        sensorListPreference.setEntries(sensorNames);
        sensorListPreference.setEntryValues(sensorNames);
        sensorListPreference.setDefaultValue(new HashSet<String>(Arrays.asList(sensorNames)));
        preferenceCategory.addPreference(sensorListPreference);

        updateSensorsPreference(sensorListPreference);
    }

    private void updateHeightPreference(Preference preference) {
        String height = ((EditTextPreference) preference).getText();
        preference.setSummary("Your height: "+height+" centimeter");
    }

    private void updateSensorsPreference(Preference preference) {
        Set<String> sensors = ((MultiSelectListPreference) preference).getValues();
        preference.setSummary(
                getResources().getString(R.string.settings_summary_sensors) + ": " +
                        sensors.stream().collect(Collectors.joining(", "))
        );
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        switch (key)
        {
            case "pref_height": updateHeightPreference(preference); break;
            case "pref_sensors": updateSensorsPreference(preference); break;
            case "pref_media_directory": updateMediaDirectoryPreference(preference); break;
        }
    }

}
