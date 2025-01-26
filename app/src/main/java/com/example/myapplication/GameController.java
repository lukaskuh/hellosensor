package com.example.myapplication;

import android.os.Handler;
import android.util.Log;

public class GameController {
    public final SensorInterpreter sensorInterpreter = new SensorInterpreter();
    public Orientation[] queue = {Orientation.FLAT, Orientation.FLAT_REVERSE};
    public float[] scores = new float[queue.length];
    public int queueCounter = 0;


    // Code from:
    // https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
    final Handler handler = new Handler();
    final Runnable runnable = this::run;
    final int delay = 5*1000; // 1000 milliseconds == 1 second



    public void start() {
        Log.d("INFO", "start: Game started!");

        nextGoal();
        gameLoop();
    }

    public void gameLoop() {
        handler.postDelayed( runnable, delay);

    }

    public void run() {
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
        handler.removeCallbacks(runnable);
    }

    private boolean nextGoal() {
        if (queueCounter >= queue.length) {
            return false;
        }

        sensorInterpreter.setGoalOrientation(queue[queueCounter]);
        queueCounter++;

        return true;
    }

}
