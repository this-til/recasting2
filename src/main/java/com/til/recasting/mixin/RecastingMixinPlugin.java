package com.til.recasting.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class RecastingMixinPlugin implements IMixinConfigPlugin {

    private static final String JEI_COMPAT_MIXIN = "com.til.recasting.mixin.JEICompatMixin";
    private static final String JEI_COMPAT_TARGET = "mods.flammpfeil.slashblade.compat.jei.JEICompat";

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

        return isClassPresent(JEI_COMPAT_TARGET);
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

    private boolean isClassPresent(String className) {
        try {
            Class.forName(className, false, this.getClass().getClassLoader());
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
