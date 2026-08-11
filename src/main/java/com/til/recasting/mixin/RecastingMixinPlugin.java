package com.til.recasting.mixin;

import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class RecastingMixinPlugin implements IMixinConfigPlugin {

    private static final String JEI_COMPAT_MIXIN = "com.til.recasting.mixin.JEICompatMixin";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!JEI_COMPAT_MIXIN.equals(mixinClassName)) {
            return true;
        }

        // Mixin prepare 时尚无法可靠解析 JEICompat（类加载器隔离 / JEI 接口未就绪），改以 mod 列表判定。
        return FMLLoader.getLoadingModList().getModFileById("jei") != null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
