package de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.Activity;
import android.util.Log;

import java.util.LinkedList;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.DSPUitility;

/**
 * Created by lukas on 26.02.18.
 */

public class AccSensor extends Sensor implements SensorEventListener {

    private int totalSteps;
    private LinkedList<Long> timeOfEvents;
    private LinkedList<Float> rEvents;

    private int windowLength;

    private LinkedList<Long> timeOfSteps;
    private LinkedList<Double> lastEnergy;

    public AccSensor (Context context)
    {
        super(context);
        super.setWindowLengthMillis(1500);
        stepUp = true;
        windowLength = 25;
        lastEnergy = new LinkedList<>();

        senSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        rEvents = new LinkedList<>();
        timeOfEvents = new LinkedList<>();
        timeOfSteps = new LinkedList<>();
    }

    private SensorManager senSensorManager;
    private android.hardware.Sensor senAccelerometer;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        android.hardware.Sensor mySensor = sensorEvent.sensor;


        if (mySensor.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER) {

            float newValue = (float) Math.sqrt(Math.pow(sensorEvent.values[0], 2) + Math.pow(sensorEvent.values[1], 2) + Math.pow(sensorEvent.values[2], 2));
            newValue-= 10;
            rEvents.add(newValue);
            timeOfEvents.add(System.currentTimeMillis());

            if (timeOfEvents.size() >= windowLength) {
                if (timeOfEvents.size() != rEvents.size())
                    throw new IllegalArgumentException("illegal argument exception AccSensor");

                lastEnergy.add(DSPUitility.calculateShortTermEnergy(rEvents,rEvents.size()-windowLength, windowLength));


                if (lastEnergy.size() == 3) {
                    //Log.d("test", preLastDerivation.toString() + " " + newValue);

                    if (lastEnergy.get(2) > 12) {
                        double preLastDerivation = lastEnergy.get(1) - lastEnergy.get(0);
                        double lastDerivation = lastEnergy.get(2) - lastEnergy.get(1);

                        /*
                        if ((preLastDerivation > 0 && lastDerivation < 0) ||
                                (preLastDerivation < 0 && lastDerivation > 0)) {
                                */
                        if (performStepCheck(lastDerivation)){
                            totalSteps++;
                            timeOfSteps.add(System.currentTimeMillis());
                        }
                    }
                    lastEnergy.poll();
                }

            }
        }
        clearOldSensorValues();
    }

    boolean stepUp;
    private boolean performStepCheck(double lastDerivation)
    {
        if (stepUp && lastDerivation > 5) {
            stepUp = false;
            return true;
        }
        else if (!stepUp && lastDerivation < -5)
        {
            stepUp = true;
            return true;
        }
        return false;
    }

    private void clearOldSensorValues() {
        long currentTime = System.currentTimeMillis();
        while (!timeOfEvents.isEmpty() && currentTime - timeOfEvents.peek() > windowLengthMillis) {
            timeOfEvents.poll();
            rEvents.poll();
        }
    }

    private float calculateStepFrequency() {
        long currentTime = System.currentTimeMillis();
        while (!timeOfSteps.isEmpty() && currentTime - timeOfSteps.peek() > 10000) {
            timeOfSteps.poll();
        }
        return timeOfSteps.size()  * 6 ;
    }


    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }

    @Override
    public float getCurrentlyDesiredBpm() {
        return calculateStepFrequency();
    }

    @Override
    public String getSensorName() {
        return "Accelerometer";
    }

    @Override
    public double getRawSensorValue() {
        return totalSteps;
    }

    @Override
    public boolean isReady() {
        return true;
    }



    @Override
    public void initialize() {

    }
}