package de.uni_augsburg.mobilesensingforfitnessandwellbeing.activity;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.BTSensor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.AccSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.GpsSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.Sensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.service.SensorToMusic;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;

public class SensorTestActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    //private Map<String, Sensor> sensors;

    private EditText logText;
    private Spinner sensorSpinner;
    private Button startButton;
    private Button clearButton;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BroadcastAction.VALUES.VALUEBROADCAST.ACTION:
                        logText.append(
                                        "sensor: " + intent.getStringExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_SENSORNAME) + "\n" +
                                        "value_name: " + intent.getStringExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_VALUENAME) + "\n" +
                                        "value: " + intent.getDoubleExtra(BroadcastAction.VALUES.VALUEBROADCAST.EXTRA_VALUE,0.0d) + "\n");
                        break;
                }
            }
        };
        registerBroadcastReceiver();

        setContentView(R.layout.activity_sensor_test);

        findViews();

        initStartButton();
        initClearButton();

        initSensors();
        initSensorSpinner();
    }

    private void findViews() {
        this.logText = findViewById(R.id.logText);
        this.sensorSpinner = findViewById(R.id.sensorSpinner);
        this.startButton = findViewById(R.id.startButton);
        this.clearButton = findViewById(R.id.clearButton);
    }

    private void initClearButton() {
        this.clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logText.post(new Runnable() {
                    @Override
                    public void run() {
                        logText.getText().clear();
                    }
                });
            }
        });
    }

    private void initStartButton() {
        this.startButton.setText(R.string.button_start);
        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startButton.getText() == getResources().getString(R.string.button_start)) {
                    startButton.setText(R.string.button_stop);
                    startSensor();
                } else {
                    startButton.setText(R.string.button_start);
                }
            }
        });
    }

    private void initSensors() {

        requestPermissions(BTSensor.necessaryPermissions());
        requestPermissions(GpsSensor.necessaryPermissions());

        Intent i =new Intent(getApplicationContext(),SensorToMusic.class);
        startService(i);
        android.util.Log.d("application start", "x");

        /*
        sensors = new HashMap<>();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> activatedSensors = settings.getStringSet("pref_sensors", new HashSet<String>());

        // Put new sensors over here
        Sensor gpsSensor = new GpsSensor(this);
        if (activatedSensors.contains(gpsSensor.getSensorName())) {
            requestPermissions(gpsSensor.necessaryPermissions());
            sensors.put(gpsSensor.getSensorName(), gpsSensor);
        }

        Sensor accSensor = new AccSensor(this);
        if (activatedSensors.contains(accSensor.getSensorName())) {
            requestPermissions(accSensor.necessaryPermissions());
            sensors.put(accSensor.getSensorName(), accSensor);
        }

        Sensor btSensor = new BTSensor(this);
        if(activatedSensors.contains(btSensor.getSensorName())) {
            requestPermissions(btSensor.necessaryPermissions());
            sensors.put(btSensor.getSensorName(), btSensor);
        }*/
    }

    private void initSensorSpinner() {
        /*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(sensors.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sensorSpinner.setAdapter(adapter);

        sensorSpinner.setSelection(0);
        */
    }

    private void requestPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        0);
            }
        }
    }

    private void startSensor() {

        /*
        final Sensor sensor = sensors.get(sensorSpinner.getSelectedItem().toString());
        sensor.initialize();

        this.countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {

            public void onTick(long millisUntilFinished) {
                if (sensor.isReady()) {
                    logText.append(String.format("%s: %s (%s) \n",
                            sensor.getSensorName(),
                            sensor.getCurrentlyDesiredBpm(),
                            sensor.getRawSensorValue())
                    );
                } else {
                    logText.append("Sensor is not ready.\n");
                }
            }

            public void onFinish() {
                start();
            }

        }.start();
        */
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.VALUES.VALUEBROADCAST.ACTION);
        registerReceiver(this.broadcastReceiver, filter);
    }
}
