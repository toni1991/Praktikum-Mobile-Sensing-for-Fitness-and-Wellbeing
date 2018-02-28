package de.uni_augsburg.mobilesensingforfitnessandwellbeing.media;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;


public class LocalMusicProvider extends MusicProvider {

    private int songCount;
    private String[] genres;

    public LocalMusicProvider(Context context) {
        super(context);
        songCount = 0;
        genres = new String[]{"Alternative Music", "Blues", "Classical Music", "Country Music", "Dance Music", "Easy Listening", "Electronic Music", "European Music (Folk / Pop)", "Hip Hop / Rap", "Indie Pop", "Inspirational (incl. Gospel)", "Asian Pop (J-Pop, K-pop)", "Jazz", "Latin Music", "New Age", "Opera", "Pop (Popular music)", "R&B / Soul", "Reggae", "Rock", "Singer / Songwriter (inc. Folk)", "World Music / Beats"};
    }

    @Override
    public BpmMappedSong getNextSong(float bpm) {

        songCount++;

        File audioFile = getRandomAudioFile();
        bpm = getRandomBpm();
        String genre = getRandomGenre();

        return new BpmMappedSong(audioFile, bpm, genre);
    }

    @Override
    public void dislike(BpmMappedSong bpmMappedSong) {
        //Nothing to do here
    }

    private File getRandomAudioFile() {
        File mediaDirectory = getMediaDirectory();

        File[] suitableFiles = mediaDirectory.listFiles(
                (File directory, String fileName) -> fileName.endsWith(".mp3")
        );

        return suitableFiles[songCount % suitableFiles.length];
    }

    private float getRandomBpm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ThreadLocalRandom.current().nextInt(5000, 20000 + 1) / 100f;
        } else {
            return 80 + songCount;
        }
    }

    private String getRandomGenre() {
        return genres[songCount % genres.length];
    }

}
