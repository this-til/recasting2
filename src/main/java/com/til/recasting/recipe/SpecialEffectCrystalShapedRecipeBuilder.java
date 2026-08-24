package com.til.recasting.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.til.recasting.registry.RecastingItems;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * SE结晶有序合成配方构建器（datagen）。
 */
public class SpecialEffectCrystalShapedRecipeBuilder implements RecipeBuilder {

    private final RecipeCategory category = RecipeCategory.MISC;
    private final Item result;
    private final int count;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;
    private boolean showNotification = true;
    @Nullable
    private ResourceLocation specialEffectType = null;
    private int level = -1;

    public SpecialEffectCrystalShapedRecipeBuilder(ItemLike item, int count) {
        this.result = item.asItem();
        this.count = count;
    }

    public static SpecialEffectCrystalShapedRecipeBuilder shaped(ResourceLocation specialEffectType, int level) {
        SpecialEffectCrystalShapedRecipeBuilder builder = shaped(RecastingItems.SE_CRYSTAL.get(), 1);
        builder.specialEffectType = specialEffectType;
        builder.level = level;
        return builder;
    }

    public static SpecialEffectCrystalShapedRecipeBuilder shaped(DeferredHolder<SpecialEffect, SpecialEffect> specialEffect, int level) {
        ResourceLocation seLocation = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.getKey(specialEffect.get());
        return shaped(seLocation, level);
    }

    public static SpecialEffectCrystalShapedRecipeBuilder shaped(DeferredHolder<SpecialEffect, SpecialEffect> specialEffect) {
        return shaped(specialEffect, 1);
    }

    public static SpecialEffectCrystalShapedRecipeBuilder shaped(ResourceLocation specialEffectType) {
        return shaped(specialEffectType, -1);
    }

    public static SpecialEffectCrystalShapedRecipeBuilder shaped(ItemLike result) {
        return shaped(result, 1);
    }

    public static SpecialEffectCrystalShapedRecipeBuilder shaped(ItemLike result, int count) {
        return new SpecialEffectCrystalShapedRecipeBuilder(result, count);
    }

    public SpecialEffectCrystalShapedRecipeBuilder specialEffectType(ResourceLocation specialEffectType) {
        this.specialEffectType = specialEffectType;
        return this;
    }

    public SpecialEffectCrystalShapedRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    public SpecialEffectCrystalShapedRecipeBuilder define(Character key, TagKey<Item> tag) {
        return this.define(key, Ingredient.of(tag));
    }

    public SpecialEffectCrystalShapedRecipeBuilder define(Character key, ItemLike item) {
        return this.define(key, Ingredient.of(item));
    }

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

    public SpecialEffectCrystalShapedRecipeBuilder pattern(String pattern) {
        if (!this.rows.isEmpty() && pattern.length() != this.rows.getFirst().length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(pattern);
            return this;
        }
    }

    @Override
    public @NotNull SpecialEffectCrystalShapedRecipeBuilder unlockedBy(@NotNull String key, @NotNull Criterion<?> trigger) {
        this.criteria.put(key, trigger);
        this.advancement.addCriterion(key, trigger);
        return this;
    }

    @Override
    public @NotNull SpecialEffectCrystalShapedRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    public SpecialEffectCrystalShapedRecipeBuilder showNotification(boolean show) {
        this.showNotification = show;
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.result;
    }

    @Override
    public void save(@NotNull RecipeOutput output) {
        ResourceLocation id = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(this.getResult()));
        if (this.specialEffectType != null) {
            String suffix = this.specialEffectType.getPath();
            if (this.level >= 0) {
                suffix += "_" + this.level;
            }
            id = id.withSuffix("_" + suffix);
        }
        this.save(output, id);
    }

    @Override
    public void save(@NotNull RecipeOutput output, @NotNull ResourceLocation id) {
        this.ensureValid(id);
        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);

        ShapedRecipePattern pattern = ShapedRecipePattern.of(this.key, this.rows);
        ItemStack resultStack = new ItemStack(this.result, this.count);
        SpecialEffectCrystalShapedRecipe recipe = new SpecialEffectCrystalShapedRecipe(
                this.group == null ? "" : this.group,
                CraftingBookCategory.MISC,
                pattern,
                resultStack,
                Optional.ofNullable(this.specialEffectType),
                this.level
        );

        output.accept(id, recipe, advancementBuilder.build(
                id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.rows.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        } else {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');

            for (String s : this.rows) {
                for (int i = 0; i < s.length(); i++) {
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
            } else if (this.rows.size() == 1 && this.rows.getFirst().length() == 1) {
                throw new IllegalStateException("Shaped recipe " + id
                        + " only takes in a single item - should it be a shapeless recipe instead?");
            } else if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + id);
            }
        }
    }
}
