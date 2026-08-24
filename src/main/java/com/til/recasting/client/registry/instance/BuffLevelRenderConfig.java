package com.til.recasting.client.registry.instance;

import com.til.recasting.registry.instance.BuffType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

/**
 * Buff层数渲染配置
 * 用于标记哪些 BuffType 需要在实体名称上方显示层数
 */
@Getter
@AllArgsConstructor
public class BuffLevelRenderConfig {

    /**
     * 关联的 BuffType 供应商
     */
    private final Supplier<? extends BuffType> buffTypeSupplier;

    /**
     * 直接使用的翻译键
     */
    private final String translationKey;

    public BuffType getBuffType() {
        return buffTypeSupplier.get();
    }
}

