package de.uni_augsburg.mobilesensingforfitnessandwellbeing.activity;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.AccSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.GpsSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.Sensor;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SensorTestActivity extends AppCompatActivity {

    private Map<String, Sensor> sensors;
    private Spinner sensorSpinner;
    private EditText logText;
    private Button startButton;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_test);

        this.logText = findViewById(R.id.logText);
        this.sensorSpinner = findViewById(R.id.sensorSpinner);
        this.startButton = findViewById(R.id.startButton);

        initSensors();

        initButton();
    }

    private void initButton() {
        this.startButton.setText(R.string.button_start);
        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startButton.getText() == getResources().getString(R.string.button_start))
                {
                    startButton.setText(R.string.button_stop);
                    startSensor();
                }
                else {
                    countDownTimer.cancel();
                    startButton.setText(R.string.button_start);
                }
            }
        });
    }

    private void initSensors() {
        sensors = new HashMap<>();

        Sensor gpsSensor = new GpsSensor(this);
        sensors.put(gpsSensor.getSensorName(), gpsSensor);

        Sensor accSensor = new AccSensor(this);
        sensors.put(accSensor.getSensorName(), accSensor);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, new ArrayList<String>(sensors.keySet()));
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sensorSpinner.setAdapter(adapter);

        sensorSpinner.setSelection(0);
    }

    private void startSensor()
    {
        this.countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {

            // This is called after every 10 sec interval.
            public void onTick(long millisUntilFinished) {
                Sensor sensor  = sensors.get(sensorSpinner.getSelectedItem().toString());
                if(sensor.isReady())
                {
                    logText.append(String.format("%s: %s (%s) \n",
                            sensor.getSensorName(),
                            sensor.getCurrentlyDesiredBpm(),
                            sensor.getRawSensorValue())
                    );
                }
                else {
                    logText.append("Sensor is not ready.");
                }
            }

            public void onFinish() {
                start();
            }

        }.start();
    }
}
