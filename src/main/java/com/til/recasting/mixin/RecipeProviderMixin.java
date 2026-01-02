package com.til.recasting.mixin;

import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Mixin 用于将 RecipeProvider.has() 方法从 protected 改为 public
 * 允许在 RecastingRecipes 常量类中直接访问该方法
 */
@Mixin(RecipeProvider.class)
public interface RecipeProviderMixin {

    /**
     * 公共访问器方法，用于访问 protected 的 has 方法
     * 这样可以在 RecastingRecipes 中直接调用 RecipeProvider.has()
     */
    @Invoker("has")
    static InventoryChangeTrigger.TriggerInstance invokeHas(ItemLike itemLike) {
        throw new UnsupportedOperationException("Mixin should not be called directly");
    }
}

