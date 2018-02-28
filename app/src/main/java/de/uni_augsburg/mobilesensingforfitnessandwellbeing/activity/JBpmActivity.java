package de.uni_augsburg.mobilesensingforfitnessandwellbeing.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.security.Permission;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.BpmMappedSong;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.LocalMusicProvider;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MediaListener;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MediaServiceConstants;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MusicProvider;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.service.JBpmMusicService;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.view.InfoView;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.view.MediaView;

public class JBpmActivity extends AppCompatActivity {

    private InfoView infoView;
    private MediaView mediaView;
    private MusicProvider musicProvider;

    /*private JBpmMusicService musicService;
    private Intent playIntent;
    private boolean musicBound=false;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_j_bpm);

        requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}
                );
        findViews();
        init();
    }

    private void startMusicService() {
        Intent service = new Intent(this, JBpmMusicService.class);
        if (!JBpmMusicService.IS_SERVICE_RUNNING) {
            service.setAction(MediaServiceConstants.ACTION.STARTFOREGROUND_ACTION);
            JBpmMusicService.IS_SERVICE_RUNNING = true;
        } else {
            service.setAction(MediaServiceConstants.ACTION.STOPFOREGROUND_ACTION);
            JBpmMusicService.IS_SERVICE_RUNNING = false;

        }
        startService(service);
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
        this.musicProvider = new LocalMusicProvider(this);
        this.mediaView.setMediaTotalTime(246);
        this.mediaView.setMediaListener(new MediaListener() {

            @Override
            public void onSkip(BpmMappedSong bpmMappedSong) {
                musicProvider.dislike(bpmMappedSong);
                bpmMappedSong = musicProvider.getNextSong(100f);
                mediaView.setCurrentSong(bpmMappedSong);
                infoView.setCurrentSong(bpmMappedSong);
            }

            @Override
            public void onPlayStatusChange(boolean isPlaying) {
                startMusicService();
            }
        });
    }
}
