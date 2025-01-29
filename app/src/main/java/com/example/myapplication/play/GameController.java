package com.example.myapplication.play;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.myapplication.PlayActivity;

public class GameController {
    private final PlayActivity playActivity;
    public final SensorInterpreter sensorInterpreter = new SensorInterpreter();
    private final SoundManager soundManager = new SoundManager();
    private final OrientationShower orientationShower;

    private final TextView countdown;
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


    public GameController(PlayActivity playActivity, TextView output, OrientationShower orientationShower, TextView countdown) {
        this.playActivity = playActivity;
        this.output = output;
        this.orientationShower = orientationShower;
        this.countdown = countdown;
    }

    public void onResume() {

    }

    public void onPause() {
        //handler.removeCallbacks(runnable);
    }

    public void start() {
        init();
        Log.d("GAME", "start: Game started!");

        playActivity.setView(GameState.INSTRUCTIONS);
        countdown.setVisibility(TextView.VISIBLE);
        orientationShower.setVisible(false);
        startCountdown(
            () -> {
                countdown.setVisibility(TextView.GONE);
                orientationShower.setVisible(true);
                showSequence(
                        () -> {
                            playActivity.setView(GameState.PLAY);
                            nextGoal();
                            gameLoop();
                        }
                );

            }
        );
    }


    private void init () {
        queueCounter = 0;
        queue = new Orientation[queueSize];
        scores = new float[queueSize];

        for (int i = 0; i < queueSize; i++) {
            queue[i] = Orientation.randomOrientation();
        }

    }

    private void run() {
        float avg = sensorInterpreter.getScoreAverage();
        scores[queueCounter - 1] = avg;
        Log.d("GAME", "score: " + avg);


        if (nextGoal()) {
            soundManager.playBeep();
            gameLoop();
        } else {
            endGame();
        }
    }

    private void gameLoop() {
        handler.postDelayed( runnable, delay);

    }

    public void endGame() {
        sensorInterpreter.setGoalOrientation(null);

        float finalScore = 0.0f;

        for (float s : scores) {
            finalScore += s;
        }

        finalScore /= scores.length;


        playActivity.setView(GameState.POST);
        Log.d("GAME", "GAME FINISHED. FINAL SCORE: " + finalScore);
        output.setText("Great work! Your score: " + finalScore + ". Play again?");
    }

    private boolean nextGoal() {
        if (queueCounter >= queue.length) {
            return false;
        }

        playActivity.setPage(queueCounter + 1, queueSize);

        sensorInterpreter.setGoalOrientation(queue[queueCounter]);
        queueCounter++;

        return true;
    }


    private void startCountdown(Runnable afterCountdown) {
        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("TAG", "onTick: ");

                if (millisUntilFinished > 3000) {
                    countdown.setText("Ready?");
                } else {
                    countdown.setText(String.format("%s", millisUntilFinished/1000 + 1));
                    soundManager.playBeep();
                }
            }

            @Override
            public void onFinish() {
                afterCountdown.run();
            }

        }.start();
    }

    private void showSequence(Runnable afterCountdown) {
        Log.d("GAME", "millisInFuture : " + delay * (queueSize) + ". Delay: " + delay);

        //Fattar inte varför -2? Funkar iaf.
        new CountDownTimer((long) delay * (queueSize), delay) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("GAME",
                        "QueueSize: " + queueSize + ". Counter: " + queueCounter
                        );
                orientationShower.setOrientation(queue[queueCounter]);
                queueCounter++;
                playActivity.setPage(queueCounter, queueSize);
            }


            @Override
            public void onFinish() {
                queueCounter = 0;
                afterCountdown.run();
            }
        }.start();
    }

}
