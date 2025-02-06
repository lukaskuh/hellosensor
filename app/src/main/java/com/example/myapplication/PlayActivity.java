package com.example.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.play.GameController;
import com.example.myapplication.play.GameState;
import com.example.myapplication.play.OrientationShower;
import com.example.myapplication.play.ProgressDotsView;
import com.example.myapplication.play.ViewManager;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class PlayActivity extends AppCompatActivity implements SensorEventListener {
    public ViewManager viewManager;
    private GameController gameController;
    private TextView logger;

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

        this.viewManager = new ViewManager(this);

        gameController = new GameController(
                this,
                findViewById(R.id.logger),
                new OrientationShower(findViewById(R.id.PoseView))
        );

        gameController.sensorInterpreter.setSensorManager(
                (SensorManager) getSystemService(Context.SENSOR_SERVICE)
        );

        // setView(GameState.PRE);
        viewManager.setView(GameState.PRE);
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
        Log.d("GAME", "Orientation: " + getResources().getConfiguration().orientation);
    }

    public void startGame(View view) {
        gameController.start();
    }
}