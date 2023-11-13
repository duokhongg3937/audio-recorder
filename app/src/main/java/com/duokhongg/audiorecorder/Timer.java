package com.duokhongg.audiorecorder;


import android.os.Handler;
import android.os.Looper;

public class Timer {
    interface OnTimerClickListener {
        void onTimerTick(String value);
    }


    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private long duration = 1L;
    private long delay = 100L;
    private OnTimerClickListener onTimerClickListener;

    Timer() {
        runnable = new Runnable() {
            @Override
            public void run() {
                duration += delay;
                handler.postDelayed(runnable, delay);
                onTimerClickListener.onTimerTick(String.valueOf(duration));
            }
        };
    }

    private void start() {
        handler.postDelayed(runnable, delay);
    }

    private void pause() {
        handler.removeCallbacks(runnable);
    }

    private void stop() {
        handler.removeCallbacks(runnable);
        duration = 0L;
    }

    public void setOnTimerClickListener(OnTimerClickListener listener) {
        this.onTimerClickListener = listener;
    }






}
