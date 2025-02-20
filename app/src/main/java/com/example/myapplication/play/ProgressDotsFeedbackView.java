package com.example.myapplication.play;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class ProgressDotsFeedbackView extends ProgressDotsView {
    private boolean[] fails = new boolean[totalDots];

    public ProgressDotsFeedbackView(Context context) {
        super(context);
    }

    public ProgressDotsFeedbackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressDotsFeedbackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTotalDots(int totalDots) {
        fails = new boolean[totalDots];
        super.setTotalDots(totalDots);
    }

    public void setProgress(int progress, boolean failed) {
        fails[progress-1] = failed;
        updateProgress(progress);
    }

    public void setFeedback(int i, boolean failed) {
        fails[i] = failed;
    }

    private void updateProgress(int progress) {
        removeAllViews(); // Clear existing dots

        for (int i = 0; i < totalDots; i++) {
            ImageView dot = new ImageView(getContext());

            var filledSrc = fails[i] ? R.drawable.dot_filled_error : R.drawable.dot_filled;

            dot.setImageResource(i < progress ? filledSrc : R.drawable.dot_hollow);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
            if (i > 0) {
                params.setMarginStart(dotSpacing); // Set spacing between dots
            }
            dot.setLayoutParams(params);

            addView(dot);
        }
    }
}
