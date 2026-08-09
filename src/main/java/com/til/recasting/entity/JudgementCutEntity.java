package com.til.recasting.entity;

import com.til.recasting.handler.ParticleHelper;
import com.til.recasting.registry.RecastingAttackTypes;
import lombok.Getter;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: til
 * @Description: 次次次次次元斩
 */
public class JudgementCutEntity extends ContinuousDamageEntity {

    @Getter
    protected int seed;

    static private final ResourceLocation modelLocation = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/util/slashdim.obj");
    static private final ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(SlashBlade.MODID, "model/util/slashdim.png");

    public JudgementCutEntity(EntityType<? extends JudgementCutEntity> entityTypeIn, Level worldIn, LivingEntity shooting) {
        super(entityTypeIn, worldIn, shooting);

        this.setRepeatedAttack(false);
        this.setAttackInterval(2);
        this.setMaxLifeTime(10);
        this.seed = this.random.nextInt(360);

        addAttackType(RecastingAttackTypes.JUDGEMENT_CUT_ATTACK.get());

        setModel(modelLocation);
        setTexture(textureLocation);
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        if (level().isClientSide()) {
            ParticleHelper.spawnParticle(level(), ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 16, 0.5, 0.5, 0.5, 0.25f);
        }
    }

}
