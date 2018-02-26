package de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors;

import android.content.Context;

public abstract class Sensor {

    protected int windowLengthMillis;

    protected Context context;

    public abstract float getCurrentlyDesiredBpm();

    public abstract String getSensorName();

    public abstract double getRawSensorValue();

    public Sensor(Context context) {
        this.context = context;
    }

    public void setWindowLengthMillis(int windowLenghtMillis) {
        this.windowLengthMillis  = windowLenghtMillis;
    }

}
