package de.uni_augsburg.mobilesensingforfitnessandwellbeing.util;

/**
 * Created by toni on 28.02.18.
 */

public interface BroadcastAction {
    interface PLAYBACK {
        interface PLAY {
            String ACTION = "de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.PLAY";
        }
        interface PAUSE {
            String ACTION = "de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.PAUSE";
        }
        interface PROGRESS {
            String ACTION = "de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.PROGRESS";
            String EXTRA_PROGRESS = "progress";
        }
        interface SET_PROGRESS {
            String ACTION = "de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.SET_PROGRESS";
            String EXTRA_PROGRESS = "progress";
        }
        interface PLAYBACK_TOGGLED {
            String ACTION = "de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.PLAYBACK_TOGGLED";
            String EXTRA_ISPLAYING = "isPlaying";
        }
    }

    interface FILE {
        interface REQUEST_NEXT_SONG {
            String ACTION = "de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.REQUEST_NEXT_SONG";
            String EXTRA_DISLIKE = "dislike";
        }
        interface NEXT_SONG {
            String ACTION = "de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.NEXT_SONG";
            String EXTRA_SONG = "song";
        }
    }

    interface VALUES {
        interface VALUEBROADCAST {
            String ACTION = "de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.VALUEBROADCAST";
            String EXTRA_SENSORNAME = "sensorname";
            String EXTRA_VALUENAME = "valuename";
            String EXTRA_VALUE = "extraValue";
        }
        interface BPMESTIMATION {
            String ACTION = "de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.BPMESTIMATION";
            String EXTRA_VALUEBPM = "valuename";
        }
    }
}
