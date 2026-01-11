package com.til.recasting.recipe;

import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.registry.RecastingItems;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * SE结晶的有序合成配方
 * 支持输出指定特殊效果类型和等级的 SE结晶
 */
public class SpecialEffectCrystalShapedRecipe extends ShapedRecipe {

    public static final RecipeSerializer<SpecialEffectCrystalShapedRecipe> SERIALIZER = new SpecialEffectCrystalShapedRecipeSerializer<>(
            RecipeSerializer.SHAPED_RECIPE, SpecialEffectCrystalShapedRecipe::new);

    @Getter
    @Nullable
    private final ResourceLocation specialEffectType;
    @Getter
    private final int level;

    public SpecialEffectCrystalShapedRecipe(ShapedRecipe compose, @Nullable ResourceLocation specialEffectType, int level) {
        super(compose.getId(), compose.getGroup(), compose.category(), compose.getWidth(), compose.getHeight(),
                compose.getIngredients(), getResultItem(specialEffectType, level));
        this.specialEffectType = specialEffectType;
        this.level = level;
    }

    /**
     * 创建结果物品
     */
    private static ItemStack getResultItem(@Nullable ResourceLocation specialEffectType, int level) {
        Item item = ForgeRegistries.ITEMS.containsKey(RecastingItems.SE_CRYSTAL.getId()) 
                ? ForgeRegistries.ITEMS.getValue(RecastingItems.SE_CRYSTAL.getId())
                : RecastingItems.SE_CRYSTAL.get();

        ItemStack stack = Objects.requireNonNullElseGet(item, RecastingItems.SE_CRYSTAL).getDefaultInstance();
        
        // 设置 SE 结晶的特殊效果类型和等级
        if (specialEffectType != null || level >= 0) {
            stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
                if (specialEffectType != null) {
                    data.setSpecialEffectType(specialEffectType);
                }
                if (level >= 0) {
                    data.setSpecialEffectLevel(level);
                }
            });
        }
        
        return stack;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
        return getResultItem(this.specialEffectType, this.level);
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull RegistryAccess access) {
        // 创建输出物品
        ItemStack result = this.getResultItem(access).copy();
        
        // 确保能力数据已正确设置
        result.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
            if (this.specialEffectType != null) {
                data.setSpecialEffectType(this.specialEffectType);
            }
            if (this.level >= 0) {
                data.setSpecialEffectLevel(this.level);
            }
        });
        
        return result;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
