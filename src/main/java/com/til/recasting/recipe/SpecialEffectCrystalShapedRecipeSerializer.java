package com.til.recasting.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import java.util.Optional;

/**
 * SE结晶有序合成配方的序列化器。
 */
public record SpecialEffectCrystalShapedRecipeSerializer()
        implements RecipeSerializer<SpecialEffectCrystalShapedRecipe> {

    private record SeCrystalExtra(@org.jetbrains.annotations.Nullable ResourceLocation specialEffectType, int level) {
        private static final Codec<SeCrystalExtra> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("special_effect_type")
                        .forGetter(extra -> Optional.ofNullable(extra.specialEffectType())),
                Codec.INT.optionalFieldOf("level", -1).forGetter(SeCrystalExtra::level)
        ).apply(instance, (type, level) -> new SeCrystalExtra(type.orElse(null), level)));

        private static final SeCrystalExtra EMPTY = new SeCrystalExtra(null, -1);
    }

    public static final MapCodec<SpecialEffectCrystalShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(SpecialEffectCrystalShapedRecipe::getGroup),
            CraftingBookCategory.CODEC.fieldOf("category").forGetter(SpecialEffectCrystalShapedRecipe::category),
            ShapedRecipePattern.MAP_CODEC.forGetter(SpecialEffectCrystalShapedRecipe::getPattern),
            ItemStack.CODEC.fieldOf("result").forGetter(SpecialEffectCrystalShapedRecipe::getResultStack),
            SeCrystalExtra.CODEC.optionalFieldOf("se_crystal", SeCrystalExtra.EMPTY)
                    .forGetter(recipe -> new SeCrystalExtra(recipe.getSpecialEffectType(), recipe.getLevel()))
    ).apply(instance, (group, category, pattern, result, extra) ->
            new SpecialEffectCrystalShapedRecipe(
                    group,
                    category,
                    pattern,
                    result,
                    Optional.ofNullable(extra.specialEffectType()),
                    extra.level()
            )));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpecialEffectCrystalShapedRecipe> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    @Override
    public MapCodec<SpecialEffectCrystalShapedRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SpecialEffectCrystalShapedRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
