package de.uni_augsburg.mobilesensingforfitnessandwellbeing.service;

/**
 * Created by lukas on 28.02.2018.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.musicLibrary.MusicTrack;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.musicLibrary.TrackFinder;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.AccSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.GpsSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.Sensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;

public class SensorToMusic extends Service {

    public static long lastChangedSong; // mills
    public static MusicTrack lastMusic;
    float lastBPMEstimation = -100.0f;
    float bpmSongChangeThreshold;
    private BroadcastReceiver broadcastReceiver;
    private CountDownTimer countDownTimer;
    private Map<String, Sensor> sensors;
    private LinkedList<Float> BPMs;
    private LinkedList<Long> Times;
    private long windowLength;
    private long minSongDuration; // mills
    private TrackFinder trackFinder;

    public void onCreate() {
        lastChangedSong = 1;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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
                    case BroadcastAction.FILE.REQUEST_NEXT_SONG.ACTION: {
                        boolean dislike = intent.getBooleanExtra(BroadcastAction.FILE.REQUEST_NEXT_SONG.EXTRA_DISLIKE, false);

                        if (lastBPMEstimation < 0.0f) {
                            broadcastNewSong(75, false);
                        }

                        broadcastNewSong(BPMEstimation(), dislike);
                    }
                    break;

                }
            }
        };
        registerBroadcastReceiver();

        sensors = new HashMap<>();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        // Put new sensors over here
        Sensor gpsSensor = new GpsSensor(this);
        if (settings.getBoolean("pref_sensor_gps", true)) {
            sensors.put(gpsSensor.getSensorName(), gpsSensor);
        }

        Sensor accSensor = new AccSensor(this);
        if (settings.getBoolean("pref_sensor_accelerometer", true)) {
            sensors.put(accSensor.getSensorName(), accSensor);
        }


//        Sensor btSensor = new BTSensor(this);
//        if(activatedSensors.contains(btSensor.getSensorName()) || activatedSensors.isEmpty()) {
//            sensors.put(btSensor.getSensorName(), btSensor);
//        }


        sensors.forEach((name, sensor) -> sensor.initialize());

        this.countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {

            public void onTick(long millisUntilFinished) {
                long time = System.currentTimeMillis();
                sensors.forEach((name, sensor) -> broadCastSensor(sensor));
                sensors.forEach((name, sensor) -> addBPMEstimation(sensor, time));
                float estimation = BPMEstimation();

                deleteOldEstimations(time);
                broadCastBPMEstimation(estimation);
                decisionNewSong(time, estimation);
            }

            public void onFinish() {
                start(); // keep it running
            }

        }.start();

        return START_STICKY;
    }

    private void addBPMEstimation(Sensor sensor, long time) {
        if (!sensor.isReady())
            return;
        this.BPMs.add(sensor.getCurrentlyDesiredBpm());
        this.Times.add(time);
    }

    private void deleteOldEstimations(long time) {
        while (!Times.isEmpty() && time - Times.peek() > windowLength) {
            BPMs.poll();
            Times.poll();
        }
    }

    private float BPMEstimation() {
        float estimation = 0.0f;
        for (float bpm : BPMs) {
            estimation += bpm;
        }
        estimation /= BPMs.size();
        return estimation;
    }

    private void broadCastBPMEstimation(float estimation) {

        Intent broadcast = new Intent();
        broadcast.setAction(BroadcastAction.VALUES.BPMESTIMATION.ACTION);
        broadcast.putExtra(BroadcastAction.VALUES.BPMESTIMATION.EXTRA_VALUEBPM, estimation);
        sendBroadcast(broadcast);

    }

    private void decisionNewSong(long time, float bpmEstimation) {
        if (!(time - SensorToMusic.lastChangedSong > +this.minSongDuration))
            return;


        if (Math.abs(bpmEstimation - lastBPMEstimation) > bpmSongChangeThreshold) {
            broadcastNewSong(bpmEstimation, false);
        }
    }

    private void broadcastNewSong(float estimation, boolean dislike) {
        MusicTrack track = trackFinder.getNextSong(estimation);

        if (dislike && lastMusic != null) {
            trackFinder.dislike(lastMusic);
        }

        if (lastMusic == null ||
                (lastMusic.isValidTrackFile() &&
                        !lastMusic.equals(track) &&
                        Math.abs(lastMusic.getBpm() - track.getBpm()) > bpmSongChangeThreshold))
        {
            SensorToMusic.lastChangedSong = System.currentTimeMillis();

            lastMusic = track;

            Intent broadcast = new Intent();
            broadcast.setAction(BroadcastAction.FILE.NEXT_SONG.ACTION);
            broadcast.putExtra(BroadcastAction.FILE.NEXT_SONG.EXTRA_SONG, track);
            sendBroadcast(broadcast);
        }
    }

    private void broadCastSensorValue(String sensor_name, String value_name, Double value) {
        Intent broadcast = new Intent();
        broadcast.setAction(BroadcastAction.VALUES.VALUEBROADCAST.ACTION);
        broadcast.putExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_SENSORNAME, sensor_name);

        broadcast.putExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_VALUENAME, value_name);
        broadcast.putExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_VALUE, value);
        sendBroadcast(broadcast);
    }

    private void broadCastSensor(Sensor sensor) {
        android.util.Log.d("broadcast", sensor.getSensorName());
        if (sensor.isReady()) {
            broadCastSensorValue(sensor.getSensorName(), "raw", sensor.getRawSensorValue());
            broadCastSensorValue(sensor.getSensorName(), "bpm", (double) sensor.getCurrentlyDesiredBpm());
        } else {
            broadCastSensorValue(sensor.getSensorName(), "notReady", 0.0d);
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.FILE.REQUEST_NEXT_SONG.ACTION);
        registerReceiver(this.broadcastReceiver, filter);
    }

}
