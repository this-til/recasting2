package com.til.recasting.registry.sa;

import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.constant.R;
import com.til.recasting.entity.MatrixEntity;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.PosHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import com.til.recasting.registry.RecastingBuffTypes;
import com.til.recasting.registry.RecastingEntities;
import com.til.recasting.registry.instance.BuffType;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.util.KnockBacks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 穷观阵 Slash Arts
 * 在目标位置创建一个穷观阵，持续造成伤害，同时叠加演算buff层数，演算使目标受到的伤害更高
 */
@Setter
@Accessors(chain = true)
public class MatrixSlashArts extends ExtendedSlashArts {

    static final Map<Entity, MatrixEntity> matrixEntity = new WeakHashMap<>();

    float attack = 0.02f;
    int attackInterval = 10;
    int life = 200;
    float size = 16;

    ResourceLocation saTexture = R.Models.Special.matrix$png;
    ResourceLocation saModel = R.Models.Special.matrix$obj;

    /**
     * 从 Map 中移除指定实体的穷观阵记录
     * 当 MatrixEntity 被移除时调用此方法清理记录
     */
    public static void removeMatrixEntity(Entity entity) {
        matrixEntity.remove(entity);
    }

    @Override
    public void trigger(LivingEntity livingEntity, ItemStack itemStack, ISlashBladeState slashBladeState, RenderDefinitionExtension renderDefinitionExtension, PropertiesDefinitionExtension propertiesDefinitionExtension) {
        // 如果已存在穷观阵，先移除旧的
        if (matrixEntity.containsKey(livingEntity)) {
            MatrixEntity matrix = matrixEntity.get(livingEntity);
            if (matrix.isAlive()) {
                matrix.remove(RemovalReason.DISCARDED);
            }
        }

        Level worldIn = livingEntity.level();
        if (worldIn.isClientSide()) {
            return;
        }

        // 获取攻击目标位置
        Vec3 attackPos = PosHelper.getAttackTargetPosition(livingEntity, slashBladeState);

        // 创建穷观阵实体
        MatrixEntity matrix = new MatrixEntity(
                RecastingEntities.MATRIX.get(),
                worldIn,
                livingEntity
        ) {
            @Override
            public void remove(@NotNull RemovalReason reason) {
                super.remove(reason);
                LivingEntity shooter = getShooter();
                if (shooter != null) {
                    removeMatrixEntity(shooter);
                }
            }
        };

        // 设置位置
        matrix.setPos(attackPos.x, attackPos.y + 0.01, attackPos.z);

        // 设置模型和纹理
        matrix.setModel(saModel);
        matrix.setTexture(saTexture);

        // 设置属性
        matrix.setMaxLifeTime(life);
        matrix.setAttackInterval(attackInterval);

        matrix.setModifiedRatio(attack);
        matrix.setColor(slashBladeState.getColorCode());
        matrix.setSize(size);

        // 添加攻击类型
        matrix.setAttackTypeModelList(List.of(RecastingAttackTypes.MATRIX.get()));

        // 添加攻击回调：命中时给目标添加混乱层buff
        matrix.attackActionCallbackPoint.register(hitEntity -> hitEntity.getCapability(CapabilityRegistryHandler.BUFF_STACK_DATA).ifPresent(
                buffStackData -> {
                    Level world = hitEntity.level();
                    BuffType chaosLayerBuffType = RecastingBuffTypes.CALCULUS.get();

                    // 获取当前层数
                    int currentLevel = buffStackData.getLevel(chaosLayerBuffType, world);

                    // 增加层数
                    int newLevel = currentLevel + 1;
                    buffStackData.setLevel(chaosLayerBuffType, newLevel, world);

                    KnockBacks.cancel.action.accept(hitEntity);
                }
        ));

        // 添加到世界
        worldIn.addFreshEntity(matrix);
        matrixEntity.put(livingEntity, matrix);
    }
}
