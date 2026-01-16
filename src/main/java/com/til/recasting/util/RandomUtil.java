package com.til.recasting.util;

import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * 随机工具类
 * 
 * @author til
 */
public class RandomUtil {
    
    /**
     * 在圆形范围内生成随机向量
     * 
     * @param random 随机数生成器
     * @param radius 半径
     * @return 随机向量
     */
    public static Vec3 nextVector3dOnCircles(Random random, double radius) {
        double theta = random.nextDouble() * 2.0 * Math.PI;
        double phi = random.nextDouble() * Math.PI;
        double x = radius * Math.sin(phi) * Math.cos(theta);
        double y = radius * Math.sin(phi) * Math.sin(theta);
        double z = radius * Math.cos(phi);
        return new Vec3(x, y, z);
    }
}

