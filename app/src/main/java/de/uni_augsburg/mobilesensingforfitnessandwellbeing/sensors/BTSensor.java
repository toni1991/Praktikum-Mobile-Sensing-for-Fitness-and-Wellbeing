package de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;

/**
 * Created by Kevin on 28.02.2018.
 */

public class BTSensor extends Sensor implements SensorEventListener
{

    private boolean isReady;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
//    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (BroadcastAction.ACTION_DATA_AVAILABLE.equals(action)) {
//                final BleSensor<?> sensor = BleSensors.getSensor(serviceUuid);
//                final String text = intent.getStringExtra(BleService.EXTRA_TEXT);
//                onDataRecieved(sensor, text);
//            }
//        }
//    };


    public BTSensor(Context context) {
        super(context);
        this.isReady = false;
    }

    @Override
    public float getCurrentlyDesiredBpm() {
        return 0;
    }

    @Override
    public String getSensorName() {
        return "Bluetooth";
    }

    @Override
    public double getRawSensorValue() {
        return 0;
    }

    @Override
    public boolean isReady() {
        return this.isReady;
    }

    public static String[] necessaryPermissions() {
        return new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
    }

    @Override
    public void initialize() {
        BluetoothManager btManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        if (btAdapter != null && !btAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        BluetoothDevice device = btAdapter.getRemoteDevice("F1:CD:28:6D:BF:79");
        Log.e("whatever", device.getName());
        try {
            BluetoothSocket bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            Log.e("wuuut", "conn wuuut: "+bluetoothSocket.toString());
            bluetoothSocket.connect();
            Log.e("woooot", "connect success");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.isReady = true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }
}
