package com.example.myapplication.play;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class Level {

    private final GameController gameController;

    private int queueSize = 4;
    private Orientation[] queue;
    private boolean[] reversed;
    private float[] scores = new float[queueSize];
    private int queueCounter = 0;


    private static final Random PRNG = new Random();

    // Code from:
    // https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
    final Handler handler = new Handler();
    final Runnable runnable = this::run;
    final int delay; // 1000 milliseconds == 1 second
    final float reversedChance;

    private int currentStreak = 0;

    public Level(GameController gameController, int stepsAmount, int stepsDelay, float reversedChance) {
        this.gameController = gameController;
        this.queueSize = stepsAmount;
        this.delay = stepsDelay;
        this.reversedChance = reversedChance;
    }

    public void start() {
        init();
        Log.d("GAME", "start: Game started!");

        gameController.playActivity.viewManager.setCountdownMode(GameState.INSTRUCTIONS);
        gameController.playActivity.viewManager.setView(GameState.COUNTDOWN);

        startCountdown()
                .thenRun(() -> {
                    gameController.playActivity.viewManager.setView(GameState.INSTRUCTIONS);
                })
                .thenCompose(v -> showSequence())
                .thenRun(() -> {
                    gameController.playActivity.viewManager.setView(GameState.COUNTDOWN);
                    gameController.playActivity.viewManager.setCountdownMode(GameState.PLAY);
                })
                .thenCompose(v -> startCountdown(true))
                .thenRun(() -> {
                    gameController.playActivity.viewManager.setView(GameState.PLAY);
                    gameController.soundManager.playBlastOff();
                    nextGoal();
                    gameLoop();
                });
    }


    private void init () {
        queueCounter = 0;
        queue = new Orientation[queueSize];
        reversed = new boolean[queueSize];
        scores = new float[queueSize];

        gameController.playActivity.viewManager.setOrientationAmount(queueSize);

        for (int i = 0; i < queueSize; i++) {
            queue[i] = Orientation.randomOrientation();
            reversed[i] = PRNG.nextFloat() < reversedChance;
        }

    }

    private void run() {
        float avg = gameController.sensorInterpreter.getScoreAverage();
        scores[queueCounter - 1] = avg;
        Log.d("GAME", "score: " + avg);


        showFeedback(avg)
                .thenRun(() -> {
                    if (nextGoal()) {
                        if (avg > 0.6f) {
                            currentStreak++;
                        } else {
                            currentStreak = 0;
                        }

                        gameLoop();
                    } else {
                        endLevel();
                    }
                });
    }

    private void gameLoop() {
        handler.postDelayed(runnable, delay);

    }

    // https://chatgpt.com/share/67a53d8d-6e40-8004-9e96-fc0fbd93ab76
    private CompletableFuture<Void> showFeedback(float score) {
        CompletableFuture<Void> future = new CompletableFuture<>();


        gameController.playActivity.viewManager.setView(GameState.FEEDBACK);

        if (score > 0.6f) {
            gameController.playActivity.viewManager.feedbackPositive();
            gameController.soundManager.playPositiveFeedback();
        } else {
            gameController.playActivity.viewManager.feedbackNegative();
            gameController.soundManager.playNegativeFeedback();
        }

        handler.postDelayed(() -> {
            gameController.playActivity.viewManager.setView(GameState.PLAY);
            gameController.playActivity.viewManager.setBackgroundTinted();
            future.complete(null);
        }, 500);

        return future;
    }

    public void endLevel() {
        gameController.sensorInterpreter.setGoalOrientation(null, false);

        float finalScore = 0.0f;

        for (float s : scores) {
            finalScore += s;
        }

        finalScore /= scores.length;

        gameController.soundManager.playFinish();

        gameController.levelEnded(Rating.rateScore(finalScore), currentStreak);
        Log.d("GAME", "LEVEL FINISHED. FINAL SCORE: " + finalScore);
    }

    private boolean nextGoal() {
        if (queueCounter >= queue.length) {
            return false;
        }

        gameController.playActivity.viewManager.setOrientationProgress(queueCounter + 1);

        gameController.sensorInterpreter.setGoalOrientation(queue[queueCounter], reversed[queueCounter]);
        queueCounter++;

        return true;
    }

    private CompletableFuture<Void> startCountdown() {
        return startCountdown(false);
    }

    private CompletableFuture<Void> startCountdown(boolean quick) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        long millisInFuture = quick ? 3000 : 6500;
        gameController.playActivity.viewManager.setBackgroundPositive();

        new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("TAG", "onTick: ");

                if (millisUntilFinished > 5500) {
                    gameController.playActivity.viewManager.setCountdown("");
                } else if (millisUntilFinished > 4000) {
                    gameController.playActivity.viewManager.setCountdown("Ready?");
                } else if (millisUntilFinished < 1000) {
                    gameController.playActivity.viewManager.setCountdown("Go!");
                    
                } else {
                    gameController.playActivity.viewManager.setCountdown(String.format("%s", millisUntilFinished/1000));
                    gameController.soundManager.playBeepLow();
                    gameController.vibrationManager.tick();
                }
            }

            @Override
            public void onFinish() {
                future.complete(null);
            }

        }.start();

        return future;
    }

    private CompletableFuture<Void> showSequence() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Log.d("GAME", "millisInFuture : " + delay * (queueSize) + ". Delay: " + delay);

        new CountDownTimer((long) delay * (queueSize), delay) {
            @Override
            public void onTick(long millisUntilFinished) {
                gameController.soundManager.playBeepHigh();
                Log.d("GAME",
                        "QueueSize: " + queueSize + ". Counter: " + queueCounter
                );
                gameController.playActivity.viewManager.setOrientation(queue[queueCounter], reversed[queueCounter]);
                queueCounter++;
                gameController.playActivity.viewManager.setOrientationProgress(queueCounter);
            }


            @Override
            public void onFinish() {
                queueCounter = 0;
                future.complete(null);
            }
        }.start();

        return future;
    }
}
