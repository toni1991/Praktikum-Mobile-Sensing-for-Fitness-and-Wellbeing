package de.uni_augsburg.mobilesensingforfitnessandwellbeing.media;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

/**
 * Created by toni on 28.02.18.
 */

public abstract class  MusicProvider {

    private final Context context;

    protected MusicProvider(Context context)
    {
        this.context = context;
    }

    public abstract BpmMappedSong getNextSong(float bpm);

    public abstract void dislike(BpmMappedSong bpmMappedSong);

    final protected Uri getMediaDirectory()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.context);
        return Uri.parse(settings.getString("pref_media_directory", ""));
    }

}
