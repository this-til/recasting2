# Recasting（重铸）

Minecraft Forge 1.20.1 的 [SlashBlade Resharped](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) 扩展模组。

新增 **61** 把名刀、**85** 种拔刀术（SA）、**59** 种特殊效果（SE）。在铁砧上铭刻结晶、精炼刀身，沿「重铸之路」成长。

A SlashBlade addon with **61** named blades, **85** slash arts, and **59** special effects. Engrave crystals at the anvil, refine your blade, and grow along the Path of Recasting.

## 展示

![Overview](showcase/overview.png)

![P0](showcase/P0.png)

![P1](showcase/P1.png)

![P2](showcase/P2.png)

![P3](showcase/P3.png)

![P4](showcase/P4.png)

![P5](showcase/P5.png)

## 前置

- Minecraft **1.20.1** + Forge **47.4.13+** + Java **17**
- 强制依赖：[SlashBlade Resharped](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) **1.9+**
- 可选：[JEI](https://www.curseforge.com/minecraft/mc-mods/jei)

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
