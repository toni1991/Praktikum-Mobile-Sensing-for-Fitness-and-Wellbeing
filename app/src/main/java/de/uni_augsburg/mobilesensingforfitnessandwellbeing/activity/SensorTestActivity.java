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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;


import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.GpsSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.service.SensorToMusic;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;

public class SensorTestActivity extends AppCompatActivity {

    private ArrayList<String> allPermissions;
    private EditText logText;
    private Spinner sensorSpinner;
    private Button startButton;
    private Button clearButton;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.allPermissions = new ArrayList<>();

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

    private void startService() {
        Intent i =new Intent(getApplicationContext(),SensorToMusic.class);
        startService(i);
        android.util.Log.d("application start", "x");
    }

    private void initSensors() {
        logText.append("initSensors");
        boolean request1 = requestPermissions(BTSensor.necessaryPermissions());
        boolean request2 = requestPermissions(GpsSensor.necessaryPermissions()) ;
        if (request1 && request2)
        {
            startService();
        }
    }

    private boolean checkPermissions() {
        boolean allPermissionsGranted = true;

        for (String permission : allPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
            }
        }
        return allPermissionsGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            logText.append("permission: " + "?" + " newly granted \n");

            if (checkPermissions())
                startService();

        } else {
            logText.append("permission: " + "?" + " denied \n");
        }
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

    private boolean requestPermissions(String[] permissions) {
        boolean allPermissionsGranted = false;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        0);
                allPermissions.add(permission);
                allPermissionsGranted = false;
                logText.append("permission " + permission);

            }
        }
        return allPermissionsGranted;
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
