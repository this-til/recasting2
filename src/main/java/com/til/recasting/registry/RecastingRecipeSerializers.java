package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.recipe.SpecialEffectCrystalIngredient;
import com.til.recasting.recipe.SpecialEffectCrystalShapedRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 配方序列化器注册表
 * 用于注册自定义配方类型和材料序列化器
 */
public class RecastingRecipeSerializers {

    /**
     * 配方序列化器的 DeferredRegister
     */
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Recasting.MODID);

    /**
     * SE结晶有序合成配方序列化器
     */
    public static final RegistryObject<RecipeSerializer<SpecialEffectCrystalShapedRecipe>> SE_CRYSTAL_SHAPED =
            RECIPE_SERIALIZERS.register("se_crystal_shaped", () -> SpecialEffectCrystalShapedRecipe.SERIALIZER);

    /**
     * 注册自定义材料序列化器
     * 这个方法会在模组初始化时被调用
     */
    public static void registerIngredientSerializers() {
        // 注册 SE结晶材料序列化器
        CraftingHelper.register(
                Recasting.prefix("se_crystal"),
                SpecialEffectCrystalIngredient.Serializer.INSTANCE
        );
    }

    /**
     * 初始化注册
     *
     * @param modEventBus 模组事件总线
     */
    public static void register(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
        // 材料序列化器需要在这里注册
        registerIngredientSerializers();
    }
}

