package com.example.myapplication.play;

import com.example.myapplication.R;

public enum GameState {
    PRE(R.id.ResultLayout),
    COUNTDOWN(R.id.CountdownLayout),
    INSTRUCTIONS(R.id.InstructionsLayout),
    PLAY(R.id.nextLevelButton),
    FEEDBACK(R.id.FeedbackLayout),
    BETWEEN(R.id.BetweenLayout),
    POST(R.id.ResultLayout),
    DEBUG(R.id.DebugLayout);

    private final int layoutId;

    GameState(int value) {
        this.layoutId = value;
    }

    public int getLayoutId() {
        return layoutId;
    }

}
