package de.uni_augsburg.mobilesensingforfitnessandwellbeing.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.musicLibrary.MusicTrack;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BroadcastAction;

/**
 * Created by toni on 27.02.18.
 */

public class InfoView extends ConstraintLayout {

    private TextView bpmTextViewCurrentSong;
    private TextView bpmTextViewEstimated;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            switch (action) {
                case BroadcastAction.FILE.NEXT_SONG.ACTION:
                    MusicTrack musicTrack = intent.getParcelableExtra(BroadcastAction.FILE.NEXT_SONG.EXTRA_SONG);
                    setCurrentSongBpm(musicTrack != null ? musicTrack.getBpm() : 0.0f);
                    break;
                case BroadcastAction.VALUES.BPMESTIMATION.ACTION:
                    float bpm = intent.getFloatExtra(BroadcastAction.VALUES.BPMESTIMATION.EXTRA_VALUEBPM, 0.0f);
                    setEstimatedBpm(bpm);
                    break;

            }
        }
    };

    public InfoView(Context context) {
        super(context);
        init(context);
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ConstraintLayout.inflate(context, R.layout.view_info, this);
        findViews();
    }

    private void findViews() {
        bpmTextViewCurrentSong = findViewById(R.id.bpmTextViewCurrentSong);
        bpmTextViewEstimated = findViewById(R.id.bpmTextViewEstimated);
    }

    private void setCurrentSongBpm(float bpm) {
        bpmTextViewCurrentSong.setText(String.format("%.2f", bpm));
    }

    private void setEstimatedBpm(float bpm) {
        bpmTextViewEstimated.setText(String.format("%.2f", bpm));
    }

    public BroadcastReceiver getBroadcastReceiver() {
        return this.broadcastReceiver;
    }

    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastAction.FILE.NEXT_SONG.ACTION);
        filter.addAction(BroadcastAction.VALUES.BPMESTIMATION.ACTION);
        return filter;
    }


}
