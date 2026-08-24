package com.til.recasting.advancement;

import com.mojang.serialization.Codec;
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
    EXTRACT_SLASH_ARTS("extract_slash_arts"),
    SWAP_SPECIAL("swap_special"),
    ERASE_SE("erase_se");

    public static final Codec<ForgeSeAction> CODEC = StringRepresentable.fromEnum(ForgeSeAction::values);

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
