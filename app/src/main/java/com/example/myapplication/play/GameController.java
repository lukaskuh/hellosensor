package com.example.myapplication.play;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class GameController {
    public final SensorInterpreter sensorInterpreter = new SensorInterpreter();
    private TextView output;

    public int queueSize = 4;
    public Orientation[] queue;
    public float[] scores = new float[queueSize];
    public int queueCounter = 0;


    // Code from:
    // https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
    final Handler handler = new Handler();
    final Runnable runnable = this::run;
    final int delay = 5*1000; // 1000 milliseconds == 1 second


    public GameController(TextView output) {
        this.output = output;
    }


    public void start() {
        init();
        Log.d("INFO", "start: Game started!");

        startCountdown(
                () -> showSequence(
                        () -> {
                            nextGoal();
                            gameLoop();
                        }
                )
        );
    }

    private void gameLoop() {
        handler.postDelayed( runnable, delay);

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
        Log.d("INFO", "score: " + avg);





        if (nextGoal()) {
            gameLoop();
        } else {
            endGame();
        }
    }

    public void endGame() {
        sensorInterpreter.setGoalOrientation(null);

        float finalScore = 0.0f;

        for (float s : scores) {
            finalScore += s;
        }

        finalScore /= scores.length;

        Log.d("INFO", "GAME FINISHED. FINAL SCORE: " + finalScore);
    }

    public void onResume() {

    }

    public void onPause() {
        //handler.removeCallbacks(runnable);
    }

    private boolean nextGoal() {
        if (queueCounter >= queue.length) {
            return false;
        }

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
                    output.setText("Ready?");
                } else {
                    output.setText(String.format("%s", millisUntilFinished/1000 + 1));

                }
            }

            @Override
            public void onFinish() {
                afterCountdown.run();
            }

        }.start();
    }

    private void showSequence(Runnable afterCountdown) {
        new CountDownTimer((long) delay * queueSize, delay) {
            @Override
            public void onTick(long millisUntilFinished) {


                output.setText(
                        queue[queueCounter].toString()
                );

                queueCounter++;
            }


            @Override
            public void onFinish() {

                queueCounter = 0;
                output.setText("Go!");
                afterCountdown.run();
            }
        }.start();
    }

}
