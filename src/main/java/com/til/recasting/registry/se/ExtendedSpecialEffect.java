package com.til.recasting.registry.se;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.entity.JudgementCutEntity;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.handler.AttackHelper;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.registry.RecastingDataComponents;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.BladeStateAccess;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.jetbrains.annotations.Nullable;

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
    private int maxLevel = 5;

    @Getter
    @Setter
    private boolean isSpecial = false;

    public ExtendedSpecialEffect() {
        super(0, false, false);
        NeoForge.EVENT_BUS.register(this);
    }

    public boolean hasSpecialEffect(ISlashBladeState slashBladeState) {
        if (slashBladeState == null || slashBladeState.isBroken()) {
            return false;
        }
        ResourceLocation key = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.getKey(this);
        if (key == null) {
            return false;
        }
        return slashBladeState.hasSpecialEffect(key);
    }

    public int getLevel(PropertiesDefinitionExtension propertiesDefinitionExtension) {
        if (propertiesDefinitionExtension == null) {
            return 0;
        }
        ResourceLocation resourceLocation =
                mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.getKey(this);
        if (resourceLocation == null) {
            return 0;
        }
        return propertiesDefinitionExtension.getExtendedSpecialLevels(resourceLocation);
    }

    @Nullable
    protected PropertiesDefinitionExtension getPropertiesDefinitionExtension(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return null;
        }
        return AttackHelper.propertiesOf(itemStack);
    }

    @Nullable
    protected RenderDefinitionExtension getRenderDefinitionExtension(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return null;
        }
        return itemStack.getOrDefault(
                RecastingDataComponents.RENDER_DEFINITION_EXTENSION.get(),
                RenderDefinitionExtension.EMPTY
        );
    }

    @Nullable
    protected IBuffStackData getBuffStackData(Entity entity) {
        if (entity == null) {
            return null;
        }
        return RecastingAttachments.buffStackData(entity);
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

        ISlashBladeState state = BladeStateAccess.of(blade).orElse(null);
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
