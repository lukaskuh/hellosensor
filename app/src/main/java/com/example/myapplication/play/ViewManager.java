package com.example.myapplication.play;

import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

import com.example.myapplication.PlayActivity;

import java.util.TreeMap;

public class ViewManager {
    private final PlayActivity playActivity;
    private final View rootView;
    private final TreeMap<GameState, View> layouts = new TreeMap<>();
    private final TextView countdownPromptText;
    private GameState gameState = GameState.PRE;
    private final TextView feedbackText;
    private final TextView difficultyText;
    private final TextView ratingText;
    private final TextView streakText;
    private final TextView roundCountText;


    private final ProgressDotsView playProgress;
    private final ProgressDotsView instructionsProgress;

    private final OrientationShower orientationShower;

    public ViewManager(PlayActivity playActivity) {
        this.playActivity = playActivity;

        this.playProgress = playActivity.findViewById(R.id.playProgress);
        this.instructionsProgress = playActivity.findViewById(R.id.instructionsProgress);

        this.rootView = playActivity.findViewById(R.id.main);

        this.difficultyText = playActivity.findViewById(R.id.difficultyText);
        this.feedbackText = playActivity.findViewById(R.id.feedbackText);
        this.ratingText = playActivity.findViewById(R.id.ratingText);
        this.streakText = playActivity.findViewById(R.id.streakText);
        this.roundCountText = playActivity.findViewById(R.id.roundCountText);
        this.countdownPromptText = playActivity.findViewById(R.id.countdownPrompt);

        this.orientationShower = new OrientationShower(playActivity.findViewById(R.id.PoseView));
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

    public void setCountdownMode(GameState state) {
        countdownPromptText.setVisibility(View.VISIBLE);

        switch (state) {
            case INSTRUCTIONS:
                countdownPromptText.setText(R.string.memorize_prompt);
                break;
            case PLAY:
                countdownPromptText.setText(R.string.play_prompt);
                break;
            default:
                countdownPromptText.setVisibility(View.GONE);
        }
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

    public void feedbackPositive() {
        feedbackText.setText(R.string.feedback_good);
        setBackgroundPositive();
    }

    public void feedbackNegative() {
        feedbackText.setText(R.string.feedback_bad);
        setBackgroundNegative();
    }

    public void setBackgroundNeutral() {
        rootView.setBackgroundColor(ContextCompat.getColor(playActivity, R.color.md_theme_background));
    }

    public void setBackgroundTinted() {
        rootView.setBackgroundColor(ContextCompat.getColor(playActivity, R.color.md_theme_primaryFixed));
    }

    public void setBackgroundPositive() {
        rootView.setBackgroundColor(ContextCompat.getColor(playActivity, R.color.md_theme_primaryContainer));
    }

    public void setBackgroundNegative() {
        rootView.setBackgroundColor(ContextCompat.getColor(playActivity, R.color.md_theme_errorContainer_mediumContrast));
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
            case FEEDBACK:
                playActivity.findViewById(R.id.GameLayout).setVisibility(View.VISIBLE);
                break;
            case BETWEEN:
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


    public void betweenView(Rating rating, int streak, int roundCount) {
        setView(GameState.BETWEEN);
        ratingText.setText(rating.toString());
        streakText.setText(String.valueOf(streak));
        roundCountText.setText(String.valueOf(roundCount));
    }

    public void setDifficulty(Difficulty currentDifficulty) {
        difficultyText.setText(currentDifficulty.toString());
    }



    public void setOrientation(Orientation orientation, boolean reversed) {
        orientationShower.setOrientation(orientation);
        if (reversed) {
            setBackgroundNegative();
        } else {
            setBackgroundPositive();
        }
    }
}
