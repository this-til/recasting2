package com.til.recasting.generated.client;

import com.til.recasting.Recasting;
import com.til.recasting.generated.client.language.LanguageItem;
import com.til.recasting.constant.LanguageItems;
import com.til.recasting.generated.client.language.LanguageTypes;
import lombok.extern.log4j.Log4j2;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Log4j2
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientDataGenerator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        net.minecraft.data.DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();


        // 收集所有 LanguageItem 字段
        List<LanguageItem> languageItemList = Arrays.stream(LanguageItems.class.getDeclaredFields())
                .filter(field -> field.getType().equals(LanguageItem.class))
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> Modifier.isFinal(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .map(field -> {
                    try {
                        return field.get(null);
                    } catch (IllegalAccessException e) {
                        log.warn("无法访问 LanguageItem 字段", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(o -> o instanceof LanguageItem)
                .map(o -> (LanguageItem) o)
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

