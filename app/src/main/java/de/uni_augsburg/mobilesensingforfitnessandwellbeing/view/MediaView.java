package de.uni_augsburg.mobilesensingforfitnessandwellbeing.view;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.BpmMappedSong;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MediaListener;

/**
 * Created by toni on 27.02.18.
 */

public class MediaView extends ConstraintLayout {

    private SeekBar mediaProgressBar;
    private TextView mediaCurrentTimeTextView;
    private TextView mediaTotalTimeTextView;
    private TextView mediaTitleTextView;
    private ImageButton mediaPlayPauseButton;
    private ImageButton mediaSkipButton;

    private BpmMappedSong currentSong;

    private boolean isPlaying = false;
    private MediaListener mediaListener;

    public MediaView(Context context) {
        super(context);
        init(context);
    }

    public MediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MediaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflateLayout(context);
        findViews();
        initMediaPlayPauseButtonListener();
        initMediaSkipButtonListener();
        initSeekBarListener();
    }

    private void initMediaSkipButtonListener() {
        this.mediaSkipButton.setOnClickListener((View v) -> this.mediaListener.onSkip());
    }

    private void initMediaPlayPauseButtonListener() {
        this.mediaPlayPauseButton.setOnClickListener((View v) -> {
            if (isPlaying) {
                pausePlaying();
            } else {
                startPlaying();
            }
            isPlaying = !isPlaying;
            this.mediaListener.onPlayStatusChange(isPlaying);
        });
    }

    private void pausePlaying() {
        mediaPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void startPlaying() {
        mediaPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
    }

    private void inflateLayout(Context context) {
        ConstraintLayout.inflate(context, R.layout.view_media_control, this);
    }

    private void findViews() {
        this.mediaPlayPauseButton = findViewById(R.id.mediaPlayPauseButton);
        this.mediaSkipButton = findViewById(R.id.mediaSkipButton);
        this.mediaCurrentTimeTextView = findViewById(R.id.mediaCurrentTimeTextView);
        this.mediaTotalTimeTextView = findViewById(R.id.mediaTotalTimeTextView);
        this.mediaTitleTextView = findViewById(R.id.mediaTitleTextView);
        this.mediaProgressBar = findViewById(R.id.mediaProgressBar);
    }

    private void initSeekBarListener() {
        this.mediaProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setMediaCurrentTime(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaListener.onSeekbarProgressChange(seekBar.getProgress());
            }
        });
    }

    public void setMediaTotalTime(int totalTime) {
        this.mediaProgressBar.setMax(totalTime);
        setTimeTextView(mediaTotalTimeTextView, totalTime);
    }

    private void setMediaTotalTime(File audioFile) {
        MediaMetadataRetriever mmR = new MediaMetadataRetriever();
        mmR.setDataSource(audioFile.getAbsolutePath());
        String duration = mmR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        setMediaTotalTime(Integer.valueOf(duration) / 1000);
    }

    public void setMediaCurrentTime(int currentTime) {
        this.mediaProgressBar.setProgress(currentTime);
        setTimeTextView(mediaCurrentTimeTextView, currentTime);
    }

    private void setTimeTextView(TextView timeTextView, int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        timeTextView.setText(minutes + ":" + String.format("%02d", seconds));
    }

    public void setCurrentSong(BpmMappedSong currentSong) {
        this.currentSong = currentSong;
        setMediaTitleOfCurrentSong();
        setMediaTotalTimeOfCurrentSong();
        setMediaCurrentTime(0); // Time after song change is always 0
    }

    private void setMediaTitleOfCurrentSong() {
        setMediaTitle(this.currentSong.getAudioFile());
    }

    private void setMediaTitle(File audioFile) {
        MediaMetadataRetriever mmR = new MediaMetadataRetriever();
        mmR.setDataSource(audioFile.getAbsolutePath());
        String artist = mmR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = mmR.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        String artistAndMaybeTitle = artist + (title.isEmpty() ? "" :  " - "+title);

        setMediaTitle(artistAndMaybeTitle.isEmpty() ? audioFile.getName() : artistAndMaybeTitle);
    }

    private void setMediaTotalTimeOfCurrentSong() {
        setMediaTotalTime(this.currentSong.getAudioFile());
    }

    public void setMediaListener(MediaListener mediaListener) {
        this.mediaListener = mediaListener;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitleTextView.setText(mediaTitle);
    }
}
