package de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CallSuper;

public abstract class Sensor {

    protected int windowLengthMillis;

    protected Activity activity;

    public abstract float getCurrentlyDesiredBpm();

    public abstract String getSensorName();

    public abstract double getRawSensorValue();

    public abstract boolean isReady();

    public abstract String[] necessaryPermissions();

    public abstract void initialize();

    public Sensor(Activity activity) {
        this.activity = activity;
    }

    public void setWindowLengthMillis(int windowLenghtMillis) {
        this.windowLengthMillis  = windowLenghtMillis;
    }

}
