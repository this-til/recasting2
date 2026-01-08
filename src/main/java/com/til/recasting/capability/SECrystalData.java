package com.til.recasting.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

/**
 * SE Crystal 数据实现类
 */
public class SECrystalData implements ISECrystalData, INBTSerializable<CompoundTag> {
    
    private static final String KEY_SPECIAL_EFFECT_TYPE = "SpecialEffectType";
    private static final String KEY_SPECIAL_EFFECT_LEVEL = "SpecialEffectTypeLevel";

    @Nullable
    private ResourceLocation specialEffectType;
    private int specialEffectLevel = 0;

    @Override
    @Nullable
    public ResourceLocation getSpecialEffectType() {
        return specialEffectType;
    }

    @Override
    public void setSpecialEffectType(@Nullable ResourceLocation specialEffectType) {
        this.specialEffectType = specialEffectType;
    }

    @Override
    public int getSpecialEffectLevel() {
        return specialEffectLevel;
    }

    @Override
    public void setSpecialEffectLevel(int level) {
        this.specialEffectLevel = Math.max(0, level);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        
        if (specialEffectType != null) {
            tag.putString(KEY_SPECIAL_EFFECT_TYPE, specialEffectType.toString());
        }
        
        if (specialEffectLevel > 0) {
            tag.putInt(KEY_SPECIAL_EFFECT_LEVEL, specialEffectLevel);
        }
        
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag == null) {
            clear();
            return;
        }

        if (tag.contains(KEY_SPECIAL_EFFECT_TYPE)) {
            String typeString = tag.getString(KEY_SPECIAL_EFFECT_TYPE);
            this.specialEffectType = ResourceLocation.tryParse(typeString);
        } else {
            this.specialEffectType = null;
        }

        if (tag.contains(KEY_SPECIAL_EFFECT_LEVEL)) {
            this.specialEffectLevel = tag.getInt(KEY_SPECIAL_EFFECT_LEVEL);
        } else {
            this.specialEffectLevel = 0;
        }
    }
}

