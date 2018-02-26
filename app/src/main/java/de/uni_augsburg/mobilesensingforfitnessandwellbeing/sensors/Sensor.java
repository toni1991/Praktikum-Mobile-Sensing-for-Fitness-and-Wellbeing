package de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors;

public abstract class Sensor {

    private int windowLengthMillis;

    public abstract float getCurrentlyDesiredBpm();

    public abstract String getSensorName();

    public abstract double getRawSensorValue();

    public void setWindowLengthMillis(int windowLenghtMillis){
        this.windowLengthMillis  = windowLenghtMillis;
    }

}
