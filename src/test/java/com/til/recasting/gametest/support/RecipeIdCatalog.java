package com.til.recasting.gametest.support;

import com.til.recasting.Recasting;
import com.til.recasting.generated.RecastingRecipes;
import com.til.recasting.generated.SlashBladeRecipes;
import com.til.recasting.generated.SpecialEffectRecipes;
import com.til.recasting.generated.RecipeBuilderWrapper;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 与 {@link com.til.recasting.generated.RecastingRecipeProvider} 相同规则枚举期望配方 ID。
 */
public final class RecipeIdCatalog {

    private static final List<Class<?>> RECIPE_CLASSES = Arrays.asList(
            RecastingRecipes.class,
            SlashBladeRecipes.class,
            SpecialEffectRecipes.class
    );

    private RecipeIdCatalog() {
    }

    public static List<ResourceLocation> allExpectedIds() {
        List<ResourceLocation> ids = new ArrayList<>();
        for (Class<?> recipeClass : RECIPE_CLASSES) {
            for (String fieldName : scanFieldNames(recipeClass)) {
                ids.add(Recasting.prefix(fieldName.toLowerCase()));
            }
        }
        return ids;
    }

    private static List<String> scanFieldNames(Class<?> recipeClass) {
        List<String> names = new ArrayList<>();
        for (Field field : recipeClass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            if (field.getType().equals(RecipeBuilderWrapper.class)) {
                names.add(field.getName());
                continue;
            }
            if (field.getType().equals(List.class) && isListOfRecipeBuilderWrapper(field)) {
                try {
                    field.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    List<RecipeBuilderWrapper> list = (List<RecipeBuilderWrapper>) field.get(null);
                    if (list == null) {
                        continue;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) != null) {
                            names.add(field.getName() + "_" + i);
                        }
                    }
                } catch (IllegalAccessException ignored) {
                    // skip inaccessible list field
                }
            }
        }
        return names;
    }

    private static boolean isListOfRecipeBuilderWrapper(Field field) {
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            return false;
        }
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        return typeArguments.length == 1 && typeArguments[0].equals(RecipeBuilderWrapper.class);
    }
}
