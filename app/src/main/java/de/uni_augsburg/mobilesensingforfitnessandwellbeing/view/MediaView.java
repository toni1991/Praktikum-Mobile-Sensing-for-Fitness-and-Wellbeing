package de.uni_augsburg.mobilesensingforfitnessandwellbeing.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;

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

    private boolean isPlaying = false;

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
        initMediaButtonListener();
        initSeekBarListener();
    }

    private void initMediaButtonListener() {
        this.mediaPlayPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying) {
                    pausePlaying();
                }
                else {
                    startPlaying();
                }
                isPlaying = !isPlaying;
            }
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
                setMediaCurrentTime(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setMediaTotalTime(int totalTime)
    {
        this.mediaProgressBar.setMax(totalTime);
        setTimeTextView(mediaTotalTimeTextView, totalTime);
    }

    public void setMediaCurrentTime(int currentTime)
    {
        setTimeTextView(mediaCurrentTimeTextView, currentTime);
    }

    private void setTimeTextView(TextView timeTextView, int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        timeTextView.setText(minutes+":"+String.format("%02d",seconds));
    }
}
