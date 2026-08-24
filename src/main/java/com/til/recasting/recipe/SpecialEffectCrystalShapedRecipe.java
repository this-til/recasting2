package com.til.recasting.recipe;

import com.til.recasting.capability.SECrystalData;
import com.til.recasting.registry.RecastingDataComponents;
import com.til.recasting.registry.RecastingItems;
import lombok.Getter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * SE结晶的有序合成配方。
 */
public class SpecialEffectCrystalShapedRecipe extends ShapedRecipe {

    public static final RecipeSerializer<SpecialEffectCrystalShapedRecipe> SERIALIZER =
            new SpecialEffectCrystalShapedRecipeSerializer();

    private final ShapedRecipePattern pattern;
    private final ItemStack resultStack;
    @Getter
    @Nullable
    private final ResourceLocation specialEffectType;
    @Getter
    private final int level;

    public SpecialEffectCrystalShapedRecipe(
            String group,
            net.minecraft.world.item.crafting.CraftingBookCategory category,
            ShapedRecipePattern pattern,
            ItemStack result,
            Optional<ResourceLocation> specialEffectType,
            int level
    ) {
        super(group, category, pattern, result);
        this.pattern = pattern;
        this.resultStack = result;
        this.specialEffectType = specialEffectType.orElse(null);
        this.level = level;
    }

    public ShapedRecipePattern getPattern() {
        return pattern;
    }

    public ItemStack getResultStack() {
        return resultStack;
    }

    private static ItemStack createResultItem(@Nullable ResourceLocation specialEffectType, int level) {
        ItemStack stack = RecastingItems.SE_CRYSTAL.get().getDefaultInstance();
        applySeCrystalData(stack, specialEffectType, level);
        return stack;
    }

    private static void applySeCrystalData(ItemStack stack, @Nullable ResourceLocation specialEffectType, int level) {
        if (specialEffectType == null && level < 0) {
            return;
        }
        SECrystalData data = stack.getOrDefault(RecastingDataComponents.SE_CRYSTAL_DATA.get(), new SECrystalData());
        if (specialEffectType != null) {
            data.setSpecialEffectType(specialEffectType);
        }
        if (level >= 0) {
            data.setSpecialEffectLevel(level);
        }
        stack.set(RecastingDataComponents.SE_CRYSTAL_DATA.get(), data);
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider access) {
        return createResultItem(this.specialEffectType, this.level);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput container, @NotNull HolderLookup.Provider access) {
        ItemStack result = this.getResultItem(access).copy();
        applySeCrystalData(result, this.specialEffectType, this.level);
        return result;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
