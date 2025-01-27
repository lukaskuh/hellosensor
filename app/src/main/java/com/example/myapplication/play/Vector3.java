package com.example.myapplication.play;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

public class Vector3 {
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;

    public Vector3() {
        new Vector3(0.0f, 0.0f, 0.0f);
    }

    public Vector3(float[] in) {
        new Vector3(in[0], in[1], in[2]);
    }

    public  Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(float[] in) {
        this.x = in[0];
        this.y = in[1];
        this.z = in[2];
    }

    public float dot(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public float magnitude() {
        return (float) Math.sqrt(
          x * x + y * y + z * z
        );
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("X: %.2f, Y: %.2f, Z: %.2f", this.x, this.y, this.z);
    }

}
