package com.example.myapplication.play;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class ProgressDotsView extends LinearLayout {
    // Code completely from:
    // https://chatgpt.com/share/67a4e5f9-88a8-8004-9b86-664586e44a13
    private int totalDots = 5; // Default count
    private int currentProgress = 1; // Default progress
    private int dotSize = 20; // Default size in pixels
    private int dotSpacing = 10; // Default spacing in pixels

    public ProgressDotsView(Context context) {
        super(context);
        init(null);
    }

    public ProgressDotsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ProgressDotsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        setOrientation(HORIZONTAL); // Align dots horizontally

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressDotsView);
            totalDots = a.getInt(R.styleable.ProgressDotsView_totalDots, totalDots);
            dotSize = a.getDimensionPixelSize(R.styleable.ProgressDotsView_dotSize, dotSize);
            dotSpacing = a.getDimensionPixelSize(R.styleable.ProgressDotsView_dotSpacing, dotSpacing);
            a.recycle();
        }

        updateProgress(currentProgress);
    }

    public void setProgress(int progress) {
        this.currentProgress = Math.min(progress, totalDots);
        updateProgress(currentProgress);
    }

    public void setTotalDots(int totalDots) {
        this.totalDots = totalDots;
        updateProgress(currentProgress);
    }

    private void updateProgress(int progress) {
        removeAllViews(); // Clear existing dots

        for (int i = 0; i < totalDots; i++) {
            ImageView dot = new ImageView(getContext());
            dot.setImageResource(i < progress ? R.drawable.dot_filled : R.drawable.dot_hollow);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
            if (i > 0) {
                params.setMarginStart(dotSpacing); // Set spacing between dots
            }
            dot.setLayoutParams(params);

            addView(dot);
        }
    }
}
