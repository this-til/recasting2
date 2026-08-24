package com.til.recasting.util;

/**
 * 基础值 + 等级倍率。
 */
public record NumberPack(float basics, float multiply) {
    public float of(float level) {
        return this.basics + level * this.multiply;
    }
}
