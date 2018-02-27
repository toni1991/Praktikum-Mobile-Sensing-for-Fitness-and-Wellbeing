package de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GpsSensor extends Sensor implements LocationListener {

    private LocationManager locationManager;
    private List<Location> lastKnownLocations;
    private boolean isReady;

    public GpsSensor(Activity activity) {
        super(activity);
    }

    @Override
    public void initialize()
    {
        this.lastKnownLocations = new ArrayList<>();
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.isReady = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        registerLocationListener();
    }

    @Override
    public String[] necessaryPermissions() {
        return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    }

    private void registerLocationListener() {
        for(String permission : necessaryPermissions())
        {
            if(ActivityCompat.checkSelfPermission( activity, permission) != PackageManager.PERMISSION_GRANTED) {
                throw new RuntimeException("Missing permission!");

            }
        }
        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                200L,
                1f,
                this);
    }

    @Override
    public float getCurrentlyDesiredBpm() {
        double rawSensorValue = getRawSensorValue();
        // TODO: Multiply with (height in cm / 2) instead of 100
        return (float) (rawSensorValue * 100);
    }

    @Override
    public double getRawSensorValue() {
        if (isReady()) {
            Location lastKnownLocation = lastKnownLocations.get(lastKnownLocations.size()-1);
            Log.d(this.getSensorName(), String.format("Lat: %s, Long: %s, Time: %s",
                    lastKnownLocation.getLatitude(),
                    lastKnownLocation.getLongitude(),
                    lastKnownLocation.getTime()));
            return lastKnownLocation.getSpeed();
        } else {
            return -1;
        }
    }

    @Override
    public String getSensorName() {
        return "GPS Sensor";
    }

    @Override
    public boolean isReady() {
        return  this.isReady && !this.lastKnownLocations.isEmpty();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lastKnownLocations.add(location);
        if(this.lastKnownLocations.size() > 10)
        {
            this.lastKnownLocations.remove(0);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(provider != LocationManager.GPS_PROVIDER) {
            return;
        }

        isReady = (status == LocationProvider.AVAILABLE);
    }

    @Override
    public void onProviderEnabled(String provider) {
        if(provider == LocationManager.GPS_PROVIDER) {
            isReady = true;
            this.lastKnownLocations.clear();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if(provider != LocationManager.GPS_PROVIDER) {
            isReady = false;
        }
    }
}
