package tsi.ailton.android.jokenpo.controllers;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerController {
    private MediaPlayer mediaPlayer;
    private Context context;

    public MediaPlayerController(Context context) {
        this.context = context;
    }

    public void play(int mediaId) {
        releasePlayer();
        mediaPlayer = MediaPlayer.create(context, mediaId);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                releasePlayer();
            }
        });

        mediaPlayer.start();
    }
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
