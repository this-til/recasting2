package com.til.recasting.handler;

import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.registry.requir.SlashBladeItems;
import mods.flammpfeil.slashblade.entity.BladeStandEntity;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.til.recasting.Recasting.MODID;

/**
 * 刀架台上用无 SA 的耀魂宝珠敲击，切换刀的基础射击形态（幻影剑 / 幻影飞刃）。
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PhantomBladeModeStandHandler {

    @SubscribeEvent
    public static void onBladeStandAttack(SlashBladeEvent.BladeStandAttackEvent event) {
        if (!(event.getDamageSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack blade = event.getBlade();
        if (blade.isEmpty()) {
            return;
        }
        if (!stack.is(SlashBladeItems.PROUDSOUL_SPHERE.get())) {
            return;
        }
        if (stack.isEnchanted()) {
            return;
        }

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SpecialAttackType")) {
            return;
        }

        blade.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(extension -> {
            int shrink = player.isCreative()
                    ? 0
                    : 1;
            if (stack.getCount() < shrink) {
                return;
            }

            boolean next = !extension.trackingPhantomBlade();
            extension.trackingPhantomBlade(next);

            if (shrink > 0) {
                stack.shrink(shrink);
            }

            spawnSucceedEffects(player.level(), event.getBladeStand(), player.getRandom());
            player.displayClientMessage(
                    Component.translatable(
                            next
                                    ? RecastingLanguageKeys.MESSAGE_PHANTOM_BLADE_TRACKING
                                    : RecastingLanguageKeys.MESSAGE_PHANTOM_BLADE_NORMAL
                    ),
                    true
            );
            event.setCanceled(true);
        });
    }

    private static void spawnSucceedEffects(Level world, BladeStandEntity bladeStand, RandomSource random) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        serverLevel.playSound(
                bladeStand,
                bladeStand.getPos(),
                SoundEvents.WITHER_SPAWN,
                SoundSource.BLOCKS,
                0.5f,
                0.8f
        );
        for(int i = 0; i < 32; ++i) {
            double xDist = (random.nextFloat() * 2.0F - 1.0F);
            double yDist = (random.nextFloat() * 2.0F - 1.0F);
            double zDist = (random.nextFloat() * 2.0F - 1.0F);
            if ((xDist * xDist + yDist * yDist + zDist * zDist) <= 1.0D) {
                double x = bladeStand.getX(xDist / 4.0D);
                double y = bladeStand.getY(0.5D + yDist / 4.0D);
                double z = bladeStand.getZ(zDist / 4.0D);
                serverLevel.sendParticles(
                        ParticleTypes.PORTAL,
                        x, y, z,
                        0,
                        xDist, yDist + 0.2D, zDist,
                        1
                );
            }
        }
    }
}
