package com.til.recasting.event;

import lombok.Getter;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 客户端收集拔刀剑物品栏进度条数据；先 {@link #addBar} 的在最底下。
 */
@Getter
public class SlashBladeItemBarCollectEvent extends Event {

    private final ItemStack stack;
    private final List<Bar> bars = new ArrayList<>();

    public SlashBladeItemBarCollectEvent(ItemStack stack) {
        this.stack = stack;
    }

    public record Bar(int colorRgb, float progress) {
    }

    public void addBar(int colorRgb, float progress) {
        bars.add(new Bar(colorRgb, Mth.clamp(progress, 0.0f, 1.0f)));
    }

    public List<Bar> getBars() {
        return Collections.unmodifiableList(bars);
    }
}
