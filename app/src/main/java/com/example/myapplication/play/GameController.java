package com.example.myapplication.play;


import android.widget.TextView;

import com.example.myapplication.PlayActivity;


public class GameController {
    public final PlayActivity playActivity;
    public final SensorInterpreter sensorInterpreter = new SensorInterpreter();
    public final SoundManager soundManager;
    public final VibrationManager vibrationManager;

    private Level currentLevel;
    private int livesLeft = 3;

    private int longestStreak;

    private int roundCount = 0;
    private Difficulty currentDifficulty = Difficulty.EASY;


    private static final int NEXT_DIFFICULTY_INCREMENT = 1;
    private static final int INSTRUCTIONS_STEP_TIME = 2000;
    private static final int PLAY_STEP_TIME = 7000;
    private static final int STEPS_AMOUNT = 3;
    private static final float REVERSED_CHANCE = 0.0f;


    private int nextDifficultyIncrement = NEXT_DIFFICULTY_INCREMENT;
    private float instructionsStepTime = INSTRUCTIONS_STEP_TIME;
    private float playStepTime = PLAY_STEP_TIME;
    private int stepsAmount = STEPS_AMOUNT;
    private float reversedChance = REVERSED_CHANCE;



    public GameController(PlayActivity playActivity) {
        this.playActivity = playActivity;
        this.vibrationManager = new VibrationManager(playActivity);
        this.soundManager = new SoundManager(playActivity);
    }

    public void start() {
        init();
        nextLevel();
    }



    public void onResume() {

    }

    public void onPause() {
        //handler.removeCallbacks(runnable);
    }

    private void init() {
        longestStreak = 0;
        roundCount = 0;
        nextDifficultyIncrement = NEXT_DIFFICULTY_INCREMENT;
        currentDifficulty = Difficulty.EASY;
        instructionsStepTime = INSTRUCTIONS_STEP_TIME;
        playStepTime = PLAY_STEP_TIME;
        stepsAmount = STEPS_AMOUNT;
        reversedChance = REVERSED_CHANCE;

    }

    public void nextLevel() {
        roundCount++;

        if (nextDifficultyIncrement <= 0) {
            increaseDifficulty();
            playActivity.viewManager.setDifficulty(currentDifficulty);
        }

        currentLevel = new Level(this, stepsAmount, (int) instructionsStepTime, (int) playStepTime, reversedChance);
        currentLevel.start();
    }

    private void increaseDifficulty() {
        switch (currentDifficulty) {
            case EASY:
                currentDifficulty = Difficulty.MEDIUM;
                stepsAmount = 4;
                instructionsStepTime = 2000;
                nextDifficultyIncrement = 2;
                reversedChance = 0.1f;
                break;
            case MEDIUM:
                currentDifficulty = Difficulty.HARD;
                stepsAmount = 5;
                nextDifficultyIncrement = 3;
                reversedChance = 0.2f;
                instructionsStepTime = 1700;
                break;
            case HARD:
                currentDifficulty = Difficulty.EXTREME;
                stepsAmount= 8;
                reversedChance = 0.3f;
                nextDifficultyIncrement = 1000;
                instructionsStepTime = 1000;
                break;
        }
    }

    public void levelEnded(Rating rating, int streak) {
        longestStreak = streak >= stepsAmount ? longestStreak + streak : Math.max(longestStreak, streak);

        if (rating == Rating.F) {
            livesLeft--;

        }

        playActivity.viewManager.setLivesLeft(livesLeft);

        if (livesLeft <= 0) {
            gameOver();
        } else {
            playActivity.viewManager.betweenView(
                    rating,
                    longestStreak,
                    roundCount,
                    rating != Rating.F
            );
            nextDifficultyIncrement--;
        }
    }

    public void gameOver() {
        playActivity.viewManager.gameOver(Rating.B, roundCount, longestStreak, currentDifficulty);
        soundManager.gameOver();
    }






}
