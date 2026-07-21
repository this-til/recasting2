package com.til.recasting.client.constant;

import com.til.recasting.client.generated.language.LanguageItem;
import com.til.recasting.client.generated.language.LanguageTypes;
import com.til.recasting.constant.SlashBladeDefinitions;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.SlashArtsRegistry;
import com.til.recasting.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

public class LanguageItems {

    public static final LanguageItem TEST = new LanguageItem("other.test")
            .addTranslation(LanguageTypes.ZH_CN, "总该说些什么...")
            .addTranslation(LanguageTypes.EN_US, "I should say something...");

    // ========== Buff Names ==========

    public static final LanguageItem BUFF_SOUL_BURN = new LanguageItem("buff.recasting.soul_burn")
            .addTranslation(LanguageTypes.ZH_CN, "灵魂燃烧")
            .addTranslation(LanguageTypes.EN_US, "Soul Burn");

    public static final LanguageItem BUFF_FRAGMENT = new LanguageItem("buff.recasting.fragment")
            .addTranslation(LanguageTypes.ZH_CN, "破片")
            .addTranslation(LanguageTypes.EN_US, "Fragment");

    public static final LanguageItem BUFF_IONIZATION = new LanguageItem("buff.recasting.ionization")
            .addTranslation(LanguageTypes.ZH_CN, "电离")
            .addTranslation(LanguageTypes.EN_US, "Ionization");

    public static final LanguageItem BUFF_ENERGY_STORAGE = new LanguageItem("buff.recasting.energy_storage")
            .addTranslation(LanguageTypes.ZH_CN, "蓄能")
            .addTranslation(LanguageTypes.EN_US, "Energy Storage");

    public static final LanguageItem BUFF_THUNDER_LIGHT = new LanguageItem("buff.recasting.thunder_light")
            .addTranslation(LanguageTypes.ZH_CN, "雷光")
            .addTranslation(LanguageTypes.EN_US, "Thunder Light");

    public static final LanguageItem BUFF_TEAR = new LanguageItem("buff.recasting.tear")
            .addTranslation(LanguageTypes.ZH_CN, "撕裂")
            .addTranslation(LanguageTypes.EN_US, "Tear");

    public static final LanguageItem BUFF_ANNIHILATION = new LanguageItem("buff.recasting.annihilation")
            .addTranslation(LanguageTypes.ZH_CN, "断灭")
            .addTranslation(LanguageTypes.EN_US, "Annihilation");

    public static final LanguageItem BUFF_PHOTON_SCAR = new LanguageItem("buff.recasting.photon_scar")
            .addTranslation(LanguageTypes.ZH_CN, "光子灼痕")
            .addTranslation(LanguageTypes.EN_US, "Photon Scar");

    public static final LanguageItem BUFF_PHOTON_BURN = new LanguageItem("buff.recasting.photon_burn")
            .addTranslation(LanguageTypes.ZH_CN, "光子灼烧")
            .addTranslation(LanguageTypes.EN_US, "Photon Burn");

    public static final LanguageItem BUFF_SUNSET_CORE = new LanguageItem("buff.recasting.sunset_core")
            .addTranslation(LanguageTypes.ZH_CN, "日核")
            .addTranslation(LanguageTypes.EN_US, "Sunset Core");

    public static final LanguageItem BUFF_SUNSET_STACK = new LanguageItem("buff.recasting.sunset_stack")
            .addTranslation(LanguageTypes.ZH_CN, "叠晖")
            .addTranslation(LanguageTypes.EN_US, "Sunset Stack");

    public static final LanguageItem BUFF_GOLDEN_HALBERD = new LanguageItem("buff.recasting.golden_halberd")
            .addTranslation(LanguageTypes.ZH_CN, "金戈")
            .addTranslation(LanguageTypes.EN_US, "Golden Halberd");

    public static final LanguageItem BUFF_TEA_AROMA = new LanguageItem("buff.recasting.tea_aroma")
            .addTranslation(LanguageTypes.ZH_CN, "茶韵")
            .addTranslation(LanguageTypes.EN_US, "Tea Aroma");


    // ========== Void SlashBlades ==========
    public static final LanguageItem VOID_1 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.VOID_1)
            .addTranslation(LanguageTypes.ZH_CN, "洞虚利刃")
            .addTranslation(LanguageTypes.EN_US, "Void Blade I");

    public static final LanguageItem VOID_2 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.VOID_2)
            .addTranslation(LanguageTypes.ZH_CN, "洞虚利刃[漆黑]")
            .addTranslation(LanguageTypes.EN_US, "Void Blade II");

    public static final LanguageItem VOID_3 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.VOID_3)
            .addTranslation(LanguageTypes.ZH_CN, "洞虚利刃[猩红]")
            .addTranslation(LanguageTypes.EN_US, "Void Blade III");

    // ========== Base SlashBlades ==========
    public static final LanguageItem BA_GUA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BA_GUA)
            .addTranslation(LanguageTypes.ZH_CN, "八卦剑")
            .addTranslation(LanguageTypes.EN_US, "Ba Gua Sword");

    public static final LanguageItem BA_GUA_BIG = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BA_GUA_BIG)
            .addTranslation(LanguageTypes.ZH_CN, "八卦巨剑")
            .addTranslation(LanguageTypes.EN_US, "Ba Gua Greatsword");

    public static final LanguageItem BA_GUA_BIG_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BA_GUA_BIG_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^八卦巨剑")
            .addTranslation(LanguageTypes.EN_US, "Ba Gua Greatsword Lambda");

    public static final LanguageItem BLACK = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BLACK)
            .addTranslation(LanguageTypes.ZH_CN, "黑刃")
            .addTranslation(LanguageTypes.EN_US, "Black Blade");

    public static final LanguageItem ART_KNIFE = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.ART_KNIFE)
            .addTranslation(LanguageTypes.ZH_CN, "美工刀")
            .addTranslation(LanguageTypes.EN_US, "Art Knife");

    public static final LanguageItem BLUE_CLOUD = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BLUE_CLOUD)
            .addTranslation(LanguageTypes.ZH_CN, "青云")
            .addTranslation(LanguageTypes.EN_US, "Blue Cloud");

    public static final LanguageItem BLUE_CLOUD_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BLUE_CLOUD_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^青云")
            .addTranslation(LanguageTypes.EN_US, "Blue Cloud Lambda");

    public static final LanguageItem SILVER_WING = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.SILVER_WING)
            .addTranslation(LanguageTypes.ZH_CN, "云翼")
            .addTranslation(LanguageTypes.EN_US, "Silver Wing");

    public static final LanguageItem SILVER_WING_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.SILVER_WING_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^云翼")
            .addTranslation(LanguageTypes.EN_US, "Silver Wing Lambda");

    public static final LanguageItem COLOR_WING = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.COLOR_WING)
            .addTranslation(LanguageTypes.ZH_CN, "彩翼")
            .addTranslation(LanguageTypes.EN_US, "Color Wing");

    public static final LanguageItem COLOR_WING_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.COLOR_WING_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^彩翼")
            .addTranslation(LanguageTypes.EN_US, "Color Wing Lambda");

    public static final LanguageItem COOL_MINT = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.COOL_MINT)
            .addTranslation(LanguageTypes.ZH_CN, "冰薄荷")
            .addTranslation(LanguageTypes.EN_US, "Cool Mint");

    public static final LanguageItem COOL_MINT_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.COOL_MINT_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^冰薄荷")
            .addTranslation(LanguageTypes.EN_US, "Cool Mint Lambda");

    public static final LanguageItem DHARMA_STICK = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.DHARMA_STICK)
            .addTranslation(LanguageTypes.ZH_CN, "法棍")
            .addTranslation(LanguageTypes.EN_US, "Dharma Stick");

    public static final LanguageItem DHARMA_STICK_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.DHARMA_STICK_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^法棍")
            .addTranslation(LanguageTypes.EN_US, "Dharma Stick Lambda");

    public static final LanguageItem DRAGON_SCALE = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.DRAGON_SCALE)
            .addTranslation(LanguageTypes.ZH_CN, "龙鳞")
            .addTranslation(LanguageTypes.EN_US, "Dragon Scale");

    public static final LanguageItem DRAGON_SCALE_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.DRAGON_SCALE_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^龙鳞")
            .addTranslation(LanguageTypes.EN_US, "Dragon Scale Lambda");

    public static final LanguageItem DRAGON = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.DRAGON)
            .addTranslation(LanguageTypes.ZH_CN, "龙魂")
            .addTranslation(LanguageTypes.EN_US, "Dragon Soul");

    public static final LanguageItem DRAGON_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.DRAGON_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^龙魂")
            .addTranslation(LanguageTypes.EN_US, "Dragon Soul Lambda");

    public static final LanguageItem HOE = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.HOE)
            .addTranslation(LanguageTypes.ZH_CN, "锄头")
            .addTranslation(LanguageTypes.EN_US, "Hoe");

    public static final LanguageItem LONG_SKY_SUNSET = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.LONG_SKY_SUNSET)
            .addTranslation(LanguageTypes.ZH_CN, "长空落日")
            .addTranslation(LanguageTypes.EN_US, "Long Sky Sunset");

    public static final LanguageItem LONG_SKY_SUNSET_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.LONG_SKY_SUNSET_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^长空落日")
            .addTranslation(LanguageTypes.EN_US, "Long Sky Sunset Lambda");

    public static final LanguageItem OBLITERATE = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.OBLITERATE)
            .addTranslation(LanguageTypes.ZH_CN, "烈焰")
            .addTranslation(LanguageTypes.EN_US, "Obliterate");

    public static final LanguageItem OBLITERATE_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.OBLITERATE_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^烈焰")
            .addTranslation(LanguageTypes.EN_US, "Obliterate Lambda");

    public static final LanguageItem PHYSICS_SWORD = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.PHYSICS_SWORD)
            .addTranslation(LanguageTypes.ZH_CN, "物理学圣剑")
            .addTranslation(LanguageTypes.EN_US, "Physics Sword");

    public static final LanguageItem UMBRELLA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.UMBRELLA)
            .addTranslation(LanguageTypes.ZH_CN, "伞")
            .addTranslation(LanguageTypes.EN_US, "Umbrella");

    public static final LanguageItem UMBRELLA_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.UMBRELLA_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^伞")
            .addTranslation(LanguageTypes.EN_US, "Umbrella Lambda");

    public static final LanguageItem XUAN_YUAN = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.XUAN_YUAN)
            .addTranslation(LanguageTypes.ZH_CN, "轩辕剑")
            .addTranslation(LanguageTypes.EN_US, "Xuan Yuan");

    public static final LanguageItem BROADSWORD_IRON = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BROADSWORD_IRON)
            .addTranslation(LanguageTypes.ZH_CN, "阔刃（铁）")
            .addTranslation(LanguageTypes.EN_US, "Broadsword (Iron)");

    public static final LanguageItem BROADSWORD_WOOD = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BROADSWORD_WOOD)
            .addTranslation(LanguageTypes.ZH_CN, "阔刃（木）")
            .addTranslation(LanguageTypes.EN_US, "Broadsword (Wood)");

    public static final LanguageItem BROKEN_WHITE = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BROKEN_WHITE)
            .addTranslation(LanguageTypes.ZH_CN, "碎白")
            .addTranslation(LanguageTypes.EN_US, "Broken White");

    public static final LanguageItem GREEN_BLADE_IRON = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.GREEN_BLADE_IRON)
            .addTranslation(LanguageTypes.ZH_CN, "青锋（铁）")
            .addTranslation(LanguageTypes.EN_US, "Green Blade (Iron)");

    public static final LanguageItem GREEN_BLADE_WOOD = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.GREEN_BLADE_WOOD)
            .addTranslation(LanguageTypes.ZH_CN, "青锋（木）")
            .addTranslation(LanguageTypes.EN_US, "Green Blade (Wood)");

    public static final LanguageItem SOULBLADE = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.SOULBLADE)
            .addTranslation(LanguageTypes.ZH_CN, "魂刃")
            .addTranslation(LanguageTypes.EN_US, "Soulblade");

    public static final LanguageItem SUPREME_POLE = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.SUPREME_POLE)
            .addTranslation(LanguageTypes.ZH_CN, "太极")
            .addTranslation(LanguageTypes.EN_US, "Taiji");

    public static final LanguageItem SUPREME_POLE_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.SUPREME_POLE_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^太极")
            .addTranslation(LanguageTypes.EN_US, "Taiji Lambda");

    public static final LanguageItem WIND_CLOUD = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.WIND_CLOUD)
            .addTranslation(LanguageTypes.ZH_CN, "风云")
            .addTranslation(LanguageTypes.EN_US, "Wind Cloud");

    public static final LanguageItem WIND_CLOUD_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.WIND_CLOUD_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^风云")
            .addTranslation(LanguageTypes.EN_US, "Wind Cloud Lambda");

    public static final LanguageItem BRILLIANT_GOLD = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BRILLIANT_GOLD)
            .addTranslation(LanguageTypes.ZH_CN, "灿金")
            .addTranslation(LanguageTypes.EN_US, "Brilliant Gold");

    public static final LanguageItem BRILLIANT_GOLD_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BRILLIANT_GOLD_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^灿金")
            .addTranslation(LanguageTypes.EN_US, "Brilliant Gold Lambda");

    public static final LanguageItem BRILLIANT_TEA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BRILLIANT_TEA)
            .addTranslation(LanguageTypes.ZH_CN, "灿茶")
            .addTranslation(LanguageTypes.EN_US, "Brilliant Tea");

    public static final LanguageItem BRILLIANT_TEA_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.BRILLIANT_TEA_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^灿茶")
            .addTranslation(LanguageTypes.EN_US, "Brilliant Tea Lambda");

    public static final LanguageItem SHINE_GOLD = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.SHINE_GOLD)
            .addTranslation(LanguageTypes.ZH_CN, "闪金")
            .addTranslation(LanguageTypes.EN_US, "Shine Gold");

    public static final LanguageItem SHINE_GOLD_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.SHINE_GOLD_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^闪金")
            .addTranslation(LanguageTypes.EN_US, "Shine Gold Lambda");

    public static final LanguageItem SHINE_TEA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.SHINE_TEA)
            .addTranslation(LanguageTypes.ZH_CN, "闪茶")
            .addTranslation(LanguageTypes.EN_US, "Shine Tea");

    public static final LanguageItem SHINE_TEA_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.SHINE_TEA_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^闪茶")
            .addTranslation(LanguageTypes.EN_US, "Shine Tea Lambda");

    // ========== Fluorescence SlashBlades ==========
    public static final LanguageItem FLUORESCENCE_1 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.FLUORESCENCE_1)
            .addTranslation(LanguageTypes.ZH_CN, "荧光")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade I");

    public static final LanguageItem FLUORESCENCE_2 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.FLUORESCENCE_2)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[剑]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade II");

    public static final LanguageItem FLUORESCENCE_3 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.FLUORESCENCE_3)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[砍刀]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade III");

    public static final LanguageItem FLUORESCENCE_4 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.FLUORESCENCE_4)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[刃]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade IV");

    public static final LanguageItem FLUORESCENCE_5 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.FLUORESCENCE_5)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[战斧]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade V");

    public static final LanguageItem FLUORESCENCE_6 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.FLUORESCENCE_6)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[闪电]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade VI");

    public static final LanguageItem FLUORESCENCE_7 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.FLUORESCENCE_7)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[锤子]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade VII");

    public static final LanguageItem FLUORESCENCE_8 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.FLUORESCENCE_8)
            .addTranslation(LanguageTypes.ZH_CN, "荧光[镰刀]")
            .addTranslation(LanguageTypes.EN_US, "Fluorescence Blade VIII");

    // ========== Laser SlashBlades ==========
    public static final LanguageItem LASER_1 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.LASER_1)
            .addTranslation(LanguageTypes.ZH_CN, "激光剑")
            .addTranslation(LanguageTypes.EN_US, "Laser Blade I");

    public static final LanguageItem LASER_2 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.LASER_2)
            .addTranslation(LanguageTypes.ZH_CN, "激光剑[脉冲]")
            .addTranslation(LanguageTypes.EN_US, "Laser Blade II");

    public static final LanguageItem LASER_3 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.LASER_3)
            .addTranslation(LanguageTypes.ZH_CN, "激光剑[阻断]")
            .addTranslation(LanguageTypes.EN_US, "Laser Blade III");

    public static final LanguageItem LASER_3_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.LASER_3_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^激光剑[阻断]")
            .addTranslation(LanguageTypes.EN_US, "Laser Blade III Lambda");

    // ========== Special SlashBlades ==========
    public static final LanguageItem TIL = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.TIL)
            .addTranslation(LanguageTypes.ZH_CN, "til的刀")
            .addTranslation(LanguageTypes.EN_US, "TIL");

    public static final LanguageItem TIL_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.TIL_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^til的刀")
            .addTranslation(LanguageTypes.EN_US, "TIL Lambda");

    public static final LanguageItem HTOD = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.HTOD)
            .addTranslation(LanguageTypes.ZH_CN, "HTOD的刀")
            .addTranslation(LanguageTypes.EN_US, "HTOD");

    public static final LanguageItem HTOD_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.HTOD_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^HTOD的刀")
            .addTranslation(LanguageTypes.EN_US, "HTOD Lambda");

    public static final LanguageItem XING_KONG = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.XING_KONG)
            .addTranslation(LanguageTypes.ZH_CN, "星空的刀")
            .addTranslation(LanguageTypes.EN_US, "Xing Kong");

    public static final LanguageItem XING_KONG_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.XING_KONG_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^星空的刀")
            .addTranslation(LanguageTypes.EN_US, "Xing Kong Lambda");

    // ========== Star SlashBlades ==========
    public static final LanguageItem STAR_1 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.STAR_1)
            .addTranslation(LanguageTypes.ZH_CN, "星流利刃I")
            .addTranslation(LanguageTypes.EN_US, "Star Blade I");

    public static final LanguageItem STAR_2 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.STAR_2)
            .addTranslation(LanguageTypes.ZH_CN, "星流利刃II")
            .addTranslation(LanguageTypes.EN_US, "Star Blade II");

    public static final LanguageItem STAR_3 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.STAR_3)
            .addTranslation(LanguageTypes.ZH_CN, "星流利刃III")
            .addTranslation(LanguageTypes.EN_US, "Star Blade III");

    public static final LanguageItem STAR_4 = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.STAR_4)
            .addTranslation(LanguageTypes.ZH_CN, "星流利刃VI")
            .addTranslation(LanguageTypes.EN_US, "Star Blade IV");

    public static final LanguageItem STAR_4_LAMBDA = createSlashBladeDefinitionLanguage(SlashBladeDefinitions.STAR_4_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^星流利刃VI")
            .addTranslation(LanguageTypes.EN_US, "Star Blade IV Lambda");

    // ========== Special Flame Names ==========
    // 执念火名称
    public static final LanguageItem OBSESSION_FLAME = createItemLanguage(RecastingItems.OBSESSION_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "执念火")
            .addTranslation(LanguageTypes.EN_US, "Obsession Flame");

    // 记忆火名称
    public static final LanguageItem MEMORY_FLAME = createItemLanguage(RecastingItems.MEMORY_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "记忆火")
            .addTranslation(LanguageTypes.EN_US, "Memory Flame");

    // 罪孽火名称
    public static final LanguageItem SIN_FLAME = createItemLanguage(RecastingItems.SIN_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "罪孽火")
            .addTranslation(LanguageTypes.EN_US, "Sin Flame");

    // 圣愿火名称
    public static final LanguageItem HOLY_FLAME = createItemLanguage(RecastingItems.HOLY_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "圣愿火")
            .addTranslation(LanguageTypes.EN_US, "Holy Flame");

    // 混沌火名称
    public static final LanguageItem CHAOS_FLAME = createItemLanguage(RecastingItems.CHAOS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "混沌火")
            .addTranslation(LanguageTypes.EN_US, "Chaos Flame");

    // 彼岸火名称
    public static final LanguageItem OTHER_SHORE_FLAME = createItemLanguage(RecastingItems.OTHER_SHORE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "彼岸火")
            .addTranslation(LanguageTypes.EN_US, "Other Shore Flame");

    // 诗烬火名称
    public static final LanguageItem POETRY_ASH_FLAME = createItemLanguage(RecastingItems.POETRY_ASH_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "诗烬火")
            .addTranslation(LanguageTypes.EN_US, "Poetry Ash Flame");

    // 蜃楼火名称
    public static final LanguageItem MIRAGE_FLAME = createItemLanguage(RecastingItems.MIRAGE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "蜃楼火")
            .addTranslation(LanguageTypes.EN_US, "Mirage Flame");

    // 匠魂火名称
    public static final LanguageItem CRAFTSMAN_FLAME = createItemLanguage(RecastingItems.CRAFTSMAN_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "匠魂火")
            .addTranslation(LanguageTypes.EN_US, "Craftsman Flame");

    // 冰核火名称
    public static final LanguageItem ICE_CORE_FLAME = createItemLanguage(RecastingItems.ICE_CORE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "冰核火")
            .addTranslation(LanguageTypes.EN_US, "Ice Core Flame");

    // 因果火名称
    public static final LanguageItem KARMA_FLAME = createItemLanguage(RecastingItems.KARMA_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "因果火")
            .addTranslation(LanguageTypes.EN_US, "Karma Flame");

    // 摇篮火名称
    public static final LanguageItem CRADLE_FLAME = createItemLanguage(RecastingItems.CRADLE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "摇篮火")
            .addTranslation(LanguageTypes.EN_US, "Cradle Flame");

    // 渊寂火名称
    public static final LanguageItem ABYSS_FLAME = createItemLanguage(RecastingItems.ABYSS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "渊寂火")
            .addTranslation(LanguageTypes.EN_US, "Abyss Flame");

    // 王权火名称
    public static final LanguageItem ROYAL_FLAME = createItemLanguage(RecastingItems.ROYAL_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "王权火")
            .addTranslation(LanguageTypes.EN_US, "Royal Flame");

    // 衔尾火名称
    public static final LanguageItem OUROBOROS_FLAME = createItemLanguage(RecastingItems.OUROBOROS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "衔尾火")
            .addTranslation(LanguageTypes.EN_US, "Ouroboros Flame");

    // 镜生火名称
    public static final LanguageItem MIRROR_FLAME = createItemLanguage(RecastingItems.MIRROR_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "镜生火")
            .addTranslation(LanguageTypes.EN_US, "Mirror Flame");

    // 遗言火名称
    public static final LanguageItem LAST_WORDS_FLAME = createItemLanguage(RecastingItems.LAST_WORDS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "遗言火")
            .addTranslation(LanguageTypes.EN_US, "Last Words Flame");

    // 潮汐火名称
    public static final LanguageItem TIDE_FLAME = createItemLanguage(RecastingItems.TIDE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "潮汐火")
            .addTranslation(LanguageTypes.EN_US, "Tide Flame");

    public static final LanguageItem SE_CRYSTAL = createItemLanguage(RecastingItems.SE_CRYSTAL)
            .addTranslation(LanguageTypes.ZH_CN, "SE结晶")
            .addTranslation(LanguageTypes.EN_US, "SE CRYSTAL");

    // ========== SE 铭刻规则 ==========
    public static final LanguageItem ENGRAVING_RULE_TITLE = new LanguageItem("recasting.tooltip.engraving_rule.title")
            .addTranslation(LanguageTypes.ZH_CN, "铭刻规则：")
            .addTranslation(LanguageTypes.EN_US, "Engraving Rules:");

    public static final LanguageItem ENGRAVING_RULE_MAIN = new LanguageItem("recasting.tooltip.engraving_rule.main")
            .addTranslation(LanguageTypes.ZH_CN, "• 在铁砧上铭刻：一把刀最多可铭刻 %d 个普通SE和 %d 个特殊SE（特殊SE可在铁砧中替换；创造模式不受限制）")
            .addTranslation(LanguageTypes.EN_US, "• Engrave on anvil: A blade can engrave up to %d normal SEs and %d special SE (special SE can be replaced on the anvil; Creative mode has no restrictions)");

    public static final LanguageItem ENGRAVING_RULE_UPGRADE = new LanguageItem("recasting.tooltip.engraving_rule.upgrade")
            .addTranslation(LanguageTypes.ZH_CN, "• 升级：使用相同类型并且更高级SE的结晶可提升等级")
            .addTranslation(LanguageTypes.EN_US, "• Upgrade: Use crystals with the same type and higher-level SE to increase level.");

    public static final LanguageItem ENGRAVING_RULE_ERASE = new LanguageItem("recasting.tooltip.engraving_rule.erase")
            .addTranslation(LanguageTypes.ZH_CN, "• 抹除：使用等级为0的结晶可移除SE")
            .addTranslation(LanguageTypes.EN_US, "• Erase: Use crystals with level 0 to remove SE");

    public static final LanguageItem SPECIAL_SE_EXTRACT_RULE = new LanguageItem("recasting.tooltip.special_se_extract")
            .addTranslation(LanguageTypes.ZH_CN, "• 渊寂火：左刀右火去除特殊SE（保留刀）；左火右刀提取为结晶（刀损毁）")
            .addTranslation(LanguageTypes.EN_US, "• Abyss Flame: blade+flame removes special SE (keeps blade); flame+blade extracts a crystal (destroys blade)");

    public static final LanguageItem SPECIAL_SE_BADGE = new LanguageItem("recasting.tooltip.special_se.badge")
            .addTranslation(LanguageTypes.ZH_CN, "(特殊)")
            .addTranslation(LanguageTypes.EN_US, "(Special)");

    public static final LanguageItem ABYSS_FLAME_EXTRACT_HINT = new LanguageItem("recasting.tooltip.abyss_flame.extract")
            .addTranslation(LanguageTypes.ZH_CN, "• 铁砧：左刀右火去除特殊SE；左火右刀提取结晶（刀损毁）")
            .addTranslation(LanguageTypes.EN_US, "• Anvil: blade+flame removes special SE; flame+blade extracts a crystal (blade destroyed)");

    // 聚散变体名称
    public static final LanguageItem GATHERING_PARTING_VARIANT = createItemLanguage(RecastingItems.GATHERING_PARTING_VARIANT)
            .addTranslation(LanguageTypes.ZH_CN, "聚散变体")
            .addTranslation(LanguageTypes.EN_US, "Gathering Parting Variant");

    // 升格变体名称
    public static final LanguageItem UPGRADE_VARIANT = createItemLanguage(RecastingItems.UPGRADE_VARIANT)
            .addTranslation(LanguageTypes.ZH_CN, "升格变体 I")
            .addTranslation(LanguageTypes.EN_US, "Upgrade Variant II");

    // 升格变体 II 名称
    public static final LanguageItem UPGRADE_VARIANT_2 = createItemLanguage(RecastingItems.UPGRADE_VARIANT_2)
            .addTranslation(LanguageTypes.ZH_CN, "升格变体 II")
            .addTranslation(LanguageTypes.EN_US, "Upgrade Variant II");

    // 升格变体 III 名称
    public static final LanguageItem UPGRADE_VARIANT_3 = createItemLanguage(RecastingItems.UPGRADE_VARIANT_3)
            .addTranslation(LanguageTypes.ZH_CN, "升格变体 III")
            .addTranslation(LanguageTypes.EN_US, "Upgrade Variant III");

    // 升格变体 IV 名称
    public static final LanguageItem UPGRADE_VARIANT_4 = createItemLanguage(RecastingItems.UPGRADE_VARIANT_4)
            .addTranslation(LanguageTypes.ZH_CN, "升格变体 IV")
            .addTranslation(LanguageTypes.EN_US, "Upgrade Variant IV");

    // ========== 庸魂立方体 ==========
    // 银灰庸魂立方体名称
    public static final LanguageItem IRON_MEDIUM_SOUL_CUBE = createItemLanguage(RecastingItems.IRON_MEDIUM_SOUL_CUBE)
            .addTranslation(LanguageTypes.ZH_CN, "银灰色的庸魂立方体")
            .addTranslation(LanguageTypes.EN_US, "Silver Gray Medium Soul Cube");

    // 金黄庸魂立方体名称
    public static final LanguageItem GOLD_MEDIUM_SOUL_CUBE = createItemLanguage(RecastingItems.GOLD_MEDIUM_SOUL_CUBE)
            .addTranslation(LanguageTypes.ZH_CN, "金黄色的庸魂立方体")
            .addTranslation(LanguageTypes.EN_US, "Golden Medium Soul Cube");

    // 古铜庸魂立方体名称
    public static final LanguageItem COPPER_MEDIUM_SOUL_CUBE = createItemLanguage(RecastingItems.COPPER_MEDIUM_SOUL_CUBE)
            .addTranslation(LanguageTypes.ZH_CN, "古铜色的庸魂立方体")
            .addTranslation(LanguageTypes.EN_US, "Bronze Medium Soul Cube");

    // 天蓝庸魂立方体名称
    public static final LanguageItem DIAMOND_MEDIUM_SOUL_CUBE = createItemLanguage(RecastingItems.DIAMOND_MEDIUM_SOUL_CUBE)
            .addTranslation(LanguageTypes.ZH_CN, "天蓝色的庸魂立方体")
            .addTranslation(LanguageTypes.EN_US, "Sky Blue Medium Soul Cube");

    // 翠绿庸魂立方体名称
    public static final LanguageItem EMERALD_MEDIUM_SOUL_CUBE = createItemLanguage(RecastingItems.EMERALD_MEDIUM_SOUL_CUBE)
            .addTranslation(LanguageTypes.ZH_CN, "翠绿色的庸魂立方体")
            .addTranslation(LanguageTypes.EN_US, "Jade Medium Soul Cube");

    // 漆黑庸魂立方体名称
    public static final LanguageItem NETHERITE_MEDIUM_SOUL_CUBE = createItemLanguage(RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE)
            .addTranslation(LanguageTypes.ZH_CN, "漆黑色的庸魂立方体")
            .addTranslation(LanguageTypes.EN_US, "Pitch Black Medium Soul Cube");

    // 靛蓝庸魂立方体名称
    public static final LanguageItem LAPIS_MEDIUM_SOUL_CUBE = createItemLanguage(RecastingItems.LAPIS_MEDIUM_SOUL_CUBE)
            .addTranslation(LanguageTypes.ZH_CN, "靛蓝色的庸魂立方体")
            .addTranslation(LanguageTypes.EN_US, "Indigo Medium Soul Cube");

    // 赤红庸魂立方体名称
    public static final LanguageItem REDSTONE_MEDIUM_SOUL_CUBE = createItemLanguage(RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE)
            .addTranslation(LanguageTypes.ZH_CN, "赤红色的庸魂立方体")
            .addTranslation(LanguageTypes.EN_US, "Scarlet Medium Soul Cube");

    // ========== Special Flame Descriptions ==========
    // 执念火介绍
    public static final LanguageItem OBSESSION_FLAME_DESC = createItemDescription(RecastingItems.OBSESSION_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "躁动不定的橘红色，焰心处有苍白闪烁。")
            .addTranslation(LanguageTypes.EN_US, "Restless orange-red, with pale flashes at the core.");

    // 记忆火介绍
    public static final LanguageItem MEMORY_FLAME_DESC = createItemDescription(RecastingItems.MEMORY_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "半透明的琉璃色，焰苗中浮动着朦胧的光影。")
            .addTranslation(LanguageTypes.EN_US, "Translucent glazed color, with hazy light and shadow floating in the flame.");

    // 罪孽火介绍
    public static final LanguageItem SIN_FLAME_DESC = createItemDescription(RecastingItems.SIN_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "污浊的暗红色，带有不祥的黑色脉纹，燃烧时散发焦臭。")
            .addTranslation(LanguageTypes.EN_US, "Foul dark red with ominous black veins, emitting a charred stench when burning.");

    // 圣愿火介绍
    public static final LanguageItem HOLY_FLAME_DESC = createItemDescription(RecastingItems.HOLY_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "纯净的乳白色光焰，边缘环绕着淡淡的金色光晕。")
            .addTranslation(LanguageTypes.EN_US, "Pure milky white flame, surrounded by a faint golden halo at the edges.");

    // 混沌火介绍
    public static final LanguageItem CHAOS_FLAME_DESC = createItemDescription(RecastingItems.CHAOS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "色彩无定，在同一秒内可能呈现光谱上的任何颜色。")
            .addTranslation(LanguageTypes.EN_US, "Indeterminate colors, may display any color on the spectrum within the same second.");

    // 彼岸火介绍
    public static final LanguageItem OTHER_SHORE_FLAME_DESC = createItemDescription(RecastingItems.OTHER_SHORE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "冰冷的青白色，摇曳如烛，无温度。")
            .addTranslation(LanguageTypes.EN_US, "Cold bluish-white, flickering like a candle, without warmth.");

    // 诗烬火介绍
    public static final LanguageItem POETRY_ASH_FLAME_DESC = createItemDescription(RecastingItems.POETRY_ASH_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "朦胧的月白色，焰心跃动着如文字般的淡金符文。")
            .addTranslation(LanguageTypes.EN_US, "Hazy moon-white, with pale golden runes dancing like text at the core.");

    // 蜃楼火介绍
    public static final LanguageItem MIRAGE_FLAME_DESC = createItemDescription(RecastingItems.MIRAGE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "折射的虹彩色，边缘模糊，仿佛隔着一层水汽。")
            .addTranslation(LanguageTypes.EN_US, "Refracted rainbow colors with blurred edges, as if seen through a layer of mist.");

    // 匠魂火介绍
    public static final LanguageItem CRAFTSMAN_FLAME_DESC = createItemDescription(RecastingItems.CRAFTSMAN_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "沉稳的铜黄色，焰形规整，时有金属光泽闪过。")
            .addTranslation(LanguageTypes.EN_US, "Steady bronze-yellow with regular flame shape, occasionally flashing with metallic luster.");

    // 冰核火介绍
    public static final LanguageItem ICE_CORE_FLAME_DESC = createItemDescription(RecastingItems.ICE_CORE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "外层是炽热的亮蓝色，内核却是深邃的、仿佛能吸收光线的绝对暗蓝。")
            .addTranslation(LanguageTypes.EN_US, "Outer layer is blazing bright blue, but the core is deep, absolute dark blue that seems to absorb light.");

    // 因果火介绍
    public static final LanguageItem KARMA_FLAME_DESC = createItemDescription(RecastingItems.KARMA_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "无形的透明之火，只有当它烧灼命运丝线时，才会泛起密麻交错的银线与血线。")
            .addTranslation(LanguageTypes.EN_US, "Invisible transparent fire, only revealing densely interwoven silver and blood-red threads when burning the threads of fate.");

    // 摇篮火介绍
    public static final LanguageItem CRADLE_FLAME_DESC = createItemDescription(RecastingItems.CRADLE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "柔和的鹅黄色，光芒如同最安稳的烛光，令人心生宁静。")
            .addTranslation(LanguageTypes.EN_US, "Soft goose-yellow, its light like the most peaceful candlelight, bringing tranquility to the heart.");

    // 渊寂火介绍
    public static final LanguageItem ABYSS_FLAME_DESC = createItemDescription(RecastingItems.ABYSS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "纯粹的哑光黑色，吞噬周围光线，形成一片无声的黑暗区域。")
            .addTranslation(LanguageTypes.EN_US, "Pure matte black that devours surrounding light, forming a silent dark region.");

    // 王权火介绍
    public static final LanguageItem ROYAL_FLAME_DESC = createItemDescription(RecastingItems.ROYAL_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "威严的暗金色与深紫色交织，焰形升腾如帝王冠冕。")
            .addTranslation(LanguageTypes.EN_US, "Majestic dark gold and deep purple interwoven, the flame rising like an imperial crown.");

    // 衔尾火介绍
    public static final LanguageItem OUROBOROS_FLAME_DESC = createItemDescription(RecastingItems.OUROBOROS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "一种自我吞噬的莫比乌斯环状焰流，颜色在银灰与暗蓝间循环。")
            .addTranslation(LanguageTypes.EN_US, "A self-devouring Möbius ring-shaped flame flow, cycling between silver-gray and dark blue.");

    // 镜生火介绍
    public static final LanguageItem MIRROR_FLAME_DESC = createItemDescription(RecastingItems.MIRROR_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "如水银般高度反光的镜面之色，完美映照出它所\"模仿\"之火的外貌。")
            .addTranslation(LanguageTypes.EN_US, "Highly reflective mirror-like color like mercury, perfectly reflecting the appearance of the flame it 'imitates'.");

    // 遗言火介绍
    public static final LanguageItem LAST_WORDS_FLAME_DESC = createItemDescription(RecastingItems.LAST_WORDS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "琥珀金色，焰心深处封存着一点即将消散的、代表生命最后色彩的星芒。")
            .addTranslation(LanguageTypes.EN_US, "Amber gold, with a fading starlight representing life's final color sealed deep in the core.");

    // 潮汐火介绍
    public static final LanguageItem TIDE_FLAME_DESC = createItemDescription(RecastingItems.TIDE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "随着涨落，颜色从新月时的深紫，到满月时如海浪泡沫般的银白。")
            .addTranslation(LanguageTypes.EN_US, "With the ebb and flow, colors shift from deep purple at new moon to silvery white like sea foam at full moon.");

    // ========== Slash Arts ==========
    // 青芒
    public static final LanguageItem CYAN_GLOW = createSlashArtsLanguage(SlashArtsRegistry.CYAN_GLOW)
            .addTranslation(LanguageTypes.ZH_CN, "青芒")
            .addTranslation(LanguageTypes.EN_US, "Cyan Glow");

    public static final LanguageItem CYAN_GLOW_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.CYAN_GLOW_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "青芒^")
            .addTranslation(LanguageTypes.EN_US, "Cyan Glow Lambda");


    // 乱舞
    public static final LanguageItem FANATICAL_DANCE = createSlashArtsLanguage(SlashArtsRegistry.FANATICAL_DANCE)
            .addTranslation(LanguageTypes.ZH_CN, "乱舞")
            .addTranslation(LanguageTypes.EN_US, "Fanatical Dance");

    public static final LanguageItem FANATICAL_DANCE_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.FANATICAL_DANCE_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^乱舞")
            .addTranslation(LanguageTypes.EN_US, "Fanatical Dance Lambda");

    // 风暴幻影剑
    public static final LanguageItem STORM_PHANTOM_SWORDS = createSlashArtsLanguage(SlashArtsRegistry.STORM_PHANTOM_SWORDS)
            .addTranslation(LanguageTypes.ZH_CN, "风暴幻影剑")
            .addTranslation(LanguageTypes.EN_US, "Storm Phantom Swords");

    public static final LanguageItem STORM_PHANTOM_SWORDS_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.STORM_PHANTOM_SWORDS_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^风暴幻影剑")
            .addTranslation(LanguageTypes.EN_US, "Storm Phantom Swords Lambda");

    // 剑雨
    public static final LanguageItem SWORD_RAIN = createSlashArtsLanguage(SlashArtsRegistry.SWORD_RAIN)
            .addTranslation(LanguageTypes.ZH_CN, "剑雨")
            .addTranslation(LanguageTypes.EN_US, "Sword Rain");

    public static final LanguageItem SWORD_RAIN_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.SWORD_RAIN_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "剑雨[顶点]")
            .addTranslation(LanguageTypes.EN_US, "Sword Rain [Vertex]");

    // 拟似黑洞
    public static final LanguageItem VOID_HOLE = createSlashArtsLanguage(SlashArtsRegistry.VOID_HOLE)
            .addTranslation(LanguageTypes.ZH_CN, "拟似黑洞")
            .addTranslation(LanguageTypes.EN_US, "Void Hole");

    public static final LanguageItem VOID_HOLE_PITCH_BLACK = createSlashArtsLanguage(SlashArtsRegistry.VOID_HOLE_PITCH_BLACK)
            .addTranslation(LanguageTypes.ZH_CN, "拟似黑洞[漆黑]")
            .addTranslation(LanguageTypes.EN_US, "Void Hole [Pitch Black]");

    public static final LanguageItem VOID_HOLE_FISHY_RED = createSlashArtsLanguage(SlashArtsRegistry.VOID_HOLE_FISHY_RED)
            .addTranslation(LanguageTypes.ZH_CN, "拟似黑洞[腥红]")
            .addTranslation(LanguageTypes.EN_US, "Void Hole [Fishy Red]");

    // 多重次元斩·决
    public static final LanguageItem MULTIPLE_JUDGEMENT_CUT = createSlashArtsLanguage(SlashArtsRegistry.MULTIPLE_JUDGEMENT_CUT)
            .addTranslation(LanguageTypes.ZH_CN, "多重次元斩")
            .addTranslation(LanguageTypes.EN_US, "Multiple Judgement Cut");

    // 无限次元斩
    public static final LanguageItem INFINITE_JUDGEMENT_CUT = createSlashArtsLanguage(SlashArtsRegistry.INFINITE_JUDGEMENT_CUT)
            .addTranslation(LanguageTypes.ZH_CN, "无限次元斩")
            .addTranslation(LanguageTypes.EN_US, "Infinite Judgement Cut");

    // 苍穹十二连
    public static final LanguageItem HEAVEN_TWELVE_HIT = createSlashArtsLanguage(SlashArtsRegistry.HEAVEN_TWELVE_HIT)
            .addTranslation(LanguageTypes.ZH_CN, "苍穹十二连")
            .addTranslation(LanguageTypes.EN_US, "Heaven Twelve Hit");

    public static final LanguageItem HEAVEN_TWELVE_HIT_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.HEAVEN_TWELVE_HIT_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^苍穹十二连")
            .addTranslation(LanguageTypes.EN_US, "Heaven Twelve Hit Lambda");

    // 云轮
    public static final LanguageItem CLOUD_WHEEL = createSlashArtsLanguage(SlashArtsRegistry.CLOUD_WHEEL)
            .addTranslation(LanguageTypes.ZH_CN, "云轮")
            .addTranslation(LanguageTypes.EN_US, "Cloud Wheel");

    // 云轮风暴
    public static final LanguageItem CLOUD_WHEEL_STORM = createSlashArtsLanguage(SlashArtsRegistry.CLOUD_WHEEL_STORM)
            .addTranslation(LanguageTypes.ZH_CN, "云轮风暴")
            .addTranslation(LanguageTypes.EN_US, "Cloud Wheel Storm");

    // 星
    public static final LanguageItem STAR_1_SA = createSlashArtsLanguage(SlashArtsRegistry.STAR_1)
            .addTranslation(LanguageTypes.ZH_CN, "星流")
            .addTranslation(LanguageTypes.EN_US, "Star I");

    public static final LanguageItem STAR_2_SA = createSlashArtsLanguage(SlashArtsRegistry.STAR_2)
            .addTranslation(LanguageTypes.ZH_CN, "星流II")
            .addTranslation(LanguageTypes.EN_US, "Star II");

    public static final LanguageItem STAR_3_SA = createSlashArtsLanguage(SlashArtsRegistry.STAR_3)
            .addTranslation(LanguageTypes.ZH_CN, "星流III")
            .addTranslation(LanguageTypes.EN_US, "Star III");

    public static final LanguageItem STAR_4_SA = createSlashArtsLanguage(SlashArtsRegistry.STAR_4)
            .addTranslation(LanguageTypes.ZH_CN, "星流IV")
            .addTranslation(LanguageTypes.EN_US, "Star IV");

    public static final LanguageItem STAR_4_LAMBDA_SA = createSlashArtsLanguage(SlashArtsRegistry.STAR_4_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^星流IV")
            .addTranslation(LanguageTypes.EN_US, "Star IV Lambda");

    // 多重剑气
    public static final LanguageItem MULTIPLE_DRIVE = createSlashArtsLanguage(SlashArtsRegistry.MULTIPLE_DRIVE)
            .addTranslation(LanguageTypes.ZH_CN, "多重剑气")
            .addTranslation(LanguageTypes.EN_US, "Multiple Drive");

    public static final LanguageItem MULTIPLE_DRIVE_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.MULTIPLE_DRIVE_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^多重剑气")
            .addTranslation(LanguageTypes.EN_US, "Multiple Drive Lambda");

    // 引雷
    public static final LanguageItem LIGHTNING_CALL = createSlashArtsLanguage(SlashArtsRegistry.LIGHTNING_CALL)
            .addTranslation(LanguageTypes.ZH_CN, "引雷")
            .addTranslation(LanguageTypes.EN_US, "Lightning Call");

    // 闪电链
    public static final LanguageItem LIGHTNING_CHAIN_1_SA = createSlashArtsLanguage(SlashArtsRegistry.LIGHTNING_CHAIN_1)
            .addTranslation(LanguageTypes.ZH_CN, "闪电链")
            .addTranslation(LanguageTypes.EN_US, "Lightning Chain");

    public static final LanguageItem LIGHTNING_CHAIN_2_SA = createSlashArtsLanguage(SlashArtsRegistry.LIGHTNING_CHAIN_2)
            .addTranslation(LanguageTypes.ZH_CN, "闪电链[脉冲]")
            .addTranslation(LanguageTypes.EN_US, "Lightning Chain [Pulse]");

    public static final LanguageItem LIGHTNING_CHAIN_3_SA = createSlashArtsLanguage(SlashArtsRegistry.LIGHTNING_CHAIN_3)
            .addTranslation(LanguageTypes.ZH_CN, "闪电链[连射]")
            .addTranslation(LanguageTypes.EN_US, "Lightning Chain [Barrage]");

    public static final LanguageItem LIGHTNING_CHAIN_3_LAMBDA_SA = createSlashArtsLanguage(SlashArtsRegistry.LIGHTNING_CHAIN_3_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^闪电链[连射]")
            .addTranslation(LanguageTypes.EN_US, "^Lightning Chain [Barrage]");


    // 星旋
    public static final LanguageItem STELLAR_ROTATION = createSlashArtsLanguage(SlashArtsRegistry.STELLAR_ROTATION)
            .addTranslation(LanguageTypes.ZH_CN, "星旋")
            .addTranslation(LanguageTypes.EN_US, "Stellar Rotation");

    // 急行幻影剑
    public static final LanguageItem RAPID_PHANTOM_SWORDS = createSlashArtsLanguage(SlashArtsRegistry.RAPID_PHANTOM_SWORDS)
            .addTranslation(LanguageTypes.ZH_CN, "急行幻影剑")
            .addTranslation(LanguageTypes.EN_US, "Rapid Phantom Swords");

    // 穷观阵
    public static final LanguageItem MATRIX = createSlashArtsLanguage(SlashArtsRegistry.MATRIX)
            .addTranslation(LanguageTypes.ZH_CN, "穷观阵")
            .addTranslation(LanguageTypes.EN_US, "Matrix");

    public static final LanguageItem MATRIX_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.MATRIX_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^穷观阵")
            .addTranslation(LanguageTypes.EN_US, "Matrix Lambda");

    // 碎段
    public static final LanguageItem FRAGMENT_SA = createSlashArtsLanguage(SlashArtsRegistry.FRAGMENT)
            .addTranslation(LanguageTypes.ZH_CN, "碎段")
            .addTranslation(LanguageTypes.EN_US, "Fragment");

    // ========== Slash Arts Descriptions ==========
    // 青芒介绍
    public static final LanguageItem CYAN_GLOW_DESC = createSlashArtsDescription(SlashArtsRegistry.CYAN_GLOW)
            .addTranslation(LanguageTypes.ZH_CN, "快速连续发动多次均匀角度的斩击。")
            .addTranslation(LanguageTypes.EN_US, "Quickly launch multiple slashes at evenly distributed angles in succession.");

    public static final LanguageItem CYAN_GLOW_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.CYAN_GLOW_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "快速连续发动多次均匀角度的斩击。")
            .addTranslation(LanguageTypes.EN_US, "Quickly launch multiple slashes at evenly distributed angles in succession.");

    // 乱舞介绍
    public static final LanguageItem FANATICAL_DANCE_DESC = createSlashArtsDescription(SlashArtsRegistry.FANATICAL_DANCE)
            .addTranslation(LanguageTypes.ZH_CN, "快速连续发动多次随机角度的斩击。")
            .addTranslation(LanguageTypes.EN_US, "Quickly launch multiple slashes at random angles in succession.");

    public static final LanguageItem FANATICAL_DANCE_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.FANATICAL_DANCE_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "快速连续发动多次随机角度的斩击。")
            .addTranslation(LanguageTypes.EN_US, "Quickly launch multiple slashes at random angles in succession.");

    // 风暴幻影剑介绍
    public static final LanguageItem STORM_PHANTOM_SWORDS_DESC = createSlashArtsDescription(SlashArtsRegistry.STORM_PHANTOM_SWORDS)
            .addTranslation(LanguageTypes.ZH_CN, "在自身周围召唤多把幻影剑并发射。")
            .addTranslation(LanguageTypes.EN_US, "Summon multiple phantom swords around the entity and launch them.");

    public static final LanguageItem STORM_PHANTOM_SWORDS_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.STORM_PHANTOM_SWORDS_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "在自身周围召唤多把幻影剑并发射。")
            .addTranslation(LanguageTypes.EN_US, "Summon multiple phantom swords around the entity and launch them.");

    // 剑雨介绍
    public static final LanguageItem SWORD_RAIN_DESC = createSlashArtsDescription(SlashArtsRegistry.SWORD_RAIN)
            .addTranslation(LanguageTypes.ZH_CN, "在自身区域召唤大量剑雨攻击。")
            .addTranslation(LanguageTypes.EN_US, "Summon a massive sword rain attack in the area around the caster.");

    public static final LanguageItem SWORD_RAIN_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.SWORD_RAIN_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "在自身区域召唤大量剑雨攻击，集中攻击目标")
            .addTranslation(LanguageTypes.EN_US, "Summon a massive sword rain attack in the area around the caster, concentrating on the target.");

    // 拟似黑洞介绍
    public static final LanguageItem VOID_HOLE_DESC = createSlashArtsDescription(SlashArtsRegistry.VOID_HOLE)
            .addTranslation(LanguageTypes.ZH_CN, "创建一个黑洞，吸引范围内的所有实体向中心。")
            .addTranslation(LanguageTypes.EN_US, "Create a void hole that attracts all entities within range toward the center.");

    public static final LanguageItem VOID_HOLE_PITCH_BLACK_DESC = createSlashArtsDescription(SlashArtsRegistry.VOID_HOLE_PITCH_BLACK)
            .addTranslation(LanguageTypes.ZH_CN, "创建一个黑洞，吸引范围内的所有实体向中心。")
            .addTranslation(LanguageTypes.EN_US, "Create a void hole that attracts all entities within range toward the center.");

    public static final LanguageItem VOID_HOLE_FISHY_RED_DESC = createSlashArtsDescription(SlashArtsRegistry.VOID_HOLE_FISHY_RED)
            .addTranslation(LanguageTypes.ZH_CN, "创建一个黑洞，吸引范围内的所有实体向中心。")
            .addTranslation(LanguageTypes.EN_US, "Create a void hole that attracts all entities within range toward the center.");

    // 多重次元斩介绍
    public static final LanguageItem MULTIPLE_JUDGEMENT_CUT_DESC = createSlashArtsDescription(SlashArtsRegistry.MULTIPLE_JUDGEMENT_CUT)
            .addTranslation(LanguageTypes.ZH_CN, "在同一位置连续发动多次次元斩")
            .addTranslation(LanguageTypes.EN_US, "Launch multiple judgement cuts consecutively at the same position.");

    // 无限次元斩介绍
    public static final LanguageItem INFINITE_JUDGEMENT_CUT_DESC = createSlashArtsDescription(SlashArtsRegistry.INFINITE_JUDGEMENT_CUT)
            .addTranslation(LanguageTypes.ZH_CN, "在范围内的敌人位置随机发动大量次元斩。")
            .addTranslation(LanguageTypes.EN_US, "Launch numerous judgement cuts randomly at enemy positions within range.");

    // 苍穹十二连介绍
    public static final LanguageItem HEAVEN_TWELVE_HIT_DESC = createSlashArtsDescription(SlashArtsRegistry.HEAVEN_TWELVE_HIT)
            .addTranslation(LanguageTypes.ZH_CN, "发射多把幻影剑，击中敌人后在敌人位置生成闪电。")
            .addTranslation(LanguageTypes.EN_US, "Launch multiple summoned swords that generate lightning at enemy positions upon hit.");

    public static final LanguageItem HEAVEN_TWELVE_HIT_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.HEAVEN_TWELVE_HIT_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "发射多把幻影剑，击中敌人后在敌人位置生成闪电。")
            .addTranslation(LanguageTypes.EN_US, "Launch multiple summoned swords that generate lightning at enemy positions upon hit.");

    // 云轮介绍
    public static final LanguageItem CLOUD_WHEEL_DESC = createSlashArtsDescription(SlashArtsRegistry.CLOUD_WHEEL)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置上方生成多把幻影剑，击中后生成闪电。")
            .addTranslation(LanguageTypes.EN_US, "Generate multiple summoned swords above the target position that create lightning upon hit.");

    // 云轮风暴介绍
    public static final LanguageItem CLOUD_WHEEL_STORM_DESC = createSlashArtsDescription(SlashArtsRegistry.CLOUD_WHEEL_STORM)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置上方生成多把幻影剑，击中后生成闪电。")
            .addTranslation(LanguageTypes.EN_US, "Generate multiple summoned swords above the target position that create lightning upon hit.");

    // 星流介绍
    public static final LanguageItem STAR_1_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.STAR_1)
            .addTranslation(LanguageTypes.ZH_CN, "发射多把追踪幻影剑，击中后产生次元斩。")
            .addTranslation(LanguageTypes.EN_US, "Launch multiple tracking summoned swords that create judgement cuts upon hit.");

    public static final LanguageItem STAR_2_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.STAR_2)
            .addTranslation(LanguageTypes.ZH_CN, "发射多把追踪幻影剑，击中后产生次元斩。")
            .addTranslation(LanguageTypes.EN_US, "Launch multiple tracking summoned swords that create judgement cuts upon hit.");

    public static final LanguageItem STAR_3_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.STAR_3)
            .addTranslation(LanguageTypes.ZH_CN, "发射多把追踪幻影剑，击中后产生次元斩。")
            .addTranslation(LanguageTypes.EN_US, "Launch multiple tracking summoned swords that create judgement cuts upon hit.");

    public static final LanguageItem STAR_4_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.STAR_4)
            .addTranslation(LanguageTypes.ZH_CN, "发射多把追踪幻影剑，击中后产生次元斩。")
            .addTranslation(LanguageTypes.EN_US, "Launch multiple tracking summoned swords that create judgement cuts upon hit.");

    public static final LanguageItem STAR_4_LAMBDA_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.STAR_4_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "发射多把追踪幻影剑，击中后产生次元斩。在周围生成持续的次元斩阵地，定期发射幻影剑。")
            .addTranslation(LanguageTypes.EN_US, "Launch multiple tracking summoned swords that create judgement cuts upon hit. Generate persistent judgement cut zones around the caster that periodically launch summoned swords.");

    // 多重剑气介绍
    public static final LanguageItem MULTIPLE_DRIVE_DESC = createSlashArtsDescription(SlashArtsRegistry.MULTIPLE_DRIVE)
            .addTranslation(LanguageTypes.ZH_CN, "向前发射多条剑气。")
            .addTranslation(LanguageTypes.EN_US, "Launch multiple sword qi forward, each with random size and rotation angle.");

    public static final LanguageItem MULTIPLE_DRIVE_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.MULTIPLE_DRIVE_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "向前发射多条剑气。")
            .addTranslation(LanguageTypes.EN_US, "Launch multiple sword qi forward, each with random size and rotation angle.");

    // 引雷介绍
    public static final LanguageItem LIGHTNING_CALL_DESC = createSlashArtsDescription(SlashArtsRegistry.LIGHTNING_CALL)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置召唤一道闪电。")
            .addTranslation(LanguageTypes.EN_US, "Summon a lightning bolt at the target position.");

    // 闪电链介绍
    public static final LanguageItem LIGHTNING_CHAIN_1_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.LIGHTNING_CHAIN_1)
            .addTranslation(LanguageTypes.ZH_CN, "击中看向处目标后，闪电在周围敌人间跳跃传导。")
            .addTranslation(LanguageTypes.EN_US, "Strike the look-at target, then leap the lightning between nearby enemies.");

    public static final LanguageItem LIGHTNING_CHAIN_2_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.LIGHTNING_CHAIN_2)
            .addTranslation(LanguageTypes.ZH_CN, "脉冲连射闪电链，索敌范围更大。")
            .addTranslation(LanguageTypes.EN_US, "Pulses lightning chains repeatedly, with a wider seek range.");

    public static final LanguageItem LIGHTNING_CHAIN_3_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.LIGHTNING_CHAIN_3)
            .addTranslation(LanguageTypes.ZH_CN, "高速连射闪电链，范围更广，可反复跃向已击中的敌人。")
            .addTranslation(LanguageTypes.EN_US, "Rapidly barrages lightning chains with greater range that can leap back onto already-struck foes.");

    public static final LanguageItem LIGHTNING_CHAIN_3_SA_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.LIGHTNING_CHAIN_3_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "高速连射闪电链，范围更广，可反复跃向已击中的敌人。")
            .addTranslation(LanguageTypes.EN_US, "Rapidly barrages lightning chains with greater range that can leap back onto already-struck foes.");

    // 星旋介绍
    public static final LanguageItem STELLAR_ROTATION_DESC = createSlashArtsDescription(SlashArtsRegistry.STELLAR_ROTATION)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置创建一个旋转的次元斩，持续造成伤害。")
            .addTranslation(LanguageTypes.EN_US, "Create a rotating judgement cut at the target position that deals continuous damage.");

    // 急行幻影剑介绍
    public static final LanguageItem RAPID_PHANTOM_SWORDS_DESC = createSlashArtsDescription(SlashArtsRegistry.RAPID_PHANTOM_SWORDS)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置周围召唤多把幻影剑。")
            .addTranslation(LanguageTypes.EN_US, "Summon multiple phantom swords around the target position.");

    // 穷观阵介绍
    public static final LanguageItem MATRIX_DESC = createSlashArtsDescription(SlashArtsRegistry.MATRIX)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置创建一个穷观阵，持续造成伤害，同时叠加演算buff层数，演算使目标受到的伤害更高。")
            .addTranslation(LanguageTypes.EN_US, "Create a matrix at the target position that deals continuous damage and stacks calculus buff layers, increasing damage taken by targets.");

    public static final LanguageItem MATRIX_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.MATRIX_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置创建一个穷观阵，持续造成伤害，同时叠加演算buff层数，演算使目标受到的伤害更高。")
            .addTranslation(LanguageTypes.EN_US, "Create a matrix at the target position that deals continuous damage and stacks calculus buff layers, increasing damage taken by targets.");

    // 碎段介绍
    public static final LanguageItem FRAGMENT_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.FRAGMENT)
            .addTranslation(LanguageTypes.ZH_CN, "发动一次斩击，设置重复攻击和取消击退。")
            .addTranslation(LanguageTypes.EN_US, "Perform a slash with repeated attacks and knockback cancellation.");

    // 幻影爆破
    public static final LanguageItem PHANTOM_EXPLOSION = createSlashArtsLanguage(SlashArtsRegistry.PHANTOM_EXPLOSION)
            .addTranslation(LanguageTypes.ZH_CN, "幻影爆破")
            .addTranslation(LanguageTypes.EN_US, "Phantom Explosion");

    public static final LanguageItem PHANTOM_EXPLOSION_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.PHANTOM_EXPLOSION_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^幻影爆破")
            .addTranslation(LanguageTypes.EN_US, "Phantom Explosion Lambda");

    public static final LanguageItem PHANTOM_EXPLOSION_DESC = createSlashArtsDescription(SlashArtsRegistry.PHANTOM_EXPLOSION)
            .addTranslation(LanguageTypes.ZH_CN, "在目标周围产生多组螺旋幻影剑，围绕目标旋转。")
            .addTranslation(LanguageTypes.EN_US, "Generate multiple groups of spiral phantom swords around the target that rotate around it.");

    public static final LanguageItem PHANTOM_EXPLOSION_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.PHANTOM_EXPLOSION_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "在目标周围产生多组螺旋幻影剑，围绕目标旋转。")
            .addTranslation(LanguageTypes.EN_US, "Generate multiple groups of spiral phantom swords around the target that rotate around it.");

    // 无限剑制
    public static final LanguageItem UNLIMITED_BLADE_WORKS = createSlashArtsLanguage(SlashArtsRegistry.UNLIMITED_BLADE_WORKS)
            .addTranslation(LanguageTypes.ZH_CN, "无限剑制")
            .addTranslation(LanguageTypes.EN_US, "Unlimited Blade Works");

    public static final LanguageItem UNLIMITED_BLADE_WORKS_DESC = createSlashArtsDescription(SlashArtsRegistry.UNLIMITED_BLADE_WORKS)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置上方的半球面上生成大量幻影剑，均匀分布并延迟发射。")
            .addTranslation(LanguageTypes.EN_US, "Generate numerous phantom swords evenly distributed on a hemisphere above the target position, launching them with delays.");

    public static final LanguageItem UNLIMITED_BLADE_WORKS_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.UNLIMITED_BLADE_WORKS_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^无限剑制")
            .addTranslation(LanguageTypes.EN_US, "Unlimited Blade Works Lambda");

    public static final LanguageItem UNLIMITED_BLADE_WORKS_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.UNLIMITED_BLADE_WORKS_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置上方的半球面上生成大量幻影剑。")
            .addTranslation(LanguageTypes.EN_US, "Generate numerous phantom swords evenly distributed on a hemisphere above the target position.");

    // 剑刃风暴
    public static final LanguageItem BLADE_STORM = createSlashArtsLanguage(SlashArtsRegistry.BLADE_STORM)
            .addTranslation(LanguageTypes.ZH_CN, "剑刃风暴")
            .addTranslation(LanguageTypes.EN_US, "Blade Storm");

    public static final LanguageItem BLADE_STORM_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.BLADE_STORM_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^剑刃风暴")
            .addTranslation(LanguageTypes.EN_US, "Blade Storm Lambda");

    public static final LanguageItem BLADE_STORM_DESC = createSlashArtsDescription(SlashArtsRegistry.BLADE_STORM)
            .addTranslation(LanguageTypes.ZH_CN, "在玩家周围随机位置生成大量高速旋转的幻影剑，持续攻击周围敌人。")
            .addTranslation(LanguageTypes.EN_US, "Generate numerous high-speed rotating phantom swords at random positions around the player, continuously attacking nearby enemies.");

    public static final LanguageItem BLADE_STORM_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.BLADE_STORM_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "在玩家周围随机位置生成大量高速旋转的幻影剑，持续攻击周围敌人。")
            .addTranslation(LanguageTypes.EN_US, "Generate numerous high-speed rotating phantom swords at random positions around the player, continuously attacking nearby enemies.");

    // 斩铁式·极
    public static final LanguageItem ZANTETSUDEN_MAX = createSlashArtsLanguage(SlashArtsRegistry.ZANTETSUDEN_MAX)
            .addTranslation(LanguageTypes.ZH_CN, "斩铁式·极")
            .addTranslation(LanguageTypes.EN_US, "Zantetsuden Max");

    public static final LanguageItem ZANTETSUDEN_MAX_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.ZANTETSUDEN_MAX_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^斩铁式·极")
            .addTranslation(LanguageTypes.EN_US, "Zantetsuden Max Lambda");

    public static final LanguageItem ZANTETSUDEN_MAX_DESC = createSlashArtsDescription(SlashArtsRegistry.ZANTETSUDEN_MAX)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置超高频率连续发动大量斩击。")
            .addTranslation(LanguageTypes.EN_US, "Launch numerous slashes at extremely high frequency at the target position.");

    public static final LanguageItem ZANTETSUDEN_MAX_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.ZANTETSUDEN_MAX_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置超高频率连续发动大量斩击。")
            .addTranslation(LanguageTypes.EN_US, "Launch numerous slashes at extremely high frequency at the target position.");

    // 斩铁式·行
    public static final LanguageItem ZANTETSUDEN_ROW = createSlashArtsLanguage(SlashArtsRegistry.ZANTETSUDEN_ROW)
            .addTranslation(LanguageTypes.ZH_CN, "斩铁式·行")
            .addTranslation(LanguageTypes.EN_US, "Zantetsuden Row");

    public static final LanguageItem ZANTETSUDEN_ROW_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.ZANTETSUDEN_ROW_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^斩铁式·行")
            .addTranslation(LanguageTypes.EN_US, "Zantetsuden Row Lambda");

    public static final LanguageItem ZANTETSUDEN_ROW_DESC = createSlashArtsDescription(SlashArtsRegistry.ZANTETSUDEN_ROW)
            .addTranslation(LanguageTypes.ZH_CN, "向各个方向发射大量驱动剑气，剑气会从目标位置向四面八方飞散。")
            .addTranslation(LanguageTypes.EN_US, "Launch numerous drive sword qi in all directions from the target position, scattering in every direction.");

    public static final LanguageItem ZANTETSUDEN_ROW_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.ZANTETSUDEN_ROW_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置向各个方向发射大量驱动剑气，剑气会从目标位置向四面八方飞散。")
            .addTranslation(LanguageTypes.EN_US, "Launch numerous drive sword qi in all directions from the target position, scattering in every direction.");

    // 业火
    public static final LanguageItem INFERNO = createSlashArtsLanguage(SlashArtsRegistry.INFERNO)
            .addTranslation(LanguageTypes.ZH_CN, "业火")
            .addTranslation(LanguageTypes.EN_US, "Inferno");

    public static final LanguageItem INFERNO_LAMBDA = createSlashArtsLanguage(SlashArtsRegistry.INFERNO_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^业火")
            .addTranslation(LanguageTypes.EN_US, "Inferno Lambda");

    public static final LanguageItem INFERNO_DESC = createSlashArtsDescription(SlashArtsRegistry.INFERNO)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置召唤大型次元斩；命中后为敌人叠加灵魂燃烧。灵魂燃烧：使目标持续受到生命百分比的火焰伤害，层数随时间衰减。")
            .addTranslation(LanguageTypes.EN_US, "Summons a large judgement cut at the target; applies soul burn on hit. Soul burn: inflicts ongoing fire damage based on current health percentage; stacks decay over time.");

    public static final LanguageItem INFERNO_LAMBDA_DESC = createSlashArtsDescription(SlashArtsRegistry.INFERNO_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "在目标位置召唤大型次元斩；命中后为敌人叠加更高层数的灵魂燃烧。灵魂燃烧：使目标持续受到生命百分比的火焰伤害，层数随时间衰减。")
            .addTranslation(LanguageTypes.EN_US, "Summons a large judgement cut at the target; applies more soul burn stacks on hit. Soul burn: inflicts ongoing fire damage based on current health percentage; stacks decay over time.");

    // 光棱
    public static final LanguageItem LASER_1_SA = createSlashArtsLanguage(SlashArtsRegistry.LASER_1)
            .addTranslation(LanguageTypes.ZH_CN, "光棱射线")
            .addTranslation(LanguageTypes.EN_US, "Prism Beam");

    public static final LanguageItem LASER_2_SA = createSlashArtsLanguage(SlashArtsRegistry.LASER_2)
            .addTranslation(LanguageTypes.ZH_CN, "光棱射线[脉冲]")
            .addTranslation(LanguageTypes.EN_US, "Prism Beam [Pulse]");

    public static final LanguageItem LASER_3_SA = createSlashArtsLanguage(SlashArtsRegistry.LASER_3)
            .addTranslation(LanguageTypes.ZH_CN, "光棱射线[连射]")
            .addTranslation(LanguageTypes.EN_US, "Prism Beam [Barrage]");

    public static final LanguageItem LASER_3_LAMBDA_SA = createSlashArtsLanguage(SlashArtsRegistry.LASER_3_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^光棱射线[连射]")
            .addTranslation(LanguageTypes.EN_US, "Prism Beam [Barrage] Lambda");

    public static final LanguageItem LASER_1_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.LASER_1)
            .addTranslation(LanguageTypes.ZH_CN, "自头顶射出光棱，自动锁定敌人；命中后向周围散射分光。")
            .addTranslation(LanguageTypes.EN_US, "Fires a prism beam from above the head that auto-locks onto foes; on hit, scatters secondary beams outward.");

    public static final LanguageItem LASER_2_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.LASER_2)
            .addTranslation(LanguageTypes.ZH_CN, "自头顶脉冲连射光棱，自动锁定敌人；命中后散射，分光还可再次散射。")
            .addTranslation(LanguageTypes.EN_US, "Pulses prism beams from above the head with auto-lock; on hit, scatters outward, and secondary beams may scatter again.");

    public static final LanguageItem LASER_3_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.LASER_3)
            .addTranslation(LanguageTypes.ZH_CN, "自头顶高速连射光棱，自动锁定敌人；命中后散射，分光还可再次散射。")
            .addTranslation(LanguageTypes.EN_US, "Rapidly barrages prism beams from above the head with auto-lock; on hit, scatters outward, and secondary beams may scatter again.");

    public static final LanguageItem LASER_3_LAMBDA_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.LASER_3_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "自头顶高速连射光棱，自动锁定敌人；命中后层层散射，分光可继续分裂。")
            .addTranslation(LanguageTypes.EN_US, "Rapidly barrages prism beams from above the head with auto-lock; on hit, beams cascade through successive scatters.");

    // 长空落日
    public static final LanguageItem LONG_SKY_SUNSET_SA = createSlashArtsLanguage(SlashArtsRegistry.LONG_SKY_SUNSET)
            .addTranslation(LanguageTypes.ZH_CN, "叠晖")
            .addTranslation(LanguageTypes.EN_US, "Sunset Mark");

    public static final LanguageItem LONG_SKY_SUNSET_LAMBDA_SA = createSlashArtsLanguage(SlashArtsRegistry.LONG_SKY_SUNSET_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^叠晖")
            .addTranslation(LanguageTypes.EN_US, "Sunset Mark Lambda");

    public static final LanguageItem LONG_SKY_SUNSET_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.LONG_SKY_SUNSET)
            .addTranslation(LanguageTypes.ZH_CN, "向目标周边随机敌人齐射幻影剑，命中叠加日核；带有日核的目标再受幻影剑伤害时触发晖光并叠加叠晖，叠满后幻影剑伤害大幅提升。")
            .addTranslation(LanguageTypes.EN_US, "Volleys phantom swords at random foes near the aim point, applying Sunset Core on hit. Targets with Sunset Core take Hui Guang and gain Sunset Stack when struck by phantom swords; at full stacks, phantom sword damage rises sharply.");

    public static final LanguageItem LONG_SKY_SUNSET_LAMBDA_SA_DESC = createSlashArtsDescription(SlashArtsRegistry.LONG_SKY_SUNSET_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "向目标周边随机敌人齐射更多幻影剑，命中叠加日核；带有日核的目标再受幻影剑伤害时触发晖光并叠加叠晖，叠满后幻影剑伤害大幅提升。")
            .addTranslation(LanguageTypes.EN_US, "Volleys more phantom swords at random foes near the aim point, applying Sunset Core on hit. Targets with Sunset Core take Hui Guang and gain Sunset Stack when struck by phantom swords; at full stacks, phantom sword damage rises sharply.");

    // ========== Special Effects ==========
    // 协同攻击
    public static final LanguageItem COOPERATE_WITH = createSpecialEffectLanguage(SpecialEffectsRegistry.COOPERATE_WITH)
            .addTranslation(LanguageTypes.ZH_CN, "协同攻击")
            .addTranslation(LanguageTypes.EN_US, "Cooperate With");

    public static final LanguageItem COOPERATE_WITH_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.COOPERATE_WITH)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时概率额外挥刀。")
            .addTranslation(LanguageTypes.EN_US, "Has a chance to perform an additional slash when slashing.");

    // 十字斩
    public static final LanguageItem CROSS_CHOP = createSpecialEffectLanguage(SpecialEffectsRegistry.CROSS_CHOP)
            .addTranslation(LanguageTypes.ZH_CN, "十字斩")
            .addTranslation(LanguageTypes.EN_US, "Cross Chop");

    public static final LanguageItem CROSS_CHOP_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.CROSS_CHOP)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时追加一道剑气。")
            .addTranslation(LanguageTypes.EN_US, "Adds an additional sword qi when slashing.");

    // 剑气释放
    public static final LanguageItem DRIVE_RELEASE = createSpecialEffectLanguage(SpecialEffectsRegistry.DRIVE_RELEASE)
            .addTranslation(LanguageTypes.ZH_CN, "剑气释放")
            .addTranslation(LanguageTypes.EN_US, "Drive Release");

    public static final LanguageItem DRIVE_RELEASE_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.DRIVE_RELEASE)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时有概率发出剑气。")
            .addTranslation(LanguageTypes.EN_US, "Has a chance to release sword qi when slashing.");

    // 生长
    public static final LanguageItem GROWTH = createSpecialEffectLanguage(SpecialEffectsRegistry.GROWTH)
            .addTranslation(LanguageTypes.ZH_CN, "生长")
            .addTranslation(LanguageTypes.EN_US, "Growth");

    public static final LanguageItem GROWTH_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.GROWTH)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时恢复生命。")
            .addTranslation(LanguageTypes.EN_US, "Restores health when slashing.");

    // 回溯
    public static final LanguageItem REGRESSION = createSpecialEffectLanguage(SpecialEffectsRegistry.REGRESSION)
            .addTranslation(LanguageTypes.ZH_CN, "回溯")
            .addTranslation(LanguageTypes.EN_US, "Regression");

    public static final LanguageItem REGRESSION_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.REGRESSION)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时恢复耐久。")
            .addTranslation(LanguageTypes.EN_US, "Restores durability when slashing.");

    // 断罪
    public static final LanguageItem JUDGEMENT = createSpecialEffectLanguage(SpecialEffectsRegistry.JUDGEMENT)
            .addTranslation(LanguageTypes.ZH_CN, "断罪")
            .addTranslation(LanguageTypes.EN_US, "Judgement");

    public static final LanguageItem JUDGEMENT_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.JUDGEMENT)
            .addTranslation(LanguageTypes.ZH_CN, "触发SA时追加次元斩攻击。")
            .addTranslation(LanguageTypes.EN_US, "Adds a judgement cut attack when triggering SA.");

    // 冲击
    public static final LanguageItem IMPACT = createSpecialEffectLanguage(SpecialEffectsRegistry.IMPACT)
            .addTranslation(LanguageTypes.ZH_CN, "冲击")
            .addTranslation(LanguageTypes.EN_US, "Impact");

    public static final LanguageItem IMPACT_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.IMPACT)
            .addTranslation(LanguageTypes.ZH_CN, "造成伤害有几率召唤幻影剑造成瞬间伤害。")
            .addTranslation(LanguageTypes.EN_US, "Has a chance to summon phantom swords that deal instant damage when dealing damage.");

    // 过载
    public static final LanguageItem OVERLOAD = createSpecialEffectLanguage(SpecialEffectsRegistry.OVERLOAD)
            .addTranslation(LanguageTypes.ZH_CN, "过载")
            .addTranslation(LanguageTypes.EN_US, "Overload");

    public static final LanguageItem OVERLOAD_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.OVERLOAD)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时小概率触发次元斩。")
            .addTranslation(LanguageTypes.EN_US, "Has a small chance to trigger a judgement cut when slashing.");

    // 抵抗
    public static final LanguageItem RESIST = createSpecialEffectLanguage(SpecialEffectsRegistry.RESIST)
            .addTranslation(LanguageTypes.ZH_CN, "抵抗")
            .addTranslation(LanguageTypes.EN_US, "Resist");

    public static final LanguageItem RESIST_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.RESIST)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时获得伤害吸收。")
            .addTranslation(LanguageTypes.EN_US, "Gains damage absorption when slashing.");

    // 断却
    public static final LanguageItem SEVER_BREAK = createSpecialEffectLanguage(SpecialEffectsRegistry.SEVER_BREAK)
            .addTranslation(LanguageTypes.ZH_CN, "断却")
            .addTranslation(LanguageTypes.EN_US, "Sever Break");

    public static final LanguageItem SEVER_BREAK_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.SEVER_BREAK)
            .addTranslation(LanguageTypes.ZH_CN, "触发次元斩之后造成一次大伤害和大范围的劈砍。")
            .addTranslation(LanguageTypes.EN_US, "Deals massive damage and a wide-range slash after triggering a judgement cut.");

    // 风暴
    public static final LanguageItem STORM = createSpecialEffectLanguage(SpecialEffectsRegistry.STORM)
            .addTranslation(LanguageTypes.ZH_CN, "风暴")
            .addTranslation(LanguageTypes.EN_US, "Storm");

    public static final LanguageItem STORM_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.STORM)
            .addTranslation(LanguageTypes.ZH_CN, "触发次元斩时，召唤幻影剑进行攻击。")
            .addTranslation(LanguageTypes.EN_US, "Summons phantom swords to attack when triggering a judgement cut.");

    // 风暴.变体
    public static final LanguageItem STORM_VARIANT = createSpecialEffectLanguage(SpecialEffectsRegistry.STORM_VARIANT)
            .addTranslation(LanguageTypes.ZH_CN, "风暴[变体]")
            .addTranslation(LanguageTypes.EN_US, "Storm [Variant]");

    public static final LanguageItem STORM_VARIANT_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.STORM_VARIANT)
            .addTranslation(LanguageTypes.ZH_CN, "触发次元斩时，从上方召唤幻影剑进行攻击。")
            .addTranslation(LanguageTypes.EN_US, "Summons phantom swords from above to attack when triggering a judgement cut.");

    // 太虚
    public static final LanguageItem GREAT_VOID = createSpecialEffectLanguage(SpecialEffectsRegistry.GREAT_VOID)
            .addTranslation(LanguageTypes.ZH_CN, "太虚")
            .addTranslation(LanguageTypes.EN_US, "Great Void");

    public static final LanguageItem GREAT_VOID_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.GREAT_VOID)
            .addTranslation(LanguageTypes.ZH_CN, "增加幻影剑伤害。")
            .addTranslation(LanguageTypes.EN_US, "Increases phantom sword damage.");

    // 斩击精通
    public static final LanguageItem SLASH_MASTERY = createSpecialEffectLanguage(SpecialEffectsRegistry.SHARP_BLADE)
            .addTranslation(LanguageTypes.ZH_CN, "利刃")
            .addTranslation(LanguageTypes.EN_US, "Sharp Blade");

    public static final LanguageItem SLASH_MASTERY_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.SHARP_BLADE)
            .addTranslation(LanguageTypes.ZH_CN, "增加斩击伤害。")
            .addTranslation(LanguageTypes.EN_US, "Increases slash damage.");

    // 震荡
    public static final LanguageItem SHOCK = createSpecialEffectLanguage(SpecialEffectsRegistry.SHOCK)
            .addTranslation(LanguageTypes.ZH_CN, "震荡")
            .addTranslation(LanguageTypes.EN_US, "Shock");

    public static final LanguageItem SHOCK_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.SHOCK)
            .addTranslation(LanguageTypes.ZH_CN, "增加次元斩伤害。")
            .addTranslation(LanguageTypes.EN_US, "Increases judgement cut damage.");

    // 剑气纵横
    public static final LanguageItem SWORD_QI_MASTERY = createSpecialEffectLanguage(SpecialEffectsRegistry.SWORD_QI_MASTERY)
            .addTranslation(LanguageTypes.ZH_CN, "剑气纵横")
            .addTranslation(LanguageTypes.EN_US, "Sword Qi Mastery");

    public static final LanguageItem SWORD_QI_MASTERY_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.SWORD_QI_MASTERY)
            .addTranslation(LanguageTypes.ZH_CN, "增加剑气伤害。")
            .addTranslation(LanguageTypes.EN_US, "Increases sword qi damage.");

    // 雷霆万钧
    public static final LanguageItem THUNDER_STRIKE = createSpecialEffectLanguage(SpecialEffectsRegistry.THUNDER_STRIKE)
            .addTranslation(LanguageTypes.ZH_CN, "雷霆万钧")
            .addTranslation(LanguageTypes.EN_US, "Thunder Strike");

    public static final LanguageItem THUNDER_STRIKE_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.THUNDER_STRIKE)
            .addTranslation(LanguageTypes.ZH_CN, "增加闪电伤害。")
            .addTranslation(LanguageTypes.EN_US, "Increases lightning damage.");

    // 黑色玫瑰
    public static final LanguageItem BLACK_ROSE = createSpecialEffectLanguage(SpecialEffectsRegistry.BLACK_ROSE)
            .addTranslation(LanguageTypes.ZH_CN, "黑色玫瑰")
            .addTranslation(LanguageTypes.EN_US, "Black Rose");

    public static final LanguageItem BLACK_ROSE_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.BLACK_ROSE)
            .addTranslation(LanguageTypes.ZH_CN, "记录受到的伤害，并按照记录造成持续伤害，每次造成伤害后有衰减")
            .addTranslation(LanguageTypes.EN_US, "Records damage taken and deals continuous damage based on the record. Damage is halved after each tick.");

    // 吸血转化
    public static final LanguageItem LIFE_STEAL = createSpecialEffectLanguage(SpecialEffectsRegistry.LIFE_STEAL)
            .addTranslation(LanguageTypes.ZH_CN, "吸血转化")
            .addTranslation(LanguageTypes.EN_US, "Life Steal");

    public static final LanguageItem LIFE_STEAL_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.LIFE_STEAL)
            .addTranslation(LanguageTypes.ZH_CN, "将攻击伤害的一部分转化为生命恢复。")
            .addTranslation(LanguageTypes.EN_US, "Converts a portion of attack damage into health restoration.");

    // 雷暴
    public static final LanguageItem THUNDERSTORM = createSpecialEffectLanguage(SpecialEffectsRegistry.THUNDERSTORM)
            .addTranslation(LanguageTypes.ZH_CN, "雷暴")
            .addTranslation(LanguageTypes.EN_US, "Thunderstorm");

    public static final LanguageItem THUNDERSTORM_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.THUNDERSTORM)
            .addTranslation(LanguageTypes.ZH_CN, "触发SA时，在目标位置召唤多道闪电。")
            .addTranslation(LanguageTypes.EN_US, "Summons multiple lightning bolts at the target position when triggering SA.");

    // 雷神之怒
    public static final LanguageItem THUNDER_GODS_WRATH = createSpecialEffectLanguage(SpecialEffectsRegistry.THUNDER_GODS_WRATH)
            .addTranslation(LanguageTypes.ZH_CN, "雷神之怒")
            .addTranslation(LanguageTypes.EN_US, "Thunder God's Wrath");

    public static final LanguageItem THUNDER_GODS_WRATH_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.THUNDER_GODS_WRATH)
            .addTranslation(LanguageTypes.ZH_CN, "击杀敌人时，在死亡位置召唤强力闪电。")
            .addTranslation(LanguageTypes.EN_US, "Summons a powerful lightning bolt at the death location when killing an enemy.");

    // 电离
    public static final LanguageItem IONIZATION = createSpecialEffectLanguage(SpecialEffectsRegistry.IONIZATION)
            .addTranslation(LanguageTypes.ZH_CN, "电离")
            .addTranslation(LanguageTypes.EN_US, "Ionization");

    public static final LanguageItem IONIZATION_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.IONIZATION)
            .addTranslation(LanguageTypes.ZH_CN, "对目标造成闪电伤害时叠加电离层数，根据层数提供全伤害增伤，高等级叠层更快。")
            .addTranslation(LanguageTypes.EN_US, "Stacks ionization on the target when dealing lightning damage, providing all-damage bonus based on stack count. Higher levels stack faster.");

    // 蓄能
    public static final LanguageItem ENERGY_STORAGE = createSpecialEffectLanguage(SpecialEffectsRegistry.ENERGY_STORAGE)
            .addTranslation(LanguageTypes.ZH_CN, "蓄能")
            .addTranslation(LanguageTypes.EN_US, "Energy Storage");

    public static final LanguageItem ENERGY_STORAGE_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.ENERGY_STORAGE)
            .addTranslation(LanguageTypes.ZH_CN, "造成伤害后叠加层数，一定层数后造成一道闪电攻击目标。")
            .addTranslation(LanguageTypes.EN_US, "Stacks layers when dealing damage. Triggers a lightning bolt to attack the target at certain stacks.");

    // 雷云
    public static final LanguageItem THUNDER_CLOUD = createSpecialEffectLanguage(SpecialEffectsRegistry.THUNDER_CLOUD)
            .addTranslation(LanguageTypes.ZH_CN, "雷云")
            .addTranslation(LanguageTypes.EN_US, "Thunder Cloud");

    public static final LanguageItem THUNDER_CLOUD_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.THUNDER_CLOUD)
            .addTranslation(LanguageTypes.ZH_CN, "目标受到雷电伤害后获得雷光buff，持有雷光的实体受到伤害后附加闪电伤害。")
            .addTranslation(LanguageTypes.EN_US, "Targets gain thunder light buff when taking lightning damage. Entities with thunder light take additional lightning damage when damaged.");

    // 分裂
    public static final LanguageItem SPLIT = createSpecialEffectLanguage(SpecialEffectsRegistry.SPLIT)
            .addTranslation(LanguageTypes.ZH_CN, "分裂")
            .addTranslation(LanguageTypes.EN_US, "Split");

    public static final LanguageItem SPLIT_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.SPLIT)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时发射幻影剑进行辅助攻击。")
            .addTranslation(LanguageTypes.EN_US, "Launches phantom swords to assist in attacks when slashing.");

    // 回旋
    public static final LanguageItem SPIRAL = createSpecialEffectLanguage(SpecialEffectsRegistry.SPIRAL)
            .addTranslation(LanguageTypes.ZH_CN, "回旋")
            .addTranslation(LanguageTypes.EN_US, "Spiral");

    public static final LanguageItem SPIRAL_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.SPIRAL)
            .addTranslation(LanguageTypes.ZH_CN, "幻影剑造成伤害后叠加剑势，达到一定层数后触发风暴幻影剑。")
            .addTranslation(LanguageTypes.EN_US, "Stacks sword momentum when phantom swords deal damage. Triggers storm phantom swords at certain stacks.");

    // 破片
    public static final LanguageItem FRAGMENT_SE = createSpecialEffectLanguage(SpecialEffectsRegistry.FRAGMENT)
            .addTranslation(LanguageTypes.ZH_CN, "破片")
            .addTranslation(LanguageTypes.EN_US, "Fragment");

    public static final LanguageItem FRAGMENT_SE_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.FRAGMENT)
            .addTranslation(LanguageTypes.ZH_CN, "幻影剑造成伤害时叠加层级，达到一定层级时额外造成一次大量的伤害。")
            .addTranslation(LanguageTypes.EN_US, "Stacks layers when phantom swords deal damage. Deals massive additional damage at certain layers.");

    // 星闪
    public static final LanguageItem STAR_BLINK = createSpecialEffectLanguage(SpecialEffectsRegistry.STAR_BLINK)
            .addTranslation(LanguageTypes.ZH_CN, "星闪")
            .addTranslation(LanguageTypes.EN_US, "Star Blink");

    public static final LanguageItem STAR_BLINK_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.STAR_BLINK)
            .addTranslation(LanguageTypes.ZH_CN, "攻击目标叠加层数，达到最大层数时触发额外伤害并重置目标速度。")
            .addTranslation(LanguageTypes.EN_US, "Stacks layers on targets when attacking. Triggers additional damage and resets target velocity at max stacks.");

    public static final LanguageItem STAR_BLINK_LAMBDA = createSpecialEffectLanguage(SpecialEffectsRegistry.STAR_BLINK_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^星闪")
            .addTranslation(LanguageTypes.EN_US, "Star Blink Lambda");

    public static final LanguageItem STAR_BLINK_LAMBDA_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.STAR_BLINK_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "攻击目标叠加层数，达到最大层数时触发额外伤害并重置目标速度。")
            .addTranslation(LanguageTypes.EN_US, "Stacks layers on targets when attacking. Triggers additional damage and resets target velocity at max stacks.");

    // 断灭
    public static final LanguageItem ANNIHILATION = createSpecialEffectLanguage(SpecialEffectsRegistry.ANNIHILATION)
            .addTranslation(LanguageTypes.ZH_CN, "断灭")
            .addTranslation(LanguageTypes.EN_US, "Annihilation");

    public static final LanguageItem ANNIHILATION_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.ANNIHILATION)
            .addTranslation(LanguageTypes.ZH_CN, "召唤一定数量的次元斩之后额外召唤一个巨型次元斩。")
            .addTranslation(LanguageTypes.EN_US, "After summoning a certain number of judgement cuts, additionally summons a giant judgement cut.");

    // 撕裂
    public static final LanguageItem TEAR = createSpecialEffectLanguage(SpecialEffectsRegistry.TEAR)
            .addTranslation(LanguageTypes.ZH_CN, "撕裂")
            .addTranslation(LanguageTypes.EN_US, "Tear");

    public static final LanguageItem TEAR_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.TEAR)
            .addTranslation(LanguageTypes.ZH_CN, "次元斩造成伤害后叠加层数，满层级后造成额外的伤害。")
            .addTranslation(LanguageTypes.EN_US, "Stacks layers when judgement cuts deal damage. Deals additional damage at max stacks.");

    // 旋风
    public static final LanguageItem WHIRLWIND = createSpecialEffectLanguage(SpecialEffectsRegistry.WHIRLWIND)
            .addTranslation(LanguageTypes.ZH_CN, "旋风")
            .addTranslation(LanguageTypes.EN_US, "Whirlwind");

    public static final LanguageItem WHIRLWIND_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.WHIRLWIND)
            .addTranslation(LanguageTypes.ZH_CN, "你的次元斩将允许造成重复的伤害。")
            .addTranslation(LanguageTypes.EN_US, "Your judgement cuts will be allowed to deal repeated damage.");

    // 解算
    public static final LanguageItem RESOLVE = createSpecialEffectLanguage(SpecialEffectsRegistry.RESOLVE)
            .addTranslation(LanguageTypes.ZH_CN, "解算")
            .addTranslation(LanguageTypes.EN_US, "Resolve");

    public static final LanguageItem RESOLVE_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.RESOLVE)
            .addTranslation(LanguageTypes.ZH_CN, "攻击带有演算层数的目标时消耗一层演算，造成附加伤害。")
            .addTranslation(LanguageTypes.EN_US, "When attacking a target with calculus stacks, consumes one stack, deals bonus damage.");

    public static final LanguageItem RESOLVE_LAMBDA = createSpecialEffectLanguage(SpecialEffectsRegistry.RESOLVE_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^解算")
            .addTranslation(LanguageTypes.EN_US, "Resolve Lambda");

    public static final LanguageItem RESOLVE_LAMBDA_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.RESOLVE_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "攻击带有演算层数的目标时消耗一层演算，造成更高的附加伤害。")
            .addTranslation(LanguageTypes.EN_US, "When attacking a target with calculus stacks, consumes one stack, deals greater bonus damage.");

    // 燃沫
    public static final LanguageItem FLAME_FOAM = createSpecialEffectLanguage(SpecialEffectsRegistry.FLAME_FOAM)
            .addTranslation(LanguageTypes.ZH_CN, "燃沫")
            .addTranslation(LanguageTypes.EN_US, "Flame Foam");

    public static final LanguageItem FLAME_FOAM_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.FLAME_FOAM)
            .addTranslation(LanguageTypes.ZH_CN, "攻击带有灵魂燃烧的目标时，额外造成基于其当前生命值的伤害，并有概率叠加灵魂燃烧。")
            .addTranslation(LanguageTypes.EN_US, "When attacking a target with soul burn, deals extra damage based on their current health, with a chance to add soul burn.");

    // 染色
    public static final LanguageItem COLOR_DYE = createSpecialEffectLanguage(SpecialEffectsRegistry.COLOR_DYE)
            .addTranslation(LanguageTypes.ZH_CN, "染色")
            .addTranslation(LanguageTypes.EN_US, "Color Dye");

    public static final LanguageItem COLOR_DYE_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.COLOR_DYE)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时更改刀刃颜色为随机的。")
            .addTranslation(LanguageTypes.EN_US, "Changes blade color to random when slashing.");

    // 金戈
    public static final LanguageItem GOLDEN_HALBERD = createSpecialEffectLanguage(SpecialEffectsRegistry.GOLDEN_HALBERD)
            .addTranslation(LanguageTypes.ZH_CN, "金戈")
            .addTranslation(LanguageTypes.EN_US, "Golden Halberd");

    public static final LanguageItem GOLDEN_HALBERD_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.GOLDEN_HALBERD)
            .addTranslation(LanguageTypes.ZH_CN, "斩击命中叠加层数，满层对周围造成额外伤害。")
            .addTranslation(LanguageTypes.EN_US, "Slash hits stack layers; at max, detonates bonus damage nearby.");

    public static final LanguageItem GOLDEN_HALBERD_LAMBDA = createSpecialEffectLanguage(SpecialEffectsRegistry.GOLDEN_HALBERD_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "金戈.λ")
            .addTranslation(LanguageTypes.EN_US, "Golden Halberd.λ");

    public static final LanguageItem GOLDEN_HALBERD_LAMBDA_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.GOLDEN_HALBERD_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "斩击命中叠加层数，满层对周围造成额外伤害。")
            .addTranslation(LanguageTypes.EN_US, "Slash hits stack layers; at max, detonates bonus damage nearby.");

    // 茶韵
    public static final LanguageItem TEA_AROMA = createSpecialEffectLanguage(SpecialEffectsRegistry.TEA_AROMA)
            .addTranslation(LanguageTypes.ZH_CN, "茶韵")
            .addTranslation(LanguageTypes.EN_US, "Tea Aroma");

    public static final LanguageItem TEA_AROMA_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.TEA_AROMA)
            .addTranslation(LanguageTypes.ZH_CN, "命中储存部分伤害延迟释放，裂隙斩开后缓慢愈合；连续命中累加并刷新倒计时；剑气命中额外叠加层级。")
            .addTranslation(LanguageTypes.EN_US, "Hits store delayed damage released as a tearing rift that slowly seals; consecutive hits accumulate and reset the timer; drive hits add bonus stacks.");

    public static final LanguageItem TEA_AROMA_LAMBDA = createSpecialEffectLanguage(SpecialEffectsRegistry.TEA_AROMA_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "茶韵.λ")
            .addTranslation(LanguageTypes.EN_US, "Tea Aroma.λ");

    public static final LanguageItem TEA_AROMA_LAMBDA_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.TEA_AROMA_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "命中储存部分伤害延迟释放，裂隙斩开后缓慢愈合；连续命中累加并刷新倒计时；剑气命中额外叠加层级。")
            .addTranslation(LanguageTypes.EN_US, "Hits store delayed damage released as a tearing rift that slowly seals; consecutive hits accumulate and reset the timer; drive hits add bonus stacks.");

    // 光子灼痕
    public static final LanguageItem PHOTON_SCAR = createSpecialEffectLanguage(SpecialEffectsRegistry.PHOTON_SCAR)
            .addTranslation(LanguageTypes.ZH_CN, "光子灼痕")
            .addTranslation(LanguageTypes.EN_US, "Photon Scar");

    public static final LanguageItem PHOTON_SCAR_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.PHOTON_SCAR)
            .addTranslation(LanguageTypes.ZH_CN, "激光叠加灼烧，造成持续火焰伤害并提供全伤害增伤。灼烧状态下攻击叠加灼痕，满层释放短光束并清零。")
            .addTranslation(LanguageTypes.EN_US, "Laser stacks burn for fire DoT and all-damage amp. While burning, attacks stack scar; at max, fires a short beam and clears.");

    // 长空落日
    public static final LanguageItem LONG_SKY_SUNSET_SE = createSpecialEffectLanguage(SpecialEffectsRegistry.LONG_SKY_SUNSET)
            .addTranslation(LanguageTypes.ZH_CN, "长空落日")
            .addTranslation(LanguageTypes.EN_US, "Long Sky Sunset");

    public static final LanguageItem LONG_SKY_SUNSET_SE_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.LONG_SKY_SUNSET)
            .addTranslation(LanguageTypes.ZH_CN, "对锁定目标持续发射追踪幻影剑；等级越高，辅助剑伤害越高。")
            .addTranslation(LanguageTypes.EN_US, "Continuously fires tracking phantom swords at the locked target; higher SE levels increase their damage.");

    // 击晕
    public static final LanguageItem STUN = createSpecialEffectLanguage(SpecialEffectsRegistry.STUN)
            .addTranslation(LanguageTypes.ZH_CN, "击晕")
            .addTranslation(LanguageTypes.EN_US, "Stun");

    public static final LanguageItem STUN_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.STUN)
            .addTranslation(LanguageTypes.ZH_CN, "命中目标时使其短暂击晕，无法移动。")
            .addTranslation(LanguageTypes.EN_US, "Stuns the hit target briefly, preventing movement.");

    // 催熟
    public static final LanguageItem FERTILIZE = createSpecialEffectLanguage(SpecialEffectsRegistry.FERTILIZE)
            .addTranslation(LanguageTypes.ZH_CN, "催熟")
            .addTranslation(LanguageTypes.EN_US, "Fertilize");

    public static final LanguageItem FERTILIZE_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.FERTILIZE)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时对周围随机作物施加骨粉效果。")
            .addTranslation(LanguageTypes.EN_US, "Applies bone meal to a random nearby crop when slashing.");

    // 吃
    public static final LanguageItem EAT = createSpecialEffectLanguage(SpecialEffectsRegistry.EAT)
            .addTranslation(LanguageTypes.ZH_CN, "吃")
            .addTranslation(LanguageTypes.EN_US, "Eat");

    public static final LanguageItem EAT_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.EAT)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时消耗耐久，恢复饱和度。")
            .addTranslation(LanguageTypes.EN_US, "Consumes durability to restore saturation when slashing.");

    // 变大！
    public static final LanguageItem ENLARGE = createSpecialEffectLanguage(SpecialEffectsRegistry.ENLARGE)
            .addTranslation(LanguageTypes.ZH_CN, "变大！")
            .addTranslation(LanguageTypes.EN_US, "Enlarge!");

    public static final LanguageItem ENLARGE_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.ENLARGE)
            .addTranslation(LanguageTypes.ZH_CN, "挥刀时大幅增加攻击范围。移植此 SE 到其他刀上会发生不好的事。")
            .addTranslation(LanguageTypes.EN_US, "Greatly increases attack range when slashing. Transplanting this SE onto other blades will lead to misfortune.");

    // 静电余韵
    public static final LanguageItem STATIC_AFTERGLOW = createSpecialEffectLanguage(SpecialEffectsRegistry.STATIC_AFTERGLOW)
            .addTranslation(LanguageTypes.ZH_CN, "静电余韵")
            .addTranslation(LanguageTypes.EN_US, "Static Afterglow");

    public static final LanguageItem STATIC_AFTERGLOW_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.STATIC_AFTERGLOW)
            .addTranslation(LanguageTypes.ZH_CN, "释放 SA 后的一段时间内，造成伤害时附带雷电附加伤害；造成雷电伤害时有概率触发闪电链。")
            .addTranslation(LanguageTypes.EN_US, "For a period after releasing SA, dealing damage adds bonus lightning damage; dealing lightning damage may trigger a lightning chain.");

    public static final LanguageItem STATIC_AFTERGLOW_LAMBDA = createSpecialEffectLanguage(SpecialEffectsRegistry.STATIC_AFTERGLOW_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "^静电余韵")
            .addTranslation(LanguageTypes.EN_US, "^Static Afterglow");

    public static final LanguageItem STATIC_AFTERGLOW_LAMBDA_DESC = createSpecialEffectDescription(SpecialEffectsRegistry.STATIC_AFTERGLOW_LAMBDA)
            .addTranslation(LanguageTypes.ZH_CN, "释放 SA 后的一段时间内，造成伤害时附带更强的雷电附加伤害；造成雷电伤害时有更高概率触发闪电链。")
            .addTranslation(LanguageTypes.EN_US, "For a longer period after releasing SA, dealing damage adds stronger bonus lightning damage; dealing lightning damage has a higher chance to trigger a lightning chain.");

    private static LanguageItem createSlashBladeDefinitionLanguage(SlashBladeDefinition definition) {
        return new LanguageItem(definition.getTranslationKey());
    }

    private static LanguageItem createItemLanguage(RegistryObject<net.minecraft.world.item.Item> item) {
        return new LanguageItem(() -> item.get().getDescriptionId());
    }

    private static LanguageItem createItemDescription(RegistryObject<net.minecraft.world.item.Item> item) {
        return new LanguageItem(() -> item.get().getDescriptionId() + ".desc");
    }

    private static LanguageItem createSlashArtsLanguage(RegistryObject<? extends SlashArts> slashArts) {
        return new LanguageItem(() -> Objects.requireNonNull(slashArts.get()).getDescriptionId());
    }

    private static LanguageItem createSlashArtsDescription(RegistryObject<? extends SlashArts> slashArts) {
        return new LanguageItem(() -> Objects.requireNonNull(slashArts.get()).getDescriptionId() + ".desc");
    }

    private static LanguageItem createSpecialEffectLanguage(RegistryObject<? extends SpecialEffect> specialEffect) {
        return new LanguageItem(() -> Objects.requireNonNull(specialEffect.get()).getDescriptionId());
    }

    private static LanguageItem createSpecialEffectDescription(RegistryObject<? extends SpecialEffect> specialEffect) {
        return new LanguageItem(() -> Objects.requireNonNull(specialEffect.get()).getDescriptionId() + ".desc");
    }


}
