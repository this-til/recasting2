package com.til.recasting.registry;

import com.til.recasting.Recasting;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Recasting ComboState 注册表；条目由 {@link SlashArtsRegistry#registerExtendedSA} 同步写入。
 */
public final class RecastingComboStateRegistry {

    public static final DeferredRegister<ComboState> COMBO_STATE = DeferredRegister.create(
            ComboState.REGISTRY_KEY,
            Recasting.MODID
    );

    private RecastingComboStateRegistry() {
    }
}
