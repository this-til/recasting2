package com.til.recasting.generated;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * RecipeBuilder 包装类
 * 用于在常量类中定义 RecipeBuilder，然后通过反射自动收集并生成配方
 * 
 * 这是一个函数式接口，接受 Consumer<FinishedRecipe> 和字段名，执行配方构建逻辑
 * 配方ID会自动使用字段名转小写：Recasting.prefix(fieldName.toLowerCase())
 */
@FunctionalInterface
public interface RecipeBuilderWrapper {
    
    /**
     * 构建并保存配方
     * @param consumer 配方消费者，用于接收生成的配方
     * @param recipeId 配方ID（基于字段名自动生成）
     */
    void build(Consumer<FinishedRecipe> consumer, ResourceLocation recipeId);
}

