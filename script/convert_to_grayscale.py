#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
图片转灰度图工具
将指定图片转换为灰度图
"""

import os
import sys
import argparse
import shutil
from pathlib import Path
from typing import Optional, List
from PIL import Image


class GrayscaleConverter:
    """图片转灰度图转换器"""
    
    # 支持的图片格式
    SUPPORTED_FORMATS = {'.png', '.jpg', '.jpeg', '.bmp', '.tiff', '.tif', '.webp', '.gif'}
    
    def __init__(self, create_backup: bool = True):
        """
        初始化转换器
        
        Args:
            create_backup: 是否创建备份文件
        """
        self.create_backup = create_backup
        self.processed_files = 0
        self.failed_files = 0
        self.errors = []
    
    def is_supported_format(self, file_path: Path) -> bool:
        """
        检查文件格式是否支持
        
        Args:
            file_path: 文件路径
            
        Returns:
            是否支持该格式
        """
        return file_path.suffix.lower() in self.SUPPORTED_FORMATS
    
    def convert_image(self, input_file: Path, output_file: Optional[Path] = None) -> bool:
        """
        转换单个图片为灰度图，保留 alpha 通道不变
        
        Args:
            input_file: 输入文件路径
            output_file: 输出文件路径，如果为None则覆盖原文件
            
        Returns:
            转换是否成功
        """
        if not input_file.exists():
            error_msg = f"文件不存在: {input_file}"
            self.errors.append(error_msg)
            print(f"错误: {error_msg}")
            return False
        
        if not self.is_supported_format(input_file):
            error_msg = f"不支持的图片格式: {input_file} (支持: {', '.join(self.SUPPORTED_FORMATS)})"
            self.errors.append(error_msg)
            print(f"跳过: {error_msg}")
            return False
        
        try:
            # 打开图片
            original_mode = None
            with Image.open(input_file) as img:
                original_mode = img.mode
                # 转换为灰度图，保留 alpha 通道
                if img.mode == 'RGBA':
                    # RGBA 模式：提取 RGB 转灰度，保留 alpha
                    rgb_channels = img.split()[:3]
                    # 将 RGB 合并后转灰度
                    rgb_img = Image.merge('RGB', rgb_channels)
                    gray_img = rgb_img.convert('L')
                    # 提取 alpha 通道
                    alpha_channel = img.split()[3]
                    # 合并为 LA 模式（灰度 + alpha）
                    gray_img = Image.merge('LA', (gray_img, alpha_channel))
                elif img.mode == 'LA':
                    # LA 模式：已经是灰度+alpha，直接复制
                    gray_img = img.copy()
                elif img.mode == 'P':
                    # 调色板模式：转换为 RGBA 后处理
                    rgba_img = img.convert('RGBA')
                    rgb_channels = rgba_img.split()[:3]
                    rgb_img = Image.merge('RGB', rgb_channels)
                    gray_img = rgb_img.convert('L')
                    # 提取 alpha 通道
                    alpha_channel = rgba_img.split()[3]
                    # 合并为 LA 模式
                    gray_img = Image.merge('LA', (gray_img, alpha_channel))
                elif img.mode == 'L':
                    # 已经是灰度图，直接复制
                    gray_img = img.copy()
                else:
                    # 其他模式（RGB等）直接转换为灰度
                    gray_img = img.convert('L')
                
                # 创建备份
                if self.create_backup and output_file is None:
                    backup_file = input_file.with_suffix(input_file.suffix + '.backup')
                    shutil.copy2(input_file, backup_file)
                    print(f"创建备份: {backup_file}")
                
                # 确定输出文件路径
                if output_file is None:
                    output_file = input_file
                else:
                    # 确保输出目录存在
                    output_file.parent.mkdir(parents=True, exist_ok=True)
                
                # 保存灰度图
                # 保持原始格式，如果是PNG则保持PNG，如果是JPG则保持JPG
                save_kwargs = {}
                if output_file.suffix.lower() in ('.jpg', '.jpeg'):
                    # JPG格式不支持透明度，需要转换为RGB模式
                    if gray_img.mode in ('LA', 'RGBA'):
                        # 如果有alpha通道，先合成到白色背景上
                        if gray_img.mode == 'LA':
                            rgb_img = Image.new('RGB', gray_img.size, (255, 255, 255))
                            rgb_img.paste(gray_img.convert('RGB'), mask=gray_img.split()[1])
                            gray_img = rgb_img
                        else:
                            rgb_img = Image.new('RGB', gray_img.size, (255, 255, 255))
                            rgb_img.paste(gray_img, mask=gray_img.split()[3])
                            gray_img = rgb_img
                    elif gray_img.mode == 'L':
                        gray_img = gray_img.convert('RGB')
                    save_kwargs['quality'] = 95  # 保持较高质量
                elif output_file.suffix.lower() == '.png':
                    # PNG格式可以保持LA模式（灰度+alpha）或L模式（纯灰度）
                    # 如果当前是LA模式，保持LA；如果是L模式，保持L
                    pass
                
                gray_img.save(output_file, **save_kwargs)
            
            print(f"[成功] 转换完成: {input_file}")
            print(f"   -> 输出: {output_file}")
            print(f"   -> 原始模式: {original_mode}")
            
            self.processed_files += 1
            return True
            
        except Exception as e:
            error_msg = f"转换文件时发生错误 {input_file}: {e}"
            self.errors.append(error_msg)
            print(f"[错误] {error_msg}")
            self.failed_files += 1
            return False
    
    def process_directory(self, input_dir: Path, output_dir: Optional[Path] = None, 
                         recursive: bool = False) -> None:
        """
        批量处理目录中的图片文件
        
        Args:
            input_dir: 输入目录
            output_dir: 输出目录，如果为None则覆盖原文件
            recursive: 是否递归处理子目录
        """
        if not input_dir.exists():
            error_msg = f"目录不存在: {input_dir}"
            self.errors.append(error_msg)
            print(f"错误: {error_msg}")
            return
        
        if not input_dir.is_dir():
            error_msg = f"路径不是目录: {input_dir}"
            self.errors.append(error_msg)
            print(f"错误: {error_msg}")
            return
        
        print(f"扫描目录: {input_dir}")
        print(f"递归模式: {'是' if recursive else '否'}")
        
        # 查找所有支持的图片文件
        image_files = []
        if recursive:
            for ext in self.SUPPORTED_FORMATS:
                image_files.extend(input_dir.rglob(f"*{ext}"))
                image_files.extend(input_dir.rglob(f"*{ext.upper()}"))
        else:
            for ext in self.SUPPORTED_FORMATS:
                image_files.extend(input_dir.glob(f"*{ext}"))
                image_files.extend(input_dir.glob(f"*{ext.upper()}"))
        
        # 去重
        image_files = list(set(image_files))
        
        if not image_files:
            print("未找到任何支持的图片文件")
            return
        
        print(f"找到 {len(image_files)} 个图片文件")
        print("-" * 50)
        
        # 处理每个文件
        for image_file in image_files:
            if output_dir:
                # 计算相对路径并创建输出路径
                relative_path = image_file.relative_to(input_dir)
                output_file = output_dir / relative_path
                
                # 确保输出目录存在
                output_file.parent.mkdir(parents=True, exist_ok=True)
            else:
                output_file = None
            
            self.convert_image(image_file, output_file)
            print()
    
    def print_summary(self) -> None:
        """打印处理总结"""
        print("=" * 60)
        print("处理总结")
        print("=" * 60)
        print(f"[成功] 成功处理: {self.processed_files} 个文件")
        print(f"[失败] 处理失败: {self.failed_files} 个文件")
        print(f"[备份] 创建备份: {'是' if self.create_backup else '否'}")
        
        if self.errors:
            print(f"\n错误详情:")
            for i, error in enumerate(self.errors, 1):
                print(f"  {i}. {error}")
        
        if self.processed_files > 0:
            print(f"\n处理完成！")
        else:
            print(f"\n警告: 没有成功处理任何文件")


def main():
    """主函数"""
    parser = argparse.ArgumentParser(
        description="图片转灰度图工具 - 将指定图片转换为灰度图",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例用法:
  # 将单个图片转为灰度图
  python convert_to_grayscale.py image.png
  
  # 转换后输出到新文件
  python convert_to_grayscale.py image.png --output image_gray.png
  
  # 批量处理目录
  python convert_to_grayscale.py images/ --recursive
  
  # 批量处理并输出到指定目录
  python convert_to_grayscale.py images/ --output-dir grayscale/ --recursive

支持的格式:
  PNG, JPG, JPEG, BMP, TIFF, TIF, WEBP, GIF

注意:
  - 默认会创建 .backup 备份文件
  - 如果输出文件已存在，会被覆盖
  - 对于有透明度的图片，会保留 alpha 通道不变
  - JPG 格式不支持透明度，输出时会转换为 RGB（透明度信息丢失）
        """
    )
    
    parser.add_argument(
        'input_path',
        help='输入文件或目录路径'
    )
    
    parser.add_argument(
        '--output', '-o',
        help='输出文件路径 (仅单文件模式有效)'
    )
    
    parser.add_argument(
        '--output-dir', '-d',
        help='输出目录路径 (批量处理模式)'
    )
    
    parser.add_argument(
        '--recursive', '-r',
        action='store_true',
        help='递归处理子目录中的图片文件'
    )
    
    parser.add_argument(
        '--no-backup',
        action='store_true',
        help='不创建备份文件'
    )
    
    args = parser.parse_args()
    
    # 验证输入
    input_path = Path(args.input_path)
    
    # 检查PIL是否可用
    try:
        from PIL import Image
    except ImportError:
        print("错误: 需要安装 Pillow 库")
        print("请运行: pip install Pillow")
        sys.exit(1)
    
    # 创建转换器
    converter = GrayscaleConverter(
        create_backup=not args.no_backup
    )
    
    print("图片转灰度图工具")
    print("=" * 60)
    print(f"输入路径: {input_path}")
    
    try:
        if input_path.is_file():
            # 单文件模式
            if args.output_dir:
                print("警告: 单文件模式下忽略 --output-dir 参数")
            
            output_file = Path(args.output) if args.output else None
            if output_file:
                print(f"输出文件: {output_file}")
            
            print("-" * 60)
            converter.convert_image(input_path, output_file)
            
        elif input_path.is_dir():
            # 目录模式
            if args.output:
                print("警告: 目录模式下忽略 --output 参数")
            
            output_dir = Path(args.output_dir) if args.output_dir else None
            if output_dir:
                print(f"输出目录: {output_dir}")
                output_dir.mkdir(parents=True, exist_ok=True)
            
            print("-" * 60)
            converter.process_directory(input_path, output_dir, args.recursive)
            
        else:
            print(f"错误: 路径不存在或不是文件/目录: {input_path}")
            sys.exit(1)
        
        # 打印总结
        converter.print_summary()
        
    except KeyboardInterrupt:
        print("\n\n用户中断操作")
        sys.exit(1)
    except Exception as e:
        print(f"\n程序发生未预期的错误: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)


if __name__ == "__main__":
    main()

