package com.example.myapplication.play;


import android.widget.TextView;

import com.example.myapplication.PlayActivity;


public class GameController {
    public final PlayActivity playActivity;
    public final SensorInterpreter sensorInterpreter = new SensorInterpreter();
    public final SoundManager soundManager = new SoundManager();
    public final VibrationManager vibrationManager;

    private Level currentLevel;

    private int longestStreak;

    public final TextView output;

    private int roundCount = 0;
    private Difficulty currentDifficulty = Difficulty.EASY;

    private int nextDifficultyIncrement = 1;
    private float stepsDelay = 3 * 1000;
    private int stepsAmount = 4;
    private float reversedChance = 0.0f;



    public GameController(PlayActivity playActivity, TextView output) {
        this.playActivity = playActivity;
        this.output = output;
        this.vibrationManager = new VibrationManager(playActivity);
    }

    public void start() {
        nextLevel();
    }

    public void onResume() {

    }

    public void onPause() {
        //handler.removeCallbacks(runnable);
    }

    public void nextLevel() {
        roundCount++;

        if (nextDifficultyIncrement <= 0) {
            increaseDifficulty();
            playActivity.viewManager.setDifficulty(currentDifficulty);
        }

        currentLevel = new Level(this, stepsAmount, (int) stepsDelay, reversedChance);
        currentLevel.start();
    }

    private void increaseDifficulty() {
        switch (currentDifficulty) {
            case EASY:
                currentDifficulty = Difficulty.MEDIUM;
                stepsAmount = 4;
                stepsDelay = 2000;
                nextDifficultyIncrement = 2;
                reversedChance = 0.1f;
                break;
            case MEDIUM:
                currentDifficulty = Difficulty.HARD;
                stepsAmount = 5;
                nextDifficultyIncrement = 3;
                reversedChance = 0.2f;
                stepsDelay = 1700;
                break;
            case HARD:
                currentDifficulty = Difficulty.EXTREME;
                stepsAmount= 8;
                reversedChance = 0.3f;
                nextDifficultyIncrement = 1000;
                stepsDelay = 1000;
                break;
        }
    }

    public void levelEnded(Rating rating, int streak) {
        longestStreak = Math.max(longestStreak, streak);

        playActivity.viewManager.betweenView(
                rating,
                longestStreak,
                roundCount
        );
        nextDifficultyIncrement--;
    }






}
