#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Unity Gradient 实现
提供颜色和 alpha 渐变的插值功能
"""

from typing import List, Tuple, Optional
from dataclasses import dataclass


@dataclass
class ColorKey:
    """颜色关键点"""
    color: Tuple[float, float, float, float]  # RGBA, 范围 0.0-1.0
    time: float  # 时间点, 范围 0.0-1.0
    
    def __post_init__(self):
        """验证数据有效性"""
        if not (0.0 <= self.time <= 1.0):
            raise ValueError(f"time 必须在 0.0-1.0 之间，当前值: {self.time}")
        if len(self.color) != 4:
            raise ValueError(f"color 必须是 4 个值的元组 (RGBA)，当前长度: {len(self.color)}")
        if not all(0.0 <= c <= 1.0 for c in self.color):
            raise ValueError(f"color 的所有值必须在 0.0-1.0 之间，当前值: {self.color}")


@dataclass
class AlphaKey:
    """Alpha 关键点"""
    alpha: float  # Alpha 值, 范围 0.0-1.0
    time: float  # 时间点, 范围 0.0-1.0
    
    def __post_init__(self):
        """验证数据有效性"""
        if not (0.0 <= self.time <= 1.0):
            raise ValueError(f"time 必须在 0.0-1.0 之间，当前值: {self.time}")
        if not (0.0 <= self.alpha <= 1.0):
            raise ValueError(f"alpha 必须在 0.0-1.0 之间，当前值: {self.alpha}")


class Gradient:
    """
    Unity Gradient 实现
    
    用于在多个颜色和 alpha 关键点之间进行插值。
    支持线性插值和不同的混合模式。
    """
    
    def __init__(self, 
                 color_keys: Optional[List[ColorKey]] = None,
                 alpha_keys: Optional[List[AlphaKey]] = None,
                 mode: str = 'Blend'):
        """
        初始化渐变
        
        Args:
            color_keys: 颜色关键点列表，如果为 None 则使用默认值（黑色到白色）
            alpha_keys: Alpha 关键点列表，如果为 None 则使用默认值（完全不透明）
            mode: 混合模式，'Blend'（线性插值）或 'Fixed'（固定值）
        """
        # 设置颜色关键点
        if color_keys is None:
            self.color_keys = [
                ColorKey((0.0, 0.0, 0.0, 1.0), 0.0),  # 黑色
                ColorKey((1.0, 1.0, 1.0, 1.0), 1.0)   # 白色
            ]
        else:
            if not color_keys:
                raise ValueError("color_keys 不能为空列表")
            # 按时间排序
            self.color_keys = sorted(color_keys, key=lambda k: k.time)
            # 验证时间范围
            if self.color_keys[0].time != 0.0:
                raise ValueError("第一个颜色关键点的 time 必须为 0.0")
            if self.color_keys[-1].time != 1.0:
                raise ValueError("最后一个颜色关键点的 time 必须为 1.0")
        
        # 设置 Alpha 关键点
        if alpha_keys is None:
            self.alpha_keys = [
                AlphaKey(1.0, 0.0),  # 完全不透明
                AlphaKey(1.0, 1.0)   # 完全不透明
            ]
        else:
            if not alpha_keys:
                raise ValueError("alpha_keys 不能为空列表")
            # 按时间排序
            self.alpha_keys = sorted(alpha_keys, key=lambda k: k.time)
            # 验证时间范围
            if self.alpha_keys[0].time != 0.0:
                raise ValueError("第一个 alpha 关键点的 time 必须为 0.0")
            if self.alpha_keys[-1].time != 1.0:
                raise ValueError("最后一个 alpha 关键点的 time 必须为 1.0")
        
        # 设置混合模式
        if mode not in ('Blend', 'Fixed'):
            raise ValueError(f"mode 必须是 'Blend' 或 'Fixed'，当前值: {mode}")
        self.mode = mode
    
    def evaluate(self, time: float) -> Tuple[float, float, float, float]:
        """
        评估渐变在指定时间点的颜色值
        
        Args:
            time: 时间点，范围 0.0-1.0
        
        Returns:
            RGBA 颜色值元组，范围 0.0-1.0
        """
        # 限制时间范围
        time = max(0.0, min(1.0, time))
        
        # 插值颜色
        color = self._interpolate_color(time)
        
        # 插值 alpha
        alpha = self._interpolate_alpha(time)
        
        # 返回 RGBA
        return (color[0], color[1], color[2], alpha)
    
    def evaluate_rgb(self, time: float) -> Tuple[float, float, float]:
        """
        评估渐变在指定时间点的 RGB 颜色值（不包含 alpha）
        
        Args:
            time: 时间点，范围 0.0-1.0
        
        Returns:
            RGB 颜色值元组，范围 0.0-1.0
        """
        rgba = self.evaluate(time)
        return rgba[:3]
    
    def _interpolate_color(self, time: float) -> Tuple[float, float, float]:
        """
        在颜色关键点之间进行插值
        
        Args:
            time: 时间点，范围 0.0-1.0
        
        Returns:
            RGB 颜色值元组
        """
        # 找到时间点所在的两个关键点
        for i in range(len(self.color_keys) - 1):
            key1 = self.color_keys[i]
            key2 = self.color_keys[i + 1]
            
            if key1.time <= time <= key2.time:
                # 计算插值比例
                if key2.time == key1.time:
                    t = 0.0
                else:
                    t = (time - key1.time) / (key2.time - key1.time)
                
                # 线性插值
                if self.mode == 'Blend':
                    r = key1.color[0] + (key2.color[0] - key1.color[0]) * t
                    g = key1.color[1] + (key2.color[1] - key1.color[1]) * t
                    b = key1.color[2] + (key2.color[2] - key1.color[2]) * t
                else:  # Fixed
                    # 固定模式：使用较近的关键点
                    if t < 0.5:
                        r, g, b = key1.color[0], key1.color[1], key1.color[2]
                    else:
                        r, g, b = key2.color[0], key2.color[1], key2.color[2]
                
                return (r, g, b)
        
        # 如果时间超出范围，返回最后一个关键点的颜色
        last_key = self.color_keys[-1]
        return last_key.color[:3]
    
    def _interpolate_alpha(self, time: float) -> float:
        """
        在 Alpha 关键点之间进行插值
        
        Args:
            time: 时间点，范围 0.0-1.0
        
        Returns:
            Alpha 值
        """
        # 找到时间点所在的两个关键点
        for i in range(len(self.alpha_keys) - 1):
            key1 = self.alpha_keys[i]
            key2 = self.alpha_keys[i + 1]
            
            if key1.time <= time <= key2.time:
                # 计算插值比例
                if key2.time == key1.time:
                    t = 0.0
                else:
                    t = (time - key1.time) / (key2.time - key1.time)
                
                # 线性插值
                if self.mode == 'Blend':
                    alpha = key1.alpha + (key2.alpha - key1.alpha) * t
                else:  # Fixed
                    # 固定模式：使用较近的关键点
                    alpha = key1.alpha if t < 0.5 else key2.alpha
                
                return alpha
        
        # 如果时间超出范围，返回最后一个关键点的 alpha
        return self.alpha_keys[-1].alpha
    
    def set_color_keys(self, color_keys: List[ColorKey]) -> None:
        """
        设置颜色关键点
        
        Args:
            color_keys: 颜色关键点列表
        """
        if not color_keys:
            raise ValueError("color_keys 不能为空列表")
        # 按时间排序
        self.color_keys = sorted(color_keys, key=lambda k: k.time)
        # 验证时间范围
        if self.color_keys[0].time != 0.0:
            raise ValueError("第一个颜色关键点的 time 必须为 0.0")
        if self.color_keys[-1].time != 1.0:
            raise ValueError("最后一个颜色关键点的 time 必须为 1.0")
    
    def set_alpha_keys(self, alpha_keys: List[AlphaKey]) -> None:
        """
        设置 Alpha 关键点
        
        Args:
            alpha_keys: Alpha 关键点列表
        """
        if not alpha_keys:
            raise ValueError("alpha_keys 不能为空列表")
        # 按时间排序
        self.alpha_keys = sorted(alpha_keys, key=lambda k: k.time)
        # 验证时间范围
        if self.alpha_keys[0].time != 0.0:
            raise ValueError("第一个 alpha 关键点的 time 必须为 0.0")
        if self.alpha_keys[-1].time != 1.0:
            raise ValueError("最后一个 alpha 关键点的 time 必须为 1.0")
    
    def add_color_key(self, color_key: ColorKey) -> None:
        """
        添加颜色关键点（会自动排序）
        
        Args:
            color_key: 颜色关键点
        """
        self.color_keys.append(color_key)
        self.color_keys = sorted(self.color_keys, key=lambda k: k.time)
        # 验证时间范围
        if self.color_keys[0].time != 0.0:
            raise ValueError("第一个颜色关键点的 time 必须为 0.0")
        if self.color_keys[-1].time != 1.0:
            raise ValueError("最后一个颜色关键点的 time 必须为 1.0")
    
    def add_alpha_key(self, alpha_key: AlphaKey) -> None:
        """
        添加 Alpha 关键点（会自动排序）
        
        Args:
            alpha_key: Alpha 关键点
        """
        self.alpha_keys.append(alpha_key)
        self.alpha_keys = sorted(self.alpha_keys, key=lambda k: k.time)
        # 验证时间范围
        if self.alpha_keys[0].time != 0.0:
            raise ValueError("第一个 alpha 关键点的 time 必须为 0.0")
        if self.alpha_keys[-1].time != 1.0:
            raise ValueError("最后一个 alpha 关键点的 time 必须为 1.0")
    
    def to_uint8(self, time: float) -> Tuple[int, int, int, int]:
        """
        评估渐变并返回 0-255 范围的整数值
        
        Args:
            time: 时间点，范围 0.0-1.0
        
        Returns:
            RGBA 颜色值元组，范围 0-255
        """
        rgba = self.evaluate(time)
        return tuple(int(c * 255) for c in rgba)
    
    def to_uint8_rgb(self, time: float) -> Tuple[int, int, int]:
        """
        评估渐变并返回 0-255 范围的 RGB 整数值（不包含 alpha）
        
        Args:
            time: 时间点，范围 0.0-1.0
        
        Returns:
            RGB 颜色值元组，范围 0-255
        """
        rgb = self.evaluate_rgb(time)
        return tuple(int(c * 255) for c in rgb)
    
    def __repr__(self) -> str:
        """返回字符串表示"""
        return (f"Gradient(color_keys={len(self.color_keys)}, "
                f"alpha_keys={len(self.alpha_keys)}, mode='{self.mode}')")


# 便捷函数
def create_gradient_from_colors(colors: List[Tuple[float, float, float, float]], 
                                times: Optional[List[float]] = None) -> Gradient:
    """
    从颜色列表创建渐变
    
    Args:
        colors: RGBA 颜色列表，范围 0.0-1.0
        times: 时间点列表，如果为 None 则均匀分布
    
    Returns:
        Gradient 对象
    """
    if not colors:
        raise ValueError("colors 不能为空")
    
    if times is None:
        # 均匀分布时间点
        times = [i / (len(colors) - 1) for i in range(len(colors))]
    else:
        if len(times) != len(colors):
            raise ValueError("times 的长度必须与 colors 相同")
        # 确保第一个是 0.0，最后一个是 1.0
        times[0] = 0.0
        times[-1] = 1.0
    
    color_keys = [ColorKey(color, time) for color, time in zip(colors, times)]
    return Gradient(color_keys=color_keys)


def create_rainbow_gradient() -> Gradient:
    """
    创建彩虹渐变（红 -> 橙 -> 黄 -> 绿 -> 青 -> 蓝 -> 紫 -> 红）
    
    Returns:
        Gradient 对象
    """
    colors = [
        (1.0, 0.0, 0.0, 1.0),  # 红
        (1.0, 0.5, 0.0, 1.0),  # 橙
        (1.0, 1.0, 0.0, 1.0),  # 黄
        (0.0, 1.0, 0.0, 1.0),  # 绿
        (0.0, 1.0, 1.0, 1.0),  # 青
        (0.0, 0.0, 1.0, 1.0),  # 蓝
        (0.5, 0.0, 1.0, 1.0),  # 紫
        (1.0, 0.0, 0.0, 1.0),  # 红（循环）
    ]
    times = [i / (len(colors) - 1) for i in range(len(colors))]
    color_keys = [ColorKey(color, time) for color, time in zip(colors, times)]
    return Gradient(color_keys=color_keys)


# 示例用法
if __name__ == "__main__":
    # 示例 1: 基本使用
    print("示例 1: 基本使用")
    gradient = Gradient()
    for t in [0.0, 0.25, 0.5, 0.75, 1.0]:
        rgba = gradient.evaluate(t)
        print(f"  t={t:.2f}: RGBA={rgba}")
    print()
    
    # 示例 2: 自定义颜色渐变
    print("示例 2: 自定义颜色渐变（红 -> 绿 -> 蓝）")
    color_keys = [
        ColorKey((1.0, 0.0, 0.0, 1.0), 0.0),  # 红
        ColorKey((0.0, 1.0, 0.0, 1.0), 0.5),  # 绿
        ColorKey((0.0, 0.0, 1.0, 1.0), 1.0),  # 蓝
    ]
    gradient2 = Gradient(color_keys=color_keys)
    for t in [0.0, 0.25, 0.5, 0.75, 1.0]:
        rgba = gradient2.evaluate(t)
        rgb_uint8 = gradient2.to_uint8_rgb(t)
        print(f"  t={t:.2f}: RGBA={rgba}, RGB(uint8)={rgb_uint8}")
    print()
    
    # 示例 3: 带 Alpha 渐变的渐变
    print("示例 3: 带 Alpha 渐变的渐变（从透明到不透明）")
    alpha_keys = [
        AlphaKey(0.0, 0.0),  # 完全透明
        AlphaKey(1.0, 1.0),  # 完全不透明
    ]
    gradient3 = Gradient(alpha_keys=alpha_keys)
    for t in [0.0, 0.25, 0.5, 0.75, 1.0]:
        rgba = gradient3.evaluate(t)
        print(f"  t={t:.2f}: RGBA={rgba}")
    print()
    
    # 示例 4: 彩虹渐变
    print("示例 4: 彩虹渐变")
    rainbow = create_rainbow_gradient()
    for t in [0.0, 0.2, 0.4, 0.6, 0.8, 1.0]:
        rgba = rainbow.evaluate(t)
        rgb_uint8 = rainbow.to_uint8_rgb(t)
        print(f"  t={t:.2f}: RGBA={rgba}, RGB(uint8)={rgb_uint8}")
    print()
    
    # 示例 5: 从颜色列表创建
    print("示例 5: 从颜色列表创建渐变")
    colors = [
        (1.0, 0.0, 0.0, 1.0),  # 红
        (0.0, 1.0, 0.0, 1.0),  # 绿
        (0.0, 0.0, 1.0, 1.0),  # 蓝
    ]
    gradient4 = create_gradient_from_colors(colors)
    for t in [0.0, 0.5, 1.0]:
        rgba = gradient4.evaluate(t)
        print(f"  t={t:.2f}: RGBA={rgba}")

