package de.uni_augsburg.mobilesensingforfitnessandwellbeing.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.R;
import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.BpmMappedSong;

/**
 * Created by toni on 27.02.18.
 */

public class InfoView extends ConstraintLayout {

    public InfoView(Context context) {
        super(context);
        ConstraintLayout.inflate(context, R.layout.view_info, this);
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ConstraintLayout.inflate(context, R.layout.view_info, this);
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ConstraintLayout.inflate(context, R.layout.view_info, this);
    }

    public void setBpmTextView(float bpm)
    {
        String bpmAsString = String.format("%.2f", bpm) ;
        setBpmTextView(bpmAsString);
    }

    public void setBpmTextView(String bpmAsString)
    {
        ((TextView)findViewById(R.id.bpmTextView)).setText(bpmAsString);
    }

    public void setCurrentSong(BpmMappedSong bpmMappedSong) {
        setBpmTextView(bpmMappedSong.getBpm());
    }
}
