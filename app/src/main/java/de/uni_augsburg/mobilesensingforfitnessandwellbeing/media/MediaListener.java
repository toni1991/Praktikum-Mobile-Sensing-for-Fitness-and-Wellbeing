package de.uni_augsburg.mobilesensingforfitnessandwellbeing.media;

/**
 * Created by toni on 28.02.18.
 */

public interface MediaListener {

    void onSkip(BpmMappedSong bpmMappedSong);

    void onPlayStatusChange(boolean isPlaying);

}
