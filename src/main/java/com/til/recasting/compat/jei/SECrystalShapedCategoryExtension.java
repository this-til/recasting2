package com.til.recasting.compat.jei;

import com.til.recasting.capability.SECrystalData;
import com.til.recasting.recipe.SpecialEffectCrystalShapedRecipe;
import com.til.recasting.registry.RecastingDataComponents;
import com.til.recasting.registry.RecastingItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * SE 结晶有序合成在 JEI 中的展示扩展：输出带 DataComponent 的结晶。
 */
public final class SECrystalShapedCategoryExtension
        implements ICraftingCategoryExtension<SpecialEffectCrystalShapedRecipe> {

    @Override
    public void setRecipe(
            @NotNull RecipeHolder<SpecialEffectCrystalShapedRecipe> recipeHolder,
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull ICraftingGridHelper craftingGridHelper,
            @NotNull IFocusGroup focuses
    ) {
        SpecialEffectCrystalShapedRecipe recipe = recipeHolder.value();
        int width = getWidth(recipeHolder);
        int height = getHeight(recipeHolder);

        ItemStack output = createOutputStack(recipe);
        craftingGridHelper.createAndSetOutputs(builder, List.of(output));
        craftingGridHelper.createAndSetIngredients(builder, recipe.getIngredients(), width, height);
    }

    @Override
    public int getWidth(@NotNull RecipeHolder<SpecialEffectCrystalShapedRecipe> recipeHolder) {
        return recipeHolder.value().getWidth();
    }

    @Override
    public int getHeight(@NotNull RecipeHolder<SpecialEffectCrystalShapedRecipe> recipeHolder) {
        return recipeHolder.value().getHeight();
    }

    private static ItemStack createOutputStack(SpecialEffectCrystalShapedRecipe recipe) {
        ItemStack output = RecastingItems.SE_CRYSTAL.get().getDefaultInstance();
        ResourceLocation seType = recipe.getSpecialEffectType();
        int level = recipe.getLevel();
        if (seType == null && level < 0) {
            return output;
        }

        SECrystalData data = new SECrystalData();
        if (seType != null) {
            data.setSpecialEffectType(seType);
        }
        if (level >= 0) {
            data.setSpecialEffectLevel(level);
        }
        output.set(RecastingDataComponents.SE_CRYSTAL_DATA.get(), data);
        return output;
    }
}