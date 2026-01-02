package com.til.recasting.client.generated.language;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class LanguageItem {
    @Getter
    final Supplier<String> keySupplier;

    @Getter
    Map<LanguageTypes, String> translations = new HashMap<>();

    public LanguageItem(String key) {
        this.keySupplier = () -> key;
    }

    public LanguageItem(Supplier<String> keySupplier) {
        this.keySupplier = keySupplier;
    }

    public String getKey() {
        return keySupplier.get();
    }

    public LanguageItem addTranslation(LanguageTypes type, String translation) {
        translations.put(type, translation);
        return this;
    }

}

