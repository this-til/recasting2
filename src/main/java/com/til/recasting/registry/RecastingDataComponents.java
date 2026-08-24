package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.capability.FeBladeEnergyData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.capability.SECrystalData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

/**
 * 物品 DataComponent 注册表（原 Capability 物品侧）。
 */
public final class RecastingDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Recasting.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SECrystalData>> SE_CRYSTAL_DATA =
            register("se_crystal_data", builder -> builder
                    .persistent(SECrystalData.CODEC)
                    .networkSynchronized(SECrystalData.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PropertiesDefinitionExtension>> PROPERTIES_DEFINITION_EXTENSION =
            register("properties_definition_extension", builder -> builder
                    .persistent(PropertiesDefinitionExtension.CODEC)
                    .networkSynchronized(PropertiesDefinitionExtension.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RenderDefinitionExtension>> RENDER_DEFINITION_EXTENSION =
            register("render_definition_extension", builder -> builder
                    .persistent(RenderDefinitionExtension.CODEC)
                    .networkSynchronized(RenderDefinitionExtension.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FeBladeEnergyData>> FE_BLADE_ENERGY =
            register("fe_blade_energy", builder -> builder
                    .persistent(FeBladeEnergyData.CODEC)
                    .networkSynchronized(FeBladeEnergyData.STREAM_CODEC));

    private RecastingDataComponents() {
    }

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(
            String name,
            UnaryOperator<DataComponentType.Builder<T>> operator
    ) {
        return DATA_COMPONENTS.register(name, () -> operator.apply(DataComponentType.builder()).build());
    }
}
