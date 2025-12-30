#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
文件移动脚本
将指定目录下所有子文件夹中的文件移动到父文件夹，并可选择删除空的子文件夹
"""

import os
import sys
import shutil
from pathlib import Path


def move_files_from_folder(folder_path: Path, target_parent: Path, dry_run: bool = False, remove_empty: bool = False) -> bool:
    """
    将指定文件夹内的所有文件移动到目标父文件夹
    
    Args:
        folder_path: 源文件夹路径
        target_parent: 目标父文件夹路径
        dry_run: 是否为试运行模式
        remove_empty: 是否删除空的源文件夹
    
    Returns:
        bool: 是否成功执行
    """
    if not folder_path.is_dir():
        print(f"错误：{folder_path} 不是一个有效的文件夹")
        return False
    
    if not target_parent.is_dir():
        print(f"错误：目标父文件夹 {target_parent} 不存在")
        return False
    
    files = [f for f in folder_path.iterdir() if f.is_file()]
    
    if not files:
        print(f"跳过空文件夹：{folder_path}")
        if remove_empty and not dry_run:
            try:
                folder_path.rmdir()
                print(f"  ✓ 删除空文件夹：{folder_path}")
            except Exception as e:
                print(f"  ✗ 删除文件夹失败：{e}")
        return True
    
    print(f"\n处理文件夹：{folder_path}")
    print(f"目标目录：{target_parent}")
    
    success_count = 0
    for file_path in files:
        target_file_path = target_parent / file_path.name
        
        # 检查目标文件是否已存在
        if target_file_path.exists():
            print(f"  警告：目标文件已存在，跳过：{file_path.name}")
            continue
        
        if dry_run:
            print(f"  [试运行] 移动：{file_path.name} -> {target_file_path}")
            success_count += 1
        else:
            try:
                shutil.move(str(file_path), str(target_file_path))
                print(f"  ✓ 移动：{file_path.name} -> {target_file_path}")
                success_count += 1
            except Exception as e:
                print(f"  ✗ 移动失败：{file_path.name}, 错误：{e}")
                return False
    
    # 如果所有文件都移动成功且需要删除空文件夹
    if success_count == len(files) and remove_empty:
        if dry_run:
            print(f"  [试运行] 删除空文件夹：{folder_path}")
        else:
            try:
                folder_path.rmdir()
                print(f"  ✓ 删除空文件夹：{folder_path}")
            except Exception as e:
                print(f"  ✗ 删除文件夹失败：{e}")
    
    return True


def process_directory(target_dir: str, parent_level: int = 1, dry_run: bool = False, 
                     remove_empty: bool = False, recursive: bool = True) -> None:
    """
    处理指定目录下的所有子文件夹
    
    Args:
        target_dir: 目标目录路径
        parent_level: 移动到几级父文件夹（1=直接父文件夹，2=祖父文件夹）
        dry_run: 是否为试运行模式
        remove_empty: 是否删除空的源文件夹
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
    print(f"移动到：{parent_level}级父文件夹")
    print(f"模式：{'试运行' if dry_run else '实际执行'}")
    print(f"递归处理：{'是' if recursive else '否'}")
    print(f"删除空文件夹：{'是' if remove_empty else '否'}")
    
    # 获取所有子文件夹
    if recursive:
        # 递归获取所有子文件夹，按深度排序（深的先处理）
        subfolders = [p for p in target_path.rglob('*') if p.is_dir()]
        subfolders.sort(key=lambda x: len(x.parts), reverse=True)
    else:
        # 只获取直接子文件夹
        subfolders = [p for p in target_path.iterdir() if p.is_dir()]
    
    if not subfolders:
        print("未找到任何子文件夹")
        return
    
    print(f"找到 {len(subfolders)} 个文件夹")
    
    success_count = 0
    for folder in subfolders:
        # 计算目标父文件夹
        try:
            target_parent = folder
            for _ in range(parent_level):
                target_parent = target_parent.parent
            
            # 确保目标父文件夹存在且不是源文件夹本身
            if target_parent == folder:
                print(f"跳过：无法移动到自己：{folder}")
                continue
                
            if not target_parent.exists():
                print(f"跳过：目标父文件夹不存在：{folder} -> {target_parent}")
                continue
            
            if move_files_from_folder(folder, target_parent, dry_run, remove_empty):
                success_count += 1
                
        except Exception as e:
            print(f"处理文件夹时出错：{folder}, 错误：{e}")
    
    print(f"\n处理完成：{success_count}/{len(subfolders)} 个文件夹处理成功")


def main():
    """主函数"""
    if len(sys.argv) < 2:
        print("使用方法：")
        print("  python move_files_to_parent.py <目标目录> [选项]")
        print("")
        print("选项：")
        print("  --dry-run         试运行模式，仅显示将要执行的操作")
        print("  --parent-level N  移动到N级父文件夹（默认1，即直接父文件夹）")
        print("  --remove-empty    移动后删除空的源文件夹")
        print("  --no-recursive    不递归处理子目录")
        print("")
        print("示例：")
        print("  # 将子文件夹的文件移动到直接父文件夹")
        print("  python move_files_to_parent.py ../src/main/resources/assets/recasting/slashblade")
        print("")
        print("  # 试运行模式")
        print("  python move_files_to_parent.py ../src/main/resources/assets/recasting/slashblade --dry-run")
        print("")
        print("  # 移动到2级父文件夹并删除空文件夹")
        print("  python move_files_to_parent.py ../src/main/resources/assets/recasting/slashblade --parent-level 2 --remove-empty")
        return
    
    target_dir = sys.argv[1]
    dry_run = '--dry-run' in sys.argv
    remove_empty = '--remove-empty' in sys.argv
    recursive = '--no-recursive' not in sys.argv
    
    # 解析 parent-level 参数
    parent_level = 1
    try:
        parent_level_index = sys.argv.index('--parent-level')
        if parent_level_index + 1 < len(sys.argv):
            parent_level = int(sys.argv[parent_level_index + 1])
            if parent_level < 1:
                print("错误：parent-level 必须大于等于1")
                return
    except ValueError:
        pass
    except (IndexError, ValueError):
        print("错误：--parent-level 参数格式错误")
        return
    
    try:
        process_directory(target_dir, parent_level, dry_run, remove_empty, recursive)
    except KeyboardInterrupt:
        print("\n操作被用户中断")
    except Exception as e:
        print(f"发生错误：{e}")


if __name__ == "__main__":
    main()
