package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.recipe.SpecialEffectCrystalIngredient;
import com.til.recasting.recipe.SpecialEffectCrystalShapedRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 配方序列化器与自定义材料类型注册表。
 */
public final class RecastingRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Recasting.MODID);

    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, Recasting.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SpecialEffectCrystalShapedRecipe>> SE_CRYSTAL_SHAPED =
            RECIPE_SERIALIZERS.register("se_crystal_shaped", () -> SpecialEffectCrystalShapedRecipe.SERIALIZER);

    public static final DeferredHolder<IngredientType<?>, IngredientType<SpecialEffectCrystalIngredient>> SE_CRYSTAL_INGREDIENT =
            INGREDIENT_TYPES.register("se_crystal",
                    () -> new IngredientType<>(SpecialEffectCrystalIngredient.CODEC, SpecialEffectCrystalIngredient.STREAM_CODEC));

    static {
        SpecialEffectCrystalIngredient.TYPE = SE_CRYSTAL_INGREDIENT;
    }

    private RecastingRecipeSerializers() {
    }
}
