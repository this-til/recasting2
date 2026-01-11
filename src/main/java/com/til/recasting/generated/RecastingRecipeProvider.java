package com.til.recasting.generated;

import com.til.recasting.Recasting;
import com.til.recasting.constant.RecastingRecipes;
import com.til.recasting.constant.SlashBladeRecipes;
import com.til.recasting.constant.SpecialEffectRecipes;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Recasting 模组的配方生成器
 * 从多个常量类中收集 RecipeBuilderWrapper 并生成配方
 * 配方ID自动使用字段名转小写
 */
@Log4j2
public class RecastingRecipeProvider extends RecipeProvider implements IConditionBuilder {

    /**
     * 要扫描的配方常量类列表
     */
    private static final List<Class<?>> RECIPE_CLASSES = Arrays.asList(
            RecastingRecipes.class,
            SlashBladeRecipes.class,
            SpecialEffectRecipes.class
    );

    public RecastingRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        // 收集所有 RecipeBuilderWrapper 字段及其名称
        List<RecipeBuilderEntry> recipeBuilders = getRecipeBuilderWrappers();
        
        log.info("开始生成 Recasting 配方，共 {} 个", recipeBuilders.size());
        
        for (RecipeBuilderEntry entry : recipeBuilders) {
            try {
                // 生成配方ID：字段名转小写
                String fieldName = entry.fieldName;
                ResourceLocation recipeId = Recasting.prefix(fieldName.toLowerCase());
                
                // 调用构建器保存配方
                entry.wrapper.build(consumer, recipeId);
                log.debug("已生成配方: {} (字段名: {}, 来源类: {})", recipeId, fieldName, entry.sourceClass.getSimpleName());
            } catch (Exception e) {
                log.error("生成配方时发生错误，字段名: {}, 来源类: {}", entry.fieldName, entry.sourceClass.getSimpleName(), e);
            }
        }
        
        log.info("完成生成 Recasting 配方");
    }
    
    /**
     * 配方构建器条目（包含字段名、wrapper 和来源类）
     */
    private static class RecipeBuilderEntry {
        final String fieldName;
        final RecipeBuilderWrapper wrapper;
        final Class<?> sourceClass;
        
        RecipeBuilderEntry(String fieldName, RecipeBuilderWrapper wrapper, Class<?> sourceClass) {
            this.fieldName = fieldName;
            this.wrapper = wrapper;
            this.sourceClass = sourceClass;
        }
    }
    
    /**
     * 使用反射从多个类中获取所有的 RecipeBuilderWrapper 静态字段
     */
    private List<RecipeBuilderEntry> getRecipeBuilderWrappers() {
        List<RecipeBuilderEntry> entries = new ArrayList<>();
        
        for (Class<?> recipeClass : RECIPE_CLASSES) {
            entries.addAll(scanClassForRecipeBuilders(recipeClass));
        }
        
        return entries;
    }
    
    /**
     * 扫描指定类中的所有 RecipeBuilderWrapper 静态字段
     */
    private List<RecipeBuilderEntry> scanClassForRecipeBuilders(Class<?> recipeClass) {
        List<RecipeBuilderEntry> entries = new ArrayList<>();
        Field[] allFields = recipeClass.getDeclaredFields();
        
        for (Field field : allFields) {
            // 只处理静态 final 的 RecipeBuilderWrapper 字段
            if (Modifier.isStatic(field.getModifiers())
                    && Modifier.isFinal(field.getModifiers())
                    && field.getType().equals(RecipeBuilderWrapper.class)) {
                try {
                    field.setAccessible(true);
                    RecipeBuilderWrapper wrapper = (RecipeBuilderWrapper) field.get(null);
                    
                    if (wrapper != null) {
                        entries.add(new RecipeBuilderEntry(field.getName(), wrapper, recipeClass));
                        log.debug("找到 RecipeBuilderWrapper 字段: {} (来源类: {})", field.getName(), recipeClass.getSimpleName());
                    } else {
                        log.warn("RecipeBuilderWrapper 字段 {} (来源类: {}) 的值为 null，跳过", field.getName(), recipeClass.getSimpleName());
                    }
                } catch (IllegalAccessException e) {
                    log.error("无法访问 RecipeBuilderWrapper 字段: {} (来源类: {})", field.getName(), recipeClass.getSimpleName(), e);
                } catch (ClassCastException e) {
                    log.error("字段 {} (来源类: {}) 不是 RecipeBuilderWrapper 类型", field.getName(), recipeClass.getSimpleName(), e);
                }
            }
        }
        
        return entries;
    }
}

