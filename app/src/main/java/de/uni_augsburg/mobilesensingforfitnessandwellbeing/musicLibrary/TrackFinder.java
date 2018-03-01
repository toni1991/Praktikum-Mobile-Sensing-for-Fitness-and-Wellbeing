package de.uni_augsburg.mobilesensingforfitnessandwellbeing.musicLibrary;

import android.content.Context;
import android.os.Environment;
import android.util.Range;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import de.uni_augsburg.mobilesensingforfitnessandwellbeing.media.MusicProvider;

/**
 * Created by Lukas B on 27.02.2018.
 */

public class TrackFinder extends MusicProvider{

    private File trackDirectory;
    private ArrayList<MusicTrack> tracks = new ArrayList<>();
    private float minBPM;
    private float maxBPM;
    private ArrayList<SongQueue> hiPrioQueues;
    private ArrayList<SongQueue> loPrioQueues;

    public TrackFinder(Context context) {
        super(context);
        trackDirectory = getMediaDirectory();
        minBPM = Float.MAX_VALUE;
        maxBPM = Float.MIN_VALUE;
        loadTrackInformation();
        this.hiPrioQueues = createHiPrioQueues();
        this.loPrioQueues = createLoPrioQueues();
    }

    @Override
    public MusicTrack getNextSong(float bpm) {

        MusicTrack retval;
        final double n = 5.0;
        SongQueue hiPrio = getSuitableQueue(bpm, this.hiPrioQueues);
        SongQueue loPrio = getSuitableQueue(bpm, this.loPrioQueues);
        double random = Math.random();


        if (loPrio != null && hiPrio != null) {
            //If less than n songs are left in high priority queue and low priority queue is not empty poll song from low priority queue with prob. 1/n-1
            if ((hiPrio.size() < n && loPrio.size() > 0 && random <= 1 / (n - 1)) || (hiPrio.size() == 0)) {
                retval = loPrio.poll();
            } else {
                retval = hiPrio.poll();
            }
        }
        else {
            retval = new MusicTrack();
        }

        return retval;

    }

    @Override
    public void dislike(MusicTrack track) {

        boolean removedFromHiPrio = false;

        //search for track in high priority queues
        for(SongQueue q : this.hiPrioQueues) {
            if(q.contains(track)) {
                q.remove(track);
                removedFromHiPrio = true;
            }
        }

        //add track to correspondent low priority queue
        if(removedFromHiPrio) {
            for(SongQueue q : this.loPrioQueues) {
                if(q.getBpmRange().contains(track.getBpm())) {
                    q.add(track);
                }
            }
        }
    }

    private void loadTrackInformation() {

        int i = 0;
        BufferedReader br;
        MusicTrack track;
        File[] directoryListing;
        directoryListing = trackDirectory.listFiles();
        float tempBPM;

         if(directoryListing != null) {
            Arrays.sort(directoryListing);
            while(i < directoryListing.length) {
                try {
                    //create new musictrack object
                    track = new MusicTrack();

                    //extract BPM
                    tempBPM = readBpmFromFile(directoryListing[i]);
                    minBPM = Math.min(minBPM, tempBPM);
                    maxBPM = Math.max(maxBPM, tempBPM);
                    track.setBPM(readBpmFromFile(directoryListing[i]));
                    i++;

                    //extract .genre File of track (i+1)/2 - genre
                    track.setGenre(readGenreFromFile(directoryListing[i]));
                    i++;

                    //store filename and path in MusicTrack object
                    track.setName(directoryListing[i].getName());
                    track.setPath(directoryListing[i].getPath());
                    i++;

                    //add track to collection
                    tracks.add(track);
                }
                catch(Exception e) {
                    Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        else {
            Toast.makeText(context, "The specified directory could not be extracted.", Toast.LENGTH_LONG).show();
        }
    }

    private SongQueue getSuitableQueue(float bpm, ArrayList<SongQueue> queueSet) {

        SongQueue retval = null;
        boolean suitableQueueFound = false;
        int numQueues = queueSet.size();
        float diff = Float.MAX_VALUE;
        float oldDiff = 0;
        int index = 0;
        Float[] queueLowers = new Float[numQueues];

        //select queue with bpm range containing current track's bpm
        if(queueSet != null) {
            for (SongQueue q : queueSet) {
                if (q.getBpmRange().contains(bpm)) {
                    retval = q;
                    suitableQueueFound = true;
                }
            }
        }

        //select nearest queue if no suitable queue has been found
        if(!suitableQueueFound) {
            for(int i=0; i<numQueues; i++) {
                queueLowers[i] = queueSet.get(i).getBpmRange().getLower();
                diff = Math.min(diff, Math.abs(bpm - queueLowers[i]));
                if(diff != oldDiff) {
                    index = i;
                }
                oldDiff = diff;
            }
            if(index <= queueSet.size()) {
                retval = queueSet.get(index);
            }
        }

        return retval;
    }

    private ArrayList<SongQueue> createHiPrioQueues() {

        ArrayList<SongQueue> retval = new ArrayList<>();
        SongQueue tmpQueue;
        float lower;
        float upper;
        float bpmCut;


        for(MusicTrack t: tracks) {

            lower = (float) Math.floor(t.getBpm()/10.0) * 10;
            upper = lower + 9;
            bpmCut = (float) Math.floor(t.getBpm());

            boolean trackAdded = false;

            for(SongQueue q: retval) {
                if(q.getBpmRange().contains(bpmCut)) {
                    q.add(t);
                    trackAdded = true;
                    break;
                }
            }

            if(!trackAdded) {
                tmpQueue = new SongQueue(new Range<>(lower, upper));
                tmpQueue.add(t);
                retval.add(tmpQueue);
            }

        }

        return retval;
    }

    //PRE: createHiPrioQueues() has to be called first
    private ArrayList<SongQueue> createLoPrioQueues() {

        ArrayList<SongQueue> retval = new ArrayList<>();

        for(SongQueue q : this.hiPrioQueues) {
            retval.add(new SongQueue(q.getBpmRange()));
        }

        return retval;
    }

    private float readBpmFromFile(File file) {

        BufferedReader br;
        String bpm_string = null;

        try {
            br = new BufferedReader(new FileReader(file));
            bpm_string = br.readLine();
        }
        catch(FileNotFoundException e) {
            Toast.makeText(context, "FileNotFoundException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        catch(IOException e) {
            Toast.makeText(context, "IOException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return Float.parseFloat(bpm_string);
    }

    private String readGenreFromFile(File file) {

        String retval = null;
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(file));
            retval = br.readLine();
        }
        catch(FileNotFoundException e) {
            Toast.makeText(context, "FileNotFoundException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        catch(IOException e) {
            Toast.makeText(context, "IOException: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return retval;
    }
}
