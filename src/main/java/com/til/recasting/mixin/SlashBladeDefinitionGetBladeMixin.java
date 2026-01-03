package com.til.recasting.mixin;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.mixin_api.ISlashBladeStateExtension;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin 用于在 SlashBladeDefinition.getBlade 方法中设置扩展 Capability 的值
 */
@Mixin(SlashBladeDefinition.class)
public abstract class SlashBladeDefinitionGetBladeMixin {

    /**
     * 在 getBlade 方法返回之前，将 SlashBladeDefinition 中的扩展数据设置到 ItemStack 的 Capability 中
     */
    @Inject(method = "getBlade(Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN"), remap = false)
    private void recasting$setExtensionCapabilities(net.minecraft.world.item.Item bladeItem,
                                                    CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (result == null || result.isEmpty()) {
            return;
        }

        SlashBladeDefinition self = (SlashBladeDefinition) (Object) this;
        if (!(self instanceof ISlashBladeStateExtension extension)) {
            return;
        }

        // 设置 PropertiesDefinitionExtension
        result.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .ifPresent(capability -> {
                    PropertiesDefinitionExtension source = extension.getRecasting$propertiesDefinitionExtension();
                    if (source != null) {
                        capability.attackDistance(source.attackDistance());
                    }
                });

        // 设置 RenderDefinitionExtension
        result.getCapability(CapabilityRegistryHandler.RENDER_DEFINITION_EXTENSION)
                .ifPresent(capability -> {
                    RenderDefinitionExtension source = extension.getRecasting$renderDefinitionExtension();
                    if (source != null) {
                        capability.summondSwordModel(source.summondSwordModel());
                        capability.summondSwordTexture(source.summondSwordTexture());
                        capability.slashEffectModel(source.slashEffectModel());
                        capability.slashEffectTexture(source.slashEffectTexture());
                        capability.judgementCutModel(source.judgementCutModel());
                        capability.judgementCutTexture(source.judgementCutTexture());
                    }
                });
    }
}

