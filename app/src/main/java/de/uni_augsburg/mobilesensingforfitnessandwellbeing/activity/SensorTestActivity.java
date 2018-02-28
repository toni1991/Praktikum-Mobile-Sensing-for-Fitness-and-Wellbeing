package de.uni_augsburg.mobilesensingforfitnessandwellbeing.activity;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

public class SensorTestActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private Map<String, Sensor> sensors;

    private EditText logText;
    private Spinner sensorSpinner;
    private Button startButton;
    private Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    countDownTimer.cancel();
                    startButton.setText(R.string.button_start);
                }
            }
        });
    }

    private void initSensors() {
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
    }

    private void initSensorSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(sensors.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sensorSpinner.setAdapter(adapter);

        sensorSpinner.setSelection(0);
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
    }
}
