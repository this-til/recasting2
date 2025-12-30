#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
文件重命名脚本
将指定目录下所有子文件夹中的文件重命名为文件夹名称，保留原始文件扩展名
"""

import os
import sys
from pathlib import Path


def rename_files_in_folder(folder_path: Path, dry_run: bool = False) -> bool:
    """
    重命名指定文件夹内的所有文件为文件夹名称
    
    Args:
        folder_path: 文件夹路径
        dry_run: 是否为试运行模式（仅显示将要执行的操作）
    
    Returns:
        bool: 是否成功执行
    """
    if not folder_path.is_dir():
        print(f"错误：{folder_path} 不是一个有效的文件夹")
        return False
    
    folder_name = folder_path.name
    files = [f for f in folder_path.iterdir() if f.is_file()]
    
    if not files:
        print(f"跳过空文件夹：{folder_path}")
        return True
    
    print(f"\n处理文件夹：{folder_path}")
    
    for file_path in files:
        # 获取文件扩展名
        file_extension = file_path.suffix
        # 新文件名 = 文件夹名 + 原扩展名
        new_filename = f"{folder_name}{file_extension}"
        new_file_path = folder_path / new_filename
        
        # 如果新文件名和原文件名相同，跳过
        if file_path.name == new_filename:
            print(f"  跳过（已是正确名称）：{file_path.name}")
            continue
        
        # 检查目标文件是否已存在
        if new_file_path.exists() and new_file_path != file_path:
            print(f"  警告：目标文件已存在，跳过：{file_path.name} -> {new_filename}")
            continue
        
        if dry_run:
            print(f"  [试运行] {file_path.name} -> {new_filename}")
        else:
            try:
                file_path.rename(new_file_path)
                print(f"  ✓ {file_path.name} -> {new_filename}")
            except Exception as e:
                print(f"  ✗ 重命名失败：{file_path.name} -> {new_filename}, 错误：{e}")
                return False
    
    return True


def process_directory(target_dir: str, dry_run: bool = False, recursive: bool = True) -> None:
    """
    处理指定目录下的所有子文件夹
    
    Args:
        target_dir: 目标目录路径
        dry_run: 是否为试运行模式
        recursive: 是否递归处理子目录
    """
    target_path = Path(target_dir)
    
    if not target_path.exists():
        print(f"错误：目录 {target_dir} 不存在")
        return
    
    if not target_path.is_dir():
        print(f"错误：{target_dir} 不是一个目录")
        return
    
    print(f"开始处理目录：{target_path.absolute()}")
    print(f"模式：{'试运行' if dry_run else '实际执行'}")
    print(f"递归处理：{'是' if recursive else '否'}")
    
    # 获取所有子文件夹
    if recursive:
        # 递归获取所有子文件夹
        subfolders = [p for p in target_path.rglob('*') if p.is_dir()]
    else:
        # 只获取直接子文件夹
        subfolders = [p for p in target_path.iterdir() if p.is_dir()]
    
    if not subfolders:
        print("未找到任何子文件夹")
        return
    
    print(f"找到 {len(subfolders)} 个文件夹")
    
    success_count = 0
    for folder in sorted(subfolders):
        if rename_files_in_folder(folder, dry_run):
            success_count += 1
    
    print(f"\n处理完成：{success_count}/{len(subfolders)} 个文件夹处理成功")


def main():
    """主函数"""
    if len(sys.argv) < 2:
        print("使用方法：")
        print("  python rename_files_by_folder.py <目标目录> [选项]")
        print("")
        print("选项：")
        print("  --dry-run    试运行模式，仅显示将要执行的操作")
        print("  --no-recursive    不递归处理子目录")
        print("")
        print("示例：")
        print("  python rename_files_by_folder.py ../src/main/resources/assets/recasting/slashblade")
        print("  python rename_files_by_folder.py ../src/main/resources/assets/recasting/slashblade --dry-run")
        return
    
    target_dir = sys.argv[1]
    dry_run = '--dry-run' in sys.argv
    recursive = '--no-recursive' not in sys.argv
    
    try:
        process_directory(target_dir, dry_run, recursive)
    except KeyboardInterrupt:
        print("\n操作被用户中断")
    except Exception as e:
        print(f"发生错误：{e}")


if __name__ == "__main__":
    main()
