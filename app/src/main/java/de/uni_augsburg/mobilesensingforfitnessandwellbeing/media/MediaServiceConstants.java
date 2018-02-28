package de.uni_augsburg.mobilesensingforfitnessandwellbeing.media;

/**
 * Created by toni on 28.02.18.
 */

public class MediaServiceConstants {
    public interface ACTION {
        String MAIN_ACTION = "action.main";
        String INIT_ACTION = "action.init";
        String PREV_ACTION = "action.prev";
        String PLAY_ACTION = "action.play";
        String NEXT_ACTION = "action.next";
        String STARTFOREGROUND_ACTION = "action.startforeground";
        String STOPFOREGROUND_ACTION = "action.stopforeground";
    }

    public interface NOTIFICATION {
        int FOREGROUND_SERVICE = 1337;
        String CHANNEL_ID = "myChannelId";
    }
}
