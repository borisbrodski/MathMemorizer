package name.brodski.mathmemorizer.mathmemorizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import static android.R.attr.resource;

/**
 * Created by boris on 02.10.16.
 */

public enum Sound {
    OK("notifications_ringtone_correct_answer"),
    ERROR("notifications_ringtone_wrong_answer"),
    GET_READY("notifications_ringtone_starting_alarm");

    private MediaPlayer mp;

    Sound(String key) {
        this.key = key;
    }
    private String key;

    public void play(Context context, final Runnable ... runnablesAfterSoundPlayed) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String uriString = preferences.getString(key, "");
        if (uriString == null || uriString.trim().length() == 0) {
            runRunnables(runnablesAfterSoundPlayed);
            return;
        }
        Uri uri = Uri.parse(uriString);
        try {
            if (mp != null && mp.isPlaying()) {
                mp.stop();
                mp.release();

            }
            mp = MediaPlayer.create(context, uri);
            mp.setLooping(false);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer pMp) {
                    mp.release();
                    mp = null;
                    runRunnables(runnablesAfterSoundPlayed);
                }
            });
            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mp.stop();
                }
            });
        } catch (Exception e) {
            if (mp != null) {
                mp.release();
                mp = null;
            }

            e.printStackTrace();
            runRunnables(runnablesAfterSoundPlayed);
            return;
        }
        mp.start();
    }

    private void runRunnables(Runnable[] runnablesAfterSoundPlayed) {
        for (Runnable runnable: runnablesAfterSoundPlayed) {
            runnable.run();
        }
    }
}
