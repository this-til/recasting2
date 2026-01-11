package com.til.recasting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * SE结晶有序合成配方的序列化器
 * 
 * @param <T> 基础配方类型
 * @param <U> 目标配方类型
 */
public record SpecialEffectCrystalShapedRecipeSerializer<T extends Recipe<?>, U extends T>(
        RecipeSerializer<T> compose,
        TriFunction<T, @Nullable ResourceLocation, Integer, U> converter
) implements RecipeSerializer<U> {

    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    @Override
    @NotNull
    public U fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
        // 确保有 result 字段
        if (!json.has("result")) {
            JsonObject object = new JsonObject();
            object.addProperty("item", "recasting:se_crystal");
            json.add("result", object);
        }
        
        T recipe = compose().fromJson(id, json);
        
        // 解析特殊效果类型和等级
        ResourceLocation specialEffectType = null;
        int level = -1;
        
        if (json.has("se_crystal")) {
            JsonObject seCrystalData = json.getAsJsonObject("se_crystal");
            
            if (seCrystalData.has("special_effect_type")) {
                specialEffectType = ResourceLocation.parse(GsonHelper.getAsString(seCrystalData, "special_effect_type"));
            }
            
            if (seCrystalData.has("level")) {
                level = GsonHelper.getAsInt(seCrystalData, "level");
            }
        }
        
        return converter().apply(recipe, specialEffectType, level);
    }

    @Override
    @NotNull
    public U fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
        T recipe = compose().fromNetwork(id, buf);
        
        // 读取特殊效果类型
        ResourceLocation specialEffectType = null;
        if (buf.readBoolean()) {
            specialEffectType = buf.readResourceLocation();
        }
        
        // 读取等级
        int level = buf.readInt();
        
        return converter().apply(recipe, specialEffectType, level);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull U recipe) {
        compose().toNetwork(buf, recipe);
        
        if (recipe instanceof SpecialEffectCrystalShapedRecipe seCrystalRecipe) {
            // 写入特殊效果类型
            boolean hasType = seCrystalRecipe.getSpecialEffectType() != null;
            buf.writeBoolean(hasType);
            if (hasType) {
                buf.writeResourceLocation(seCrystalRecipe.getSpecialEffectType());
            }
            
            // 写入等级
            buf.writeInt(seCrystalRecipe.getLevel());
        } else {
            buf.writeBoolean(false);
            buf.writeInt(-1);
        }
    }
}

