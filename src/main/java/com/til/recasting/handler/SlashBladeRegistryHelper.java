package com.til.recasting.handler;

import lombok.extern.slf4j.Slf4j;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Optional;

/**
 * SlashBlade 注册表访问辅助类。
 */
@Slf4j
public final class SlashBladeRegistryHelper {

    private static volatile RegistryAccess cachedRegistryAccess = null;

    private SlashBladeRegistryHelper() {
    }

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

    public static Optional<Registry<SlashBladeDefinition>> getRegistry() {
        try {
            if (cachedRegistryAccess != null) {
                return Optional.of(cachedRegistryAccess.registryOrThrow(SlashBladeDefinition.REGISTRY_KEY));
            }

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

    public static Optional<SlashBladeDefinition> getDefinition(ResourceKey<SlashBladeDefinition> key) {
        return getDefinition(key.location());
    }

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

    public static Optional<RegistryAccess> getRegistryAccess() {
        try {
            if (cachedRegistryAccess != null) {
                return Optional.of(cachedRegistryAccess);
            }

            // 集成服服务端线程优先走 ServerLifecycleHooks；仅 Dist.CLIENT 时客户端连接也可兜底
            Optional<RegistryAccess> serverAccess = getServerRegistryAccess();
            if (serverAccess.isPresent()) {
                return serverAccess;
            }
            if (FMLEnvironment.dist == Dist.CLIENT) {
                return getClientRegistryAccess();
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("获取 RegistryAccess 失败", e);
            return Optional.empty();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static Optional<RegistryAccess> getClientRegistryAccess() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getConnection() != null) {
                return Optional.of(mc.getConnection().registryAccess());
            }
        } catch (Exception e) {
            log.debug("从客户端获取 RegistryAccess 失败", e);
        }
        return Optional.empty();
    }

    private static Optional<RegistryAccess> getServerRegistryAccess() {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                return Optional.of(server.registryAccess());
            }
        } catch (Exception e) {
            log.debug("从服务端获取 RegistryAccess 失败", e);
        }
        return Optional.empty();
    }

    public static Optional<ItemStack> getBladeStack(ResourceLocation name) {
        return getRegistryAccess().flatMap(access ->
                getDefinition(name).map(definition -> definition.getBlade(access))
        );
    }

    public static Optional<ItemStack> getBladeStack(ResourceKey<SlashBladeDefinition> key) {
        return getBladeStack(key.location());
    }

    public static Optional<ItemStack> getBladeStack(Level level, ResourceLocation name) {
        if (level == null) {
            return getBladeStack(name);
        }
        return getDefinition(level, name).map(definition -> definition.getBlade(level.registryAccess()));
    }

    public static void cacheRegistryAccess(RegistryAccess registryAccess) {
        cachedRegistryAccess = registryAccess;
    }

    public static void clearCache() {
        cachedRegistryAccess = null;
    }
}
