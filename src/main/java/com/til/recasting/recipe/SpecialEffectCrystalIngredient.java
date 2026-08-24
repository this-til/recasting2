package com.til.recasting.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.til.recasting.Recasting;
import com.til.recasting.registry.RecastingItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SE结晶的自定义材料匹配器。
 */
public class SpecialEffectCrystalIngredient implements ICustomIngredient {

    @SuppressWarnings("NotNullFieldNotInitialized")
    public static Supplier<IngredientType<SpecialEffectCrystalIngredient>> TYPE;

    public static final MapCodec<SpecialEffectCrystalIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("items")
                    .forGetter(ingredient -> ingredient.items.stream().map(BuiltInRegistries.ITEM::getKey).toList()),
            SpecialEffectCrystalRequest.CODEC.fieldOf("request").forGetter(ingredient -> ingredient.request)
    ).apply(instance, (itemIds, request) -> new SpecialEffectCrystalIngredient(
            itemIds.stream().map(BuiltInRegistries.ITEM::get).collect(Collectors.toSet()),
            request
    )));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpecialEffectCrystalIngredient> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    private final Set<Item> items;
    private final SpecialEffectCrystalRequest request;

    protected SpecialEffectCrystalIngredient(Set<Item> items, SpecialEffectCrystalRequest request) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a SECrystalIngredient with no items");
        }
        this.items = Set.copyOf(items);
        this.request = request;
    }

    public static Ingredient of(ItemLike item, SpecialEffectCrystalRequest request) {
        return new SpecialEffectCrystalIngredient(Set.of(item.asItem()), request).toVanilla();
    }

    public static Ingredient of(SpecialEffectCrystalRequest request) {
        return new SpecialEffectCrystalIngredient(Set.of(RecastingItems.SE_CRYSTAL.get()), request).toVanilla();
    }

    public static Ingredient of(ItemLike item, ResourceLocation specialEffectType, int level) {
        return new SpecialEffectCrystalIngredient(Set.of(item.asItem()),
                SpecialEffectCrystalRequest.Builder.newInstance()
                        .specialEffectType(specialEffectType)
                        .level(level)
                        .build()).toVanilla();
    }

    public static Ingredient of(ResourceLocation specialEffectType, int level) {
        return new SpecialEffectCrystalIngredient(Set.of(RecastingItems.SE_CRYSTAL.get()),
                SpecialEffectCrystalRequest.Builder.newInstance()
                        .specialEffectType(specialEffectType)
                        .level(level)
                        .build()).toVanilla();
    }

    public static Ingredient blankNameless() {
        return of(SpecialEffectCrystalRequest.Builder.newInstance().build());
    }

    @Override
    public boolean test(ItemStack input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return items.contains(input.getItem()) && this.request.test(input);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public @NotNull Stream<ItemStack> getItems() {
        return items.stream().map(item -> {
            ItemStack stack = new ItemStack(item);
            request.initItemStack(stack);
            return stack;
        });
    }

    @Override
    public @NotNull IngredientType<?> getType() {
        return TYPE.get();
    }

    public static ResourceLocation id() {
        return Recasting.prefix("se_crystal");
    }
}
