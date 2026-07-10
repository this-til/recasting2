# Claude AI 项目规则和记忆

本文档记录了 Recasting2项目的开发规则、约定和重要注意事项。

## 会话与协作

- **始终使用中文回复**
- **用户的所有规则更新都是针对本文件（CLAUDE.md）** —— 此文档是项目规则的唯一权威来源
- **用户发送「？？？」** —— 与「？？？」相同语义：用户对助手输出强烈不满；表示你的理解严重偏离正确方向，重新整理用户需求与对话上下文，明确自己错在哪里，并给出修正后的方案或正确实现。回复中应真诚、克制地安抚用户心态，避免说教、反唇相讥或油腻客套
- **计划模式必须编写伪代码（Java）** —— 在 Cursor 计划模式（Plan mode）下，除文字方案与权衡说明外，必须提供 **Java 风格**伪代码供用户对照：覆盖主要流程、分支与关键数据变换；遵循本项目命名与类型习惯，不必可编译，省略样板与无关细节；复杂模块可按步骤分段给出，便于用户核对思路与验收预期
- **除非用户明确要求，否则不编写测试文件、示例文件或演示代码** —— 只专注于实现用户请求的核心功能
- **明确禁止编写任何示例或文档**：不得创建任何示例代码文件、文档文件（如 README.md、示例文件等），除非用户明确要求
- **发布阶段无需向后兼容** —— 本项目尚未正式发布，无线上用户与历史存档包袱；重构、迁移、删改实现时**不必**为旧版本保留兼容层。典型情形包括：直接改 NBT/序列化格式而不写升级脚本；重命名或删除配置项、注册名、资源路径后同步改全仓库引用即可；不添加「若旧字段存在则……」的分支、废弃标记（`@Deprecated` 保留兼容）或双写过渡，除非用户明确要求保留某条兼容路径
- **对话末尾 AskQuestion 永续合约** —— 完成一轮实质性回复后，在正文收尾处调用 Cursor 内置 `AskQuestion` 询问下一步计划；**须开启多选**（`allow_multiple: true`），允许用户一次勾选多条后续方向；选项须基于当前对话与近期上下文给出 **3–5 条可执行猜测**（具体方向，禁止空泛的「继续」「好的」类占位），并始终保留「其他」供用户自定义。例外：用户明确表示结束会话；本轮仅为无需跟进的纯信息问答；或同轮尚有未完成的连续实现任务（避免打断工作流）

## Skill 文档

- **提升技能文档使用权重** —— 对于存在对应 Skill 的任务，必须优先读取并遵循 `.cursor/skills/**/SKILL.md` 的流程与约束，再进行分析、实现和回复；若与通用习惯冲突，以本文件规则优先，其次以对应 Skill 文档为准
- **修改代码时按需更新对应 Skill 文档** —— 若被修改的文件属于某个 Skill 的覆盖范围，仅在涉及架构规则变化或与 Skill 既有描述冲突时才更新 `.cursor/skills/**/SKILL.md`，避免记录实现细节导致文档啰唆
- **Skill 文档中的代码示例禁止使用项目内真实类型名** —— 编写或更新 `.cursor/skills/**/SKILL.md` 时，代码示例中的类名、包名、方法名等必须使用抽象占位名（如 `BaseManage`、`AppManage`、`BaseItem`、`BaseService`），禁止照搬项目里实际存在的类型名。文档是面向通用方案的，不应与具体项目实现耦合
- **修改 Skill 文档禁止旧形态表述** —— 编写或更新 `.cursor/skills/**/SKILL.md` 时，只描述当前代码库的实际行为，禁止写「不是……」「不再是……」「代替原先的……」「而非旧版本的……」等与历史、已废弃或从未存在的形态做对比的表述
- **Skill 文档禁止逆引用** —— 编写或更新 `.cursor/skills/**/SKILL.md` 时，依赖方向与模块分层一致：底层 Skill **禁止**链接、点名或 `@` 引用更高层 Skill。若需说明上层应如何使用本层能力，用中立表述代替逆引用，例如「调用方应……」「上层实现应……」「运行时应在……之后……」「消费方需先……再……」。层级与阅读顺序以 `.cursor/skills/README.md` 路由表为准；高层 Skill 可引用底层 Skill，底层不得反向依赖文档

## 工具与工作流

- **使用 PowerShell 作为终端** —— 使用 `$env:变量名` 访问环境变量，使用 PowerShell cmdlet（如 `Remove-Item`）而不是 Unix 命令
- **排查与检索以当前工作区为准，禁止默认依赖 Git** —— 查找问题、理解现状、代码检查与全文检索时，以磁盘上**当前**文件内容为准（Read、Grep、Glob、`.\gradlew build` 等），不得用 `git status`、`git diff`、`git log`、`git show`、`git blame` 等推断「应有」代码、还原历史版本或替代直接读文件；除非用户**明确要求**使用 Git（如查看提交记录、对比分支、创建 commit/PR），否则不主动执行 Git 命令作为调查手段
- **禁止使用命令行编辑仓库内源码文件** —— 修改 `.java`、`.json`、`.md` 等源文件须通过编辑器或 Cursor 提供的文件编辑工具（直接改写、补丁式替换等）；禁止用终端里的重定向、`sed`、对源文件路径的 `Set-Content`/`Out-File` 批量覆盖等方式改写内容。终端可用于 `.\gradlew build`、用户要求的 Git 操作、仅输出到终端的查询等**不写入源文件**的操作
- **`read_lints` 工具无法正常工作** —— 使用 `.\gradlew build` 手动编译检查错误，简单编写时可跳过检查

## 代码库规则

### 外部参考库（只读）

- **从外部仓库复制或引入的参考库源码仅供阅读，禁止修改** —— 通用原则：该目录下的所有源码文件仅用于参考和阅读，**禁止进行任何修改**；如需了解实现，可阅读代码，但不得编辑、删除或修改
- **`SlashBlade_Resharped`** —— 基础库源码目录，适用上述只读规则
- **构建产物与 IDE 缓存不纳入版本管理** —— `build/`、`.gradle/`、`.idea/`、`bin/`、`out/` 等

## Java 编码规范

- **项目 Java 版本** —— Java 17；可使用 `record`、`instanceof` 模式匹配、`switch` 表达式、`var`（类型显而易见时）等现代语法
- **命名** —— 类/接口/枚举/记录用 **PascalCase**（如 `BuffStackEventHandler`）；方法、字段、参数、局部变量用 **camelCase**（如 `markForSync`、`decayInterval`）；`static final` 常量用 **UPPER_SNAKE_CASE**（如 `MODID`、`BROADSWORD_WOOD`）
- **控制流语句禁止省略大括号 `{}`** —— `if`、`else`、`for`、`foreach`、`while`、`do` 等必须写清块体，禁止单行无括号写法。例如必须写 `if (target == null) { return false; }`，禁止写 `if (target == null) return false;`
- **显式写出访问修饰符** —— 类型与成员（类、接口、枚举、字段、方法等）都须显式标注 `public`、`private`、`protected` 或包级可见意图，禁止依赖默认访问级别造成歧义
- **尽可能使用早退机制（Early Return）** —— 在方法开始处检查前置条件，不满足时立即返回，避免深层嵌套
- **浮点数相等比较使用 `MathHelper.epsilonEquals`** —— 禁止直接用 `==` / `!=` 比较 `float` / `double` 是否相等；`>`、`<`、`>=`、`<=` 可正常使用
- **Helper 静态方法命名以算法语义为准** —— `*Helper` 类中的工具方法禁止使用体现单一业务场景或调用上下文的名称；应使用算法、数学变换、数据结构操作或等价的中立技术语义命名，便于多处复用
- **Javadoc 注释** —— 对外 API、非显然行为、业务约束用 `/** */`；只写代码说不清的信息（意图、约束、非显然副作用），已自述清楚的逻辑不写。禁止重述签名、注释显而易见语句。`<summary>` 式首段保持 1–3 行，细节可放后续段落。**例外（允许 `//` 行内注释）**：方法体较长且含多个相对独立步骤时，可在每步起始处用 `//` 标注该步意图（如「复制映射以避免并发修改」），禁止逐行重述语句含义
- **Lombok** —— 项目已使用 `@Getter`、`@Setter`、`@AllArgsConstructor`、`@Log4j2`、`@Accessors` 等；新增代码在同类场景下可继续使用，不为此单独手写样板代码
- **简单不可变数据优先 `record`** —— 纯数据载体（如 `NumberPack`）优先用 `record`，不必再写等价 POJO
- **可空语义用 `@Nullable` 标注** —— 参数或返回值可能为 `null` 时，用 `javax.annotation.Nullable` 标明；调用方与实现保持一致

## Minecraft / Forge 约定

- **资源标识禁止随意硬编码** —— `ResourceLocation` 等业务代码中优先使用 `R` 常量或 `Recasting.prefix(...)`；`R.java` 为自动生成文件，**禁止手动修改**
- **注册表统一走 DeferredRegister** —— 新物品、实体、配方等参照 `RecastingItems`、`RecastingEntities` 等现有注册类，不散落 `Registry.register` 调用
- **逻辑侧必须区分客户端与服务端** —— 修改世界、实体、Capability、网络同步等须在服务端执行；用 `!level().isClientSide()` / `level().isClientSide()` 或 `@OnlyIn(Dist.CLIENT)`、`DistExecutor` 区分。客户端专用渲染、粒子、按键等放在 `client/` 包下
- **本地化条目写在 `LanguageItems`** —— 玩家可见文案在 `LanguageItems` 中维护翻译，不直接手改生成的语言文件
- **Mixin 仅用于必要注入** —— 能通过事件、接口扩展、注册表钩子实现的逻辑优先不用 Mixin；Mixin 类放在 `mixin/` 包，访问器放 `mixin/` 或独立 `*Accessor` 接口

## 拔刀剑（SlashBlade）开发约定

### 刀定义与配方

- **刀定义唯一入口** —— `SlashBladeDefinitions.java` 中声明 `public static final SlashBladeDefinition`；用 `createBuild(...).renderDefinition(...).propertiesDefinition(...)[.addSpecialEffects(...)].build()` 构建，不手写 `named_blades` JSON
- **配方唯一入口** —— `SlashBladeRecipes.java` 中声明 `public static final RecipeBuilderWrapper`；用 `SlashBladeShapedRecipeBuilder.shaped(...)` + `SlashBladeIngredient.of(RequestDefinition.Builder...)` 约束前置刀
- **ResourceLocation** —— 本模组刀用 `R.Slashblade.*` 或 `Recasting.prefix("slashblade/...")`；原版物品用 `registry/requir/SlashBladeItems.java` 引用，**不在本模组重复注册**
- **资源文件** —— 模型/贴图放 `assets/recasting/slashblade/`，同名 `{name}.obj` + `{name}.png`；ID 用 snake_case，与 Java 常量对应（`BA_GUA_BIG` → `ba_gua_big`）
- **Lambda 变体** —— ID 后缀 `_lambda`；配方链递增 `killCount`/`refineCount`；中文名前缀 `^`（见 `LanguageItems`）
- **新增刀 checklist** —— ① `SlashBladeDefinitions` ② 资源文件 ③ 运行 datagen 生成 JSON 与 `R` ③ `SlashBladeRecipes` ④ `LanguageItems.createSlashBladeDefinitionLanguage(...)`

### 特殊效果（SE）与拔刀术（SA）

- **SE 注册** —— `SpecialEffectsRegistry.registerExtendedSE("snake_case_id", ...)`，继承 `ExtendedSpecialEffect`；逻辑写 `@SubscribeEvent`，服务端执行；防递归用 `RecastingAttackTypes`
- **SA 注册** —— `SlashArtsRegistry.registerExtendedSA("snake_case_id", new XxxSlashArts())`，**必须走此方法**（同步注册 ComboState）；继承 `ExtendedSlashArts`，核心逻辑在 `trigger(...)`
- **注册顺序**（`Recasting.java`）—— `SlashArtsRegistry` → `RecastingComboStateRegistry` → `SpecialEffectsRegistry`
- **绑定到刀** —— SA 用 `.slashArtsType(SlashArtsRegistry.XXX.getId())`；SE 用 `.addSpecialEffects(SpecialEffectsRegistry.XXX)` 或带等级
- **SE 结晶配方** —— `SpecialEffectRecipes.java`；本地化用 `createSpecialEffectLanguage` + `createSpecialEffectDescription`（`.desc` 后缀）

### 攻击、扩展与 Mixin

- **攻击逻辑统一走 `AttackHelper`** —— `doSlash` / `attack` / `areaAttack`；SE 挥刀优先监听 `DoSlashExtendEvent`，伤害加成用 `AttackAmplifierEvent`；不绕过 `AttackHelper` 自建平行攻击系统
- **扩展数据走 Capability** —— `PropertiesDefinitionExtension`（攻击距离、SE 等级）、`RenderDefinitionExtension`（幻影剑/斩击模型）；不往原版 NBT 硬塞未序列化字段
- **延迟序列** —— `TIME_RUN` Capability 的 `addTimerCell(...)`；实体 buff 用 `RecastingBuffTypes` + `BUFF_STACK_DATA`
- **Mixin 边界** —— 仅用于定义扩展、攻击路由（`AttackManagerMixin`）、CODEC 序列化、tooltip、JEI 兼容；目标类在 `mods.flammpfeil.slashblade.*` 时 **`remap = false`**；新 Mixin 必须登记 `recasting.mixins.json`
- **参考库** —— 运行时依赖 CurseMaven JAR；`SlashBlade_Resharped/` 源码目录只读，查 API 用，禁止修改

**最后更新：** 2026-07-10
**维护者：** til
