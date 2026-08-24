package com.til.recasting.registry.se;

/**
 * 屠巫 / 人皇阶梯共用数值。
 */
public interface EmperorLineStats {

    int getLineGrade();

    float getDamageAmplifier();

    int getProudPerDamage();

    int getMaxProudPerHit();

    int getProtectThreshold();

    int getFoodProudCost();

    int getFoodRestore();
}
