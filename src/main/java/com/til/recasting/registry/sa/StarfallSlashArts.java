package com.til.recasting.registry.sa;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.constant.R;
import com.til.recasting.entity.StarfallArrayEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.EntityHelper;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * [回到未来计划]群星坠落：展开粒子阵体；视向锁定目标时钉阵追击，否则跟随自身并在范围内随机落星。
 */
@Getter
@Setter
@Accessors(chain = true)
public class StarfallSlashArts extends ExtendedSlashArts {

    private static final String KEY_ARRAY_UUID = "StarfallArrayUuid";

    private ResourceLocation starModel = R.Models.Special.starfallStar$obj;
    private ResourceLocation starTexture = R.Models.Special.starfallStar$png;
    private float starRatio = 0.08f;
    private int life = 600;
    private int arrayColor = 0xAACCFF;
    private float seekRange = 45.0f;

    @Override
    public void trigger(
            LivingEntity livingEntity,
            ItemStack itemStack,
            ISlashBladeState slashBladeState,
            RenderDefinitionExtension renderDefinitionExtension,
            PropertiesDefinitionExtension propertiesDefinitionExtension
    ) {
        Level level = livingEntity.level();
        if (level.isClientSide()) {
            return;
        }

        IBuffStackData buffStackData = livingEntity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).orElse(null);
        if (buffStackData == null) {
            return;
        }

        discardExistingArray(buffStackData, level);

        Entity locked = slashBladeState.getTargetEntity(level);
        LivingEntity pinTarget = locked instanceof LivingEntity living && living.isAlive()
                ? living
                : null;
        boolean pinOnLockedTarget = pinTarget != null
                && EntityHelper.selectClosestInViewCone(livingEntity)
                .filter(view -> view.getId() == pinTarget.getId())
                .isPresent();

        StarfallArrayEntity array = new StarfallArrayEntity(
                RecastingEntities.STARFALL_ARRAY.get(),
                level,
                livingEntity
        );
        array.setMaxLifeTime(life);
        array.setModifiedRatio(starRatio);
        array.setColor(arrayColor);
        array.setSeekRange(seekRange);
        array.setStarColor(slashBladeState.getColorCode());
        array.setStarModel(starModel);
        array.setStarTexture(starTexture);

        if (pinOnLockedTarget) {
            array.setMode(StarfallArrayEntity.MODE_PIN);
            array.setPinTarget(pinTarget);
            array.setPos(pinTarget.getX(), pinTarget.getY(), pinTarget.getZ());
        } else {
            array.setMode(StarfallArrayEntity.MODE_FOLLOW);
            array.setPos(livingEntity.getX(), livingEntity.getY() + 0.1, livingEntity.getZ());
        }

        level.addFreshEntity(array);

        CompoundTag customData = buffStackData.getOrCreateCustomData(RecastingBuffTypes.STARFALL.get(), level);
        customData.putUUID(KEY_ARRAY_UUID, array.getUUID());
        buffStackData.setLevel(RecastingBuffTypes.STARFALL.get(), life, level);

        level.playSound(
                null,
                livingEntity.getX(),
                livingEntity.getY(),
                livingEntity.getZ(),
                SoundEvents.END_PORTAL_SPAWN,
                SoundSource.PLAYERS,
                0.35f,
                1.4f
        );
    }

    private static void discardExistingArray(IBuffStackData buffStackData, Level world) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }

        IBuffStackData.BuffEntry entry = buffStackData.getEntry(RecastingBuffTypes.STARFALL.get());
        if (entry == null || entry.getCustomData() == null) {
            return;
        }

        CompoundTag customData = entry.getCustomData();
        if (!customData.hasUUID(KEY_ARRAY_UUID)) {
            return;
        }

        UUID uuid = customData.getUUID(KEY_ARRAY_UUID);
        Entity entity = serverLevel.getEntity(uuid);
        if (entity != null && entity.isAlive()) {
            entity.remove(RemovalReason.DISCARDED);
        }
    }
}
