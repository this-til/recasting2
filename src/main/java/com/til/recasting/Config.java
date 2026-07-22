package com.til.recasting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Recasting.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final int NORMAL_SE_ENGRAVING_LIMIT = 4;
    private static final int SPECIAL_SE_ENGRAVING_LIMIT = 1;


    // 附魔加成
    public  static final ForgeConfigSpec.DoubleValue SMITE_ATTACK_BONUS = BUILDER
            .comment("亡灵杀手附魔伤害加成（每级）")
            .defineInRange("smiteAttackBonus", 0.1, 0, 10);
    
    public  static final ForgeConfigSpec.DoubleValue BANE_OF_ARTHROPODS_ATTACK_BONUS = BUILDER
            .comment("节肢杀手附魔伤害加成（每级）")
            .defineInRange("baneOfArthropodsAttackBonus", 0.1, 0, 10);
    
    public  static final ForgeConfigSpec.DoubleValue FIRE_ASPECT_DAMAGE = BUILDER
            .comment("火焰附加附魔的火焰伤害倍率（每级）")
            .defineInRange("fireAspectDamage", 0.05, 0, 10);
    
    public  static final ForgeConfigSpec.DoubleValue POWER_ATTACK_BONUS = BUILDER
            .comment("力量附魔对幻影剑攻击的伤害加成（每级）")
            .defineInRange("powerAttackBonus", 0.15, 0, 10);
    
    // 评分等级加成
    public  static final ForgeConfigSpec.DoubleValue RANK_MAX_BONUS = BUILDER
            .comment("评分等级最大伤害加成（满级时）")
            .defineInRange("rankMaxBonus", 0.1, 0, 10);
    
    // 精炼和击杀加成
    public  static final ForgeConfigSpec.DoubleValue REFINE_ATTACK_BONUS = BUILDER
            .comment("精炼等级伤害加成（每级）")
            .defineInRange("refineAttackBonus", 0.001, 0, 10);
    
    public  static final ForgeConfigSpec.DoubleValue SUMMONED_SWORD_BASE_DAMAGE = BUILDER
            .comment("召唤剑基础伤害倍率")
            .defineInRange("summonedSwordBaseDamage", 0.1, 0.0, 100.0);

    public static final ForgeConfigSpec.DoubleValue JUDGEMENT_CUT_SLASH_DAMAGE = BUILDER
            .comment("次元斩生成的斩击效果伤害倍率")
            .defineInRange("judgementCutSlashDamage", 0.1, 0.0, 100.0);
    
    public  static final ForgeConfigSpec.DoubleValue THOUSAND_KILL_ATTACK_BONUS = BUILDER
            .comment("击杀数超过1000的伤害加成")
            .defineInRange("thousandKillAttackBonus", 0.1, 0.0, 10.0);
    
    public  static final ForgeConfigSpec.DoubleValue TEN_THOUSAND_KILL_ATTACK_BONUS = BUILDER
            .comment("击杀数超过10000的伤害加成")
            .defineInRange("tenThousandKillAttackBonus", 0.1, 0.0, 10.0);
    
    public  static final ForgeConfigSpec.DoubleValue THOUSAND_REFINE_ATTACK_BONUS = BUILDER
            .comment("精炼等级超过1000的伤害加成")
            .defineInRange("thousandRefineAttackBonus", 0.1, 0.0, 10.0);
    
    public  static final ForgeConfigSpec.DoubleValue TEN_THOUSAND_REFINE_ATTACK_BONUS = BUILDER
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

    public static final ForgeConfigSpec.IntValue PROUD_SOUL_ENCHANTED_TINY_LEVEL = BUILDER
            .comment("掉落的随机附魔破碎耀魂的附魔等级")
            .defineInRange("proudSoulEnchantedTinyLevel", 1, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.BooleanValue PROUD_SOUL_ENCHANTMENT_IGNORE_MAX_LEVEL = BUILDER
            .comment("掉落的随机附魔破碎耀魂是否无视附魔自身的最大等级限制")
            .define("proudSoulEnchantmentIgnoreMaxLevel", false);

    public static final ForgeConfigSpec.BooleanValue UNLIMITED_SE_ENGRAVING = BUILDER
            .comment("是否解除铁砧 SE 铭刻数量限制。开启后，普通 SE 与特殊 SE 都不再受数量上限约束")
            .define("unlimitedSeEngraving", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean isUnlimitedSeEngraving() {
        return UNLIMITED_SE_ENGRAVING.get();
    }

    public static int getNormalSeEngravingLimit() {
        return NORMAL_SE_ENGRAVING_LIMIT;
    }

    public static int getSpecialSeEngravingLimit() {
        return SPECIAL_SE_ENGRAVING_LIMIT;
    }



}
