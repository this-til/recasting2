package com.til.recasting.compat.jei;

import com.til.recasting.capability.SECrystalData;
import com.til.recasting.registry.RecastingDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * SE 结晶 JEI 子类型：按 SE 类型与等级区分，避免全部折叠为同一物品。
 */
public final class SECrystalSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final SECrystalSubtypeInterpreter INSTANCE = new SECrystalSubtypeInterpreter();

    private SECrystalSubtypeInterpreter() {
    }

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return getStringName(ingredient);
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return getStringName(ingredient);
    }

    public String getStringName(ItemStack stack) {
        SECrystalData data = stack.getOrDefault(
                RecastingDataComponents.SE_CRYSTAL_DATA.get(),
                SECrystalData.EMPTY
        );
        if (!data.hasSpecialEffect()) {
            return "empty";
        }
        ResourceLocation seType = data.getSpecialEffectType();
        if (seType == null) {
            return "empty";
        }
        return seType + ":" + data.getSpecialEffectLevel();
    }
}
