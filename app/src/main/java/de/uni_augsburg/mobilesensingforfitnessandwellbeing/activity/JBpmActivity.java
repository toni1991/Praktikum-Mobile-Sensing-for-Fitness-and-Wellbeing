package de.uni_augsburg.mobilesensingforfitnessandwellbeing.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.security.Permission;
import java.util.HashSet;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.BpmMappedSong;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.LocalMusicProvider;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MediaListener;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MediaServiceConstants;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MusicProvider;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.service.JBpmMusicService;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.view.InfoView;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.view.MediaView;

public class JBpmActivity extends AppCompatActivity {

    private InfoView infoView;
    private MediaView mediaView;
    private MusicProvider musicProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_j_bpm);
        requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}
                );
        findViews();
        startMusicService();
        //registerBroadcastReceiver();
        init();
    }



    private void startMusicService() {
        Intent musicService = new Intent(this, JBpmMusicService.class);
        startService(musicService);
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
    }

    private void init() {
        this.musicProvider = new LocalMusicProvider(this);
        BpmMappedSong newSong = this.musicProvider.getNextSong(100);
        this.mediaView.setCurrentSong(newSong);
        broadCastNewSong(newSong);
        this.mediaView.setMediaListener(new MediaListener() {

            @Override
            public void onSkip() {
                musicProvider.dislike(null); // TODO: Get current song from music service
                BpmMappedSong bpmMappedSong = musicProvider.getNextSong(100f); // TODO: Get current desired BPM From bpm service
                broadCastNewSong(bpmMappedSong);
                mediaView.setCurrentSong(bpmMappedSong);
                infoView.setCurrentSong(bpmMappedSong);
            }

            @Override
            public void onPlayStatusChange(boolean isPlaying) {
                Intent broadcast = new Intent();
                if(isPlaying)
                {
                    broadcast.setAction(BroadcastAction.PLAYBACK.PLAY.ACTION);
                }
                else {
                    broadcast.setAction(BroadcastAction.PLAYBACK.PAUSE.ACTION);
                }
                sendBroadcast(broadcast);
            }

            @Override
            public void onSeekbarProgressChange(int progress) {
                Toast.makeText(JBpmActivity.this, ""+progress, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void broadCastNewSong(BpmMappedSong newSong)
    {
        Intent broadcast = new Intent();
        broadcast.setAction(BroadcastAction.FILE.NEXT_SONG.ACTION);
        broadcast.putExtra(BroadcastAction.FILE.NEXT_SONG.EXTRA_SONG, newSong);
        sendBroadcast(broadcast);
    }
}
