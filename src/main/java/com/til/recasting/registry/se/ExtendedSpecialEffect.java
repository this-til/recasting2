package com.til.recasting.registry.se;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.CapabilityRegistryHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

@Accessors(chain = true)
public class ExtendedSpecialEffect extends SpecialEffect {

    protected record JudgementCutContext(
            JudgementCutEntity judgementCut,
            LivingEntity shooter,
            ItemStack blade,
            ISlashBladeState state,
            @Nullable PropertiesDefinitionExtension properties,
            int effectLevel
    ) {
    }

    @Getter
    @Setter
    int maxLevel = 5;

    @Getter
    @Setter
    boolean isSpecial = false;

    public ExtendedSpecialEffect() {
        super(0, false, false);
        MinecraftForge.EVENT_BUS.register(this);
    }


    public boolean hasSpecialEffect(ISlashBladeState slashBladeState) {
        return slashBladeState.hasSpecialEffect(
                mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getKey(this)
        );
    }

    public int getLevel(PropertiesDefinitionExtension propertiesDefinitionExtension) {
        if (propertiesDefinitionExtension == null) {
            return 0;
        }

        // 获取当前 SpecialEffect 的 ResourceLocation
        IForgeRegistry<SpecialEffect> registry = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get();
        ResourceLocation resourceLocation = registry.getKey(this);

        return propertiesDefinitionExtension.getExtendedSpecialLevels(resourceLocation);
    }

    protected PropertiesDefinitionExtension getPropertiesDefinitionExtension(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return null;
        }
        //noinspection DataFlowIssue
        return itemStack.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .orElse(null);
    }

    protected RenderDefinitionExtension getRenderDefinitionExtension(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return null;
        }
        //noinspection DataFlowIssue
        return itemStack.getCapability(CapabilityRegistryHandler.RENDER_DEFINITION_EXTENSION)
                .orElse(null);
    }

    protected IBuffStackData getBuffStackData(Entity entity) {
        if (entity == null) {
            return null;
        }
        //noinspection DataFlowIssue
        return entity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA)
                .orElse(null);
    }

    @Nullable
    protected LivingEntity resolveServerLivingTarget(AttackAmplifierEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return null;
        }
        if (event.getAttacker().level().isClientSide()) {
            return null;
        }
        if (!(event.getTarget() instanceof LivingEntity target) || !target.isAlive()) {
            return null;
        }
        return target;
    }

    @Nullable
    protected JudgementCutContext resolveJudgementCutContext(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return null;
        }
        if (!(event.getEntity() instanceof JudgementCutEntity judgementCut)) {
            return null;
        }

        LivingEntity shooter = judgementCut.getShooter();
        if (shooter == null) {
            return null;
        }
        ItemStack blade = shooter.getMainHandItem();
        if (blade.isEmpty()) {
            return null;
        }

        ISlashBladeState state = blade.getCapability(ItemSlashBlade.BLADESTATE).orElse(null);
        if (state == null || !hasSpecialEffect(state)) {
            return null;
        }
        PropertiesDefinitionExtension properties = getPropertiesDefinitionExtension(blade);
        return new JudgementCutContext(
                judgementCut,
                shooter,
                blade,
                state,
                properties,
                getLevel(properties)
        );
    }

    public String getDescId() {
        return getDescriptionId() + ".desc";
    }

    @Override
    public String toString() {
        try {
            return super.toString();
        } catch (Exception ignored) {
        }
        return this.getClass().getSimpleName();
    }

}
