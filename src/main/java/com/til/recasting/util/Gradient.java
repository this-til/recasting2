package com.til.recasting.util;

import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unity Gradient 实现
 * 用于在多个颜色和 alpha 关键点之间进行插值
 */
public class Gradient {

    /**
     * 颜色关键点
     */
    @Getter
    public static class ColorKey {
        private final float r, g, b, a;
        private final float time;

        /**
         * 创建颜色关键点
         *
         * @param r    红色分量 (0.0-1.0)
         * @param g    绿色分量 (0.0-1.0)
         * @param b    蓝色分量 (0.0-1.0)
         * @param a    Alpha 分量 (0.0-1.0)
         * @param time 时间点 (0.0-1.0)
         */
        public ColorKey(float r, float g, float b, float a, float time) {
            if (time < 0.0f || time > 1.0f) {
                throw new IllegalArgumentException("time 必须在 0.0-1.0 之间，当前值: " + time);
            }
            if (r < 0.0f || r > 1.0f || g < 0.0f || g > 1.0f ||
                    b < 0.0f || b > 1.0f || a < 0.0f || a > 1.0f) {
                throw new IllegalArgumentException("颜色分量必须在 0.0-1.0 之间");
            }
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.time = time;
        }

    }

    /**
     * Alpha 关键点
     */
    @Getter
    public static class AlphaKey {
        private final float alpha;
        private final float time;

        /**
         * 创建 Alpha 关键点
         *
         * @param alpha Alpha 值 (0.0-1.0)
         * @param time  时间点 (0.0-1.0)
         */
        public AlphaKey(float alpha, float time) {
            if (time < 0.0f || time > 1.0f) {
                throw new IllegalArgumentException("time 必须在 0.0-1.0 之间，当前值: " + time);
            }
            if (alpha < 0.0f || alpha > 1.0f) {
                throw new IllegalArgumentException("alpha 必须在 0.0-1.0 之间，当前值: " + alpha);
            }
            this.alpha = alpha;
            this.time = time;
        }

    }

    /**
     * 混合模式
     */
    public enum Mode {
        /**
         * 线性插值
         */
        BLEND,
        /**
         * 固定值（使用较近的关键点）
         */
        FIXED
    }

    private final List<ColorKey> colorKeys;
    private final List<AlphaKey> alphaKeys;
    @Getter
    private final Mode mode;

    /**
     * 创建默认渐变（黑色到白色）
     */
    public Gradient() {
        this(null, null, Mode.BLEND);
    }

    public Gradient(List<ColorKey> colorKeys, List<AlphaKey> alphaKeys) {
        this(colorKeys, alphaKeys, Mode.BLEND);
    }

    /**
     * 创建渐变
     *
     * @param colorKeys 颜色关键点列表，如果为 null 则使用默认值（黑色到白色）
     * @param alphaKeys Alpha 关键点列表，如果为 null 则使用默认值（完全不透明）
     * @param mode      混合模式
     */
    public Gradient(List<ColorKey> colorKeys, List<AlphaKey> alphaKeys, Mode mode) {
        // 设置颜色关键点
        if (colorKeys == null || colorKeys.isEmpty()) {
            this.colorKeys = new ArrayList<>();
            this.colorKeys.add(new ColorKey(0.0f, 0.0f, 0.0f, 1.0f, 0.0f)); // 黑色
            this.colorKeys.add(new ColorKey(1.0f, 1.0f, 1.0f, 1.0f, 1.0f)); // 白色
        } else {
            this.colorKeys = new ArrayList<>(colorKeys);
            this.colorKeys.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
            validateColorKeys(this.colorKeys);
        }

        // 设置 Alpha 关键点
        if (alphaKeys == null || alphaKeys.isEmpty()) {
            this.alphaKeys = new ArrayList<>();
            this.alphaKeys.add(new AlphaKey(1.0f, 0.0f)); // 完全不透明
            this.alphaKeys.add(new AlphaKey(1.0f, 1.0f)); // 完全不透明
        } else {
            this.alphaKeys = new ArrayList<>(alphaKeys);
            this.alphaKeys.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
            validateAlphaKeys(this.alphaKeys);
        }

        this.mode = mode;
    }

    /**
     * 验证颜色关键点
     */
    private void validateColorKeys(List<ColorKey> keys) {
        if (keys.isEmpty()) {
            throw new IllegalArgumentException("colorKeys 不能为空");
        }
        if (keys.get(0).getTime() != 0.0f) {
            throw new IllegalArgumentException("第一个颜色关键点的 time 必须为 0.0");
        }
        if (keys.get(keys.size() - 1).getTime() != 1.0f) {
            throw new IllegalArgumentException("最后一个颜色关键点的 time 必须为 1.0");
        }
    }

    /**
     * 验证 Alpha 关键点
     */
    private void validateAlphaKeys(List<AlphaKey> keys) {
        if (keys.isEmpty()) {
            throw new IllegalArgumentException("alphaKeys 不能为空");
        }
        if (keys.get(0).getTime() != 0.0f) {
            throw new IllegalArgumentException("第一个 alpha 关键点的 time 必须为 0.0");
        }
        if (keys.get(keys.size() - 1).getTime() != 1.0f) {
            throw new IllegalArgumentException("最后一个 alpha 关键点的 time 必须为 1.0");
        }
    }

    /**
     * 评估渐变在指定时间点的颜色值
     *
     * @param time 时间点 (0.0-1.0)
     * @return RGBA 颜色值数组，范围 0.0-1.0
     */
    public float[] evaluate(float time) {
        // 限制时间范围
        time = Math.max(0.0f, Math.min(1.0f, time));

        // 插值颜色
        float[] rgb = interpolateColor(time);

        // 插值 alpha
        float alpha = interpolateAlpha(time);

        return new float[]{rgb[0], rgb[1], rgb[2], alpha};
    }

    /**
     * 评估渐变在指定时间点的 RGB 颜色值（不包含 alpha）
     *
     * @param time 时间点 (0.0-1.0)
     * @return RGB 颜色值数组，范围 0.0-1.0
     */
    public float[] evaluateRGB(float time) {
        float[] rgba = evaluate(time);
        return new float[]{rgba[0], rgba[1], rgba[2]};
    }

    /**
     * 在颜色关键点之间进行插值
     */
    private float[] interpolateColor(float time) {
        // 找到时间点所在的两个关键点
        for(int i = 0; i < colorKeys.size() - 1; i++) {
            ColorKey key1 = colorKeys.get(i);
            ColorKey key2 = colorKeys.get(i + 1);

            if (key1.getTime() <= time && time <= key2.getTime()) {
                // 计算插值比例
                float t;
                if (key2.getTime() == key1.getTime()) {
                    t = 0.0f;
                } else {
                    t = (time - key1.getTime()) / (key2.getTime() - key1.getTime());
                }

                // 线性插值
                float r, g, b;
                if (mode == Mode.BLEND) {
                    r = key1.getR() + (key2.getR() - key1.getR()) * t;
                    g = key1.getG() + (key2.getG() - key1.getG()) * t;
                    b = key1.getB() + (key2.getB() - key1.getB()) * t;
                } else { // FIXED
                    // 固定模式：使用较近的关键点
                    if (t < 0.5f) {
                        r = key1.getR();
                        g = key1.getG();
                        b = key1.getB();
                    } else {
                        r = key2.getR();
                        g = key2.getG();
                        b = key2.getB();
                    }
                }

                return new float[]{r, g, b};
            }
        }

        // 如果时间超出范围，返回最后一个关键点的颜色
        ColorKey lastKey = colorKeys.get(colorKeys.size() - 1);
        return new float[]{lastKey.getR(), lastKey.getG(), lastKey.getB()};
    }

    /**
     * 在 Alpha 关键点之间进行插值
     */
    private float interpolateAlpha(float time) {
        // 找到时间点所在的两个关键点
        for(int i = 0; i < alphaKeys.size() - 1; i++) {
            AlphaKey key1 = alphaKeys.get(i);
            AlphaKey key2 = alphaKeys.get(i + 1);

            if (key1.getTime() <= time && time <= key2.getTime()) {
                // 计算插值比例
                float t;
                if (key2.getTime() == key1.getTime()) {
                    t = 0.0f;
                } else {
                    t = (time - key1.getTime()) / (key2.getTime() - key1.getTime());
                }

                // 线性插值
                if (mode == Mode.BLEND) {
                    return key1.getAlpha() + (key2.getAlpha() - key1.getAlpha()) * t;
                } else { // FIXED
                    // 固定模式：使用较近的关键点
                    return t < 0.5f
                            ? key1.getAlpha()
                            : key2.getAlpha();
                }
            }
        }

        // 如果时间超出范围，返回最后一个关键点的 alpha
        return alphaKeys.get(alphaKeys.size() - 1).getAlpha();
    }

    /**
     * 评估渐变并返回 0-255 范围的整数值
     *
     * @param time 时间点 (0.0-1.0)
     * @return RGBA 颜色值数组，范围 0-255
     */
    public int[] evaluateToInt(float time) {
        float[] rgba = evaluate(time);
        return new int[]{
                Math.round(rgba[0] * 255),
                Math.round(rgba[1] * 255),
                Math.round(rgba[2] * 255),
                Math.round(rgba[3] * 255)
        };
    }

    /**
     * 评估渐变并返回 0-255 范围的 RGB 整数值（不包含 alpha）
     *
     * @param time 时间点 (0.0-1.0)
     * @return RGB 颜色值数组，范围 0-255
     */
    public int[] evaluateRGBToInt(float time) {
        float[] rgb = evaluateRGB(time);
        return new int[]{
                Math.round(rgb[0] * 255),
                Math.round(rgb[1] * 255),
                Math.round(rgb[2] * 255)
        };
    }

    /**
     * 评估渐变并返回 Minecraft 格式的颜色值（ARGB，0xAARRGGBB）
     *
     * @param time 时间点 (0.0-1.0)
     * @return ARGB 颜色值
     */
    public int evaluateToARGB(float time) {
        float[] rgba = evaluate(time);
        int a = Math.round(rgba[3] * 255);
        int r = Math.round(rgba[0] * 255);
        int g = Math.round(rgba[1] * 255);
        int b = Math.round(rgba[2] * 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * 评估渐变并返回 Minecraft 格式的 RGB 颜色值（0xRRGGBB，不包含 alpha）
     *
     * @param time 时间点 (0.0-1.0)
     * @return RGB 颜色值
     */
    public int evaluateToRGB(float time) {
        float[] rgb = evaluateRGB(time);
        int r = Math.round(rgb[0] * 255);
        int g = Math.round(rgb[1] * 255);
        int b = Math.round(rgb[2] * 255);
        return (r << 16) | (g << 8) | b;
    }

    /**
     * 设置颜色关键点
     */
    public void setColorKeys(List<ColorKey> colorKeys) {
        if (colorKeys == null || colorKeys.isEmpty()) {
            throw new IllegalArgumentException("colorKeys 不能为空");
        }
        this.colorKeys.clear();
        this.colorKeys.addAll(colorKeys);
        this.colorKeys.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
        validateColorKeys(this.colorKeys);
    }

    /**
     * 设置 Alpha 关键点
     */
    public void setAlphaKeys(List<AlphaKey> alphaKeys) {
        if (alphaKeys == null || alphaKeys.isEmpty()) {
            throw new IllegalArgumentException("alphaKeys 不能为空");
        }
        this.alphaKeys.clear();
        this.alphaKeys.addAll(alphaKeys);
        this.alphaKeys.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
        validateAlphaKeys(this.alphaKeys);
    }

    /**
     * 添加颜色关键点（会自动排序）
     */
    public void addColorKey(ColorKey colorKey) {
        colorKeys.add(colorKey);
        colorKeys.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
        validateColorKeys(colorKeys);
    }

    /**
     * 添加 Alpha 关键点（会自动排序）
     */
    public void addAlphaKey(AlphaKey alphaKey) {
        alphaKeys.add(alphaKey);
        alphaKeys.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
        validateAlphaKeys(alphaKeys);
    }

    public List<ColorKey> getColorKeys() {
        return Collections.unmodifiableList(colorKeys);
    }

    public List<AlphaKey> getAlphaKeys() {
        return Collections.unmodifiableList(alphaKeys);
    }

    /**
     * 创建彩虹渐变（红 -> 橙 -> 黄 -> 绿 -> 青 -> 蓝 -> 紫 -> 红）
     */
    public static Gradient createRainbowGradient() {
        List<ColorKey> colors = new ArrayList<>();
        colors.add(new ColorKey(1.0f, 0.0f, 0.0f, 1.0f, 0.0f));  // 红
        colors.add(new ColorKey(1.0f, 0.5f, 0.0f, 1.0f, 1.0f / 6.0f));  // 橙
        colors.add(new ColorKey(1.0f, 1.0f, 0.0f, 1.0f, 2.0f / 6.0f));  // 黄
        colors.add(new ColorKey(0.0f, 1.0f, 0.0f, 1.0f, 3.0f / 6.0f));  // 绿
        colors.add(new ColorKey(0.0f, 1.0f, 1.0f, 1.0f, 4.0f / 6.0f));  // 青
        colors.add(new ColorKey(0.0f, 0.0f, 1.0f, 1.0f, 5.0f / 6.0f));  // 蓝
        colors.add(new ColorKey(0.5f, 0.0f, 1.0f, 1.0f, 1.0f));  // 紫
        return new Gradient(colors, null, Mode.BLEND);
    }
    
    /**
     * 从 RGB 颜色值创建渐变
     * 从较暗的版本渐变到较亮的版本，再回到较暗的版本（循环）
     * @param baseColor RGB 颜色值（0xRRGGBB 格式）
     * @param darkFactor 暗色系数（默认 0.5）
     * @param brightFactor 亮色系数（默认 1.2）
     * @return Gradient 对象
     */
    public static Gradient createFromColor(int baseColor, float darkFactor, float brightFactor) {
        // 提取 RGB 分量
        int r = (baseColor >> 16) & 0xFF;
        int g = (baseColor >> 8) & 0xFF;
        int b = baseColor & 0xFF;
        
        // 转换为 0.0-1.0 范围
        float rNorm = r / 255.0f;
        float gNorm = g / 255.0f;
        float bNorm = b / 255.0f;
        
        // 创建较暗和较亮的版本
        float darkR = Math.min(1.0f, rNorm * darkFactor);
        float darkG = Math.min(1.0f, gNorm * darkFactor);
        float darkB = Math.min(1.0f, bNorm * darkFactor);
        
        float brightR = Math.min(1.0f, rNorm * brightFactor);
        float brightG = Math.min(1.0f, gNorm * brightFactor);
        float brightB = Math.min(1.0f, bNorm * brightFactor);
        
        List<ColorKey> colorKeys = new ArrayList<>();
        // 从暗色开始（time = 0.0）
        colorKeys.add(new ColorKey(darkR, darkG, darkB, 1.0f, 0.0f));
        // 中间是正常颜色（time = 0.5）
        colorKeys.add(new ColorKey(rNorm, gNorm, bNorm, 1.0f, 0.5f));
        // 最亮（time = 0.75）
        colorKeys.add(new ColorKey(brightR, brightG, brightB, 1.0f, 0.75f));
        // 回到暗色（time = 1.0），实现平滑循环
        colorKeys.add(new ColorKey(darkR, darkG, darkB, 1.0f, 1.0f));
        
        return new Gradient(colorKeys, null, Mode.BLEND);
    }
    
    /**
     * 从 RGB 颜色值创建渐变（使用默认系数）
     * @param baseColor RGB 颜色值（0xRRGGBB 格式）
     * @return Gradient 对象
     */
    public static Gradient createFromColor(int baseColor) {
        return createFromColor(baseColor, 0.5f, 1.2f);
    }
    public static Gradient createFromColor(Color baseColor) {
        return createFromColor(baseColor.getRGB());
    }

}

