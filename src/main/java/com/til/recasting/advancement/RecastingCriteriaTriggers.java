package com.til.recasting.advancement;

import net.minecraft.advancements.CriteriaTriggers;

/**
 * 本模组成就触发器注册入口。
 */
public final class RecastingCriteriaTriggers {

    public static final ForgeSeActionTrigger FORGE_SE_ACTION =
            CriteriaTriggers.register(new ForgeSeActionTrigger());

    private RecastingCriteriaTriggers() {
    }

    /** 触发类加载以完成 {@link CriteriaTriggers#register}。 */
    public static void bootstrap() {
    }
}
