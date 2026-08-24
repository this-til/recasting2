package com.til.recasting.mixin;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Mixin 条件加载：JEI 相关 Mixin 仅在 JEI 存在时启用。
 * <p>
 * TODO(P4): 登记 LockOnManagerMixin / ItemSlashBladeInventoryTickMixin / SlashBladeDefinition* /
 * RefineHandlerMixin / LivingEntitySetHealthMixin / SummonedSwordArtsMixin / EnchantmentHelperMixin 等。
 * TODO(P5): 登记客户端 ItemSlashBladeMixin / LayerMainBladeMixin。
 * TODO(P7): 登记 JEICompatMixin。
 */
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
        return LoadingModList.get().getModFileById("jei") != null;
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
