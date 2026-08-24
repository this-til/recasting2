package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.mixin.SlashArtsAccessor;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.event.handler.FallHandler;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

/**
 * 扩展的 SlashArts 类
 * 自动关联一个 ComboState
 */
public abstract class ExtendedSlashArts extends SlashArts {

    public ExtendedSlashArts() {
        super(e -> SlashBlade.prefix("none"));
        SlashArtsAccessor slashArtsAccessor = (SlashArtsAccessor) this;
        slashArtsAccessor.setComboState(e -> getComboStateName());
        slashArtsAccessor.setComboStateJust(e -> getComboStateName());
    }

    public ResourceLocation getComboStateName() {
        return SlashArts.getRegistryKey(this);
    }

    public ComboState createComboState() {
        return ComboState.Builder.newInstance()
                .startAndEnd(1923, 1928)
                .speed(0.5F)
                .priority(50)
                .next(entity -> SlashBlade.prefix("judgement_cut_slash_air"))
                .nextOfTimeout(entity -> SlashBlade.prefix("judgement_cut_sheath_air"))
                .addTickAction(ComboState.TimeLineTickAction.getBuilder().put(0, e -> {
                    if (e.level().isClientSide()) {
                        return;
                    }

                    ItemStack mainHandItem = e.getMainHandItem();
                    if (mainHandItem.isEmpty()) {
                        return;
                    }

                    LazyOptional<ISlashBladeState> slashBladeStateLazyOptional = mainHandItem.getCapability(ItemSlashBlade.BLADESTATE);
                    LazyOptional<PropertiesDefinitionExtension> propertiesDefinitionExtensionLazyOptional = mainHandItem.getCapability(com.til.recasting.handler.CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION);
                    LazyOptional<RenderDefinitionExtension> renderDefinitionExtensionLazyOptional = mainHandItem.getCapability(com.til.recasting.handler.CapabilityRegistryHandler.RENDER_DEFINITION_EXTENSION);

                    if (slashBladeStateLazyOptional.isPresent() && propertiesDefinitionExtensionLazyOptional.isPresent() && renderDefinitionExtensionLazyOptional.isPresent()) {
                        //noinspection DataFlowIssue
                        trigger(e, mainHandItem, slashBladeStateLazyOptional.orElse(null), renderDefinitionExtensionLazyOptional.orElse(null), propertiesDefinitionExtensionLazyOptional.orElse(null));
                    }
                }).build())
                .addTickAction(FallHandler::fallResist)
                .addTickAction(UserPoseOverrider::resetRot)
                .addHitEffect(StunManager::setStun).build();
    }

    public abstract void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension);

    public String getDescId() {
        return getDescriptionId() + ".desc";
    }
}
