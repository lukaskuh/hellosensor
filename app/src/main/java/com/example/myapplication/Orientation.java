package com.example.myapplication;

// Code inspired from:
// https://chatgpt.com/share/67963115-acbc-8004-86db-734499784833
public enum Orientation {
    FLAT(new float[]{0, 0, 1}),
    FLAT_REVERSE(new float[]{0, 0, -1}),
    PORTRAIT(new float[]{0, 1, 0}),
    PORTRAIT_REVERSE(new float[]{0, -1, 0}),
    LANDSCAPE_LEFT(new float[]{1, 0, 0}),
    LANDSCAPE_RIGHT(new float[]{-1, 0, 0}),
    SHAKE(new float[]{0, 0, 0}),
    ERROR(new float[]{0, 0, 0});


    private final float[] vector;

    Orientation(float[] value) {
        this.vector = value;
    }

    public float[] getVector() {
        return vector;
    }

    public float dot(float[] other) {
        return vector[0] * other[0] + vector[1] * other[1] + vector[2] * other[2];
    }

    public float dot(float[] other, float factor) {
        return vector[0] * other[0] / factor + vector[1] * other[1] / factor + vector[2] * other[2] / factor;
    }


//    if (gravity.y > GRAVITY_THRESHOLD) {
//        return Orientation.PORTRAIT;
//    } else if (gravity.y < -GRAVITY_THRESHOLD) {
//        return Orientation.PORTRAIT_REVERSE;
//    } else if (gravity.z > GRAVITY_THRESHOLD) {
//        return Orientation.FLAT;
//    } else if (gravity.z < -GRAVITY_THRESHOLD) {
//        return Orientation.FLAT_REVERSE;
//    } else if (gravity.x > GRAVITY_THRESHOLD) {
//        return Orientation.LANDSCAPE_LEFT;
//    } else if (gravity.x < -GRAVITY_THRESHOLD) {
//        return Orientation.LANDSCAPE_RIGHT;
//    }
}
