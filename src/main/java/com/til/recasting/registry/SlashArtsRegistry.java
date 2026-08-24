package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.registry.sa.ExtendedSlashArts;
import com.til.recasting.registry.sa.ProbeSlashArts;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Slash Arts (SA) 注册表。
 */
public final class SlashArtsRegistry {

    public static final DeferredRegister<SlashArts> SLASH_ARTS = DeferredRegister.create(
            SlashArts.REGISTRY_KEY,
            Recasting.MODID
    );

    // TODO(P3): 从 1.20 批量移植其余 SA 注册项后删除探针
    public static final DeferredHolder<SlashArts, ExtendedSlashArts> PROBE =
            registerExtendedSA("probe", ProbeSlashArts::new);

    private SlashArtsRegistry() {
    }

    /**
     * 注册扩展 SA，并同步注册同名 ComboState。
     */
    public static DeferredHolder<SlashArts, ExtendedSlashArts> registerExtendedSA(
            String name,
            Supplier<ExtendedSlashArts> factory
    ) {
        Supplier<ExtendedSlashArts> slashArtsSupplier = memoize(factory);
        DeferredHolder<SlashArts, ExtendedSlashArts> slashArtsHolder =
                SLASH_ARTS.register(name, slashArtsSupplier);

        RecastingComboStateRegistry.COMBO_STATE.register(
                name,
                () -> slashArtsSupplier.get().createComboState()
        );

        return slashArtsHolder;
    }

    private static <T> Supplier<T> memoize(Supplier<T> factory) {
        AtomicReference<T> reference = new AtomicReference<>();
        return () -> {
            T cached = reference.get();
            if (cached != null) {
                return cached;
            }
            T created = factory.get();
            if (reference.compareAndSet(null, created)) {
                return created;
            }
            return reference.get();
        };
    }
}
