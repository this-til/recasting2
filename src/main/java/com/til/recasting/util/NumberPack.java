package com.til.recasting.util;

public record NumberPack(float basics, float multiply) {
    public float of(float level) {
        return this.basics + level * this.multiply;
    }
}
