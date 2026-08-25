package com.til.recasting.compat.jei;

import com.til.recasting.Recasting;
import com.til.recasting.recipe.SpecialEffectCrystalShapedRecipe;
import com.til.recasting.registry.RecastingItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Recasting JEI 插件：SE 结晶子类型区分与结晶配方展示。
 */
@JeiPlugin
public class RecastingJEICompat implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return Recasting.prefix("jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(
                RecastingItems.SE_CRYSTAL.get(),
                SECrystalSubtypeInterpreter.INSTANCE
        );
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(
                SpecialEffectCrystalShapedRecipe.class,
                new SECrystalShapedCategoryExtension()
        );
    }
}
