package com.example.myapplication;

import android.os.Handler;
import android.util.Log;

public class GameController {
    public final SensorInterpreter sensorInterpreter = new SensorInterpreter();
    public Orientation[] queue = {Orientation.FLAT, Orientation.FLAT_REVERSE};
    public int queueCounter = 0;


    // Code from:
    // https://stackoverflow.com/questions/11434056/how-to-run-a-method-every-x-seconds
    final Handler handler = new Handler();
    Runnable runnable;
    final int delay = 5*1000; // 1000 milliseconds == 1 second



    public void start() {
        Log.d("INFO", "start: Game started!");


        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                Log.d("INFO", "run: " + sensorInterpreter.validateOrientation(queue[queueCounter]));



                if (queueCounter < queue.length) {
                    queueCounter += 1;
                    handler.postDelayed(runnable, delay);
                }
            }
        }, delay);

    }

    public void onResume() {

    }

    public void onPause() {
        handler.removeCallbacks(runnable);
    }

}
