package de.uni_augsburg.mobilesensingforfitnessandwellbeing.media;

import android.os.Parcel;
import android.os.Parcelable;

public class BpmMappedSong implements Parcelable {

    private final String audioFile;
    private final float bpm;
    private final String genre;

    public BpmMappedSong(String audioFile, float bpm, String genre) {
        this.audioFile = audioFile;
        this.bpm = bpm;
        this.genre = genre;
    }

    protected BpmMappedSong(Parcel in) {
        audioFile = in.readString();
        bpm = in.readFloat();
        genre = in.readString();
    }

    public static final Creator<BpmMappedSong> CREATOR = new Creator<BpmMappedSong>() {
        @Override
        public BpmMappedSong createFromParcel(Parcel in) {
            return new BpmMappedSong(in);
        }

        @Override
        public BpmMappedSong[] newArray(int size) {
            return new BpmMappedSong[size];
        }
    };

    public String getAudioFile() {
        return audioFile;
    }

    public float getBpm() {
        return bpm;
    }

    public String getGenre() {
        return genre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(audioFile);
        dest.writeFloat(bpm);
        dest.writeString(genre);
    }
}
