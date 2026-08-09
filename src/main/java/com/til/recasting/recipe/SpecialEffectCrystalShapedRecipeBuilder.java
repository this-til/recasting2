package com.til.recasting.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.til.recasting.registry.RecastingItems;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.CraftingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * SE结晶有序合成配方构建器
 * 用于在数据生成时创建 SE结晶配方
 */
public class SpecialEffectCrystalShapedRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category = RecipeCategory.MISC;
    private final Item result;
    private final int count;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    @Nullable
    private String group;
    private boolean showNotification = true;

    // SE结晶特定字段
    @Nullable
    private ResourceLocation specialEffectType = null;
    private int level = -1;

    public SpecialEffectCrystalShapedRecipeBuilder(ItemLike item, int count) {
        this.result = item.asItem();
        this.count = count;
    }

    /**
     * 创建 SE结晶配方构建器（指定特殊效果类型和等级）
     */
    public static SpecialEffectCrystalShapedRecipeBuilder shaped(ResourceLocation specialEffectType, int level) {
        SpecialEffectCrystalShapedRecipeBuilder builder = shaped(RecastingItems.SE_CRYSTAL.get(), 1);
        builder.specialEffectType = specialEffectType;
        builder.level = level;
        return builder;
    }

    /**
     * 创建 SE结晶配方构建器（使用 RegistryObject<SpecialEffect> 和等级）
     */
    public static SpecialEffectCrystalShapedRecipeBuilder shaped(RegistryObject<SpecialEffect> specialEffect, int level) {
        ResourceLocation seLocation = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getKey(specialEffect.get());
        return shaped(seLocation, level);
    }

    /**
     * 创建 SE结晶配方构建器（使用 RegistryObject<SpecialEffect>，等级为1）
     */
    public static SpecialEffectCrystalShapedRecipeBuilder shaped(RegistryObject<SpecialEffect> specialEffect) {
        return shaped(specialEffect, 1);
    }

    /**
     * 创建 SE结晶配方构建器（仅指定特殊效果类型）
     */
    public static SpecialEffectCrystalShapedRecipeBuilder shaped(ResourceLocation specialEffectType) {
        return shaped(specialEffectType, -1);
    }

    /**
     * 创建基础配方构建器
     */
    public static SpecialEffectCrystalShapedRecipeBuilder shaped(ItemLike result) {
        return shaped(result, 1);
    }

    /**
     * 创建基础配方构建器（指定数量）
     */
    public static SpecialEffectCrystalShapedRecipeBuilder shaped(ItemLike result, int count) {
        return new SpecialEffectCrystalShapedRecipeBuilder(result, count);
    }

    /**
     * 设置特殊效果类型
     */
    public SpecialEffectCrystalShapedRecipeBuilder specialEffectType(ResourceLocation specialEffectType) {
        this.specialEffectType = specialEffectType;
        return this;
    }

    /**
     * 设置等级
     */
    public SpecialEffectCrystalShapedRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    /**
     * 定义配方材料（使用标签）
     */
    public SpecialEffectCrystalShapedRecipeBuilder define(Character key, TagKey<Item> tag) {
        return this.define(key, Ingredient.of(tag));
    }

    /**
     * 定义配方材料（使用物品）
     */
    public SpecialEffectCrystalShapedRecipeBuilder define(Character key, ItemLike item) {
        return this.define(key, Ingredient.of(item));
    }

    /**
     * 定义配方材料（使用材料）
     */
    public SpecialEffectCrystalShapedRecipeBuilder define(Character key, Ingredient ingredient) {
        if (this.key.containsKey(key)) {
            throw new IllegalArgumentException("Symbol '" + key + "' is already defined!");
        } else if (key == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(key, ingredient);
            return this;
        }
    }

    /**
     * 添加配方模式行
     */
    public SpecialEffectCrystalShapedRecipeBuilder pattern(String pattern) {
        if (!this.rows.isEmpty() && pattern.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(pattern);
            return this;
        }
    }

    @Override
    public @NotNull SpecialEffectCrystalShapedRecipeBuilder unlockedBy(@NotNull String key, @NotNull CriterionTriggerInstance trigger) {
        this.advancement.addCriterion(key, trigger);
        return this;
    }

    @Override
    public @NotNull SpecialEffectCrystalShapedRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    /**
     * 设置是否显示配方解锁通知
     */
    public SpecialEffectCrystalShapedRecipeBuilder showNotification(boolean show) {
        this.showNotification = show;
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.result;
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> consumer) {
        ResourceLocation id = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(this.getResult()));
        // 如果设置了特殊效果类型，在 ID 中添加后缀
        if (this.specialEffectType != null) {
            String suffix = this.specialEffectType.getPath();
            if (this.level >= 0) {
                suffix += "_" + this.level;
            }
            id = id.withSuffix("_" + suffix);
        }
        this.save(consumer, id);
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
        this.ensureValid(id);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT)
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);
        consumer.accept(new SpecialEffectCrystalShapedRecipeBuilder.Result(
                id, this.result, this.count, this.specialEffectType, this.level,
                this.group == null
                        ? ""
                        : this.group, this.rows, this.key, this.advancement,
                id.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.showNotification
        ));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.rows.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        } else {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');

            for(String s : this.rows) {
                for(int i = 0; i < s.length(); ++i) {
                    char c0 = s.charAt(i);
                    if (!this.key.containsKey(c0) && c0 != ' ') {
                        throw new IllegalStateException(
                                "Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
                    }
                    set.remove(c0);
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException(
                        "Ingredients are defined but not used in pattern for recipe " + id);
            } else if (this.rows.size() == 1 && this.rows.get(0).length() == 1) {
                throw new IllegalStateException("Shaped recipe " + id
                        + " only takes in a single item - should it be a shapeless recipe instead?");
            } else if (this.advancement.getCriteria().isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + id);
            }
        }
    }

    /**
     * 配方结果类，用于序列化配方数据
     */
    public static class Result extends CraftingRecipeBuilder.CraftingResult {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        @Nullable
        private final ResourceLocation specialEffectType;
        private final int level;
        private final boolean showNotification;

        public Result(ResourceLocation id, Item result, int count, @Nullable ResourceLocation specialEffectType,
                      int level, String group, List<String> pattern, Map<Character, Ingredient> key,
                      Advancement.Builder advancement, ResourceLocation advancementId, boolean showNotification) {
            super(CraftingBookCategory.MISC);
            this.id = id;
            this.result = result;
            this.count = count;
            this.specialEffectType = specialEffectType;
            this.level = level;
            this.group = group;
            this.pattern = pattern;
            this.key = key;
            this.advancement = advancement;
            this.advancementId = advancementId;
            this.showNotification = showNotification;
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            super.serializeRecipeData(json);

            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }

            JsonArray jsonarray = new JsonArray();
            for(String s : this.pattern) {
                jsonarray.add(s);
            }
            json.add("pattern", jsonarray);

            JsonObject jsonobject = new JsonObject();
            for(Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
                jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }
            json.add("key", jsonobject);

            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(this.result)).toString());
            if (this.count > 1) {
                jsonobject1.addProperty("count", this.count);
            }
            json.add("result", jsonobject1);

            // 添加 SE结晶特定数据
            if (this.specialEffectType != null || this.level >= 0) {
                JsonObject seCrystalData = new JsonObject();
                if (this.specialEffectType != null) {
                    seCrystalData.addProperty("special_effect_type", this.specialEffectType.toString());
                }
                if (this.level >= 0) {
                    seCrystalData.addProperty("level", this.level);
                }
                json.add("se_crystal", seCrystalData);
            }

            json.addProperty("show_notification", this.showNotification);
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return SpecialEffectCrystalShapedRecipe.SERIALIZER;
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return this.id;
        }

        @Override
        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Override
        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}

