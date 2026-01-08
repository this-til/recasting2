package com.til.recasting.client.generated;

import com.til.recasting.Recasting;
import com.til.recasting.client.generated.language.LanguageItem;
import com.til.recasting.client.constant.LanguageItems;
import com.til.recasting.client.generated.language.LanguageTypes;
import lombok.extern.log4j.Log4j2;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Log4j2
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientDataGenerator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        net.minecraft.data.DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // 注册物品模型生成器
        dataGenerator.addProvider(event.includeClient(), new RecastingItemModelProvider(packOutput, existingFileHelper));

        // 收集所有 LanguageItem 字段（包括单个 LanguageItem 和 List<LanguageItem>）
        List<LanguageItem> languageItemList = Arrays.stream(LanguageItems.class.getDeclaredFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> Modifier.isFinal(field.getModifiers()))
                .filter(field -> {
                    // 检查是否为单个 LanguageItem 字段
                    if (field.getType().equals(LanguageItem.class)) {
                        return true;
                    }
                    // 检查是否为 List<LanguageItem> 字段
                    if (List.class.isAssignableFrom(field.getType())) {
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType paramType = (ParameterizedType) genericType;
                            Type[] actualTypes = paramType.getActualTypeArguments();
                            if (actualTypes.length == 1 && actualTypes[0] == LanguageItem.class) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .peek(field -> field.setAccessible(true))
                .flatMap(field -> {
                    try {
                        Object fieldValue = field.get(null);
                        if (fieldValue == null) {
                            return Stream.empty();
                        }
                        
                        // 如果是单个 LanguageItem
                        if (fieldValue instanceof LanguageItem) {
                            return Stream.of((LanguageItem) fieldValue);
                        }
                        
                        // 如果是 List<LanguageItem>
                        if (fieldValue instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<LanguageItem> list = (List<LanguageItem>) fieldValue;
                            return list.stream()
                                    .filter(Objects::nonNull)
                                    .filter(item -> item instanceof LanguageItem);
                        }
                        
                        // 如果是其他 Collection 类型（兼容性处理）
                        if (fieldValue instanceof Collection) {
                            @SuppressWarnings("unchecked")
                            Collection<?> collection = (Collection<?>) fieldValue;
                            return collection.stream()
                                    .filter(Objects::nonNull)
                                    .filter(item -> item instanceof LanguageItem)
                                    .map(item -> (LanguageItem) item);
                        }
                        
                        return Stream.empty();
                    } catch (IllegalAccessException e) {
                        log.warn("无法访问字段: {} (类型: {})", field.getName(), field.getType().getSimpleName(), e);
                        return Stream.empty();
                    } catch (Exception e) {
                        log.error("处理字段时发生错误: {} (类型: {})", field.getName(), field.getType().getSimpleName(), e);
                        return Stream.empty();
                    }
                })
                .toList();

        // 为每种语言类型创建 LanguageProvider
        Arrays.stream(LanguageTypes.values())
                .forEach(type -> {
                    LanguageProvider provider = new LanguageProvider(packOutput, Recasting.MODID, type.getLocale()) {
                        @Override
                        protected void addTranslations() {
                            languageItemList.stream()
                                    .filter(item -> item.getTranslations().containsKey(type))
                                    .forEach(item -> {
                                        String translation = item.getTranslations().get(type);
                                        if (translation != null) {
                                            add(item.getKey(), translation);
                                        }
                                    });
                        }
                    };
                    event.getGenerator().addProvider(event.includeClient(), provider);
                });
    }
}

