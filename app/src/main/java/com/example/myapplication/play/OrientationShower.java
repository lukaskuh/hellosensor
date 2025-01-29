package com.example.myapplication.play;

import android.widget.ImageView;

import com.example.myapplication.R;

public class OrientationShower {
    private final ImageView imageView;

    public OrientationShower(ImageView imageView) {
        this.imageView = imageView;

    }


    public void setOrientation(Orientation orientation) {
        imageView.setImageResource(getOrientationSource(orientation));
    }

    private int getOrientationSource(Orientation orientation) {
        switch (orientation) {
            case PORTRAIT:
                return R.drawable.portrait;
            case PORTRAIT_REVERSE:
                return R.drawable.portrait_reverse;
            case FLAT:
                return R.drawable.flat;
            case FLAT_REVERSE:
                return R.drawable.flat_reverse;
            case LANDSCAPE_RIGHT:
                return R.drawable.landscape_right;
            case LANDSCAPE_LEFT:
                return R.drawable.landscape_left;
            case SHAKE:
                return R.drawable.shake;
            default:
                return -1;
        }
    }

    public void setVisible(boolean visible) {
        if (visible) {
            imageView.setVisibility(ImageView.VISIBLE);
        } else {
            imageView.setVisibility(ImageView.GONE);
        }
    }



}
