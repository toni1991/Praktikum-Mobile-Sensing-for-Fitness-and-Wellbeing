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

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.LinkedList;

import android.os.CountDownTimer;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.musicLibrary.MusicTrack;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.musicLibrary.TrackFinder;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.AccSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.BTSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.GpsSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.Sensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;

public class SensorToMusic extends Service {

    private BroadcastReceiver broadcastReceiver;
    private CountDownTimer countDownTimer;

    private Map<String, Sensor> sensors;
    private LinkedList<Float> BPMs;
    private LinkedList<Long> Times;
    private long windowLength;

    private long lastChangedSong; // mills
    private long minSongDuration; // mills

    private TrackFinder trackFinder;

    float lastBPMEstimation = -100.0f;
    float bpmSongChangeThreshold;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        trackFinder = new TrackFinder(this.getApplicationContext());

        windowLength = 1500;
        minSongDuration = 10 * 1000; // 10 sec
        bpmSongChangeThreshold = 25;
        this.BPMs = new LinkedList<>();
        this.Times = new LinkedList<>();
        this.broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BroadcastAction.FILE.REQUEST_NEXT_SONG.ACTION:
                    {
                        lastChangedSong = System.currentTimeMillis();
                        boolean dislike =  intent.getBooleanExtra(BroadcastAction.FILE.REQUEST_NEXT_SONG.EXTRA_DISLIKE, false);

                        if (lastBPMEstimation < 0.0f)
                        {

                            // there is no value yet
                            
                        }

                        // NEXT SONG
                    }
                    break;

                }
                }
        };
        registerBroadcastReceiver();

        sensors = new HashMap<>();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> activatedSensors = settings.getStringSet("pref_sensors", new HashSet<String>());

        // Put new sensors over here

        Sensor gpsSensor = new GpsSensor(this);
        if (activatedSensors.contains(gpsSensor.getSensorName()) || activatedSensors.isEmpty()) {
            sensors.put(gpsSensor.getSensorName(), gpsSensor);
        }

        Sensor accSensor = new AccSensor(this);
        if (activatedSensors.contains(accSensor.getSensorName()) || activatedSensors.isEmpty()) {
            sensors.put(accSensor.getSensorName(), accSensor);
        }


//        Sensor btSensor = new BTSensor(this);
//        if(activatedSensors.contains(btSensor.getSensorName())) {
//            sensors.put(btSensor.getSensorName(), btSensor);
//        }


        sensors.forEach((name,sensor)->sensor.initialize());

        this.countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {

            public void onTick(long millisUntilFinished) {
                long time = System.currentTimeMillis();
                sensors.forEach((name,sensor)-> broadCastSensor(sensor));
                sensors.forEach((name,sensor)-> addBPMEstimation(sensor,time) );
                float estimation = BPMEstimation();

                deleteOldEstimations(time);
                broadCastBPMEstimation(estimation);
                decisionNewSong(time, estimation);
            }

            public void onFinish() {
                start(); // keep it running
            }

        }.start();

    }

    private void addBPMEstimation(Sensor sensor, long time)
    {
        if (!sensor.isReady())
            return;
        this.BPMs.add(sensor.getCurrentlyDesiredBpm());
        this.Times.add(time);
    }

    private void deleteOldEstimations(long time)
    {
        while (!Times.isEmpty() && time - Times.peek() > windowLength) {
            BPMs.poll();
            Times.poll();
        }
    }

    private float BPMEstimation()
    {
        float estimation = 0.0f;
        for (float bpm : BPMs)
        {
            estimation += bpm;
        }
        estimation /= BPMs.size();
        return estimation;
    }

    private void broadCastBPMEstimation(float estimation)
    {

        Intent broadcast = new Intent();
        broadcast.setAction(BroadcastAction.VALUES.BPMESTIMATION.ACTION);
        broadcast.putExtra(BroadcastAction.VALUES.BPMESTIMATION.EXTRA_VALUEBPM, estimation);
        sendBroadcast(broadcast);

    }

    private void decisionNewSong(long time, float bpmEstimation)
    {
        if (!(lastChangedSong + this.minSongDuration > time))
            return;

        if (Math.abs(bpmEstimation - lastBPMEstimation) > bpmSongChangeThreshold)
        {
            lastChangedSong = System.currentTimeMillis();
            broadcastNewSong(bpmEstimation, false);
        }
    }

    private void broadcastNewSong(float estimation, boolean dislike)
    {

        MusicTrack track;

        //that's how to receive a track object
        track = trackFinder.getNextSong(estimation);

        //that's how to dislike a track
        if(dislike) {
            trackFinder.dislike(track);
        }


        //TODO: brodcast audiofile/audio object

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
        filter.addAction(BroadcastAction.FILE.NEXT_SONG.ACTION);
        registerReceiver(this.broadcastReceiver, filter);
    }

}
