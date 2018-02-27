package de.uni_augsburg.mobilesensingforfitnessandwellbeing.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.view.InfoView;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.view.MediaView;

public class JBpmActivity extends AppCompatActivity {

    private InfoView infoView;
    private MediaView mediaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_j_bpm);

        findViews();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sensor_test:
                Intent sensorTestIntent = new Intent(this, SensorTestActivity.class);
                startActivity(sensorTestIntent);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void findViews() {
        this.infoView = findViewById(R.id.infoView);
        this.mediaView = findViewById(R.id.mediaView);
    }

    private void init() {
        mediaView.setMediaTotalTime(246);
    }
}
