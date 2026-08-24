package com.til.recasting.advancement;

import com.til.recasting.Recasting;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 本模组成就触发器注册入口。
 */
public final class RecastingCriteriaTriggers {

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, Recasting.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, ForgeSeActionTrigger> FORGE_SE_ACTION =
            TRIGGERS.register("forge_se_action", ForgeSeActionTrigger::new);

    private RecastingCriteriaTriggers() {
    }
}
