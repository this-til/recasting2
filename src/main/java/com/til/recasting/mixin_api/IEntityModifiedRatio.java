package com.til.recasting.mixin_api;

import org.spongepowered.asm.mixin.Unique;

public interface IEntityModifiedRatio {
    float getRecasting$modifiedRatio();
    void setRecasting$modifiedRatio(float recasting$modifiedRatio);
}
