package name.brodski.mathmemorizer.mathmemorizer;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by boris on 02.10.16.
 */

public enum Sound {
    OK(R.raw.ok),
    GET_READY(R.raw.get_ready),
    ERROR(R.raw.error);

    private Sound(int resource) {
        this.resource = resource;
    }
    private int resource;
    public void play(Context context) {
        MediaPlayer mp = MediaPlayer.create(context, resource);

        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();

                mp = MediaPlayer.create(context, resource);
            }

            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
