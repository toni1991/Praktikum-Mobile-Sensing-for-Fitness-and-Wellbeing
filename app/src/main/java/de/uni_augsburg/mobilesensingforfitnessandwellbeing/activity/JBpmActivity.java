package de.uni_augsburg.mobilesensingforfitnessandwellbeing.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.Arrays;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.LocalMusicProvider;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MediaListener;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MusicProvider;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.musicLibrary.MusicTrack;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.BTSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.sensors.GpsSensor;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.service.JBpmMusicService;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.service.SensorToMusic;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.view.InfoView;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.view.MediaView;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.view.SensorGraphView;

public class JBpmActivity extends AppCompatActivity {

    private InfoView infoView;
    private MediaView mediaView;
    private SensorGraphView sensorGraphView;
    private GraphView graphView;
    private MusicProvider musicProvider;
    private int permissionRequestCode = 0;

    // Sensor Service
    private ArrayList<String> allPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_j_bpm);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        this.allPermissions = new ArrayList<>();
        requestAllPermissions();

        findViews();
        startMusicService();
        init();
        initSensors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcastReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBroadcastReceivers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(JBpmActivity.this, JBpmMusicService.class));
        stopService(new Intent(JBpmActivity.this, SensorToMusic.class));
    }

    private void registerBroadcastReceivers() {
        registerReceiver(infoView.getBroadcastReceiver(), infoView.getIntentFilter());
        registerReceiver(mediaView.getBroadcastReceiver(), mediaView.getIntentFilter());
        this.sensorGraphView.registerBroadcastReceiver(this);
    }

    private void unregisterBroadcastReceivers() {
        unregisterReceiver(infoView.getBroadcastReceiver());
        unregisterReceiver(mediaView.getBroadcastReceiver());
        this.sensorGraphView.unregisterBroadcastReceiver(this);
    }

    /*
    private void requestPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        0);
            }
        }
    }*/

    private void startMusicService() {
        Intent musicService = new Intent(this, JBpmMusicService.class);
        startService(musicService);
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
            case R.id.action_stop_service:
                stopService(new Intent(JBpmActivity.this, JBpmMusicService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void findViews() {
        this.infoView = findViewById(R.id.infoView);
        this.mediaView = findViewById(R.id.mediaView);
        this.graphView = findViewById(R.id.graphView);
    }

    private void init() {
        this.musicProvider = new LocalMusicProvider(this);
        this.mediaView.setMediaListener(new MediaListener() {

            @Override
            public void onSkip() {
                broadcastRequestNextSong(true);
            }

            @Override
            public void onPlayStatusChange(boolean isPlaying) {
                broadcastPlayStatusRequest(isPlaying);
            }

            @Override
            public void onSeekbarProgressChange(int progress) {
                broadcastSeekbarChanged(progress);
            }
        });
        this.sensorGraphView = new SensorGraphView(this.graphView);
        this.sensorGraphView.init();
        new Thread(this.sensorGraphView.getGraphListener()).start();
    }

    private void broadcastRequestNextSong(boolean dislike) {
        Intent broadcast = new Intent();
        broadcast.setAction(BroadcastAction.FILE.REQUEST_NEXT_SONG.ACTION);
        broadcast.putExtra(BroadcastAction.FILE.REQUEST_NEXT_SONG.EXTRA_DISLIKE, dislike);
        sendBroadcast(broadcast);
    }

    private void broadcastSeekbarChanged(int progress) {
        Intent broadcast = new Intent();
        broadcast.setAction(BroadcastAction.PLAYBACK.SET_PROGRESS.ACTION);
        broadcast.putExtra(BroadcastAction.PLAYBACK.SET_PROGRESS.EXTRA_PROGRESS, progress);
        sendBroadcast(broadcast);
    }

    private void broadcastPlayStatusRequest(boolean isPlaying) {
        Intent broadcast = new Intent();
        if (isPlaying) {
            broadcast.setAction(BroadcastAction.PLAYBACK.PLAY.ACTION);
        } else {
            broadcast.setAction(BroadcastAction.PLAYBACK.PAUSE.ACTION);
        }
        sendBroadcast(broadcast);
    }

    public void broadcastNewSong(MusicTrack newSong) {
        Intent broadcast = new Intent();
        broadcast.setAction(BroadcastAction.FILE.NEXT_SONG.ACTION);
        broadcast.putExtra(BroadcastAction.FILE.NEXT_SONG.EXTRA_SONG, newSong);
        sendBroadcast(broadcast);
    }

    private void startSensorService() {
        Intent i = new Intent(getApplicationContext(), SensorToMusic.class);
        startService(i);
    }

    private void initSensors() {
        allPermissions.addAll(Arrays.asList(BTSensor.necessaryPermissions()));
        allPermissions.addAll(Arrays.asList(GpsSensor.necessaryPermissions()));
        if (checkPermissions()) {
            startSensorService();
        }
    }

    private void requestAllPermissions() {

        requestPermissions(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN},
                permissionRequestCode++);

        /*boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        0);
                allPermissions.add(permission);
                allPermissionsGranted = false;

            }
        }
        return allPermissionsGranted;*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (checkPermissions())
                startSensorService();

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
}
