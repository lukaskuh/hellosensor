package com.example.myapplication.play;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class HeartCountView extends LinearLayout {
    // Code completely from:
    // https://chatgpt.com/share/67a4e5f9-88a8-8004-9b86-664586e44a13
    private int totalLives = 3; // Default count
    private int livesLeft = totalLives; // Default progress
    private int heartSize = 20; // Default size in pixels
    private int heartSpacing = 10; // Default spacing in pixels

    public HeartCountView(Context context) {
        super(context);
        init(null);
    }

    public HeartCountView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HeartCountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        setOrientation(HORIZONTAL); // Align dots horizontally

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HeartCountView);
            totalLives = a.getInt(R.styleable.ProgressDotsView_totalDots, totalLives);
            heartSize = a.getDimensionPixelSize(R.styleable.ProgressDotsView_dotSize, heartSize);
            heartSpacing = a.getDimensionPixelSize(R.styleable.ProgressDotsView_dotSpacing, heartSpacing);
            a.recycle();
        }

        updateProgress(livesLeft);
    }

    public void setLivesLeft(int lives) {
        Log.d("GAME", "setLivesLeftAAAAA: " + lives);
        this.livesLeft = Math.min(Math.max(lives, 0), totalLives);
        updateProgress(livesLeft);
    }

    public void setTotalLives(int totalLives) {
        this.totalLives = totalLives;
        updateProgress(livesLeft);
    }

    private void updateProgress(int progress) {
        removeAllViews(); // Clear existing dots

        for (int i = 0; i < totalLives; i++) {
            ImageView dot = new ImageView(getContext());
            dot.setImageResource(i >= progress ? R.drawable.heart_hollow : R.drawable.heart_filled);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(heartSize, heartSize);
            if (i > 0) {
                params.setMarginStart(heartSpacing); // Set spacing between dots
            }
            dot.setLayoutParams(params);

            addView(dot);
        }
    }
}
