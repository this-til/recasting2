# Recasting（重铸）

Minecraft Forge 1.20.1 的 [SlashBlade Resharped](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) 扩展模组。

新增名刀、拔刀术与特殊效果。在铁砧上铭刻结晶、精炼刀身，沿「重铸之路」成长。

A SlashBlade addon with new named blades, slash arts, and special effects. Engrave crystals at the anvil, refine your blade, and grow along the Path of Recasting.

## 运行环境

| 项目 | 要求 |
|------|------|
| Minecraft | 1.20.1 |
| Forge | 47.4.13 及以上 |
| Java | 17 |
| [SlashBlade Resharped](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) | 1.9 及以上（强制） |
| [JEI](https://www.curseforge.com/minecraft/mc-mods/jei) | 可选，用于查看配方 |

## 特性

- **名刀**：多条成长链与 λ 变体，含荧光、光棱、星、虚空等系列
- **拔刀术（SA）**：乱舞、剑雨、次元斩、闪电链、无限剑制等
- **特殊效果（SE）**：挥刀、击杀、增幅与刀专属效果；普通 SE 与特殊 SE 分槽铭刻
- **铁砧锻造**：用 SE 结晶铭刻、替换、升级；渊寂火抹除特殊 SE；聚散变体提取结晶
- **魂火与道具**：多种魂火、SE 结晶、耀魂背包、物质球
- **成长系统**：击杀掉落、附魔加成、精炼加成，以及对应进度

游戏内进度「重铸之路」会引导上述内容。配方可在 JEI 中查看。

## 安装

1. 安装 Minecraft 1.20.1 与对应 Forge
2. 安装 SlashBlade Resharped
3. 将本模组 JAR 放入 `mods` 文件夹
4. （可选）安装 JEI 以便查阅配方

## 从源码构建

需要 JDK 17。在仓库根目录执行：

```powershell
.\gradlew build
```

产物位于 `build/libs/`，文件名形如 `recasting2-1.20.1-1.0.0.jar`。

开发客户端：

```powershell
.\gradlew runClient
```

数据生成（刀定义 JSON、语言文件、`R` 常量等）：

```powershell
.\gradlew runData
```

## 许可证

本项目源码以 [MIT License](LICENSE) 发布。版权所有 © 2026 til, THOD。

Minecraft、Forge 与 SlashBlade Resharped 分别遵循其各自许可证；本仓库不授予这些上游项目的权利。

## 作者

til、THOD
