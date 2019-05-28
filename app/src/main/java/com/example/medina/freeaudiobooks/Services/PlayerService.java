package com.example.medina.freeaudiobooks.Services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.RestrictTo;
import android.util.Log;

import java.io.IOException;

public class PlayerService extends Service implements MediaPlayer.OnCompletionListener {

    /**
     * This field is used for testing purposes.
     * It can be used by the tests only
     */
    @RestrictTo(RestrictTo.Scope.TESTS)
    private static boolean isServiceStarted;

    final String LOG_TAG = "myLogs";

    private MediaPlayer mediaPlayer;

    /**
     * creating new MediaPlayer instance
     */
    public void onCreate() {
        isServiceStarted = true;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    /**
     * this method get the intent sent by BookActivity
     * extracts extras from it, check if the media is
     * already playing, and starts playback
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        String link = intent.getStringExtra("link");
        try {
            Uri myUri = Uri.parse(link);
            if (!mediaPlayer.isPlaying()) {
                try {
                    mediaPlayer.setDataSource(this, myUri);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "ERROR!");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    /**
     * release mediaplayer when the secvice is
     * stopped
     */
    public void onDestroy(){
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        isServiceStarted = false;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        stopSelf();
    }

    public static boolean isServiceStarted() {
        return isServiceStarted;
    }
}
