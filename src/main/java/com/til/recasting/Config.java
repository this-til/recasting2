package com.til.recasting;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final int NORMAL_SE_ENGRAVING_LIMIT = 4;
    private static final int SPECIAL_SE_ENGRAVING_LIMIT = 1;


    // 附魔加成
    public static final ForgeConfigSpec.DoubleValue SMITE_ATTACK_BONUS = BUILDER
            .comment("亡灵杀手附魔伤害加成（每级）")
            .defineInRange("smiteAttackBonus", 0.05, 0, 10);

    public static final ForgeConfigSpec.DoubleValue BANE_OF_ARTHROPODS_ATTACK_BONUS = BUILDER
            .comment("节肢杀手附魔伤害加成（每级）")
            .defineInRange("baneOfArthropodsAttackBonus", 0.05, 0, 10);

    public static final ForgeConfigSpec.DoubleValue FIRE_ASPECT_DAMAGE = BUILDER
            .comment("火焰附加附魔的火焰伤害（每级，仅剑刃/斩击攻击）")
            .defineInRange("fireAspectDamage", 0.03, 0, 10);

    public static final ForgeConfigSpec.DoubleValue FLAME_ARROWS_DAMAGE = BUILDER
            .comment("火矢附魔的火焰伤害（每级，仅幻影剑攻击）")
            .defineInRange("flameArrowsDamage", 0.03, 0, 10);

    public static final ForgeConfigSpec.DoubleValue POWER_ATTACK_BONUS = BUILDER
            .comment("力量附魔对幻影剑攻击的伤害加成（每级）")
            .defineInRange("powerAttackBonus", 0.05, 0, 10);

    // 评分等级加成
    public static final ForgeConfigSpec.DoubleValue RANK_MAX_BONUS = BUILDER
            .comment("评分等级最大伤害加成（满级时）")
            .defineInRange("rankMaxBonus", 0.1, 0, 10);

    // 精炼和击杀加成：f(x) = max * x / (x + half)，极限为 max，x=half 时为 max/2
    public static final ForgeConfigSpec.DoubleValue REFINE_ATTACK_BONUS_MAX = BUILDER
            .comment("精炼伤害加成上限（x→∞ 时趋近该值）")
            .defineInRange("refineAttackBonusMax", 3.0, 0, 100);

    public static final ForgeConfigSpec.DoubleValue REFINE_ATTACK_BONUS_HALF = BUILDER
            .comment("精炼伤害加成半饱和点（达到上限一半时的精炼等级")
            .defineInRange("refineAttackBonusHalf", 1000, 1, Double.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue SUMMONED_SWORD_BASE_DAMAGE = BUILDER
            .comment("召唤剑基础伤害倍率")
            .defineInRange("summonedSwordBaseDamage", 0.1, 0.0, 100.0);

    public static final ForgeConfigSpec.DoubleValue JUDGEMENT_CUT_SLASH_DAMAGE = BUILDER
            .comment("次元斩生成的斩击效果伤害倍率")
            .defineInRange("judgementCutSlashDamage", 0.1, 0.0, 100.0);

    public static final ForgeConfigSpec.DoubleValue THOUSAND_KILL_ATTACK_BONUS = BUILDER
            .comment("击杀数超过1000的伤害加成")
            .defineInRange("thousandKillAttackBonus", 0.1, 0.0, 10.0);

    public static final ForgeConfigSpec.DoubleValue TEN_THOUSAND_KILL_ATTACK_BONUS = BUILDER
            .comment("击杀数超过10000的伤害加成")
            .defineInRange("tenThousandKillAttackBonus", 0.1, 0.0, 10.0);

    public static final ForgeConfigSpec.DoubleValue HUNDRED_THOUSAND_KILL_ATTACK_BONUS = BUILDER
            .comment("击杀数超过100000的伤害加成")
            .defineInRange("hundredThousandKillAttackBonus", 0.05, 0.0, 10.0);

    public static final ForgeConfigSpec.DoubleValue MILLION_KILL_ATTACK_BONUS = BUILDER
            .comment("击杀数超过1000000的伤害加成")
            .defineInRange("millionKillAttackBonus", 0.05, 0.0, 10.0);

    public static final ForgeConfigSpec.DoubleValue THOUSAND_REFINE_ATTACK_BONUS = BUILDER
            .comment("精炼等级超过1000的伤害加成")
            .defineInRange("thousandRefineAttackBonus", 0.1, 0.0, 10.0);

    public static final ForgeConfigSpec.DoubleValue TEN_THOUSAND_REFINE_ATTACK_BONUS = BUILDER
            .comment("精炼等级超过10000的伤害加成")
            .defineInRange("tenThousandRefineAttackBonus", 0.1, 0.0, 10.0);

    // 手持拔刀剑击杀掉落耀魂
    public static final ForgeConfigSpec.DoubleValue PROUD_SOUL_TINY_DROP_CHANCE = BUILDER
            .comment("手持拔刀剑击败生物时，掉落破碎的耀魂（proudsoul_tiny）的概率")
            .defineInRange("proudSoulTinyDropChance", 0.08, 0.0, 1.0);

    public static final ForgeConfigSpec.DoubleValue PROUD_SOUL_DROP_CHANCE = BUILDER
            .comment("手持拔刀剑击败生物时，掉落耀魂碎片（proudsoul）的概率")
            .defineInRange("proudSoulDropChance", 0.04, 0.0, 1.0);

    public static final ForgeConfigSpec.DoubleValue PROUD_SOUL_ENCHANTED_TINY_DROP_CHANCE = BUILDER
            .comment("手持拔刀剑击败生物时，掉落带1级随机附魔的破碎的耀魂的概率")
            .defineInRange("proudSoulEnchantedTinyDropChance", 0.02, 0.0, 1.0);

    public static final ForgeConfigSpec.DoubleValue BASIC_FLAME_DROP_CHANCE = BUILDER
            .comment("手持拔刀剑击败生物时，掉落任意基础火的概率")
            .defineInRange("basicFlameDropChance", 0.01, 0.0, 1.0);

    public static final ForgeConfigSpec.IntValue BASIC_FLAME_DROP_COOLDOWN_TICKS = BUILDER
            .comment("基础火掉落冷却（tick，20 tick = 1 秒；默认 3 分钟 = 3600）")
            .defineInRange("basicFlameDropCooldownTicks", 3600, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue SOUL_CUBE_DROP_CHANCE = BUILDER
            .comment("手持拔刀剑击败生物时，掉落任意庸魂立方体的概率")
            .defineInRange("soulCubeDropChance", 0.001, 0.0, 1.0);

    public static final ForgeConfigSpec.IntValue SOUL_CUBE_DROP_COOLDOWN_TICKS = BUILDER
            .comment("庸魂立方体掉落冷却（tick；默认 9 分钟 = 10800）")
            .defineInRange("soulCubeDropCooldownTicks", 10800, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.DoubleValue SE_CRYSTAL_LEVEL_1_DROP_CHANCE = BUILDER
            .comment("手持拔刀剑击败生物时，掉落配置白名单内 1 级 SE 结晶的概率")
            .defineInRange("seCrystalLevel1DropChance", 0.0005, 0.0, 1.0);

    public static final ForgeConfigSpec.IntValue SE_CRYSTAL_DROP_COOLDOWN_TICKS = BUILDER
            .comment("SE 结晶掉落冷却（tick；默认 2 小时 = 144000）")
            .defineInRange("seCrystalDropCooldownTicks", 144000, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SE_CRYSTAL_DROP_WHITELIST = BUILDER
            .comment("SE 结晶掉落白名单（ResourceLocation；空列表则不掉落）")
            .defineListAllowEmpty(
                    List.of("seCrystalDropWhitelist"),
                    List::of,
                    entry -> entry instanceof String
            );

    public static final ForgeConfigSpec.DoubleValue SLASH_ARTS_DROP_CHANCE = BUILDER
            .comment("手持拔刀剑击败生物时，掉落配置白名单内 SA（耀魂宝珠）的概率")
            .defineInRange("slashArtsDropChance", 0.0003, 0.0, 1.0);

    public static final ForgeConfigSpec.IntValue SLASH_ARTS_DROP_COOLDOWN_TICKS = BUILDER
            .comment("SA 掉落冷却（tick；默认 2 小时 = 144000）")
            .defineInRange("slashArtsDropCooldownTicks", 144000, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SLASH_ARTS_DROP_WHITELIST = BUILDER
            .comment("SA 掉落白名单（ResourceLocation；默认 [回到未来计划]）")
            .defineListAllowEmpty(
                    List.of("slashArtsDropWhitelist"),
                    () -> List.of(
                            "recasting:time_beyond",
                            "recasting:imprisonment",
                            "recasting:phase_fracture",
                            "recasting:eternal_guard",
                            "recasting:azure_haze",
                            "recasting:mortal_dust",
                            "recasting:tidal_surge",
                            "recasting:celestial_drive",
                            "recasting:starfall",
                            "recasting:sky_seize",
                            "recasting:divine_slash",
                            "recasting:verdict",
                            "recasting:infinite_bloom",
                            "recasting:blistering_qi",
                            "recasting:heavy_payload"
                    ),
                    entry -> entry instanceof String
            );

    public static final ForgeConfigSpec.IntValue PROUD_SOUL_ENCHANTED_TINY_LEVEL = BUILDER
            .comment("掉落的随机附魔破碎耀魂的附魔等级")
            .defineInRange("proudSoulEnchantedTinyLevel", 1, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.BooleanValue PROUD_SOUL_ENCHANTMENT_IGNORE_MAX_LEVEL = BUILDER
            .comment("掉落的随机附魔破碎耀魂是否无视附魔自身的最大等级限制")
            .define("proudSoulEnchantmentIgnoreMaxLevel", false);

    public static final ForgeConfigSpec.BooleanValue UNLIMITED_SE_ENGRAVING = BUILDER
            .comment("是否解除铁砧 SE 铭刻数量限制。开启后，普通 SE 与特殊 SE 都不再受数量上限约束")
            .define("unlimitedSeEngraving", false);

    public static final ForgeConfigSpec.BooleanValue SHOW_SLASHBLADE_DURABILITY_BAR = BUILDER
            .comment("是否在物品栏为拔刀剑绘制耐久进度条（SlashBlade 默认隐藏原版耐久条）")
            .define("showSlashBladeDurabilityBar", true);

    // 实验性功能，默认关闭
    public static final ForgeConfigSpec.BooleanValue TIME_BEYOND_ENTITY_TICK_ACCEL = BUILDER
            .comment(
                    "[EXPERIMENTAL] 时之彼端满蓄加速时，是否对玩家与周围实体每游戏刻额外 tick 31 次（合计约 32 倍）。",
                    "默认关闭。开启可能导致 AI/物理/其它模组副作用，仅建议调试或明确了解风险后使用。"
            )
            .define("timeBeyondEntityTickAccel", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean isUnlimitedSeEngraving() {
        return UNLIMITED_SE_ENGRAVING.get();
    }

    public static boolean isTimeBeyondEntityTickAccel() {
        return TIME_BEYOND_ENTITY_TICK_ACCEL.get();
    }

    public static int getNormalSeEngravingLimit() {
        return NORMAL_SE_ENGRAVING_LIMIT;
    }

    public static int getSpecialSeEngravingLimit() {
        return SPECIAL_SE_ENGRAVING_LIMIT;
    }


}
