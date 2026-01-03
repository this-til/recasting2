package com.til.recasting.mixin;

import mods.flammpfeil.slashblade.entity.EntityJudgementCut;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.slasharts.JudgementCut;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(JudgementCut.class)
public class JudgementCutMixin {

    /**
     * @author til
     * @reason 统一伤害
     */
    @Overwrite(remap = false)
    public static EntityJudgementCut doJudgementCutJust(LivingEntity user) {
        return com.til.recasting.util.JudgementCut.doJudgementCutJust(user, 1.5f, 0); // TODO 写入配置
    }

    /**
     * @author til
     * @reason 统一伤害
     */
    @Overwrite(remap = false)
    public static EntityJudgementCut doJudgementCut(LivingEntity user) {
        return com.til.recasting.util.JudgementCut.doJudgementCut(user, 1.5f, 0); // TODO 写入配置
    }
}
