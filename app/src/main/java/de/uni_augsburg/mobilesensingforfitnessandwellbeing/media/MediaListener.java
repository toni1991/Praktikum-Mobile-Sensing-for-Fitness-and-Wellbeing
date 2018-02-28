package de.uni_augsburg.mobilesensingforfitnessandwellbeing.media;

/**
 * Created by toni on 28.02.18.
 */

public interface MediaListener {

    void onSkip();

    void onPlayStatusChange(boolean isPlaying);

    void onSeekbarProgressChange(int progress);
}
