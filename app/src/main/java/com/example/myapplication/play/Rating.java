package com.example.myapplication.play;

import androidx.annotation.NonNull;

public enum Rating {
    APlus("A+"),
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E"),
    F("F");


    private final String text;

    Rating(String text) {
        this.text = text;
    }

    static public Rating rateScore(float score) {
        if (score > 0.97f) {
            return APlus;
        } else if (score > 0.95f) {
            return A;
        } else if (score > 0.9f) {
            return B;
        } else if (score > 0.8f) {
            return C;
        } else if (score > 0.7f) {
            return D;
        } else if (score > 0.6f) {
            return E;
        } else {
            return F;
        }
    }

    @Override
    public String toString() {
        return text;
    }



}
