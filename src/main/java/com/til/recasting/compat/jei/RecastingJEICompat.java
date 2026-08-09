package com.til.recasting.compat.jei;

import com.til.recasting.Recasting;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.recipe.SpecialEffectCrystalShapedRecipe;
import com.til.recasting.registry.RecastingItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Recasting 模组的 JEI 兼容插件
 * 用于在 JEI 中正确显示 SE 结晶和其配方
 */
@JeiPlugin
public class RecastingJEICompat implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return Recasting.prefix("jei_plugin");
    }

    /**
     * 注册物品子类型
     * 让 JEI 能够区分不同类型的 SE 结晶
     */
    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        // 注册 SE 结晶的子类型解释器
        registration.registerSubtypeInterpreter(
                RecastingItems.SE_CRYSTAL.get(),
                RecastingJEICompat::getSECrystalSubtype
        );
    }

    /**
     * 获取 SE 结晶的子类型标识
     * 用于区分不同特殊效果和等级的结晶
     */
    public static String getSECrystalSubtype(ItemStack stack, UidContext context) {
        // 同步 NBT 到 Capability（如果需要）
        if (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("se_crystal_data")) {
            stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(cap -> {
                cap.deserializeNBT(stack.getTag().getCompound("se_crystal_data"));
            });
        }

        // 从 Capability 获取 SE 类型和等级
        return stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA)
                .map(data -> {
                    if (!data.hasSpecialEffect()) {
                        return "empty";
                    }

                    ResourceLocation seType = data.getSpecialEffectType();
                    int level = data.getSpecialEffectLevel();

                    if (seType == null) {
                        return "empty";
                    }

                    // 返回格式：se_type:level
                    return seType + ":" + level;
                })
                .orElse("empty");
    }

    /**
     * 注册香草分类扩展
     * 为 SE 结晶配方添加自定义显示逻辑
     */
    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        // 为 SE 结晶有序合成配方注册扩展
        registration.getCraftingCategory().addCategoryExtension(
                SpecialEffectCrystalShapedRecipe.class,
                SECrystalShapedCategoryExtension::new
        );
    }

}

