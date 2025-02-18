package com.example.myapplication.play;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationManager {
    private final Vibrator vibrator;

    public VibrationManager(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void tick() {
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    public void correct() {
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    public void wrong() {
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    public void goal() {
        if (vibrator != null) {
            long[] pattern = {0, 200, 100, 300}; // Short pause, medium vibrate, short pause, longer vibrate
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
        }
    }



}
