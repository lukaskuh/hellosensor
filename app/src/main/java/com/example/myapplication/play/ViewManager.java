package com.example.myapplication.play;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.example.myapplication.R;
import com.example.myapplication.PlayActivity;

public class ViewManager {
    private final PlayActivity playActivity;
    private final View rootView;
    private final TextView countdownPromptText;
    private GameState gameState = GameState.PRE;
    private final TextView feedbackText;
    private final TextView difficultyText;
    private final TextView ratingText;
    private final TextView streakText;
    private final TextView roundCountText;
    private final TextView playCountdownText;
    private final TextView betweenPromptText;
    private final HeartCountView heartCountView;
    private final TextView finalRoundCountText;
    private final TextView finalDifficultyText;
    private final TextView finalStreakText;
    private final TextView finalRatingText;
    private final TextView totalScoreText;


    private final ProgressDotsFeedbackView playProgress;
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
        this.playCountdownText = playActivity.findViewById(R.id.playCountdown);
        this.betweenPromptText = playActivity.findViewById(R.id.betweenPrompt);
        this.heartCountView = playActivity.findViewById(R.id.heartCount);
        this.totalScoreText = playActivity.findViewById(R.id.totalScoreText);

        this.finalDifficultyText = playActivity.findViewById(R.id.finalDifficultyText);
        this.finalRatingText = playActivity.findViewById(R.id.finalRatingText);
        this.finalRoundCountText = playActivity.findViewById(R.id.finalRoundCountText);
        this.finalStreakText = playActivity.findViewById(R.id.finalStreakText);

        this.orientationShower = new OrientationShower(playActivity.findViewById(R.id.PoseView));
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


    public void setInstructionsProgress(int i) {
        instructionsProgress.setProgress(i);
    }

    public void setPlayProgress(int i, boolean failed) {
        playProgress.setProgress(i, failed);
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
        rootView.setBackgroundColor(ContextCompat.getColor(playActivity, R.color.md_theme_background));
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


    @SuppressLint("DefaultLocale")
    public void betweenView(float roundScore, float totalScore, int streak, int roundCount, boolean positive) {
        setView(GameState.BETWEEN);
        ratingText.setText(String.format("%.0f", roundScore));
        streakText.setText(String.valueOf(streak));
        roundCountText.setText(String.valueOf(roundCount));
        totalScoreText.setText(String.format("%.0f", totalScore));

        int betweenPrompt = positive ? R.string.between_prompt_positive : R.string.between_prompt_negative;
        betweenPromptText.setText(betweenPrompt);
    }

    public void setLivesLeft(int livesLeft) {
        Log.d("GAME", "setLivesLeft: " + livesLeft);
        heartCountView.setLivesLeft(livesLeft);
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

    public void clearOrientation() {
        orientationShower.clearOrientation();
    }

    public void setPlayRemainingCountdown(float time) {
        playCountdownText.setText(String.format("%.1f", time));
    }

    public void setPlayFeedback(int i, boolean failed) {
        playProgress.setFeedback(i, failed);
    }

    @SuppressLint("DefaultLocale")
    public void gameOver(float finalScore, int finalRoundCount, int finalStreak, Difficulty finalDifficulty) {
        setView(GameState.POST);
        finalRatingText.setText(String.format("%.0f", finalScore));
        finalDifficultyText.setText(finalDifficulty.toString());
        finalStreakText.setText(String.valueOf(finalStreak));
        finalRoundCountText.setText(String.valueOf(finalRoundCount));

    }
}
