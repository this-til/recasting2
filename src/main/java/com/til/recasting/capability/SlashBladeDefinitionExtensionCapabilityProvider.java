package com.til.recasting.capability;

import com.til.recasting.handler.CapabilityRegistryHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * CapabilityProvider 用于同时提供 PropertiesDefinitionExtension 和 RenderDefinitionExtension 两个 capabilities
 */
public class SlashBladeDefinitionExtensionCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    protected final LazyOptional<PropertiesDefinitionExtension> propertiesExtension = LazyOptional.of(PropertiesDefinitionExtension::new);
    protected final LazyOptional<RenderDefinitionExtension> renderExtension = LazyOptional.of(RenderDefinitionExtension::new);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION) {
            return propertiesExtension.cast();
        }
        if (cap == CapabilityRegistryHandler.RENDER_DEFINITION_EXTENSION) {
            return renderExtension.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag baseTag = new CompoundTag();
        
        propertiesExtension.ifPresent(instance -> {
            CompoundTag propertiesTag = instance.serializeNBT();
            if (propertiesTag != null && !propertiesTag.isEmpty()) {
                baseTag.put("properties", propertiesTag);
            }
        });
        
        renderExtension.ifPresent(instance -> {
            CompoundTag renderTag = instance.serializeNBT();
            if (renderTag != null && !renderTag.isEmpty()) {
                baseTag.put("render", renderTag);
            }
        });
        
        return baseTag;
    }

    @Override
    public void deserializeNBT(CompoundTag baseTag) {
        if (baseTag == null) {
            return;
        }
        
        if (baseTag.contains("properties")) {
            propertiesExtension.ifPresent(instance -> 
                    instance.deserializeNBT(baseTag.getCompound("properties")));
        }
        
        if (baseTag.contains("render")) {
            renderExtension.ifPresent(instance -> 
                    instance.deserializeNBT(baseTag.getCompound("render")));
        }
    }
}

