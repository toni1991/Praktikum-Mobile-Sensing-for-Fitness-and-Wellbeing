package de.uni_augsburg.mobilesensingforfitnessandwellbeing.media;

/**
 * Created by toni on 28.02.18.
 */

public interface MusicProvider {

    BpmMappedSong getNextSong(float bpm);

    void dislike(BpmMappedSong bpmMappedSong);

}
