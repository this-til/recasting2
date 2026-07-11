package com.til.recasting.gametest.support;

import com.til.recasting.capability.ISpecialEffectCrystalData;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.recipe.SpecialEffectCrystalIngredient;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.recipe.SlashBladeIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.inventory.CraftingContainer;

/**
 * 从配方 {@link Ingredient} 合成可匹配的材料堆，并提供破坏约束的负向材料。
 */
public final class IngredientStackFactory {

    private IngredientStackFactory() {
    }

    public static ItemStack matchingStack(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack[] stacks = ingredient.getItems();
        if (stacks.length == 0) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = stacks[0].copy();
        restoreSeCrystalCapability(stack);
        restoreBladeStateTag(stack);
        return stack;
    }

    public static void fillMatching(CraftingContainer container, ShapedRecipe recipe) {
        clear(container);
        int width = recipe.getWidth();
        int height = recipe.getHeight();
        var ingredients = recipe.getIngredients();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int index = row * width + col;
                if (index >= ingredients.size()) {
                    continue;
                }
                Ingredient ingredient = ingredients.get(index);
                int slot = row * container.getWidth() + col;
                container.setItem(slot, matchingStack(ingredient));
            }
        }
    }

    /**
     * 在已填满的正向网格上破坏一处约束，使配方不应再匹配。
     */
    public static void breakOneConstraint(CraftingContainer container, Recipe<?> recipe) {
        if (!(recipe instanceof ShapedRecipe shaped)) {
            replaceFirstNonEmpty(container, new ItemStack(Items.BEDROCK));
            return;
        }
        int width = shaped.getWidth();
        int height = shaped.getHeight();
        var ingredients = shaped.getIngredients();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int index = row * width + col;
                if (index >= ingredients.size()) {
                    continue;
                }
                Ingredient ingredient = ingredients.get(index);
                if (ingredient == null || ingredient.isEmpty()) {
                    continue;
                }
                int slot = row * container.getWidth() + col;
                ItemStack current = container.getItem(slot);
                if (current.isEmpty()) {
                    continue;
                }
                if (ingredient instanceof SlashBladeIngredient) {
                    container.setItem(slot, breakSlashBladeConstraint(current));
                    return;
                }
                if (ingredient instanceof SpecialEffectCrystalIngredient) {
                    container.setItem(slot, breakSeCrystalConstraint(current));
                    return;
                }
            }
        }
        replaceFirstNonEmpty(container, new ItemStack(Items.BEDROCK));
    }

    public static ItemStack breakSlashBladeConstraint(ItemStack matchingBlade) {
        ItemStack broken = matchingBlade.copy();
        broken.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            if (state.getKillCount() > 0) {
                state.setKillCount(state.getKillCount() - 1);
            } else if (state.getRefine() > 0) {
                state.setRefine(state.getRefine() - 1);
            } else if (state.getProudSoulCount() > 0) {
                state.setProudSoulCount(state.getProudSoulCount() - 1);
            } else {
                state.setTranslationKey("item.minecraft.bedrock");
            }
            broken.getOrCreateTag().put("bladeState", state.serializeNBT());
        });
        return broken;
    }

    public static ItemStack breakSeCrystalConstraint(ItemStack matchingCrystal) {
        ItemStack broken = matchingCrystal.copy();
        restoreSeCrystalCapability(broken);
        broken.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
            int level = data.getSpecialEffectLevel();
            if (level <= 0) {
                data.setSpecialEffectLevel(1);
            } else {
                data.setSpecialEffectLevel(level - 1);
            }
            broken.getOrCreateTag().put("se_crystal_data", data.serializeNBT());
        });
        return broken;
    }

    public static void restoreSeCrystalCapability(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("se_crystal_data")) {
            return;
        }
        CompoundTag dataTag = tag.getCompound("se_crystal_data");
        stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
            if (data instanceof ISpecialEffectCrystalData) {
                data.deserializeNBT(dataTag);
            }
        });
    }

    private static void restoreBladeStateTag(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("bladeState")) {
            return;
        }
        stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> state.deserializeNBT(tag.getCompound("bladeState")));
    }

    private static void replaceFirstNonEmpty(CraftingContainer container, ItemStack replacement) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (!container.getItem(i).isEmpty()) {
                container.setItem(i, replacement);
                return;
            }
        }
    }

    private static void clear(CraftingContainer container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            container.setItem(i, ItemStack.EMPTY);
        }
    }
}
