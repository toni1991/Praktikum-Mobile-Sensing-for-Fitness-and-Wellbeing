package de.uni_augsburg.mobilesensingforfitnessandwellbeing.musicLibrary;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lukas B on 27.02.2018.
 */

public class MusicTrack implements Parcelable {

    public static final Creator<MusicTrack> CREATOR = new Creator<MusicTrack>() {
        @Override
        public MusicTrack createFromParcel(Parcel in) {
            return new MusicTrack(in);
        }

        @Override
        public MusicTrack[] newArray(int size) {
            return new MusicTrack[size];
        }
    };

    private String path;
    private String name;
    private float bpm;
    private String genre;

    public MusicTrack(){

    }

    protected MusicTrack(Parcel in) {
        path = in.readString();
        name = in.readString();
        bpm = in.readFloat();
        genre = in.readString();
    }

    public void setBPM(float bpm) {
        this.bpm = bpm;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBpm() {
        return this.bpm;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeFloat(bpm);
        dest.writeString(genre);
    }
}
