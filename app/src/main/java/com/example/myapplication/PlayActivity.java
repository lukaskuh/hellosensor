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

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class PlayActivity extends AppCompatActivity implements SensorEventListener {

    private GameController gameController;
    private TextView logger;
    private GameState gameState = GameState.PRE;

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

        gameController = new GameController(
                this,
                findViewById(R.id.logger),
                new OrientationShower(findViewById(R.id.PoseView)),
                findViewById(R.id.countdown)
        );

        gameController.sensorInterpreter.setSensorManager(
                (SensorManager) getSystemService(Context.SENSOR_SERVICE)
        );

        // setView(GameState.PRE);
        setView(GameState.DEBUG);
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

    public void setView(GameState state) {
        this.gameState = state;
        switch (state) {
            case PRE:
                findViewById(R.id.DebugLayout).setVisibility(View.GONE);
                findViewById(R.id.InstructionsLayout).setVisibility(View.GONE);
                findViewById(R.id.PlayLayout).setVisibility(View.GONE);
                findViewById(R.id.ResultLayout).setVisibility(View.VISIBLE);
                break;
            case COUNTDOWN:
                findViewById(R.id.DebugLayout).setVisibility(View.GONE);
                findViewById(R.id.PlayLayout).setVisibility(View.GONE);
                findViewById(R.id.ResultLayout).setVisibility(View.GONE);
                findViewById(R.id.InstructionsLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.countdown).setVisibility(View.VISIBLE);
                findViewById(R.id.PoseView).setVisibility(View.GONE);
                findViewById(R.id.page_indicator).setVisibility(View.GONE);
            case INSTRUCTIONS:
                findViewById(R.id.DebugLayout).setVisibility(View.GONE);
                findViewById(R.id.PlayLayout).setVisibility(View.GONE);
                findViewById(R.id.ResultLayout).setVisibility(View.GONE);
                findViewById(R.id.InstructionsLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.countdown).setVisibility(View.GONE);
                findViewById(R.id.PoseView).setVisibility(View.VISIBLE);
                findViewById(R.id.page_indicator).setVisibility(View.VISIBLE);
                break;
            case PLAY:
                findViewById(R.id.DebugLayout).setVisibility(View.GONE);
                findViewById(R.id.ResultLayout).setVisibility(View.GONE);
                findViewById(R.id.InstructionsLayout).setVisibility(View.GONE);
                findViewById(R.id.PlayLayout).setVisibility(View.VISIBLE);
                break;
            case POST:
                findViewById(R.id.DebugLayout).setVisibility(View.GONE);
                findViewById(R.id.InstructionsLayout).setVisibility(View.GONE);
                findViewById(R.id.PlayLayout).setVisibility(View.GONE);
                findViewById(R.id.ResultLayout).setVisibility(View.VISIBLE);
                break;
            case DEBUG:
                findViewById(R.id.InstructionsLayout).setVisibility(View.GONE);
                findViewById(R.id.PlayLayout).setVisibility(View.GONE);
                findViewById(R.id.ResultLayout).setVisibility(View.GONE);
                findViewById(R.id.DebugLayout).setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setPage(int i, int max) {
        if (gameState == GameState.INSTRUCTIONS) {
            ((TextView) findViewById(R.id.page_indicator)).setText(
                    String.format("%d/%d", i, max)
            );

        } else if (gameState == GameState.PLAY) {
            ((TextView) findViewById(R.id.play_output)).setText(
                    String.format("Go!\n%d/%d", i, max)
            );
        }

    }
}