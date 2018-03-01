package de.uni_augsburg.mobilesensingforfitnessandwellbeing.media;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.io.File;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.musicLibrary.MusicTrack;

/**
 * Created by toni on 28.02.18.
 */

public abstract class  MusicProvider {

    protected final Context context;

    protected MusicProvider(Context context)
    {
        this.context = context;
    }

    public abstract MusicTrack getNextSong(float bpm);

    public abstract void dislike(MusicTrack bpmMappedSong);

    final protected File getMediaDirectory()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.context);
        return new File(settings.getString("pref_media_directory", ""));
    }

}
