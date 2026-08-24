package com.til.recasting.mixin;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.FeEnergyHelper;
import com.til.recasting.mixin_api.ISlashBladeStateExtension;
import com.til.recasting.registry.RecastingDataComponents;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

/**
 * 在 {@link SlashBladeDefinition#getBlade} 返回前写入 DataComponent 扩展字段。
 */
@Mixin(SlashBladeDefinition.class)
public abstract class SlashBladeDefinitionGetBladeMixin {

    @Inject(
            method = "getBlade(Lnet/minecraft/world/item/Item;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN"),
            remap = false
    )
    private void recasting$setExtensionComponents(
            Item bladeItem,
            HolderLookup.Provider registries,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        ItemStack result = cir.getReturnValue();
        if (result == null || result.isEmpty()) {
            return;
        }

        SlashBladeDefinition self = (SlashBladeDefinition) (Object) this;
        if (!(self instanceof ISlashBladeStateExtension extension)) {
            return;
        }

        PropertiesDefinitionExtension source = extension.getRecasting$propertiesDefinitionExtension();
        if (source != null) {
            PropertiesDefinitionExtension copy = new PropertiesDefinitionExtension(
                    source.attackDistance(),
                    new HashMap<>(source.extendedSpecialLevels()),
                    source.trackingPhantomBlade(),
                    source.feCapacity()
            );
            result.set(RecastingDataComponents.PROPERTIES_DEFINITION_EXTENSION.get(), copy);
            if (copy.feCapacity() > 0L) {
                FeEnergyHelper.fillToCapacity(result);
            }
        }

        RenderDefinitionExtension renderSource = extension.getRecasting$renderDefinitionExtension();
        if (renderSource != null) {
            RenderDefinitionExtension renderCopy = new RenderDefinitionExtension(
                    renderSource.summondSwordModel(),
                    renderSource.summondSwordTexture(),
                    renderSource.slashEffectModel(),
                    renderSource.slashEffectTexture(),
                    renderSource.judgementCutModel(),
                    renderSource.judgementCutTexture()
            );
            result.set(RecastingDataComponents.RENDER_DEFINITION_EXTENSION.get(), renderCopy);
        }
    }
}
