package de.uni_augsburg.mobilesensingforfitnessandwellbeing.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.activity.JBpmActivity;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MediaServiceConstants;

public class JBpmMusicService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    public static boolean IS_SERVICE_RUNNING = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(MediaServiceConstants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            showNotification();
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
        }
        return START_STICKY;
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, JBpmActivity.class);
        notificationIntent.setAction(MediaServiceConstants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, JBpmMusicService.class);
        previousIntent.setAction(MediaServiceConstants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, JBpmMusicService.class);
        playIntent.setAction(MediaServiceConstants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, JBpmMusicService.class);
        nextIntent.setAction(MediaServiceConstants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                android.R.drawable.ic_menu_compass);

        Notification notification = new NotificationCompat.Builder(this, MediaServiceConstants.NOTIFICATION.CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(android.R.drawable.zoom_plate)
                .addAction(android.R.drawable.ic_media_previous, "Previous", ppreviousIntent) // #0
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
        Toast.makeText(this, "Service Detroyed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case if services are bound (Bound Services).
        return null;
    }
}