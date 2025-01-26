package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PlayActivity extends AppCompatActivity implements SensorEventListener {

    private final GameController gameController = new GameController();

    private Orientation[] moveQueue = {Orientation.LANDSCAPE_LEFT, Orientation.LANDSCAPE_RIGHT};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gameController.sensorInterpreter.setSensorManager(
                (SensorManager) getSystemService(Context.SENSOR_SERVICE)
        );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        gameController.sensorInterpreter.onAccuracyChanged(sensor, accuracy);
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameController.sensorInterpreter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        gameController.sensorInterpreter.onPause();
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    @Override
    public void onSensorChanged(SensorEvent event) {
        gameController.sensorInterpreter.onSensorChanged(event);
        Log.d("INFO", "Orientation: " + getResources().getConfiguration().orientation);
    }

    public void startGame(View view) {
        gameController.start();
    }


}