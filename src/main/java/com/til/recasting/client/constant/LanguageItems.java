package com.til.recasting.client.constant;

import com.til.recasting.client.generated.language.LanguageItem;
import com.til.recasting.client.generated.language.LanguageTypes;
import com.til.recasting.constant.SlashBladeDefinitions;
import com.til.recasting.registry.RecastingItems;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraftforge.registries.RegistryObject;

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

    public static final LanguageItem BROADSWORD_IRON = createLanguageItem(SlashBladeDefinitions.BROADSWORD_IRON)
            .addTranslation(LanguageTypes.ZH_CN, "阔刃（铁）")
            .addTranslation(LanguageTypes.EN_US, "Broadsword (Iron)");

    public static final LanguageItem BROADSWORD_WOOD = createLanguageItem(SlashBladeDefinitions.BROADSWORD_WOOD)
            .addTranslation(LanguageTypes.ZH_CN, "阔刃（木）")
            .addTranslation(LanguageTypes.EN_US, "Broadsword (Wood)");

    public static final LanguageItem BROKEN_WHITE = createLanguageItem(SlashBladeDefinitions.BROKEN_WHITE)
            .addTranslation(LanguageTypes.ZH_CN, "碎白")
            .addTranslation(LanguageTypes.EN_US, "Broken White");

    public static final LanguageItem GREEN_BLADE_IRON = createLanguageItem(SlashBladeDefinitions.GREEN_BLADE_IRON)
            .addTranslation(LanguageTypes.ZH_CN, "青锋（铁）")
            .addTranslation(LanguageTypes.EN_US, "Green Blade (Iron)");

    public static final LanguageItem GREEN_BLADE_WOOD = createLanguageItem(SlashBladeDefinitions.GREEN_BLADE_WOOD)
            .addTranslation(LanguageTypes.ZH_CN, "青锋（木）")
            .addTranslation(LanguageTypes.EN_US, "Green Blade (Wood)");

    public static final LanguageItem SOULBLADE = createLanguageItem(SlashBladeDefinitions.SOULBLADE)
            .addTranslation(LanguageTypes.ZH_CN, "魂刃")
            .addTranslation(LanguageTypes.EN_US, "Soulblade");

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

    // ========== Special Flame Names ==========
    // 执念火名称
    public static final LanguageItem OBSESSION_FLAME = createLanguageItem(RecastingItems.OBSESSION_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "执念火")
            .addTranslation(LanguageTypes.EN_US, "Obsession Flame");

    // 记忆火名称
    public static final LanguageItem MEMORY_FLAME = createLanguageItem(RecastingItems.MEMORY_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "记忆火")
            .addTranslation(LanguageTypes.EN_US, "Memory Flame");

    // 罪孽火名称
    public static final LanguageItem SIN_FLAME = createLanguageItem(RecastingItems.SIN_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "罪孽火")
            .addTranslation(LanguageTypes.EN_US, "Sin Flame");

    // 圣愿火名称
    public static final LanguageItem HOLY_FLAME = createLanguageItem(RecastingItems.HOLY_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "圣愿火")
            .addTranslation(LanguageTypes.EN_US, "Holy Flame");

    // 混沌火名称
    public static final LanguageItem CHAOS_FLAME = createLanguageItem(RecastingItems.CHAOS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "混沌火")
            .addTranslation(LanguageTypes.EN_US, "Chaos Flame");

    // 彼岸火名称
    public static final LanguageItem OTHER_SHORE_FLAME = createLanguageItem(RecastingItems.OTHER_SHORE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "彼岸火")
            .addTranslation(LanguageTypes.EN_US, "Other Shore Flame");

    // 诗烬火名称
    public static final LanguageItem POETRY_ASH_FLAME = createLanguageItem(RecastingItems.POETRY_ASH_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "诗烬火")
            .addTranslation(LanguageTypes.EN_US, "Poetry Ash Flame");

    // 蜃楼火名称
    public static final LanguageItem MIRAGE_FLAME = createLanguageItem(RecastingItems.MIRAGE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "蜃楼火")
            .addTranslation(LanguageTypes.EN_US, "Mirage Flame");

    // 匠魂火名称
    public static final LanguageItem CRAFTSMAN_FLAME = createLanguageItem(RecastingItems.CRAFTSMAN_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "匠魂火")
            .addTranslation(LanguageTypes.EN_US, "Craftsman Flame");

    // 冰核火名称
    public static final LanguageItem ICE_CORE_FLAME = createLanguageItem(RecastingItems.ICE_CORE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "冰核火")
            .addTranslation(LanguageTypes.EN_US, "Ice Core Flame");

    // 因果火名称
    public static final LanguageItem KARMA_FLAME = createLanguageItem(RecastingItems.KARMA_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "因果火")
            .addTranslation(LanguageTypes.EN_US, "Karma Flame");

    // 摇篮火名称
    public static final LanguageItem CRADLE_FLAME = createLanguageItem(RecastingItems.CRADLE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "摇篮火")
            .addTranslation(LanguageTypes.EN_US, "Cradle Flame");

    // 渊寂火名称
    public static final LanguageItem ABYSS_FLAME = createLanguageItem(RecastingItems.ABYSS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "渊寂火")
            .addTranslation(LanguageTypes.EN_US, "Abyss Flame");

    // 王权火名称
    public static final LanguageItem ROYAL_FLAME = createLanguageItem(RecastingItems.ROYAL_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "王权火")
            .addTranslation(LanguageTypes.EN_US, "Royal Flame");

    // 衔尾火名称
    public static final LanguageItem OUROBOROS_FLAME = createLanguageItem(RecastingItems.OUROBOROS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "衔尾火")
            .addTranslation(LanguageTypes.EN_US, "Ouroboros Flame");

    // 镜生火名称
    public static final LanguageItem MIRROR_FLAME = createLanguageItem(RecastingItems.MIRROR_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "镜生火")
            .addTranslation(LanguageTypes.EN_US, "Mirror Flame");

    // 遗言火名称
    public static final LanguageItem LAST_WORDS_FLAME = createLanguageItem(RecastingItems.LAST_WORDS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "遗言火")
            .addTranslation(LanguageTypes.EN_US, "Last Words Flame");

    // 潮汐火名称
    public static final LanguageItem TIDE_FLAME = createLanguageItem(RecastingItems.TIDE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "潮汐火")
            .addTranslation(LanguageTypes.EN_US, "Tide Flame");

    // ========== Special Flame Descriptions ==========
    // 执念火介绍
    public static final LanguageItem OBSESSION_FLAME_DESC = createDescriptionItem(RecastingItems.OBSESSION_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "躁动不定的橘红色，焰心处有苍白闪烁。")
            .addTranslation(LanguageTypes.EN_US, "Restless orange-red, with pale flashes at the core.");

    // 记忆火介绍
    public static final LanguageItem MEMORY_FLAME_DESC = createDescriptionItem(RecastingItems.MEMORY_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "半透明的琉璃色，焰苗中浮动着朦胧的光影。")
            .addTranslation(LanguageTypes.EN_US, "Translucent glazed color, with hazy light and shadow floating in the flame.");

    // 罪孽火介绍
    public static final LanguageItem SIN_FLAME_DESC = createDescriptionItem(RecastingItems.SIN_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "污浊的暗红色，带有不祥的黑色脉纹，燃烧时散发焦臭。")
            .addTranslation(LanguageTypes.EN_US, "Foul dark red with ominous black veins, emitting a charred stench when burning.");

    // 圣愿火介绍
    public static final LanguageItem HOLY_FLAME_DESC = createDescriptionItem(RecastingItems.HOLY_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "纯净的乳白色光焰，边缘环绕着淡淡的金色光晕。")
            .addTranslation(LanguageTypes.EN_US, "Pure milky white flame, surrounded by a faint golden halo at the edges.");

    // 混沌火介绍
    public static final LanguageItem CHAOS_FLAME_DESC = createDescriptionItem(RecastingItems.CHAOS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "色彩无定，在同一秒内可能呈现光谱上的任何颜色。")
            .addTranslation(LanguageTypes.EN_US, "Indeterminate colors, may display any color on the spectrum within the same second.");

    // 彼岸火介绍
    public static final LanguageItem OTHER_SHORE_FLAME_DESC = createDescriptionItem(RecastingItems.OTHER_SHORE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "冰冷的青白色，摇曳如烛，无温度。")
            .addTranslation(LanguageTypes.EN_US, "Cold bluish-white, flickering like a candle, without warmth.");

    // 诗烬火介绍
    public static final LanguageItem POETRY_ASH_FLAME_DESC = createDescriptionItem(RecastingItems.POETRY_ASH_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "朦胧的月白色，焰心跃动着如文字般的淡金符文。")
            .addTranslation(LanguageTypes.EN_US, "Hazy moon-white, with pale golden runes dancing like text at the core.");

    // 蜃楼火介绍
    public static final LanguageItem MIRAGE_FLAME_DESC = createDescriptionItem(RecastingItems.MIRAGE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "折射的虹彩色，边缘模糊，仿佛隔着一层水汽。")
            .addTranslation(LanguageTypes.EN_US, "Refracted rainbow colors with blurred edges, as if seen through a layer of mist.");

    // 匠魂火介绍
    public static final LanguageItem CRAFTSMAN_FLAME_DESC = createDescriptionItem(RecastingItems.CRAFTSMAN_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "沉稳的铜黄色，焰形规整，时有金属光泽闪过。")
            .addTranslation(LanguageTypes.EN_US, "Steady bronze-yellow with regular flame shape, occasionally flashing with metallic luster.");

    // 冰核火介绍
    public static final LanguageItem ICE_CORE_FLAME_DESC = createDescriptionItem(RecastingItems.ICE_CORE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "外层是炽热的亮蓝色，内核却是深邃的、仿佛能吸收光线的绝对暗蓝。")
            .addTranslation(LanguageTypes.EN_US, "Outer layer is blazing bright blue, but the core is deep, absolute dark blue that seems to absorb light.");

    // 因果火介绍
    public static final LanguageItem KARMA_FLAME_DESC = createDescriptionItem(RecastingItems.KARMA_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "无形的透明之火，只有当它烧灼命运丝线时，才会泛起密麻交错的银线与血线。")
            .addTranslation(LanguageTypes.EN_US, "Invisible transparent fire, only revealing densely interwoven silver and blood-red threads when burning the threads of fate.");

    // 摇篮火介绍
    public static final LanguageItem CRADLE_FLAME_DESC = createDescriptionItem(RecastingItems.CRADLE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "柔和的鹅黄色，光芒如同最安稳的烛光，令人心生宁静。")
            .addTranslation(LanguageTypes.EN_US, "Soft goose-yellow, its light like the most peaceful candlelight, bringing tranquility to the heart.");

    // 渊寂火介绍
    public static final LanguageItem ABYSS_FLAME_DESC = createDescriptionItem(RecastingItems.ABYSS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "纯粹的哑光黑色，吞噬周围光线，形成一片无声的黑暗区域。")
            .addTranslation(LanguageTypes.EN_US, "Pure matte black that devours surrounding light, forming a silent dark region.");

    // 王权火介绍
    public static final LanguageItem ROYAL_FLAME_DESC = createDescriptionItem(RecastingItems.ROYAL_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "威严的暗金色与深紫色交织，焰形升腾如帝王冠冕。")
            .addTranslation(LanguageTypes.EN_US, "Majestic dark gold and deep purple interwoven, the flame rising like an imperial crown.");

    // 衔尾火介绍
    public static final LanguageItem OUROBOROS_FLAME_DESC = createDescriptionItem(RecastingItems.OUROBOROS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "一种自我吞噬的莫比乌斯环状焰流，颜色在银灰与暗蓝间循环。")
            .addTranslation(LanguageTypes.EN_US, "A self-devouring Möbius ring-shaped flame flow, cycling between silver-gray and dark blue.");

    // 镜生火介绍
    public static final LanguageItem MIRROR_FLAME_DESC = createDescriptionItem(RecastingItems.MIRROR_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "如水银般高度反光的镜面之色，完美映照出它所\"模仿\"之火的外貌。")
            .addTranslation(LanguageTypes.EN_US, "Highly reflective mirror-like color like mercury, perfectly reflecting the appearance of the flame it 'imitates'.");

    // 遗言火介绍
    public static final LanguageItem LAST_WORDS_FLAME_DESC = createDescriptionItem(RecastingItems.LAST_WORDS_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "琥珀金色，焰心深处封存着一点即将消散的、代表生命最后色彩的星芒。")
            .addTranslation(LanguageTypes.EN_US, "Amber gold, with a fading starlight representing life's final color sealed deep in the core.");

    // 潮汐火介绍
    public static final LanguageItem TIDE_FLAME_DESC = createDescriptionItem(RecastingItems.TIDE_FLAME)
            .addTranslation(LanguageTypes.ZH_CN, "随着涨落，颜色从新月时的深紫，到满月时如海浪泡沫般的银白。")
            .addTranslation(LanguageTypes.EN_US, "With the ebb and flow, colors shift from deep purple at new moon to silvery white like sea foam at full moon.");

    private static LanguageItem createLanguageItem(SlashBladeDefinition definition) {
        return new LanguageItem(definition.getTranslationKey());
    }

    private static LanguageItem createLanguageItem(RegistryObject<net.minecraft.world.item.Item> item) {
        return new LanguageItem(() -> item.get().getDescriptionId());
    }

    /**
     * 创建物品介绍翻译项
     * 翻译键格式：item.recasting.itemname.desc
     */
    private static LanguageItem createDescriptionItem(RegistryObject<net.minecraft.world.item.Item> item) {
        return new LanguageItem(() -> item.get().getDescriptionId() + ".desc");
    }
}
