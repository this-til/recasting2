# Recasting（重铸）— 1.21

Minecraft **NeoForge 1.21.1** 的 [SlashBlade Resharped](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) 扩展模组。

> 当前分支 `1.21` 为首版空壳：可正常进入游戏加载，玩法内容仍在从 [1.20.1](https://github.com/this-til/recasting2/tree/1.20) 移植中。

玩家向机制说明（1.20 参考，1.21 内容就绪后同步）：[docs/README.md](docs/README.md)

1.20→1.21 移植计划与时刻表：[docs/PORTING_1.21.md](docs/PORTING_1.21.md)
## 前置

- Minecraft **1.21.1** + NeoForge **21.1.248+** + Java **21**
- 强制依赖：[SlashBlade Resharped](https://www.curseforge.com/minecraft/mc-mods/slashblade-resharped) **2.0+**（NeoForge）

## 从源码构建

需要 JDK 21。在仓库根目录执行：

```powershell
.\gradlew build
```

产物位于 `build/libs/`，文件名形如 `recasting2-1.21.1-1.0.0.jar`。

开发客户端：

```powershell
.\gradlew runClient
```

可选开关（与 1.20 一致）：

| 属性 | 默认 | 说明 |
|------|------|------|
| `runShaders` | `true` | Embeddium + Iris + 光影包（Oculus 无 1.21 Neo 包，改用 Iris） |
| `runCompatTruePower` | `false` | True POWER 兼容验证 |
| `runCompatSrelic` | `false` | srelic 前置（GameRelic 本体暂无 1.21 Neo 包） |

数据生成：

```powershell
.\gradlew runData
```

## 许可证

本项目采用双轨许可：

- 源码及其他非美术文件以 [MIT License](LICENSE) 发布。
- 贴图、模型、粒子、音频及展示图等美术资源由 til、HTOD 共同保留所有权，适用 [Recasting Art Asset License](LICENSE-ASSETS)。

允许正常使用以及镜像分发官方、未经修改的模组 JAR（包括其中的美术资源）；禁止从官方 JAR 或仓库中单独抽取、再分发美术资源，完整条款以 `LICENSE-ASSETS` 为准。

Minecraft、NeoForge 与 SlashBlade Resharped 分别遵循其各自许可证；本仓库不授予这些上游项目的权利。

## 作者

til、HTOD
