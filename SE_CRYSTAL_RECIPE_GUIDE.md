# SE 结晶配方系统使用指南

本文档说明如何使用 SE 结晶的配方系统。

## 系统组件

### 1. SECrystalIngredient - 材料匹配器
用于在配方中匹配特定类型和等级的 SE 结晶。

### 2. SECrystalShapedRecipe - 有序合成配方
支持输出指定类型和等级的 SE 结晶。

### 3. SECrystalShapedRecipeBuilder - 配方构建器
用于在数据生成时创建配方。

### 4. SECrystalRequest - 需求定义
定义 SE 结晶的类型和等级匹配条件。

## 使用方法

### 1. 数据生成中使用构建器

```java
public class MyRecipeProvider extends RecipeProvider {
    
    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // 创建一个火焰 SE 结晶（等级 5）
        SECrystalShapedRecipeBuilder
            .shaped(new ResourceLocation("recasting", "flame"), 5)
            .pattern("ABA")
            .pattern("BCB")
            .pattern("ABA")
            .define('A', Items.DIAMOND)
            .define('B', Items.GOLD_INGOT)
            .define('C', SlashBladeItems.PROUDSOUL_CRYSTAL.get())
            .unlockedBy("has_proudsoul_crystal", 
                has(SlashBladeItems.PROUDSOUL_CRYSTAL.get()))
            .save(consumer);
            
        // 创建一个冰霜 SE 结晶（只指定类型，不指定等级）
        SECrystalShapedRecipeBuilder
            .shaped(new ResourceLocation("recasting", "frost"))
            .pattern("###")
            .pattern("#C#")
            .pattern("###")
            .define('#', Items.ICE)
            .define('C', SlashBladeItems.PROUDSOUL_CRYSTAL.get())
            .unlockedBy("has_ice", has(Items.ICE))
            .save(consumer);
    }
}
```

### 2. JSON 配方文件

在 `data/recasting/recipes/` 目录下创建配方文件：

**示例 1：指定类型和等级的 SE 结晶**
```json
{
  "type": "recasting:se_crystal_shaped",
  "pattern": [
    "ABA",
    "BCB",
    "ABA"
  ],
  "key": {
    "A": {
      "item": "minecraft:diamond"
    },
    "B": {
      "item": "minecraft:gold_ingot"
    },
    "C": {
      "item": "slashblade:proudsoul_crystal"
    }
  },
  "result": {
    "item": "recasting:se_crystal"
  },
  "se_crystal": {
    "special_effect_type": "recasting:flame",
    "level": 5
  }
}
```

**示例 2：只指定类型的 SE 结晶**
```json
{
  "type": "recasting:se_crystal_shaped",
  "pattern": [
    "###",
    "#C#",
    "###"
  ],
  "key": {
    "#": {
      "item": "minecraft:ice"
    },
    "C": {
      "item": "slashblade:proudsoul_crystal"
    }
  },
  "result": {
    "item": "recasting:se_crystal"
  },
  "se_crystal": {
    "special_effect_type": "recasting:frost"
  }
}
```

### 3. 使用 SE 结晶作为材料

在其他配方中使用特定类型和等级的 SE 结晶作为材料：

```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "SBS"
  ],
  "key": {
    "S": {
      "type": "recasting:se_crystal",
      "item": "recasting:se_crystal",
      "request": {
        "special_effect_type": "recasting:flame",
        "level": 5
      }
    },
    "B": {
      "item": "slashblade:slashblade"
    }
  },
  "result": {
    "item": "slashblade:slashblade"
  }
}
```

### 4. 匹配任意等级的特定类型

只匹配类型，不限制等级：

```json
{
  "type": "recasting:se_crystal",
  "item": "recasting:se_crystal",
  "request": {
    "special_effect_type": "recasting:flame"
  }
}
```

### 5. 匹配任意 SE 结晶

不限制类型和等级：

```json
{
  "type": "recasting:se_crystal",
  "item": "recasting:se_crystal",
  "request": {}
}
```

## 代码中的使用

### 创建匹配器

```java
// 匹配火焰 SE 结晶（等级 5）
SECrystalIngredient flame5 = SECrystalIngredient.of(
    new ResourceLocation("recasting", "flame"), 
    5
);

// 匹配火焰 SE 结晶（任意等级）
SECrystalIngredient flameAny = SECrystalIngredient.of(
    SECrystalRequest.Builder.newInstance()
        .specialEffectType(new ResourceLocation("recasting", "flame"))
        .build()
);

// 匹配任意 SE 结晶
SECrystalIngredient any = SECrystalIngredient.blankNameless();
```

### 测试物品是否匹配

```java
ItemStack stack = ...; // 某个 SE 结晶物品

if (flame5.test(stack)) {
    // 这是一个火焰 SE 结晶（等级 5）
}
```

## 注意事项

1. **等级值 -1** 表示不限制等级
2. **特殊效果类型为 null** 表示不限制类型
3. 配方构建器会自动生成合适的配方 ID
4. 所有 SE 结晶都使用能力系统存储数据，确保数据持久化

## 配方 ID 命名规则

配方构建器会自动生成 ID：
- 基础：`recasting:se_crystal`
- 带类型：`recasting:se_crystal_flame`
- 带类型和等级：`recasting:se_crystal_flame_5`

也可以手动指定 ID：
```java
SECrystalShapedRecipeBuilder
    .shaped(seType, level)
    .pattern(...)
    .save(consumer, new ResourceLocation("recasting", "custom_recipe_id"));
```

