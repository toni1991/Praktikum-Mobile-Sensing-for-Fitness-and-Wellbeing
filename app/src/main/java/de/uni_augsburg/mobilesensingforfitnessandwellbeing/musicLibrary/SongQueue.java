package de.uni_augsburg.mobilesensingforfitnessandwellbeing.musicLibrary;

import android.util.Range;

import java.util.LinkedList;

/**
 * Created by Lukas B on 28.02.2018.
 */

public class SongQueue {

    private LinkedList<MusicTrack> queue;
    private Range<Float> bpmRange;

    public SongQueue(Range<Float> bpmRange) {
        this.bpmRange = bpmRange;
        this.queue = new LinkedList<>();
    }

    public void add(MusicTrack track) {
        this.queue.addLast(track);
    }

    public MusicTrack poll() {
        this.queue.addLast(this.queue.getFirst());
        return this.queue.poll();
    }

    public Range<Float> getBpmRange() {
        return this.bpmRange;
    }

    public boolean contains(MusicTrack t) { return this.queue.contains(t); }

    public void remove(MusicTrack t) { this.queue.remove(t); }

    public int size() { return this.queue.size(); }

}
