package de.uni_augsburg.mobilesensingforfitnessandwellbeing.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.activity.JBpmActivity;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.BpmMappedSong;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MediaServiceConstants;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;

public class JBpmMusicService extends Service implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaPlayer = null;
    private BpmMappedSong currentSong;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case BroadcastAction.PLAYBACK.PLAY.ACTION:
                    if (mMediaPlayer != null) {
                        mMediaPlayer.start();
                    }
                    break;
                case BroadcastAction.PLAYBACK.PAUSE.ACTION:
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                    }
                    break;
                case BroadcastAction.PLAYBACK.SKIP.ACTION:
                    break;
                case BroadcastAction.FILE.NEXT_SONG.ACTION:
                    prepareNewSong(intent);
                    break;
                case BroadcastAction.FILE.REQUEST_CURRENT_SONG.ACTION:
                    Intent broadcast = new Intent();
                    broadcast.setAction(BroadcastAction.FILE.CURRENT_SONG.ACTION);
                    broadcast.putExtra(BroadcastAction.FILE.CURRENT_SONG.EXTRA_SONG, currentSong);
                    break;
            }

            Log.d("Service", intent.getAction() + intent.getStringExtra("testExtra"));
        }
    };

    private void prepareNewSong(Intent intent) {
        this.currentSong = intent.getParcelableExtra(BroadcastAction.FILE.NEXT_SONG.EXTRA_SONG);
        if (this.currentSong != null && !new File(this.currentSong.getAudioFile()).exists()) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        try {
            mMediaPlayer.setDataSource(
                    getApplicationContext(), Uri.parse(currentSong.getAudioFile())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.prepareAsync(); // prepare async to not block main thread
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
        registerBroadcastReceiver();
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.PLAYBACK.PLAY.ACTION);
        filter.addAction(BroadcastAction.PLAYBACK.PAUSE.ACTION);
        filter.addAction(BroadcastAction.PLAYBACK.SKIP.ACTION);
        filter.addAction(BroadcastAction.FILE.NEXT_SONG.ACTION);
        registerReceiver(this.broadcastReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();

        /*if (intent.getAction().equals(MediaServiceConstants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");

            Toast.makeText(this, "Service Started!", Toast.LENGTH_SHORT).show();

        } else if (intent.getAction().equals(MediaServiceConstants.ACTION.PREV_ACTION)) {
            Log.i(LOG_TAG, "Clicked Previous");

            Toast.makeText(this, "Clicked Previous!", Toast.LENGTH_SHORT)
                    .show();
        } else if (intent.getAction().equals(MediaServiceConstants.ACTION.PLAY_ACTION)) {
            Log.i(LOG_TAG, "Clicked Play");

            Toast.makeText(this, "Clicked Play!", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(MediaServiceConstants.ACTION.NEXT_ACTION)) {
            Log.i(LOG_TAG, "Clicked Next");

            Toast.makeText(this, "Clicked Next!", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(
                MediaServiceConstants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }*/

        return START_STICKY;
    }

    private void showNotification() {
        PendingIntent mainActivityPendingIntent = getMainActivityPendingIntent();

        Intent playIntent = new Intent(this, JBpmMusicService.class);
        playIntent.setAction(BroadcastAction.PLAYBACK.PLAY.ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, JBpmMusicService.class);
        nextIntent.setAction(BroadcastAction.PLAYBACK.SKIP.ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, MediaServiceConstants.NOTIFICATION.CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(mainActivityPendingIntent)
                .addAction(android.R.drawable.ic_media_pause, "Pause", pplayIntent)  // #1
                .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent)     // #2
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1 /* #1: pause button */))
                .setContentTitle("Wonderful music")
                .setContentText("My Awesome Band")
                .setOngoing(true)
                .build();

        startForeground(MediaServiceConstants.NOTIFICATION.FOREGROUND_SERVICE,
                notification);
    }

    private PendingIntent getMainActivityPendingIntent() {
        Intent mainActivityIntent = new Intent(this, JBpmActivity.class);
        //.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, mainActivityIntent, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Detroyed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
    }

    // Mediaplayer functions
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("mediaplayer", "started");
        mMediaPlayer.start();
    }
}