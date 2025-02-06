package com.example.myapplication.play;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.myapplication.PlayActivity;

import java.util.concurrent.CompletableFuture;

public class GameController {
    // CompletableFuture från GPT
    private final PlayActivity playActivity;
    public final SensorInterpreter sensorInterpreter = new SensorInterpreter();
    private final SoundManager soundManager = new SoundManager();
    private final VibrationManager vibrationManager;
    private final OrientationShower orientationShower;

    private final TextView output;

    public int queueSize = 4;
    public Orientation[] queue;
    public float[] scores = new float[queueSize];
    public int queueCounter = 0;


    // Code from:
    // https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
    final Handler handler = new Handler();
    final Runnable runnable = this::run;
    final int delay = 3*1000; // 1000 milliseconds == 1 second


    public GameController(PlayActivity playActivity, TextView output, OrientationShower orientationShower) {
        this.playActivity = playActivity;
        this.output = output;
        this.orientationShower = orientationShower;
        this.vibrationManager = new VibrationManager(playActivity);
    }

    public void onResume() {

    }

    public void onPause() {
        //handler.removeCallbacks(runnable);
    }

    public void start() {
        init();
        Log.d("GAME", "start: Game started!");

        playActivity.viewManager.setView(GameState.COUNTDOWN);

        startCountdown()
                .thenRun(() -> {
                    playActivity.viewManager.setView(GameState.INSTRUCTIONS);
                })
                .thenCompose(v -> showSequence())
                .thenRun(() -> {
                    playActivity.viewManager.setView(GameState.COUNTDOWN);
                })
                .thenCompose(v -> startCountdown(true))
                .thenRun(() -> {
                    playActivity.viewManager.setView(GameState.PLAY);
                    soundManager.playBlastOff();
                    nextGoal();
                    gameLoop();
                });
    }


    private void init () {
        queueCounter = 0;
        queue = new Orientation[queueSize];
        scores = new float[queueSize];

        playActivity.viewManager.setOrientationAmount(queueSize);

        for (int i = 0; i < queueSize; i++) {
            queue[i] = Orientation.randomOrientation();
        }

    }

    private void run() {
        float avg = sensorInterpreter.getScoreAverage();
        scores[queueCounter - 1] = avg;
        Log.d("GAME", "score: " + avg);


        if (nextGoal()) {
            soundManager.playBeepHigh();
            gameLoop();
        } else {
            endGame();
        }
    }

    private void gameLoop() {
        handler.postDelayed(runnable, delay);

    }

    public void endGame() {
        sensorInterpreter.setGoalOrientation(null);

        float finalScore = 0.0f;

        for (float s : scores) {
            finalScore += s;
        }

        finalScore /= scores.length;

        soundManager.playFinish();
        playActivity.viewManager.setView(GameState.POST);
        Log.d("GAME", "GAME FINISHED. FINAL SCORE: " + finalScore);
        output.setText("Great work! Your score: " + finalScore + ". Play again?");
    }

    private boolean nextGoal() {
        if (queueCounter >= queue.length) {
            return false;
        }

        playActivity.viewManager.setOrientationProgress(queueCounter + 1);

        sensorInterpreter.setGoalOrientation(queue[queueCounter]);
        queueCounter++;

        return true;
    }

    private CompletableFuture<Void> startCountdown() {
        return startCountdown(false);
    }
    private CompletableFuture<Void> startCountdown(boolean quick) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        long millisInFuture = quick ? 3000 : 5000;

        new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("TAG", "onTick: ");

                if (millisUntilFinished > 3000) {
                    playActivity.viewManager.setCountdown("Ready?");
                } else {
                    playActivity.viewManager.setCountdown(String.format("%s", millisUntilFinished/1000 + 1));
                    soundManager.playBeepLow();
                    vibrationManager.tick();
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

        //Fattar inte varför -2? Funkar iaf.
        new CountDownTimer((long) delay * (queueSize), delay) {
            @Override
            public void onTick(long millisUntilFinished) {
                soundManager.playBeepHigh();
                Log.d("GAME",
                        "QueueSize: " + queueSize + ". Counter: " + queueCounter
                        );
                orientationShower.setOrientation(queue[queueCounter]);
                queueCounter++;
                playActivity.viewManager.setOrientationProgress(queueCounter);
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
