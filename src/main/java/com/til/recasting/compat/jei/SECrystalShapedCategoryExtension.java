package com.til.recasting.compat.jei;

import com.til.recasting.capability.ISpecialEffectCrystalData;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.recipe.SpecialEffectCrystalShapedRecipe;
import com.til.recasting.registry.RecastingItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * SE 结晶有序合成配方的 JEI 分类扩展
 * 用于在 JEI 中正确显示 SE 结晶配方的输出
 */
public class SECrystalShapedCategoryExtension implements ICraftingCategoryExtension {

    private final SpecialEffectCrystalShapedRecipe recipe;

    public SECrystalShapedCategoryExtension(SpecialEffectCrystalShapedRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ICraftingGridHelper craftingGridHelper, @NotNull IFocusGroup focuses) {
        // 获取配方的材料
        List<Ingredient> ingredients = recipe.getIngredients();

        // 设置输入材料（3x3 网格）
        craftingGridHelper.createAndSetInputs(
                builder,
                ingredients.stream().map(i -> Arrays.stream(i.getItems()).toList()).toList(),
                getWidth(),
                getHeight()
        );

        // 创建输出物品（带有 SE 信息的结晶）
        ItemStack output = createOutputStack();

        // 设置输出
        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
                .addItemStack(output)
                .setOutputSlotBackground();

        //craftingGridHelper.createAndSetOutputs(builder, List.of(output));
    }

    /**
     * 创建带有特殊效果信息的输出物品
     */
    private ItemStack createOutputStack() {
        ItemStack output = new ItemStack(RecastingItems.SE_CRYSTAL.get());

        // 获取配方中定义的 SE 类型和等级
        ResourceLocation seType = recipe.getSpecialEffectType();
        int level = recipe.getLevel();

        if (seType != null) {
            // 通过 Capability 设置 SE 信息
            output.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
                data.setSpecialEffectType(seType);
                if (level >= 0) {
                    data.setSpecialEffectLevel(level);
                }

                // 将 Capability 数据序列化到 NBT
                output.getOrCreateTag().put("se_crystal_data", data.serializeNBT());
            });
        }

        return output;
    }

    @Override
    public int getWidth() {
        return recipe.getWidth();
    }

    @Override
    public int getHeight() {
        return recipe.getHeight();
    }

    @Override
    public @NotNull ResourceLocation getRegistryName() {
        return recipe.getId();
    }

}

