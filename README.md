# Recasting（重铸）

Minecraft Forge 1.20.1 的 [SlashBlade Resharped](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) 扩展模组。

新增**61**把名刀、**85**种SA、**59**种SE。

玩家向机制说明（含默认数值）：[docs/README.md](docs/README.md)

## 展示

![Overview](showcase/gallery/overview.png)

![P0](showcase/gallery/P0.png)

![P1](showcase/gallery/P1.png)

![P2](showcase/gallery/P2.png)

![P3](showcase/gallery/P3.png)

![P4](showcase/gallery/P4.png)

![P5](showcase/gallery/P5.png)

![F1](showcase/gallery/F1.png)

![F2](showcase/gallery/F2.png)

![F3](showcase/gallery/F3.png)

![F4](showcase/gallery/F4.png)

## 前置

- Minecraft **1.20.1** + Forge **47.4.13+** + Java **17**
- 强制依赖：[SlashBlade Resharped](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) **1.9+**

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

本项目采用双轨许可：

- 源码及其他非美术文件以 [MIT License](LICENSE) 发布。
- 贴图、模型、粒子、音频及展示图等美术资源由 til、HTOD 共同保留所有权，适用 [Recasting Art Asset License](LICENSE-ASSETS)。

允许正常使用以及镜像分发官方、未经修改的模组 JAR（包括其中的美术资源）；禁止从官方 JAR 或仓库中单独抽取、再分发美术资源，完整条款以 `LICENSE-ASSETS` 为准。

Minecraft、Forge 与 SlashBlade Resharped 分别遵循其各自许可证；本仓库不授予这些上游项目的权利。

## 作者

til、HTOD