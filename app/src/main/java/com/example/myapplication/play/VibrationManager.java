package com.example.myapplication.play;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibrationManager {
    private final Vibrator vibrator;

    public VibrationManager(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void tick() {
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(VibrationEffect.EFFECT_TICK, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }



}
