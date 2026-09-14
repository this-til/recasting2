package com.til.recasting.handler;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.compat.Dmc5SfxCompat;
import com.til.recasting.entity.SlashEffectEntity;
import com.til.recasting.event.DoSlashExtendEvent;
import com.til.recasting.util.DamageStructure;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.til.recasting.Recasting.MODID;

/**
 * 规则命中时取消原版 {@link SlashBladeEvent.DoSlashEvent}，改走 Recasting 斩击。
 * offset / mute / color 来自 {@link DoSlashArgsCapture}（原版事件没有这些字段）。
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VanillaDoSlashHandler {

    private VanillaDoSlashHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDoSlash(SlashBladeEvent.DoSlashEvent event) {
        if (event instanceof DoSlashExtendEvent || event.isCanceled()) {
            return;
        }
        if (!SlashBladeItemHelper.matchesReplaceRule(event.getBlade())) {
            return;
        }
        event.setCanceled(true);
        DoSlashArgsCapture.Captured captured = DoSlashArgsCapture.get();
        Vec3 centerOffset = captured == null ? Vec3.ZERO : captured.centerOffset();
        boolean mute = captured != null && captured.mute();
        int colorCode = captured == null
                ? event.getSlashBladeState().getColorCode()
                : captured.colorCode();
        float range = event.getBlade()
                .getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION)
                .map(PropertiesDefinitionExtension::attackDistance)
                .orElse(1.0f);
        SlashEffectEntity effect = AttackHelper.doSlash(
                event.getUser(),
                event.getRoll(),
                colorCode,
                centerOffset,
                mute,
                event.isCritical(),
                new DamageStructure((float) event.getDamage(), 0),
                range,
                event.getKnockback()
        );
        if (effect != null && Dmc5SfxCompat.shouldMuteSlashEffect(event.getUser())) {
            effect.setMute(true);
        }
    }
}
