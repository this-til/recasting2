package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.mixin.SlashArtsAccessor;
import com.til.recasting.registry.RecastingDataComponents;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.event.handler.FallHandler;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 扩展 SlashArts：构造时绑定同名 ComboState，并在 tick0 触发 {@link #trigger}。
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

                    ISlashBladeState state = BladeStateAccess.of(mainHandItem).orElse(null);
                    if (state == null) {
                        return;
                    }

                    PropertiesDefinitionExtension properties = AttackHelper.propertiesOf(mainHandItem);
                    RenderDefinitionExtension render = mainHandItem.getOrDefault(
                            RecastingDataComponents.RENDER_DEFINITION_EXTENSION.get(),
                            RenderDefinitionExtension.EMPTY
                    );
                    trigger(e, mainHandItem, state, render, properties);
                }).build())
                .addTickAction(FallHandler::fallResist)
                .addHitEffect(StunManager::setStun)
                .build();
    }

    public abstract void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    );

    public String getDescId() {
        return getDescriptionId() + ".desc";
    }
}
