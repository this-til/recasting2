#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
图片透明度调整工具
将图片的 alpha 通道值乘以指定值
"""

import os
import sys
import argparse
import shutil
from pathlib import Path
from typing import Optional
import numpy as np
from PIL import Image


class AlphaAdjuster:
    """图片透明度调整器"""
    
    # 支持的图片格式（主要支持带透明度的格式）
    SUPPORTED_FORMATS = {'.png', '.jpg', '.jpeg', '.bmp', '.tiff', '.tif', '.webp', '.gif'}
    
    def __init__(self, alpha_multiplier: float, create_backup: bool = True):
        """
        初始化调整器
        
        Args:
            alpha_multiplier: alpha 通道的乘数 (例如: 0.5 = 半透明, 1.0 = 不变, 2.0 = 更不透明)
            create_backup: 是否创建备份文件
        """
        if alpha_multiplier < 0:
            raise ValueError("alpha 乘数不能为负数")
        
        self.alpha_multiplier = alpha_multiplier
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
    
    def adjust_alpha(self, input_file: Path, output_file: Optional[Path] = None) -> bool:
        """
        调整单个图片的透明度
        
        Args:
            input_file: 输入文件路径
            output_file: 输出文件路径，如果为None则覆盖原文件
            
        Returns:
            调整是否成功
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
                
                # 转换为 RGBA 模式以便处理透明度
                if img.mode == 'RGBA':
                    rgba_img = img.copy()
                elif img.mode == 'LA':
                    # LA 模式转换为 RGBA
                    rgb_img = Image.new('RGB', img.size, (255, 255, 255))
                    rgb_img.paste(img.convert('RGB'))
                    rgba_img = rgb_img.convert('RGBA')
                    # 复制 L 通道作为 alpha
                    alpha = img.split()[1]
                    rgba_img.putalpha(alpha)
                elif img.mode == 'P':
                    # 调色板模式，转换为 RGBA
                    rgba_img = img.convert('RGBA')
                elif img.mode in ('RGB', 'L'):
                    # RGB 或灰度图，添加完全不透明的 alpha 通道
                    rgba_img = img.convert('RGBA')
                else:
                    # 其他模式先转换为 RGB 再添加 alpha
                    rgb_img = img.convert('RGB')
                    rgba_img = rgb_img.convert('RGBA')
                
                # 获取 alpha 通道
                alpha_channel = rgba_img.split()[3]
                
                # 将 alpha 通道转换为 numpy 数组进行处理
                alpha_array = np.array(alpha_channel, dtype=np.float32)
                
                # 乘以指定值并限制在 0-255 范围内
                adjusted_alpha = alpha_array * self.alpha_multiplier
                adjusted_alpha = np.clip(adjusted_alpha, 0, 255).astype(np.uint8)
                
                # 创建新的 alpha 通道图像
                new_alpha = Image.fromarray(adjusted_alpha, mode='L')
                
                # 将新的 alpha 通道应用到图片
                rgba_img.putalpha(new_alpha)
                
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
                
                # 保存调整后的图片
                # 对于不支持透明度的格式，需要转换为 RGB
                save_kwargs = {}
                if output_file.suffix.lower() in ('.jpg', '.jpeg'):
                    # JPG 格式不支持透明度，转换为 RGB
                    rgb_img = Image.new('RGB', rgba_img.size, (255, 255, 255))
                    rgb_img.paste(rgba_img, mask=rgba_img.split()[3])
                    rgb_img.save(output_file, quality=95, **save_kwargs)
                else:
                    # 其他格式可以保持 RGBA
                    rgba_img.save(output_file, **save_kwargs)
                
                # 计算统计信息
                original_alpha_avg = np.mean(alpha_array)
                new_alpha_avg = np.mean(adjusted_alpha)
            
            print(f"[成功] 调整完成: {input_file}")
            print(f"   -> 输出: {output_file}")
            print(f"   -> 原始模式: {original_mode}")
            print(f"   -> Alpha 乘数: {self.alpha_multiplier}")
            print(f"   -> 平均透明度: {original_alpha_avg:.1f} -> {new_alpha_avg:.1f}")
            
            self.processed_files += 1
            return True
            
        except Exception as e:
            error_msg = f"调整文件时发生错误 {input_file}: {e}"
            self.errors.append(error_msg)
            print(f"[错误] {error_msg}")
            import traceback
            traceback.print_exc()
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
            
            self.adjust_alpha(image_file, output_file)
            print()
    
    def print_summary(self) -> None:
        """打印处理总结"""
        print("=" * 60)
        print("处理总结")
        print("=" * 60)
        print(f"[成功] 成功处理: {self.processed_files} 个文件")
        print(f"[失败] 处理失败: {self.failed_files} 个文件")
        print(f"[设置] Alpha 乘数: {self.alpha_multiplier}")
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
        description="图片透明度调整工具 - 将图片的 alpha 通道值乘以指定值",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例用法:
  # 将图片透明度减半 (alpha * 0.5)
  python adjust_alpha.py image.png --alpha 0.5
  
  # 将图片透明度设为原来的 0.3 倍
  python adjust_alpha.py image.png --alpha 0.3
  
  # 调整后输出到新文件
  python adjust_alpha.py image.png --alpha 0.5 --output image_transparent.png
  
  # 批量处理目录
  python adjust_alpha.py images/ --alpha 0.5 --recursive
  
  # 批量处理并输出到指定目录
  python adjust_alpha.py images/ --alpha 0.5 --output-dir adjusted/ --recursive

支持的格式:
  PNG, JPG, JPEG, BMP, TIFF, TIF, WEBP, GIF

注意:
  - Alpha 乘数范围: >= 0.0
  - 0.0 = 完全透明, 1.0 = 保持原样, > 1.0 = 更不透明（限制在255）
  - 默认会创建 .backup 备份文件
  - 对于没有透明度的图片，会添加完全不透明的 alpha 通道后再调整
  - JPG 格式不支持透明度，输出时会转换为 RGB（透明度信息丢失）
        """
    )
    
    parser.add_argument(
        'input_path',
        help='输入文件或目录路径'
    )
    
    parser.add_argument(
        '--alpha', '-a',
        type=float,
        required=True,
        help='Alpha 通道乘数 (例如: 0.5=半透明, 1.0=不变, 2.0=更不透明)'
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
    
    if args.alpha < 0:
        print("错误: Alpha 乘数不能为负数")
        sys.exit(1)
    
    # 检查依赖库是否可用
    try:
        from PIL import Image
        import numpy as np
    except ImportError as e:
        print("错误: 需要安装必要的库")
        print("请运行: pip install Pillow numpy")
        sys.exit(1)
    
    # 创建调整器
    adjuster = AlphaAdjuster(
        alpha_multiplier=args.alpha,
        create_backup=not args.no_backup
    )
    
    print("图片透明度调整工具")
    print("=" * 60)
    print(f"输入路径: {input_path}")
    print(f"Alpha 乘数: {args.alpha}")
    
    try:
        if input_path.is_file():
            # 单文件模式
            if args.output_dir:
                print("警告: 单文件模式下忽略 --output-dir 参数")
            
            output_file = Path(args.output) if args.output else None
            if output_file:
                print(f"输出文件: {output_file}")
            
            print("-" * 60)
            adjuster.adjust_alpha(input_path, output_file)
            
        elif input_path.is_dir():
            # 目录模式
            if args.output:
                print("警告: 目录模式下忽略 --output 参数")
            
            output_dir = Path(args.output_dir) if args.output_dir else None
            if output_dir:
                print(f"输出目录: {output_dir}")
                output_dir.mkdir(parents=True, exist_ok=True)
            
            print("-" * 60)
            adjuster.process_directory(input_path, output_dir, args.recursive)
            
        else:
            print(f"错误: 路径不存在或不是文件/目录: {input_path}")
            sys.exit(1)
        
        # 打印总结
        adjuster.print_summary()
        
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

