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


    static final ForgeConfigSpec SPEC = BUILDER.build();



}
