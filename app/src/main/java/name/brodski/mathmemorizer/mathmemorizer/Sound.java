package name.brodski.mathmemorizer.mathmemorizer;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.Random;

/**
 * Created by boris on 02.10.16.
 */

public enum Sound {
    OK(R.raw.ok3),
    GET_READY(R.raw.get_ready),
    ERROR(R.raw.error, R.raw.error2);

    private MediaPlayer mp;

    private Sound(int ... resources) {
        this.resources = resources;
    }
    private int[] resources;
    private static Random RANDOM = new Random();
    public void play(Context context, final Runnable ... runnables) {
        int resource = resources[RANDOM.nextInt(resources.length)];
        if (mp == null) {
            mp = MediaPlayer.create(context, resource);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mpLocal) {
                    mp.release();
                    mp = null;
                }
            });
        }
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();

                mp = MediaPlayer.create(context, resource);
            }

            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    for (Runnable runnable: runnables) {
                        runnable.run();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
