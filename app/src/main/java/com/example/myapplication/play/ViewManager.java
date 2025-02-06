package com.example.myapplication.play;

import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;

import com.example.myapplication.PlayActivity;

import java.util.TreeMap;

public class ViewManager {
    private PlayActivity playActivity;
    private final TreeMap<GameState, View> layouts = new TreeMap<>();
    private GameState gameState = GameState.PRE;


    private final ProgressDotsView playProgress;
    private final ProgressDotsView instructionsProgress;

    public ViewManager(PlayActivity playActivity) {
        this.playActivity = playActivity;

        this.playProgress = playActivity.findViewById(R.id.playProgress);
        this.instructionsProgress = playActivity.findViewById(R.id.instructionsProgress);

//        layouts.put(GameState.PLAY, playActivity.findViewById(R.id.PlayLayout));
        //layouts.put(GameState.PRE, playActivity.findViewById(R.id.));
    }

    public void setOrientationAmount(int i) {
        playProgress.setTotalDots(i);
        instructionsProgress.setTotalDots(i);
    }

    public void setCountdown(String text) {
        ((TextView) playActivity.findViewById(R.id.countdown)).setText(text);
    }


    public void setOrientationProgress(int i) {
        switch (gameState) {
            case PLAY:
                playProgress.setProgress(i);
                break;
            case INSTRUCTIONS:
                instructionsProgress.setProgress(i);
                break;
        }
    }

    public void setView(GameState state) {
        this.gameState = state;
        hideAllViews();

        switch (state) {
            case COUNTDOWN:
                playActivity.findViewById(R.id.GameLayout).setVisibility(View.VISIBLE);
                break;
            case INSTRUCTIONS:
                playActivity.findViewById(R.id.GameLayout).setVisibility(View.VISIBLE);
                break;
            case PLAY:
                playActivity.findViewById(R.id.GameLayout).setVisibility(View.VISIBLE);
                break;
        }

        playActivity.findViewById(state.getLayoutId()).setVisibility(View.VISIBLE);
    }

    private void hideAllViews() {
        for (GameState s : GameState.values()) {
            playActivity.findViewById(s.getLayoutId()).setVisibility(View.GONE);
        }

        playActivity.findViewById(R.id.GameLayout).setVisibility(View.GONE);
    }


}
