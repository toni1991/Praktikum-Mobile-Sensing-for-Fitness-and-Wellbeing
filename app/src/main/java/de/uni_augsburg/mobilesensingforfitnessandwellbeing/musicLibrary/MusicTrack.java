package de.uni_augsburg.mobilesensingforfitnessandwellbeing.musicLibrary;

import java.io.File;

/**
 * Created by Lukas B on 27.02.2018.
 */

public class MusicTrack {

    private String path;
    private String name;
    private float bpm;
    private String genre;
    private File audioFile;

    MusicTrack() {

    }

    public void setName(String name) {
        this.name = name;
    }

    void setBPM(float bpm) {
        this.bpm = bpm;
    }

    void setPath(String path) {
        this.path = path;
    }

    void setGenre(String genre) {
        this.genre = genre;
    }

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    public String getName() {
        return this.name;
    }

    public float getBpm() {
        return this.bpm;
    }

    public String getPath() {
        return this.path;
    }

    public String getGenre() {
        return this.genre;
    }

    public File getAudioFile() { return this.audioFile; }

}
