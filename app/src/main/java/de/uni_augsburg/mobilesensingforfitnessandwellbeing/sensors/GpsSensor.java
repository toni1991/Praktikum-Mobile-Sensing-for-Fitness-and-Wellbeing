package de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

public class GpsSensor extends Sensor {

    private final LocationManager locationManager;

    public GpsSensor(Activity activity) {
        super(activity);
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
    }

    @Override
    public float getCurrentlyDesiredBpm() {
        double rawSensorValue = getRawSensorValue();
        return (float) (rawSensorValue * 100);
    }

    @Override
    public String getSensorName() {
        return "GPS Sensor";
    }

    @Override
    public double getRawSensorValue() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return 0.0;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastKnownLocation != null)
        {
            return lastKnownLocation.getSpeed();
        }
        else {
            return 0.0;
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }
}
