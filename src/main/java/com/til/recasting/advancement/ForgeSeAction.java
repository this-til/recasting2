package com.til.recasting.advancement;

import net.minecraft.util.StringRepresentable;

/**
 * 铁砧锻造相关成就动作。
 */
public enum ForgeSeAction implements StringRepresentable {
    ENGRAVE_ANY("engrave_any"),
    ENGRAVE_MAX_NORMAL("engrave_max_normal"),
    LAYOUT_FOUR_NORMAL_ONE_SPECIAL("layout_four_normal_one_special"),
    LAYOUT_FOUR_MAX_NORMAL_ONE_SPECIAL("layout_four_max_normal_one_special"),
    EXTRACT_SPECIAL("extract_special"),
    SWAP_SPECIAL("swap_special"),
    ERASE_SE("erase_se");

    private final String serializedName;

    ForgeSeAction(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    public static ForgeSeAction byName(String name) {
        for (ForgeSeAction action : values()) {
            if (action.serializedName.equals(name)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Unknown forge SE action: " + name);
    }
}
