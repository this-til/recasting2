package com.til.recasting.generated;

import com.til.recasting.Recasting;
import com.til.recasting.constant.SlashBladeRecipes;
import lombok.extern.log4j.Log4j2;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Recasting 模组的刀配方生成器
 * 从 SlashBladeRecipes 常量类中收集 RecipeBuilderWrapper 并生成配方
 * 配方ID自动使用字段名转小写
 */
@Log4j2
public class SlashBladeRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public SlashBladeRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        // 收集所有 RecipeBuilderWrapper 字段及其名称
        List<RecipeBuilderEntry> recipeBuilders = getRecipeBuilderWrappers();
        
        log.info("开始生成 Recasting 刀配方，共 {} 个", recipeBuilders.size());
        
        for (RecipeBuilderEntry entry : recipeBuilders) {
            try {
                // 生成配方ID：字段名转小写
                String fieldName = entry.fieldName;
                ResourceLocation recipeId = Recasting.prefix(fieldName.toLowerCase());
                
                // 调用构建器保存配方
                entry.wrapper.build(consumer, recipeId);
                log.debug("已生成刀配方: {} (字段名: {})", recipeId, fieldName);
            } catch (Exception e) {
                log.error("生成刀配方时发生错误，字段名: {}", entry.fieldName, e);
            }
        }
        
        log.info("完成生成 Recasting 刀配方");
    }
    
    /**
     * 配方构建器条目（包含字段名和 wrapper）
     */
    private static class RecipeBuilderEntry {
        final String fieldName;
        final RecipeBuilderWrapper wrapper;
        
        RecipeBuilderEntry(String fieldName, RecipeBuilderWrapper wrapper) {
            this.fieldName = fieldName;
            this.wrapper = wrapper;
        }
    }
    
    /**
     * 使用反射获取 SlashBladeRecipes 中所有的 RecipeBuilderWrapper 静态字段
     */
    private List<RecipeBuilderEntry> getRecipeBuilderWrappers() {
        List<RecipeBuilderEntry> entries = new ArrayList<>();
        Field[] allFields = SlashBladeRecipes.class.getDeclaredFields();
        
        for (Field field : allFields) {
            // 只处理静态 final 的 RecipeBuilderWrapper 字段
            if (Modifier.isStatic(field.getModifiers())
                    && Modifier.isFinal(field.getModifiers())
                    && field.getType().equals(RecipeBuilderWrapper.class)) {
                try {
                    field.setAccessible(true);
                    RecipeBuilderWrapper wrapper = (RecipeBuilderWrapper) field.get(null);
                    
                    if (wrapper != null) {
                        entries.add(new RecipeBuilderEntry(field.getName(), wrapper));
                        log.debug("找到 RecipeBuilderWrapper 字段: {}", field.getName());
                    } else {
                        log.warn("RecipeBuilderWrapper 字段 {} 的值为 null，跳过", field.getName());
                    }
                } catch (IllegalAccessException e) {
                    log.error("无法访问 RecipeBuilderWrapper 字段: {}", field.getName(), e);
                } catch (ClassCastException e) {
                    log.error("字段 {} 不是 RecipeBuilderWrapper 类型", field.getName(), e);
                }
            }
        }
        
        return entries;
    }
}

