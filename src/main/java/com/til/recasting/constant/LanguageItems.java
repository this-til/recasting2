package com.til.recasting.constant;

import com.til.recasting.generated.client.language.LanguageItem;
import com.til.recasting.generated.client.language.LanguageTypes;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;

public class LanguageItems {

    public static final LanguageItem TEST = new LanguageItem("other.test")
            .addTranslation(LanguageTypes.ZH_CN, "总该说些什么...")
            .addTranslation(LanguageTypes.EN_US, "I should say something...");

    // ========== Void SlashBlades ==========
    public static final LanguageItem VOID_1 = createLanguageItem(SlashBladeDefinitions.VOID_1)
            .addTranslation(LanguageTypes.ZH_CN, "洞虚利刃")
            .addTranslation(LanguageTypes.EN_US, "Void Blade I");

    public static final LanguageItem VOID_2 = createLanguageItem(SlashBladeDefinitions.VOID_2)
            .addTranslation(LanguageTypes.ZH_CN, "洞虚利刃[漆黑]")
            .addTranslation(LanguageTypes.EN_US, "Void Blade II");

    public static final LanguageItem VOID_3 = createLanguageItem(SlashBladeDefinitions.VOID_3)
            .addTranslation(LanguageTypes.ZH_CN, "洞虚利刃[猩红]")
            .addTranslation(LanguageTypes.EN_US, "Void Blade III");

    // ========== Base SlashBlades ==========
    public static final LanguageItem BA_GUA = createLanguageItem(SlashBladeDefinitions.BA_GUA)
            .addTranslation(LanguageTypes.ZH_CN, "八卦剑")
            .addTranslation(LanguageTypes.EN_US, "Ba Gua Sword");

    public static final LanguageItem BA_GUA_BIG = createLanguageItem(SlashBladeDefinitions.BA_GUA_BIG)
            .addTranslation(LanguageTypes.ZH_CN, "八卦巨剑")
            .addTranslation(LanguageTypes.EN_US, "Ba Gua Greatsword");

    public static final LanguageItem BA_GUA_BIG_LAMBDA = createLanguageItem(SlashBladeDefinitions.BA_GUA_BIG_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^八卦巨剑")
            .addTranslation(LanguageTypes.EN_US, "Ba Gua Greatsword Lambda");

    public static final LanguageItem BLACK = createLanguageItem(SlashBladeDefinitions.BLACK)
            .addTranslation(LanguageTypes.ZH_CN, "黑刃")
            .addTranslation(LanguageTypes.EN_US, "Black Blade");

    public static final LanguageItem ART_KNIFE = createLanguageItem(SlashBladeDefinitions.ART_KNIFE)
            .addTranslation(LanguageTypes.ZH_CN, "美工刀")
            .addTranslation(LanguageTypes.EN_US, "Art Knife");

    public static final LanguageItem BLUE_CLOUD = createLanguageItem(SlashBladeDefinitions.BLUE_CLOUD)
            .addTranslation(LanguageTypes.ZH_CN, "青云")
            .addTranslation(LanguageTypes.EN_US, "Blue Cloud");

    public static final LanguageItem BLUE_CLOUD_LAMBDA = createLanguageItem(SlashBladeDefinitions.BLUE_CLOUD_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^青云")
            .addTranslation(LanguageTypes.EN_US, "Blue Cloud Lambda");

    public static final LanguageItem SILVER_WING = createLanguageItem(SlashBladeDefinitions.SILVER_WING)
            .addTranslation(LanguageTypes.ZH_CN, "云翼")
            .addTranslation(LanguageTypes.EN_US, "Silver Wing");

    public static final LanguageItem SILVER_WING_LAMBDA = createLanguageItem(SlashBladeDefinitions.SILVER_WING_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^云翼")
            .addTranslation(LanguageTypes.EN_US, "Silver Wing Lambda");

    public static final LanguageItem COLOR_WING = createLanguageItem(SlashBladeDefinitions.COLOR_WING)
            .addTranslation(LanguageTypes.ZH_CN, "彩翼")
            .addTranslation(LanguageTypes.EN_US, "Color Wing");

    public static final LanguageItem COLOR_WING_LAMBDA = createLanguageItem(SlashBladeDefinitions.COLOR_WING_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^彩翼")
            .addTranslation(LanguageTypes.EN_US, "Color Wing Lambda");

    public static final LanguageItem COOL_MINT = createLanguageItem(SlashBladeDefinitions.COOL_MINT)
            .addTranslation(LanguageTypes.ZH_CN, "冰薄荷")
            .addTranslation(LanguageTypes.EN_US, "Cool Mint");

    public static final LanguageItem COOL_MINT_LAMBDA = createLanguageItem(SlashBladeDefinitions.COOL_MINT_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^冰薄荷")
            .addTranslation(LanguageTypes.EN_US, "Cool Mint Lambda");

    public static final LanguageItem DHARMA_STICK = createLanguageItem(SlashBladeDefinitions.DHARMA_STICK)
            .addTranslation(LanguageTypes.ZH_CN, "法棍")
            .addTranslation(LanguageTypes.EN_US, "Dharma Stick");

    public static final LanguageItem DRAGON_SCALE = createLanguageItem(SlashBladeDefinitions.DRAGON_SCALE)
            .addTranslation(LanguageTypes.ZH_CN, "龙鳞")
            .addTranslation(LanguageTypes.EN_US, "Dragon Scale");

    public static final LanguageItem DRAGON_SCALE_LAMBDA = createLanguageItem(SlashBladeDefinitions.DRAGON_SCALE_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^龙鳞")
            .addTranslation(LanguageTypes.EN_US, "Dragon Scale Lambda");

    public static final LanguageItem DRAGON = createLanguageItem(SlashBladeDefinitions.DRAGON)
            .addTranslation(LanguageTypes.ZH_CN, "龙一")
            .addTranslation(LanguageTypes.EN_US, "Dragon");

    public static final LanguageItem DRAGON_LAMBDA = createLanguageItem(SlashBladeDefinitions.DRAGON_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^龙一")
            .addTranslation(LanguageTypes.EN_US, "Dragon Lambda");

    public static final LanguageItem HOE = createLanguageItem(SlashBladeDefinitions.HOE)
            .addTranslation(LanguageTypes.ZH_CN, "锄头")
            .addTranslation(LanguageTypes.EN_US, "Hoe");

    public static final LanguageItem LONG_SKY_SUNSET = createLanguageItem(SlashBladeDefinitions.LONG_SKY_SUNSET)
            .addTranslation(LanguageTypes.ZH_CN, "长空落日")
            .addTranslation(LanguageTypes.EN_US, "Long Sky Sunset");

    public static final LanguageItem LONG_SKY_SUNSET_LAMBDA = createLanguageItem(SlashBladeDefinitions.LONG_SKY_SUNSET_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^长空落日")
            .addTranslation(LanguageTypes.EN_US, "Long Sky Sunset Lambda");

    public static final LanguageItem OBLITERATE = createLanguageItem(SlashBladeDefinitions.OBLITERATE)
            .addTranslation(LanguageTypes.ZH_CN, "烈焰")
            .addTranslation(LanguageTypes.EN_US, "Obliterate");

    public static final LanguageItem OBLITERATE_LAMBDA = createLanguageItem(SlashBladeDefinitions.OBLITERATE_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^烈焰")
            .addTranslation(LanguageTypes.EN_US, "Obliterate Lambda");

    public static final LanguageItem PHYSICS_SWORD = createLanguageItem(SlashBladeDefinitions.PHYSICS_SWORD)
            .addTranslation(LanguageTypes.ZH_CN, "物理学圣剑")
            .addTranslation(LanguageTypes.EN_US, "Physics Sword");

    public static final LanguageItem UMBRELLA = createLanguageItem(SlashBladeDefinitions.UMBRELLA)
            .addTranslation(LanguageTypes.ZH_CN, "伞")
            .addTranslation(LanguageTypes.EN_US, "Umbrella");

    public static final LanguageItem UMBRELLA_LAMBDA = createLanguageItem(SlashBladeDefinitions.UMBRELLA_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^伞")
            .addTranslation(LanguageTypes.EN_US, "Umbrella Lambda");

    public static final LanguageItem XUAN_YUAN = createLanguageItem(SlashBladeDefinitions.XUAN_YUAN)
            .addTranslation(LanguageTypes.ZH_CN, "轩辕剑")
            .addTranslation(LanguageTypes.EN_US, "Xuan Yuan");

    // ========== Fluorescence SlashBlades ==========
    public static final LanguageItem FLUORESCENCE_1 = createLanguageItem(SlashBladeDefinitions.FLUORESCENCE_1)
            .addTranslation(LanguageTypes.ZH_CN, "荧光")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade I");

    public static final LanguageItem FLUORESCENCE_2 = createLanguageItem(SlashBladeDefinitions.FLUORESCENCE_2)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[剑]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade II");

    public static final LanguageItem FLUORESCENCE_3 = createLanguageItem(SlashBladeDefinitions.FLUORESCENCE_3)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[砍刀]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade III");

    public static final LanguageItem FLUORESCENCE_4 = createLanguageItem(SlashBladeDefinitions.FLUORESCENCE_4)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[刃]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade IV");

    public static final LanguageItem FLUORESCENCE_5 = createLanguageItem(SlashBladeDefinitions.FLUORESCENCE_5)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[战斧]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade V");

    public static final LanguageItem FLUORESCENCE_6 = createLanguageItem(SlashBladeDefinitions.FLUORESCENCE_6)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[闪电]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade VI");

    public static final LanguageItem FLUORESCENCE_7 = createLanguageItem(SlashBladeDefinitions.FLUORESCENCE_7)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[锤子]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade VII");

    public static final LanguageItem FLUORESCENCE_8 = createLanguageItem(SlashBladeDefinitions.FLUORESCENCE_8)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[镰刀]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade VIII");

    // ========== Laser SlashBlades ==========
    public static final LanguageItem LASER_1 = createLanguageItem(SlashBladeDefinitions.LASER_1)
            .addTranslation(LanguageTypes.ZH_CN, "激光剑")
            .addTranslation(LanguageTypes.EN_US, "Laser Blade I");

    public static final LanguageItem LASER_2 = createLanguageItem(SlashBladeDefinitions.LASER_2)
            .addTranslation(LanguageTypes.ZH_CN, "激光剑[脉冲]")
            .addTranslation(LanguageTypes.EN_US, "Laser Blade II");

    public static final LanguageItem LASER_3 = createLanguageItem(SlashBladeDefinitions.LASER_3)
            .addTranslation(LanguageTypes.ZH_CN, "激光剑[阻断]")
            .addTranslation(LanguageTypes.EN_US, "Laser Blade III");

    // ========== Special SlashBlades ==========
    public static final LanguageItem TIL = createLanguageItem(SlashBladeDefinitions.TIL)
            .addTranslation(LanguageTypes.ZH_CN, "til的刀")
            .addTranslation(LanguageTypes.EN_US, "TIL");

    public static final LanguageItem TIL_LAMBDA = createLanguageItem(SlashBladeDefinitions.TIL_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^til的刀")
            .addTranslation(LanguageTypes.EN_US, "TIL Lambda");

    public static final LanguageItem HTOD = createLanguageItem(SlashBladeDefinitions.HTOD)
            .addTranslation(LanguageTypes.ZH_CN, "HTOD的刀")
            .addTranslation(LanguageTypes.EN_US, "HTOD");

    public static final LanguageItem HTOD_LAMBDA = createLanguageItem(SlashBladeDefinitions.HTOD_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^HTOD的刀")
            .addTranslation(LanguageTypes.EN_US, "HTOD Lambda");

    public static final LanguageItem XING_KONG = createLanguageItem(SlashBladeDefinitions.XING_KONG)
            .addTranslation(LanguageTypes.ZH_CN, "星空的刀")
            .addTranslation(LanguageTypes.EN_US, "Xing Kong");

    public static final LanguageItem XING_KONG_LAMBDA = createLanguageItem(SlashBladeDefinitions.XING_KONG_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^星空的刀")
            .addTranslation(LanguageTypes.EN_US, "Xing Kong Lambda");

    // ========== Star SlashBlades ==========
    public static final LanguageItem STAR_1 = createLanguageItem(SlashBladeDefinitions.STAR_1)
            .addTranslation(LanguageTypes.ZH_CN, "星流利刃I")
            .addTranslation(LanguageTypes.EN_US, "Star Blade I");

    public static final LanguageItem STAR_2 = createLanguageItem(SlashBladeDefinitions.STAR_2)
            .addTranslation(LanguageTypes.ZH_CN, "星流利刃II")
            .addTranslation(LanguageTypes.EN_US, "Star Blade II");

    public static final LanguageItem STAR_3 = createLanguageItem(SlashBladeDefinitions.STAR_3)
            .addTranslation(LanguageTypes.ZH_CN, "星流利刃III")
            .addTranslation(LanguageTypes.EN_US, "Star Blade III");

    public static final LanguageItem STAR_4 = createLanguageItem(SlashBladeDefinitions.STAR_4)
            .addTranslation(LanguageTypes.ZH_CN, "星流利刃VI")
            .addTranslation(LanguageTypes.EN_US, "Star Blade IV");

    public static final LanguageItem STAR_4_LAMBDA = createLanguageItem(SlashBladeDefinitions.STAR_4_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^星流利刃VI")
            .addTranslation(LanguageTypes.EN_US, "Star Blade IV Lambda");

    private static LanguageItem createLanguageItem(SlashBladeDefinition definition) {
        return new LanguageItem(definition.getTranslationKey());
    }
}
