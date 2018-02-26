package de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors;

public abstract class Sensor {

    public abstract void setWindowLength(int windowLenghtMillis);

    public abstract float getCurrentlyDesiredBpm();

    public abstract String getSensorName();

    public abstract double getRawSensorValue();
    
}
