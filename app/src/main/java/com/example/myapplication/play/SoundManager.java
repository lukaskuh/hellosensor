package com.example.myapplication.play;

import static android.media.AudioManager.STREAM_MUSIC;

import android.media.ToneGenerator;

public class SoundManager {
    ToneGenerator toneGenerator = new ToneGenerator(STREAM_MUSIC, ToneGenerator.MAX_VOLUME);

    public void playBeep() {
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_A, 200);
    }

}
