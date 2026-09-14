package com.til.recasting.handler;

import com.til.recasting.Config;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 原版挥砍 / 召唤剑 / 次元斩是否改走 Recasting。
 * 物品 Class / SA / SE / 附魔正则任意整串匹配（{@link java.util.regex.Matcher#matches()}）则不接管。
 */
public final class SlashBladeItemHelper {

    private static final RegexGate ITEM_CLASS_GATE = new RegexGate();
    private static final RegexGate SA_GATE = new RegexGate();
    private static final RegexGate SE_GATE = new RegexGate();
    private static final RegexGate ENCHANTMENT_GATE = new RegexGate();

    private SlashBladeItemHelper() {
    }

    public static boolean matchesReplaceRule(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (!(stack.getItem() instanceof ItemSlashBlade)) {
            return false;
        }
        if (ITEM_CLASS_GATE.anyMatches(
                Config.VANILLA_ART_REPLACE_ITEM_CLASS_PATTERNS.get(),
                List.of(stack.getItem().getClass().getName())
        )) {
            return false;
        }
        if (ENCHANTMENT_GATE.anyMatches(Config.VANILLA_ART_REPLACE_ENCHANTMENT_PATTERNS.get(), enchantmentIds(stack))) {
            return false;
        }
        return stack.getCapability(ItemSlashBlade.BLADESTATE)
                .map(state -> !matchesExclusion(state))
                .orElse(true);
    }

    private static boolean matchesExclusion(ISlashBladeState state) {
        ResourceLocation slashArts = state.getSlashArtsKey();
        if (slashArts != null && SA_GATE.anyMatches(Config.VANILLA_ART_REPLACE_SA_PATTERNS.get(), List.of(slashArts.toString()))) {
            return true;
        }
        List<String> specialEffects = new ArrayList<>();
        for (ResourceLocation effectId : state.getSpecialEffects()) {
            if (effectId != null) {
                specialEffects.add(effectId.toString());
            }
        }
        return SE_GATE.anyMatches(Config.VANILLA_ART_REPLACE_SE_PATTERNS.get(), specialEffects);
    }

    private static List<String> enchantmentIds(ItemStack stack) {
        Map<Enchantment, Integer> enchantments = stack.getAllEnchantments();
        if (enchantments.isEmpty()) {
            return List.of();
        }
        List<String> ids = new ArrayList<>(enchantments.size());
        for (Enchantment enchantment : enchantments.keySet()) {
            ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
            if (id != null) {
                ids.add(id.toString());
            }
        }
        return ids;
    }

    private static final class RegexGate {

        private List<String> source = List.of();
        private List<Pattern> compiled = List.of();
        private final Set<String> matchedIds = ConcurrentHashMap.newKeySet();
        private final Set<String> unmatchedIds = ConcurrentHashMap.newKeySet();

        private boolean anyMatches(List<? extends String> patterns, List<String> values) {
            refresh(patterns);
            if (compiled.isEmpty() || values == null || values.isEmpty()) {
                return false;
            }
            for (String value : values) {
                if (value == null || value.isEmpty()) {
                    continue;
                }
                if (matchesCached(value)) {
                    return true;
                }
            }
            return false;
        }

        private boolean matchesCached(String value) {
            if (matchedIds.contains(value)) {
                return true;
            }
            if (unmatchedIds.contains(value)) {
                return false;
            }
            boolean matched = false;
            for (Pattern pattern : compiled) {
                if (pattern.matcher(value).matches()) {
                    matched = true;
                    break;
                }
            }
            if (matched) {
                matchedIds.add(value);
            } else {
                unmatchedIds.add(value);
            }
            return matched;
        }

        private synchronized void refresh(List<? extends String> patterns) {
            List<String> snapshot = patterns == null ? List.of() : List.copyOf(patterns);
            if (snapshot.equals(source)) {
                return;
            }
            source = snapshot;
            compiled = compilePatterns(snapshot);
            matchedIds.clear();
            unmatchedIds.clear();
        }

        private static List<Pattern> compilePatterns(List<String> patterns) {
            List<Pattern> compiled = new ArrayList<>();
            for (String pattern : patterns) {
                if (pattern == null || pattern.isBlank()) {
                    continue;
                }
                try {
                    compiled.add(Pattern.compile(pattern));
                } catch (PatternSyntaxException ignored) {
                }
            }
            return compiled;
        }
    }
}
