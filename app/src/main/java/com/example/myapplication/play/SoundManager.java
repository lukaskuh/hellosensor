package com.example.myapplication.play;

import static android.media.AudioManager.STREAM_MUSIC;

import android.media.ToneGenerator;

public class SoundManager {
    ToneGenerator toneGenerator = new ToneGenerator(STREAM_MUSIC, ToneGenerator.MAX_VOLUME);

    public void playBeepLow() {
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_A, 200);
    }

    public void playBeepHigh() {
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_C, 200);
    }

    public void playBlastOff() {
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_D, 600);
    }

    public void playFinish() {
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_A, 600);
    }

    public void playPositiveFeedback() {
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_A, 200);
    }

    public void playNegativeFeedback() {
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_C, 200);
    }

}
