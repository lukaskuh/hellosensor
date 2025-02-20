package com.example.myapplication.play;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class Level {

    private final GameController gameController;

    private int queueSize;
    private Orientation[] queue;
    private boolean[] reversed;
    private float[] scores = new float[queueSize];
    private int queueCounter = 0;


    private static final Random PRNG = new Random();

    // Code from:
    // https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
    final Handler handler = new Handler();
    final int instructionsStepTime;
    final int playStepTime;
    final float reversedChance;


    private final PlayCountdown playCountdown;
    private CountDownTimer currentProcess;

    private int currentStreak = 0;

    public Level(GameController gameController, int stepsAmount, int instructionsStepTime, int playStepTime, float reversedChance) {
        this.gameController = gameController;
        this.queueSize = stepsAmount;
        this.instructionsStepTime = instructionsStepTime;
        this.playStepTime = playStepTime;
        this.reversedChance = reversedChance;
        this.playCountdown = new PlayCountdown(playStepTime, 100);
        gameController.playActivity.viewManager.clearOrientation();
    }

    public void start() {
        init();
        Log.d("GAME", "start: Game started!");

        gameController.playActivity.viewManager.setCountdownMode(GameState.INSTRUCTIONS);
        gameController.playActivity.viewManager.setView(GameState.COUNTDOWN);

        startCountdown()
                .thenRun(() -> {
                    gameController.playActivity.viewManager.clearOrientation();
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
                    nextGoal();
                });
    }


    private void init () {
        queueCounter = 0;
        queue = new Orientation[queueSize];
        reversed = new boolean[queueSize];
        scores = new float[queueSize];
        Arrays.fill(scores, -1f);

        gameController.playActivity.viewManager.setOrientationAmount(queueSize);
        gameController.playActivity.viewManager.clearOrientation();

        queue[0] = Orientation.randomOrientation();
        reversed[0] = PRNG.nextFloat() < reversedChance;

        for (int i = 1; i < queueSize; i++) {
            reversed[i] = PRNG.nextFloat() < reversedChance;
            do {
                queue[i] = Orientation.randomOrientation();
            } while (queue[i] == queue[i-1]);
        }

    }


    public void kill() {
        playCountdown.cancel();
        if (currentProcess != null) {
            currentProcess.cancel();
        }
    }



    // https://chatgpt.com/share/67a53d8d-6e40-8004-9e96-fc0fbd93ab76
    private CompletableFuture<Void> showFeedback() {
        boolean point = scores[queueCounter - 1] >= 0;

        CompletableFuture<Void> future = new CompletableFuture<>();


        gameController.playActivity.viewManager.setView(GameState.FEEDBACK);

        if (point) {
            gameController.playActivity.viewManager.feedbackPositive();
            gameController.vibrationManager.correct();
            gameController.soundManager.correct();
        } else {
            gameController.playActivity.viewManager.feedbackNegative();
            gameController.vibrationManager.wrong();
            gameController.soundManager.wrong();
        }

        handler.postDelayed(() -> future.complete(null), 500);

        return future;
    }

    public void endLevel() {
        gameController.sensorInterpreter.setGoalOrientation(null, false, i -> {});

        float finalScore = calculateFinalScore();

        gameController.soundManager.finish();

        gameController.levelEnded(finalScore, currentStreak);
        Log.d("GAME", "LEVEL FINISHED. FINAL SCORE: " + finalScore);
    }

    private float calculateFinalScore() {
        float sum = 0.0f;
        int failedCount = 0;

        for (float score : scores) {
            if (score < 0) {
                failedCount++;

                if (failedCount >= 3) {
                    return -1;
                }
            } else {
                sum += Math.round((1.0f - score / playStepTime) * 500);
                Log.d("GAME", "calculateFinalScore: " + sum);
            }
        }

        return sum;
    }

    private void nextGoal() {
        gameController.playActivity.viewManager.setView(GameState.PLAY);
        gameController.playActivity.viewManager.setBackgroundTinted();

        if (queueCounter >= queue.length) {
            endLevel();
        }


        gameController.playActivity.viewManager.setPlayProgress(queueCounter + 1, false);
        if (queueCounter > 0) {
            Log.d("GAME", "prev score: " + scores[queueCounter - 1]);
            boolean failed = scores[queueCounter - 1] < 0;
            gameController.playActivity.viewManager.setPlayFeedback(queueCounter, failed);
        }


        gameController.sensorInterpreter.setGoalOrientation(queue[queueCounter], reversed[queueCounter], this::correct);
        queueCounter++;

        playCountdown.start();
    }

    private void postGoal() {
        showFeedback()
                .thenRun(this::nextGoal);

    }

    public void correct(int time) {
        float avg = gameController.sensorInterpreter.getScoreAverage();
        scores[queueCounter - 1] = time;

        if (avg > 0.6f) {
            currentStreak++;
        } else {
            currentStreak = 0;
        }

        Log.d("GAME", "correct: !");
        playCountdown.cancel();
        postGoal();
    }

    private void fail() {
        gameController.sensorInterpreter.setGoalOrientation(null, false, i -> {});
        Log.d("GAME", "fail: ");
        postGoal();
    }

    private CompletableFuture<Void> startCountdown() {
        return startCountdown(false);
    }

    private CompletableFuture<Void> startCountdown(boolean quick) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        long millisInFuture = quick ? 3000 : 6500;
        gameController.playActivity.viewManager.setBackgroundPositive();

        if (!quick) {
            gameController.soundManager.tick();
            gameController.vibrationManager.tick();
        }

        currentProcess = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (millisUntilFinished > 5500) {
                    gameController.playActivity.viewManager.setCountdown("");
                } else if (millisUntilFinished > 4000) {
                    gameController.playActivity.viewManager.setCountdown("Ready?");
                } else if (millisUntilFinished < 1000) {
                    gameController.playActivity.viewManager.setCountdown("Go!");

                    if (quick) {
                        gameController.soundManager.blastOff();
                    } else {
                        gameController.soundManager.blastOffShort();
                    }
                } else {
                    gameController.playActivity.viewManager.setCountdown(String.format("%s", millisUntilFinished/1000));
                    gameController.soundManager.tick();
                    gameController.vibrationManager.tick();
                }
            }

            @Override
            public void onFinish() {
                future.complete(null);
                gameController.vibrationManager.correct();
            }

        };

        currentProcess.start();

        return future;
    }

    private CompletableFuture<Void> showSequence() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Log.d("GAME", "millisInFuture : " + instructionsStepTime * (queueSize) + ". Delay: " + instructionsStepTime);

        currentProcess = new CountDownTimer((long) instructionsStepTime * (queueSize), instructionsStepTime) {
            @Override
            public void onTick(long millisUntilFinished) {
                gameController.soundManager.correct();
                gameController.vibrationManager.correct();
                Log.d("GAME",
                        "QueueSize: " + queueSize + ". Counter: " + queueCounter
                );
                gameController.playActivity.viewManager.setOrientation(queue[queueCounter], reversed[queueCounter]);
                queueCounter++;
                gameController.playActivity.viewManager.setInstructionsProgress(queueCounter);
            }


            @Override
            public void onFinish() {
                queueCounter = 0;
                future.complete(null);
            }
        };

        currentProcess.start();

        return future;
    }


    private class PlayCountdown extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public PlayCountdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            gameController.playActivity.viewManager.setPlayRemainingCountdown(millisUntilFinished/1000f);
        }

        @Override
        public void onFinish() {
            Log.d("GAME", "ON FINISH!!!!!!!!!!!!!!!!!!!!");
            fail();
        }
    }

    public interface CorrectCallback {
        void onCorrect(int timeMs);
    }
}
