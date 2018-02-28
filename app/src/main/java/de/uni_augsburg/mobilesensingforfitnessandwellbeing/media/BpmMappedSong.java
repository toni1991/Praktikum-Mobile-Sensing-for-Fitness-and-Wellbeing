package de.uni_augsburg.mobilesensingforfitnessandwellbeing.media;

import java.io.File;

public class BpmMappedSong {
    private final File audioFile;
    private final float bpm;
    private final String genre;

    public BpmMappedSong(File audioFile, float bpm, String genre){
        this.audioFile = audioFile;
        this.bpm = bpm;
        this.genre = genre;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public float getBpm() {
        return bpm;
    }

    public String getGenre() {
        return genre;
    }
}
