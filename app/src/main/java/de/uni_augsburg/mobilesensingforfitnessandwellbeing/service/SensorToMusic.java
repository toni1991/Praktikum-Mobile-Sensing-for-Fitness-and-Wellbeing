package de.uni_augsburg.mobilesensingforfitnessandwellbeing.service;

/**
 * Created by lukas on 28.02.2018.
 */

import android.content.IntentFilter;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import android.os.CountDownTimer;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.AccSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.BTSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.GpsSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.Sensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;

public class SensorToMusic extends Service {

    private BroadcastReceiver broadcastReceiver;
    private CountDownTimer countDownTimer;

    private Map<String, Sensor> sensors;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            }
        };
        registerBroadcastReceiver();

        sensors = new HashMap<>();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> activatedSensors = settings.getStringSet("pref_sensors", new HashSet<String>());

        // Put new sensors over here
        /*
        Sensor gpsSensor = new GpsSensor(this);
        if (activatedSensors.contains(gpsSensor.getSensorName())) {
            sensors.put(gpsSensor.getSensorName(), gpsSensor);
        }
        */
        Sensor accSensor = new AccSensor(this);
        if (activatedSensors.contains(accSensor.getSensorName())) {
            sensors.put(accSensor.getSensorName(), accSensor);
        }

        /*
        Sensor btSensor = new BTSensor(this);
        if(activatedSensors.contains(btSensor.getSensorName())) {
            sensors.put(btSensor.getSensorName(), btSensor);
        }
        */

        sensors.forEach((name,sensor)->sensor.initialize());

        this.countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {

            public void onTick(long millisUntilFinished) {
                sensors.forEach((name,sensor)-> broadCastSensor(sensor));
            }

            public void onFinish() {
                start(); // keep it running
            }

        }.start();

    }

    private void broadCastSensorValue(String sensor_name, String value_name, Double value)
    {
        Intent broadcast = new Intent();
        broadcast.setAction(BroadcastAction.VALUES.VALUEBROADCAST.ACTION);
        broadcast.putExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_SENSORNAME , sensor_name);

        broadcast.putExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_VALUENAME ,  value_name);
        broadcast.putExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_VALUE, value);
        sendBroadcast(broadcast);
    }

    private void broadCastSensor(Sensor sensor)
    {
        android.util.Log.d("broadcast",sensor.getSensorName());
        if (sensor.isReady()) {
            broadCastSensorValue(sensor.getSensorName(), "raw", sensor.getRawSensorValue());
            broadCastSensorValue(sensor.getSensorName(), "bpm", (double)sensor.getCurrentlyDesiredBpm());
        }
        else
        {
            broadCastSensorValue(sensor.getSensorName(), "notReady",0.0d);
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
       // filter.addAction(BroadcastAction.PLAYBACK.);
        registerReceiver(this.broadcastReceiver, filter);
    }

}
