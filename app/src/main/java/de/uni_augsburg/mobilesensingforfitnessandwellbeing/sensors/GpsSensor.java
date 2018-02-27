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
import java.util.List;
import java.util.Map;

public class GpsSensor extends Sensor implements LocationListener {

    private final LocationManager locationManager;

    private boolean isReady;

    private List<Location> lastKnownLocations = null;

    public GpsSensor(Activity activity) {
        super(activity);
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        requestPermissions();
        isReady = false;
        registerLocationListener();
    }

    private void registerLocationListener() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Missing permission!");
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200L, 1f, this);
    }

    private void requestPermissions() {
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
        return (float) (rawSensorValue * 150);
    }

    @Override
    public String getSensorName() {
        return "GPS Sensor";
    }

    @Override
    public double getRawSensorValue() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Missing permission!");
        }
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
