package de.uni_augsburg.mobilesensingforfitnessandwellbeing.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.activity.JBpmActivity;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.BpmMappedSong;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MediaServiceConstants;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;

public class JBpmMusicService extends Service
        implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnSeekCompleteListener {

    private MediaPlayer mediaPlayer = null;
    private BpmMappedSong currentSong;
    private CountDownTimer countdownTimer;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("JBpmMusicService", "Action: " + intent.getAction());

            String s = intent.getAction();
            if (s.equals(BroadcastAction.PLAYBACK.PLAY.ACTION)) {
                playSongIfPossible();
            } else if (s.equals(BroadcastAction.PLAYBACK.PAUSE.ACTION)) {
                pauseIfNotNullAndPlaying();
                stopCountdownTimerIfRunning();

            } else if (s.equals(BroadcastAction.PLAYBACK.SKIP.ACTION)) {
            } else if (s.equals(BroadcastAction.PLAYBACK.SET_PROGRESS.ACTION)) {
                int progress = intent.getIntExtra(BroadcastAction.PLAYBACK.SET_PROGRESS.EXTRA_PROGRESS, 0);
                setMediaProgress(progress);

            } else if (s.equals(BroadcastAction.FILE.NEXT_SONG.ACTION)) {
                stopCountdownTimerIfRunning();
                currentSong = intent.getParcelableExtra(BroadcastAction.FILE.NEXT_SONG.EXTRA_SONG);
                prepareMediaPlayer();
                showNotification();
            } else if (s.equals(BroadcastAction.FILE.REQUEST_CURRENT_SONG.ACTION)) {
                broadCastCurrentSong();
            }
        }
    };

    private void setMediaProgress(int progress) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(progress * 1000);
            mediaPlayer.start();
        }
    }

    private void stopCountdownTimerIfRunning() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
    }

    private void pauseIfNotNullAndPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void playSongIfPossible() {
        //TODO Prepare if not done!
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        startProgressBroadcastCountdown();
    }

    private void startProgressBroadcastCountdown() {

        this.countdownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {

            public void onTick(long millisUntilFinished) {
                int position = mediaPlayer.getCurrentPosition() / 1000;
                Intent broadcast = new Intent();
                broadcast.setAction(BroadcastAction.PLAYBACK.PROGRESS.ACTION);
                broadcast.putExtra(
                        BroadcastAction.PLAYBACK.PROGRESS.EXTRA_PROGRESS,
                        position
                );
            }

            public void onFinish() {
                if (mediaPlayer.isPlaying())
                    start();
            }

        }.start();
    }

    private void broadCastCurrentSong() {
        Intent broadcast = new Intent();
        broadcast.setAction(BroadcastAction.FILE.CURRENT_SONG.ACTION);
        broadcast.putExtra(BroadcastAction.FILE.CURRENT_SONG.EXTRA_SONG, this.currentSong);
    }

    private void prepareMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();

        if (this.currentSong != null && new File(this.currentSong.getAudioFile()).exists()) {
            try {
                mediaPlayer.setDataSource(
                        getApplicationContext(), Uri.fromFile(new File(currentSong.getAudioFile()))
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.prepareAsync(); // prepare async to not block main thread
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
        registerBroadcastReceiver();
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.PLAYBACK.PLAY.ACTION);
        filter.addAction(BroadcastAction.PLAYBACK.PAUSE.ACTION);
        filter.addAction(BroadcastAction.PLAYBACK.SKIP.ACTION);
        filter.addAction(BroadcastAction.PLAYBACK.SET_PROGRESS.ACTION);
        filter.addAction(BroadcastAction.FILE.NEXT_SONG.ACTION);
        registerReceiver(this.broadcastReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        return START_STICKY;
    }

    private void showNotification() {
        PendingIntent mainActivityPendingIntent = getMainActivityPendingIntent();

        String artist = "";
        String title = "";

        if(currentSong != null && new File(currentSong.getAudioFile()).exists()) {
            MediaMetadataRetriever mmR = new MediaMetadataRetriever();
            mmR.setDataSource(currentSong.getAudioFile());
            artist = mmR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            title = mmR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }

        Notification notification = new NotificationCompat.Builder(this, MediaServiceConstants.NOTIFICATION.CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(mainActivityPendingIntent)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .setContentTitle(artist)
                .setContentText(title)
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
        if (mediaPlayer != null)
            mediaPlayer.release();
        mediaPlayer = null;
        unregisterReceiver(this.broadcastReceiver);
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
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        broadCastRequestNextSong();
    }

    private void broadCastRequestNextSong() {
        Intent broadcast = new Intent();
        broadcast.setAction(BroadcastAction.FILE.REQUEST_NEXT_SONG.ACTION);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mp.start();
    }
}