package com.til.recasting.registry.sa;

import com.til.recasting.capability.IBuffStackData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.R;
import com.til.recasting.entity.MatrixEntity;
import com.til.recasting.registry.RecastingAttachments;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

/**
 * 穷观阵 Slash Arts
 * 在目标位置创建一个穷观阵，持续造成伤害，同时叠加演算buff层数，演算使目标受到的伤害更高
 */
@Getter
@Setter
@Accessors(chain = true)
public class MatrixSlashArts extends ExtendedSlashArts {

    private static final String KEY_MATRIX_ENTITY_UUID = "MatrixEntityUuid";

    float attack = 0.02f;
    int attackIntervalTicks = 10;
    int lifeTicks = 200;
    float size = 16;

    ResourceLocation saTexture = R.Models.Special.matrix$png;
    ResourceLocation saModel = R.Models.Special.matrix$obj;

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        Level worldIn = livingEntity.level();
        if (worldIn.isClientSide()) {
            return;
        }

        IBuffStackData buffStackData = RecastingAttachments.buffStackData(livingEntity);

        discardExistingMatrix(buffStackData, worldIn);

        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        MatrixEntity matrix = new MatrixEntity(
                RecastingEntities.MATRIX.get(),
                worldIn,
                livingEntity
        );

        matrix.setPos(attackPos.x, attackPos.y + 0.01, attackPos.z);
        matrix.setModel(saModel);
        matrix.setTexture(saTexture);
        matrix.setMaxLifeTime(lifeTicks);
        matrix.setAttackInterval(attackIntervalTicks);
        matrix.setModifiedRatio(attack);
        matrix.setColor(slashBladeState.getColorCode());
        matrix.setSize(size);
        matrix.setAttackTypeModelList(List.of(RecastingAttackTypes.MATRIX.get()));

        matrix.attackActionCallbackPoint.register(hitEntity -> {
            IBuffStackData hitBuffStackData = RecastingAttachments.buffStackData(hitEntity);
            Level world = hitEntity.level();
            int currentLevel = hitBuffStackData.getLevel(RecastingBuffTypes.CALCULUS.get(), world);
            hitBuffStackData.setLevel(RecastingBuffTypes.CALCULUS.get(), currentLevel + 1, world);
        });

        worldIn.addFreshEntity(matrix);

        CompoundTag customData = buffStackData.getOrCreateCustomData(RecastingBuffTypes.MATRIX.get(), worldIn);
        customData.putUUID(KEY_MATRIX_ENTITY_UUID, matrix.getUUID());
        buffStackData.setLevel(RecastingBuffTypes.MATRIX.get(), lifeTicks, worldIn);
    }

    private static void discardExistingMatrix(IBuffStackData buffStackData, Level world) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }

        IBuffStackData.BuffEntry entry = buffStackData.getEntry(RecastingBuffTypes.MATRIX.get());
        if (entry == null || entry.getCustomData() == null) {
            return;
        }

        CompoundTag customData = entry.getCustomData();
        if (!customData.hasUUID(KEY_MATRIX_ENTITY_UUID)) {
            return;
        }

        UUID uuid = customData.getUUID(KEY_MATRIX_ENTITY_UUID);
        Entity entity = serverLevel.getEntity(uuid);
        if (entity != null && entity.isAlive()) {
            entity.remove(RemovalReason.DISCARDED);
        }
    }
}
