package com.til.recasting.generated;

import com.til.recasting.constant.SlashBladeDefinitions;
import lombok.extern.log4j.Log4j2;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class RecastingSlashBladeBuiltInRegistry {

    public static void registerAll(BootstapContext<SlashBladeDefinition> bootstrap) {
        // 使用反射获取 SlashBladeDefinitions 中所有的 SlashBladeDefinition 静态字段
        List<Field> definitionFields = getSlashBladeDefinitionFields();
        
        log.info("开始注册 Recasting SlashBlade 定义，共 {} 个", definitionFields.size());
        
        for (Field field : definitionFields) {
            try {
                field.setAccessible(true);
                SlashBladeDefinition definition = (SlashBladeDefinition) field.get(null);
                
                if (definition != null) {
                    ResourceLocation name = definition.getName();
                    ResourceKey<SlashBladeDefinition> key = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, name);
                    bootstrap.register(key, definition);
                    log.debug("已注册 SlashBlade 定义: {} ({})", field.getName(), name);
                } else {
                    log.warn("SlashBladeDefinition 字段 {} 的值为 null，跳过注册", field.getName());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("无法访问 SlashBladeDefinition 字段: " + field.getName(), e);
            } catch (ClassCastException e) {
                log.error("字段 {} 不是 SlashBladeDefinition 类型", field.getName(), e);
            }
        }
        
        log.info("完成注册 Recasting SlashBlade 定义");
    }

    private static List<Field> getSlashBladeDefinitionFields() {
        List<Field> fields = new ArrayList<>();
        Field[] allFields = SlashBladeDefinitions.class.getDeclaredFields();
        
        for (Field field : allFields) {
            // 只处理静态 final 的 SlashBladeDefinition 字段
            if (Modifier.isStatic(field.getModifiers()) 
                    && Modifier.isFinal(field.getModifiers())
                    && field.getType().equals(SlashBladeDefinition.class)) {
                fields.add(field);
            }
        }
        
        return fields;
    }
}

