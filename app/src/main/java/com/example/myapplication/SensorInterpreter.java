package com.example.myapplication;

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
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rawRotationMatrix = new float[9];
    private final float[] transformedRotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    private final float[] calibrationAxis = new float[2];

    private final float[] UP = {0, 0, 1};

    private final Vector3 gravity = new Vector3();

    private float[] gravityMagnitudes = new float[5];
    private int gravityMagnitudeIteration;

    private final float ERROR_MARGIN = 0.4f;
    private final float GRAVITY_LOWER_BOUNDS = SensorManager.GRAVITY_EARTH - ERROR_MARGIN/2;
    private final float GRAVITY_UPPER_BOUNDS = SensorManager.GRAVITY_EARTH + ERROR_MARGIN/2;
    private final float GRAVITY_THRESHOLD = SensorManager.GRAVITY_EARTH * 0.8f;

    private Orientation orientation;



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
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity.set(event.values);
            logGravityMagnitude(gravity.magnitude());
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }

        updateOrientationAngles();
        orientation = interpretOrientation();
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rawRotationMatrix, null, accelerometerReading, magnetometerReading);

        // "rotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rawRotationMatrix, orientationAngles);

        //SensorManager.remapCoordinateSystem(rawRotationMatrix, calibrationAxis[0], SensorManager.AXIS_X, transformedRotationMatrix);
        // "orientationAngles" now has up-to-date information.


    }

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




    private Orientation interpretOrientation() {
        // -1: flat upside down
        // 0: standing
        // 1: flat
        float magnitude = getAverageGravityMagnitude();

        if (magnitude < GRAVITY_LOWER_BOUNDS || magnitude > GRAVITY_UPPER_BOUNDS) {
            return Orientation.SHAKE;
        }


        if (gravity.y > GRAVITY_THRESHOLD) {
            return Orientation.PORTRAIT;
        } else if (gravity.y < -GRAVITY_THRESHOLD) {
            return Orientation.PORTRAIT_REVERSE;
        } else if (gravity.z > GRAVITY_THRESHOLD) {
            return Orientation.FLAT;
        } else if (gravity.z < -GRAVITY_THRESHOLD) {
            return Orientation.FLAT_REVERSE;
        } else if (gravity.x > GRAVITY_THRESHOLD) {
            return Orientation.LANDSCAPE_LEFT;
        } else if (gravity.x < -GRAVITY_THRESHOLD) {
            return Orientation.LANDSCAPE_RIGHT;
        }

        return Orientation.ERROR;
    }

    public Orientation getOrientation() {
        return orientation;
    }




    private void logGravityMagnitude(float magnitude) {
        gravityMagnitudes[gravityMagnitudeIteration] = magnitude;
        gravityMagnitudeIteration = (gravityMagnitudeIteration + 1) % gravityMagnitudes.length;
    }

    private float getAverageGravityMagnitude() {
        float sum = 0.0f;

        for (float gravityMagnitude : gravityMagnitudes) {
            sum += gravityMagnitude;
        }

        return sum/gravityMagnitudes.length;
    }


    public boolean validateOrientation(Orientation goal) {
        return goal == orientation;
    }
}
