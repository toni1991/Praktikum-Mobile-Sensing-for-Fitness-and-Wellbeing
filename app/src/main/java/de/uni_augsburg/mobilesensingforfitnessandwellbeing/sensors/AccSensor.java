package de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.Activity;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by lukas on 26.02.18.
 */

public class AccSensor extends Sensor implements SensorEventListener {

    private int tmp;
    private Queue<Long> timeOfEvents;
    private Queue<Float> xEvents;
    private Queue<Float> yEvents;
    private Queue<Float> zEvents;

    public AccSensor (Activity activity)
    {
        super(activity);
        super.setWindowLengthMillis(1500);

        tmp = 0;
        senSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        xEvents = new LinkedList<Float>();
        yEvents = new LinkedList<Float>();
        zEvents = new LinkedList<Float>();
        timeOfEvents = new LinkedList<Long>();
    }

    private SensorManager senSensorManager;
    private android.hardware.Sensor senAccelerometer;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        android.hardware.Sensor mySensor = sensorEvent.sensor;

        timeOfEvents.add(System.currentTimeMillis());

        if (mySensor.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER) {
            tmp++;

            xEvents.add(sensorEvent.values[0]);
            yEvents.add(sensorEvent.values[1]);
            zEvents.add(sensorEvent.values[2]);
        }

        clearOldSensorValues();
    }

    private void clearOldSensorValues() {
        long currentTime = System.currentTimeMillis();
        while (!timeOfEvents.isEmpty() && currentTime - timeOfEvents.peek() > windowLengthMillis) {
            timeOfEvents.poll();
            xEvents.poll();
            yEvents.poll();
            zEvents.poll();
        }
    }


    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }

    @Override
    public float getCurrentlyDesiredBpm() {
        return timeOfEvents.size();
    }

    @Override
    public String getSensorName() {
        return "Accelerometer";
    }

    @Override
    public double getRawSensorValue() {
        return xEvents.peek();
    }

    @Override
    public boolean isReady() {
        return !timeOfEvents.isEmpty();
    }
}