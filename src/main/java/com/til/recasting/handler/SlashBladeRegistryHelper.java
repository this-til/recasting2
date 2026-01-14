package com.til.recasting.handler;

import lombok.extern.slf4j.Slf4j;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.client.renderer.model.BladeModelManager;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Optional;

/**
 * SlashBlade 注册表访问辅助类
 * 提供在不同环境下访问 SlashBladeDefinition 注册表的统一接口
 */
@Slf4j
public class SlashBladeRegistryHelper {

    private static volatile RegistryAccess cachedRegistryAccess = null;

    /**
     * 从 Level 获取 SlashBladeDefinition 注册表
     */
    public static Optional<Registry<SlashBladeDefinition>> getRegistry(Level level) {
        if (level == null) {
            return getRegistry();
        }

        try {
            if (level.isClientSide()) {
                return getClientRegistry();
            } else {
                return Optional.of(level.registryAccess().registryOrThrow(SlashBladeDefinition.REGISTRY_KEY));
            }
        } catch (Exception e) {
            log.error("从 Level 获取 SlashBladeDefinition 注册表失败", e);
            return Optional.empty();
        }
    }

    /**
     * 尝试获取 SlashBladeDefinition 注册表（无需 Level）
     * 会尝试从服务端或客户端获取
     */
    public static Optional<Registry<SlashBladeDefinition>> getRegistry() {
        try {
            // 尝试从缓存获取
            if (cachedRegistryAccess != null) {
                return Optional.of(cachedRegistryAccess.registryOrThrow(SlashBladeDefinition.REGISTRY_KEY));
            }

            // 根据运行环境选择获取方式
            if (FMLEnvironment.dist == Dist.CLIENT) {
                return getClientRegistry();
            } else {
                return getServerRegistry();
            }
        } catch (Exception e) {
            log.error("获取 SlashBladeDefinition 注册表失败", e);
            return Optional.empty();
        }
    }

    /**
     * 从客户端获取注册表
     */
    @OnlyIn(Dist.CLIENT)
    private static Optional<Registry<SlashBladeDefinition>> getClientRegistry() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getConnection() != null) {
                Registry<SlashBladeDefinition> registry = mc.getConnection().registryAccess()
                        .registryOrThrow(SlashBladeDefinition.REGISTRY_KEY);
                return Optional.of(registry);
            }
        } catch (Exception e) {
            log.debug("从客户端获取注册表失败", e);
        }
        return Optional.empty();
    }

    /**
     * 从服务端获取注册表
     */
    private static Optional<Registry<SlashBladeDefinition>> getServerRegistry() {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                Registry<SlashBladeDefinition> registry = server.registryAccess()
                        .registryOrThrow(SlashBladeDefinition.REGISTRY_KEY);
                return Optional.of(registry);
            }
        } catch (Exception e) {
            log.debug("从服务端获取注册表失败", e);
        }
        return Optional.empty();
    }

    /**
     * 从注册表中获取 SlashBladeDefinition
     */
    public static Optional<SlashBladeDefinition> getDefinition(ResourceLocation name) {
        try {
            Optional<Registry<SlashBladeDefinition>> registryOpt = getRegistry();
            if (registryOpt.isPresent()) {
                Registry<SlashBladeDefinition> registry = registryOpt.get();
                SlashBladeDefinition definition = registry.get(name);
                if (definition != null) {
                    return Optional.of(definition);
                }
            }
        } catch (Exception e) {
            log.error("获取 SlashBladeDefinition 失败: {}", name, e);
        }
        return Optional.empty();
    }

    /**
     * 从注册表中获取 SlashBladeDefinition（使用 ResourceKey）
     */
    public static Optional<SlashBladeDefinition> getDefinition(ResourceKey<SlashBladeDefinition> key) {
        return getDefinition(key.location());
    }

    /**
     * 从 Level 获取 SlashBladeDefinition
     */
    public static Optional<SlashBladeDefinition> getDefinition(Level level, ResourceLocation name) {
        try {
            Optional<Registry<SlashBladeDefinition>> registryOpt = getRegistry(level);
            if (registryOpt.isPresent()) {
                Registry<SlashBladeDefinition> registry = registryOpt.get();
                SlashBladeDefinition definition = registry.get(name);
                if (definition != null) {
                    return Optional.of(definition);
                }
            }
        } catch (Exception e) {
            log.error("从 Level 获取 SlashBladeDefinition 失败: {}", name, e);
        }
        return Optional.empty();
    }

    /**
     * 缓存 RegistryAccess，以便在没有 Level 的情况下使用
     */
    public static void cacheRegistryAccess(RegistryAccess registryAccess) {
        cachedRegistryAccess = registryAccess;
    }

    /**
     * 清除缓存的 RegistryAccess
     */
    public static void clearCache() {
        cachedRegistryAccess = null;
    }
}
