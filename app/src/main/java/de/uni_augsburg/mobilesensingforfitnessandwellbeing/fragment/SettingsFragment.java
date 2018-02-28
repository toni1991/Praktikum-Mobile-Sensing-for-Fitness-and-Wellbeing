package de.uni_augsburg.mobilesensingforfitnessandwellbeing.fragment;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.AccSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.BTSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.GpsSensor;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        addSensorSettings();
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
        sensorListPreference.setSummary(getResources().getString(R.string.settings_summary_sensors));
        sensorListPreference.setEntries(sensorNames);
        sensorListPreference.setEntryValues(sensorNames);

        preferenceCategory.addPreference(sensorListPreference);

    }
}
