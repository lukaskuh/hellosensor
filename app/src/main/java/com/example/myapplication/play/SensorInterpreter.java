package com.example.myapplication.play;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Arrays;

public class SensorInterpreter implements SensorEventListener {
    // Code fetched from:
    // https://developer.android.com/develop/sensors-and-location/sensors/sensors_position#sensors-pos-orient
    private SensorManager sensorManager;

    private final float[] gravity = new float[3];

    private final float[] gravityMagnitudes = new float[5];
    private int gravityMagnitudeIteration;

    private static final float SHAKE_THRESHOLD = 0.8f;

    private static final long HOLD_TIME_THRESHOLD = 500000000L;
    private static final float SCORE_THRESHOLD = 0.6f;

    private Orientation goal;
    private float scoreAverage = 0.0f;
    private int debugCounter = 0;
    private float weight = 1.0f;
    private boolean reversed = false;


    private long correctHoldTime = 0;
    private long previousTimestamp;


    private Level.CorrectCallback correctCallback;



    public void setSensorManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    public void onPause() {
        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this);
    }
    public void onResume() {
        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (goal == null || event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        long deltaTimeNano = event.timestamp - previousTimestamp;


        //gravity.set(event.values);
        System.arraycopy(event.values, 0, gravity, 0, event.values.length);
        logGravityMagnitude(gravity);
        calculateScoreAverage(deltaTimeNano);


        this.previousTimestamp = event.timestamp;
    }


    public float getScoreAverage() {
        return scoreAverage;
    }

    public void setGoalOrientation(Orientation goal, boolean reversed, Level.CorrectCallback correctCallback) {
        //DEBUG
        Log.d("GAME", String.format("Average: %.2f. Counter: %d", scoreAverage, debugCounter));
        this.goal = goal;
        this.reversed = reversed;
        this.correctCallback = correctCallback;
        this.weight = 1;
        this.scoreAverage = 0.0f;
        this.previousTimestamp = android.os.SystemClock.elapsedRealtimeNanos();
        Arrays.fill(this.gravityMagnitudes, SensorManager.GRAVITY_EARTH);
    }


    //RENAME?
    private void calculateScoreAverage(long deltaTimeNano) {
        float score = -1.0f;

        float avgMagnitude = getAverageGravityMagnitude();
        if (goal == Orientation.SHAKE) {
            Log.d("GAME", "shaking");
            if (Math.abs(avgMagnitude - SensorManager.GRAVITY_EARTH) > SHAKE_THRESHOLD) {
                score = 1.0f;
            }
            //updateScoreAverage(1.0f);
        } else {
            Log.d("GAME", "GOAL: " + goal + ". DOT: " + score);
            score = goal.dot(gravity, SensorManager.GRAVITY_EARTH);
        }


        Log.d("GAME", "score before reversed: " + score);

        score = reversed ? score * -1 : score;
        Log.d("GAME", "score after reversed: " + score);

        float clampedScore = Math.min(Math.max(score, 0), 1);
        if (clampedScore > SCORE_THRESHOLD) {
            correctHoldTime += deltaTimeNano;
            Log.d("GAME", "Correct hold: " + correctHoldTime/1000000);

            if (correctHoldTime >= HOLD_TIME_THRESHOLD) {
                correctHoldTime = 0;
                int elapsedTime = (int) ((android.os.SystemClock.elapsedRealtimeNanos() - previousTimestamp + HOLD_TIME_THRESHOLD) / 1000000L);

                previousTimestamp = android.os.SystemClock.elapsedRealtimeNanos();

                correctCallback.onCorrect(elapsedTime);
            }
        } else {
            correctHoldTime = 0;
            Log.d("GAME", "WRONGGGGG HOLD");
        }


        updateScoreAverage(clampedScore);
    }

    private void updateScoreAverage(float score) {
        scoreAverage = (scoreAverage + score * weight) / (1 + weight);
        weight += 0.1f;
        debugCounter++;
    }

    private void logGravityMagnitude(float[] gravity) {
        gravityMagnitudes[gravityMagnitudeIteration] = getMagnitude(gravity);
        gravityMagnitudeIteration = (gravityMagnitudeIteration + 1) % gravityMagnitudes.length;
    }

    private float getAverageGravityMagnitude() {
        float sum = 0.0f;

        for (float gravityMagnitude : gravityMagnitudes) {
            sum += gravityMagnitude;
        }

        return sum/gravityMagnitudes.length;
    }

    private float getMagnitude(float[] a) {
        float sum = 0.0f;

        for (float v : a) {
            sum += v * v;
        }

        return (float) Math.sqrt(sum);
    }
}
