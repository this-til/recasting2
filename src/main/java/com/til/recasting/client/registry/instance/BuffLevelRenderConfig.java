package com.til.recasting.client.registry.instance;

import com.til.recasting.client.generated.language.LanguageItem;
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
    private final Supplier<BuffType> buffTypeSupplier;

    /**
     * 关联的 LanguageItem，用于获取翻译键
     */
    private final LanguageItem languageItem;

    /**
     * 获取关联的 BuffType
     */
    public BuffType getBuffType() {
        return buffTypeSupplier.get();
    }

    /**
     * 获取翻译键
     */
    public String getTranslationKey() {
        return languageItem.getKey();
    }
}

