# SE 结晶配方系统 - 完成总结

## 已创建的文件

### 核心类

1. **SECrystalRequest.java** (`src/main/java/com/til/recasting/recipe/`)
   - SE 结晶的需求定义
   - 支持精确匹配类型和等级
   - 使用 Codec 系统进行序列化

2. **SECrystalIngredient.java** (`src/main/java/com/til/recasting/recipe/`)
   - 自定义材料匹配器
   - 仿照 SlashBladeIngredient 实现
   - 支持在配方中匹配特定的 SE 结晶

3. **SECrystalShapedRecipe.java** (`src/main/java/com/til/recasting/recipe/`)
   - 扩展的有序合成配方
   - 支持输出指定类型和等级的 SE 结晶
   - 自动设置输出物品的能力数据

4. **SECrystalShapedRecipeSerializer.java** (`src/main/java/com/til/recasting/recipe/`)
   - 配方序列化器
   - 处理 JSON 和网络传输
   - 支持 `se_crystal` 数据块

5. **SECrystalShapedRecipeBuilder.java** (`src/main/java/com/til/recasting/recipe/`)
   - 配方构建器
   - 用于数据生成
   - 提供流式 API

### 注册类

6. **RecastingRecipeSerializers.java** (`src/main/java/com/til/recasting/registry/`)
   - 配方序列化器注册表
   - 注册 SE 结晶配方类型
   - 注册材料序列化器

### 文档

7. **SE_CRYSTAL_RECIPE_GUIDE.md** (项目根目录)
   - 完整的使用指南
   - 包含代码示例和 JSON 示例

## 修改的文件

- **Recasting.java** - 添加了配方序列化器的注册

## 系统特性

### 1. 类型和等级匹配
- 精确匹配特定的特殊效果类型
- 精确匹配特定的等级
- 支持只匹配类型（不限制等级）
- 支持匹配任意 SE 结晶

### 2. 配方输出
- 可以指定输出 SE 结晶的类型和等级
- 自动设置物品的能力数据
- 支持标准的有序合成配方格式

### 3. 数据生成支持
- 提供完整的 RecipeBuilder
- 支持流式 API
- 自动生成配方 ID

### 4. 序列化
- JSON 格式支持
- 网络传输支持
- 使用 Mojang Codec 系统

## 使用示例

### 创建配方（数据生成）

```java
SECrystalShapedRecipeBuilder
    .shaped(new ResourceLocation("recasting", "flame"), 5)
    .pattern("ABA")
    .pattern("BCB")
    .pattern("ABA")
    .define('A', Items.DIAMOND)
    .define('B', Items.GOLD_INGOT)
    .define('C', SlashBladeItems.PROUDSOUL_CRYSTAL.get())
    .unlockedBy("has_proudsoul_crystal", has(SlashBladeItems.PROUDSOUL_CRYSTAL.get()))
    .save(consumer);
```

### JSON 配方

```json
{
  "type": "recasting:se_crystal_shaped",
  "pattern": ["ABA", "BCB", "ABA"],
  "key": {
    "A": { "item": "minecraft:diamond" },
    "B": { "item": "minecraft:gold_ingot" },
    "C": { "item": "slashblade:proudsoul_crystal" }
  },
  "result": { "item": "recasting:se_crystal" },
  "se_crystal": {
    "special_effect_type": "recasting:flame",
    "level": 5
  }
}
```

### 使用 SE 结晶作为材料

```json
{
  "key": {
    "S": {
      "type": "recasting:se_crystal",
      "item": "recasting:se_crystal",
      "request": {
        "special_effect_type": "recasting:flame",
        "level": 5
      }
    }
  }
}
```

## 架构设计

系统完全仿照 SlashBlade 的配方系统设计：

- `SECrystalRequest` ← 对应 `RequestDefinition`
- `SECrystalIngredient` ← 对应 `SlashBladeIngredient`
- `SECrystalShapedRecipe` ← 对应 `SlashBladeShapedRecipe`
- `SECrystalShapedRecipeSerializer` ← 对应 `SlashBladeShapedRecipeSerializer`
- `SECrystalShapedRecipeBuilder` ← 对应 `SlashBladeShapedRecipeBuilder`

## 集成到项目

系统已完全集成：

1. ✅ 配方序列化器已注册到 Forge 注册表
2. ✅ 材料序列化器已注册到 CraftingHelper
3. ✅ 主类已添加注册调用
4. ✅ 使用项目的能力系统（SE_CRYSTAL_DATA）
5. ✅ 使用 @Slf4j 进行日志记录

## 下一步

现在可以：
1. 创建具体的配方 JSON 文件
2. 在数据生成器中使用构建器
3. 在游戏中测试配方功能
4. 根据需要扩展无序合成配方（Shapeless）支持

## 技术要点

- 使用 Record 类型简化代码
- 使用 Mojang Codec 进行序列化
- 完全兼容 Forge 配方系统
- 支持自定义材料序列化
- 线程安全的注册流程

