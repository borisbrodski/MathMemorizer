package name.brodski.mathmemorizer.mathmemorizer.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import java.util.Properties;

/**
 * Created by boris on 23.11.16.
 */
public class PersistentTimer {
    private Listener listener;

    public interface Listener {
        void onPersistentTimer(PersistentTimer timer, Properties bundle);
        void onPersistentTimerTick(PersistentTimer timer, Properties bundle, int index, int max);
    }

    // STATE
    @Save
    long autorestoreForMS;
    @Save
    Properties bundle;
    @Save
    long tickMS;
    @Save
    int tickCount;
    @Save
    int tickCurrent;

    // Temporary variables
    private final Handler handler = new Handler();
    private long expectedTimeMS;
    private boolean timerActive;
    private boolean paused;


    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (timerActive) {
                tickCurrent++;
                if (tickCurrent >= tickCount) {
                    timerActive = false;
                    listener.onPersistentTimer(PersistentTimer.this, bundle);
                } else {
                    listener.onPersistentTimerTick(PersistentTimer.this, bundle, tickCurrent, tickCount);
                    handler.postDelayed(timerRunnable, tickMS);
                }
            }
        }
    };

    public PersistentTimer(Listener listener) {
        this.listener = listener;
    }


//    private void clearHandler() {
//        handlerActive = false;
//        handler.removeCallbacksAndMessages(null);
//    }
    public void clearTimer() {
        this.handler.removeCallbacksAndMessages(null);
        this.autorestoreForMS = 0;
        this.tickCount = 0;
        this.tickMS = 0;
        this.tickCurrent = 0;
        this.timerActive = false;
        this.paused = false;
    }

    public void schedule(long durationMS, final Properties bundle) {
        schedule(durationMS, bundle, 1);
    }

    public void schedule(long durationMS, final Properties bundle, int tickCount) {
        clearTimer();

        this.tickCount = Math.max(1, tickCount);
        this.tickMS = durationMS / this.tickCount;
        this.tickCurrent = 0;

        this.bundle = bundle;
        expectedTimeMS = System.currentTimeMillis() + this.tickMS;
        timerActive = true;
        paused = false;
        listener.onPersistentTimerTick(this, bundle, tickCurrent, tickCount);
        handler.postDelayed(timerRunnable, this.tickMS);
    }

    public void onActivityPause() {
        if (!timerActive) {
            return;
        }
        handler.removeCallbacksAndMessages(null);
        autorestoreForMS = Math.max(1, expectedTimeMS - System.currentTimeMillis());
        paused = true;
    }

    public void onActivityResume() {
        if (!timerActive) {
            return;
        }
        if (!paused) {
            return;
        }
        paused = false;
        if (autorestoreForMS <= 0) {
            timerActive = false;
            return;
        }
        listener.onPersistentTimerTick(this, bundle, tickCurrent, tickCount);
        handler.postDelayed(timerRunnable, autorestoreForMS);
        autorestoreForMS = 0;
    }

    public void onSaveInstanceState(Bundle outState, String prefix) {
        if (!paused) {
            onActivityPause();
        }
        BundleTool.save(this, outState, prefix);
    }
    public void onRestoreInstanceState(Bundle savedInstanceState, String prefix) {
        BundleTool.load(this, savedInstanceState, prefix);
        timerActive = autorestoreForMS > 0;
        paused = true;
    }

    public boolean isActive() {
        return timerActive;
    }
}
