package com.example.myapplication.play;

import static android.media.AudioManager.STREAM_MUSIC;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.media.ToneGenerator;

import com.example.myapplication.R;

public class SoundManager {
    private final SoundPool soundPool;


    private final int tickId;
    private final int correctId;
    private final int wrongId;
    private final int wrongLongId;
    private final int blastOffId;
    private final int finishId;
    private final int blastOffShortId;


    public SoundManager(Context context) {
        soundPool = new SoundPool.Builder()
                .setMaxStreams(1) // Max sounds playing at once
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                .build();

        tickId = soundPool.load(context, R.raw.tick, 1);
        blastOffId = soundPool.load(context, R.raw.blastoff, 1);
        correctId = soundPool.load(context, R.raw.correct, 1);
        wrongId = soundPool.load(context, R.raw.wrong, 1);
        wrongLongId = soundPool.load(context, R.raw.wrong_long, 1);
        finishId = soundPool.load(context, R.raw.finish, 1);
        blastOffShortId = soundPool.load(context, R.raw.blastoff_short, 1);
    }


    public void blastOff() {
        soundPool.play(blastOffId, 1f, 1f, 1, 0, 1);
    }

    public void blastOffShort() {
        soundPool.play(blastOffShortId, 1f, 1f, 1, 0, 1);
    }

    public void finish() {
        soundPool.play(finishId, 1f, 1f, 1, 0, 1);
    }

    public void gameOver() {
        soundPool.play(wrongLongId, 1f, 1f, 1, 0, 1);
    }

    public void tick() {
        soundPool.play(tickId, 1f, 1f, 1, 0, 1);
    }

    public void correct() {
        soundPool.play(correctId, 1f, 1f, 1, 0, 1);
    }

    public void wrong() {
        soundPool.play(wrongId, 1f, 1f, 1, 0, 1);
    }
}
