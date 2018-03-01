package de.uni_augsburg.mobilesensingforfitnessandwellbeing.util;

/**
 * Created by toni on 28.02.18.
 */

public interface BroadcastAction {
    interface PLAYBACK {
        interface SKIP {
            String ACTION = "de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.NEXT";
            String EXTRA_SONG = "song";
        }
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

    }

    interface FILE {
        interface REQUEST_NEXT_SONG {
            String ACTION = "de.uni_augsburg.mobilesensingforfitnessandwellbeing.util.REQUEST_NEXT_SONG";
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
