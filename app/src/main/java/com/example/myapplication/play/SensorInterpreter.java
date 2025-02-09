package com.example.myapplication.play;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorInterpreter implements SensorEventListener {
    // Code fetched from:
    // https://developer.android.com/develop/sensors-and-location/sensors/sensors_position#sensors-pos-orient
    private SensorManager sensorManager;

    //private final Vector3 gravity = new Vector3();
    private final float[] gravity = new float[3];

    private final float[] gravityMagnitudes = new float[5];
    private int gravityMagnitudeIteration;

    private static final float ERROR_MARGIN = 0.4f;
    private static final float GRAVITY_LOWER_BOUNDS = SensorManager.GRAVITY_EARTH - ERROR_MARGIN/2;
    private static final float GRAVITY_UPPER_BOUNDS = SensorManager.GRAVITY_EARTH + ERROR_MARGIN/2;

    private Orientation orientation;
    private Orientation goal;
    private float scoreAverage = 0.0f;
    private int debugCounter = 0;
    private float weight = 1.0f;



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
        if (goal == null) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //gravity.set(event.values);
            System.arraycopy(event.values, 0, gravity, 0, event.values.length);
            logGravityMagnitude(gravity);
            calculateScoreAverage();
            orientation = interpretOrientation();
        }

    }

    public boolean validateOrientation(Orientation goal) {
        return goal == orientation;
    }

    public float getScoreAverage() {
        return scoreAverage;
    }

    public void setGoalOrientation(Orientation goal) {
        //DEBUG
        Log.d("GAME", String.format("Average: %.2f. Counter: %d", scoreAverage, debugCounter));

        this.goal = goal;
        this.weight = 1;
        this.scoreAverage = 0.0f;
    }



    private void calculateScoreAverage() {


        float magnitude = getAverageGravityMagnitude();
        if (goal == Orientation.SHAKE && (magnitude < GRAVITY_LOWER_BOUNDS || magnitude > GRAVITY_UPPER_BOUNDS)) {
            Log.d("GAME", "shaking");
            updateScoreAverage(1.0f);
            return;
        }

        //CLAMP!
        float score = Math.min(Math.max(0, goal.dot(gravity, SensorManager.GRAVITY_EARTH)), 1);

        Log.d("GAME", "GOAL: " + goal + ". DOT: " + score);
        updateScoreAverage(score);
    }

    private void updateScoreAverage(float score) {
        scoreAverage = (scoreAverage + score * weight) / (1 + weight);
        weight += 0.1f;
        debugCounter++;
    }

    private Orientation interpretOrientation() {
        // -1: flat upside down
        // 0: standing
        // 1: flat
        float magnitude = getAverageGravityMagnitude();

        if (magnitude < GRAVITY_LOWER_BOUNDS || magnitude > GRAVITY_UPPER_BOUNDS) {
            return Orientation.SHAKE;
        }


//        if (gravity.y > GRAVITY_THRESHOLD) {
//            return Orientation.PORTRAIT;
//        } else if (gravity.y < -GRAVITY_THRESHOLD) {
//            return Orientation.PORTRAIT_REVERSE;
//        } else if (gravity.z > GRAVITY_THRESHOLD) {
//            return Orientation.FLAT;
//        } else if (gravity.z < -GRAVITY_THRESHOLD) {
//            return Orientation.FLAT_REVERSE;
//        } else if (gravity.x > GRAVITY_THRESHOLD) {
//            return Orientation.LANDSCAPE_LEFT;
//        } else if (gravity.x < -GRAVITY_THRESHOLD) {
//            return Orientation.LANDSCAPE_RIGHT;
//        }

        return Orientation.ERROR;
    }

    public Orientation getOrientation() {
        return orientation;
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





    // GAMMAL KOD
    // Code fetched GPT and modified to fit:
    // https://chatgpt.com/share/67955218-98f0-8004-92d3-ad468d1c833f
    private float[] getUpDirectionVector(float[] orientation) {
        float azimuth = orientation[0];  // Rotation around Z (radians)
        float pitch = orientation[1];    // Rotation around X (radians)

        float x = (float) (Math.cos(pitch) * Math.sin(azimuth));
        float y = (float) (Math.sin(pitch));
        float z = (float) (Math.cos(pitch) * Math.cos(azimuth));

        return new float[]{x, y, z};  // Direction vector
    }

    private float[] getScreenDirectionVector(float[] orientation) {
        float azimuth = orientation[1];  // Rotation around Z (radians)
        float pitch = orientation[2];    // Rotation around X (radians)

        float x = (float) (Math.cos(pitch) * Math.sin(azimuth));
        float y = (float) (Math.sin(pitch));
        float z = (float) (Math.cos(pitch) * Math.cos(azimuth));

        return new float[]{x, y, z};  // Direction vector
    }

    private float[] getRightDirectionVector(float[] orientation) {
        float azimuth = orientation[0];  // Rotation around Z (radians)
        float pitch = orientation[1];    // Rotation around X (radians)

        // Compute the direction vector for the right side of the phone
        float x = (float) (Math.cos(pitch) * Math.cos(azimuth + Math.PI / 2)); // +Math.PI/2 for the right side
        float y = (float) (Math.sin(pitch));  // Y component based on pitch
        float z = (float) (Math.cos(pitch) * Math.sin(azimuth + Math.PI / 2)); // +Math.PI/2 for the right side

        return new float[]{x, y, z};  // Direction vector for the right side

    }

    private float dotProduct(float[] a, float[] b) {
        float sum = 0;

        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }

        return sum;
    }

    private float getMagnitude(float[] a) {
        float sum = 0.0f;

        for (float v : a) {
            sum += v * v;
        }

        return (float) Math.sqrt(sum);
    }
}
