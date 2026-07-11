package com.til.recasting.constant;

import com.til.recasting.Recasting;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.capability.RenderDefinitionExtension;
import com.til.recasting.mixin_api.ISlashBladeStateExtension;
import com.til.recasting.registry.SlashArtsRegistry;
import com.til.recasting.registry.SpecialEffectsRegistry;
import lombok.Setter;
import lombok.experimental.Accessors;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.slashblade.EnchantmentDefinition;
import mods.flammpfeil.slashblade.registry.slashblade.PropertiesDefinition;
import mods.flammpfeil.slashblade.registry.slashblade.RenderDefinition;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.RegistryObject;

public class SlashBladeDefinitions {

    //region  t6
    // 阔刃（木）
    public static final SlashBladeDefinition BROADSWORD_WOOD = createBuild(R.Slashblade.broadswordWood)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.broadswordWood$obj)
                            .textureName(R.Slashblade.broadswordWood$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(3f)
                            .maxDamage(40)
            )
            .build();


    // 青锋（木）
    public static final SlashBladeDefinition GREEN_BLADE_WOOD = createBuild(R.Slashblade.greenBladeWood)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.greenBladeWood$obj)
                            .textureName(R.Slashblade.greenBladeWood$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(3f)
                            .maxDamage(40)
            )
            .build();

    // 法棍
    public static final SlashBladeDefinition DHARMA_STICK = createBuild(R.Slashblade.dharmaStick)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.dharmaStick$obj)
                            .textureName(R.Slashblade.dharmaStick$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(2f)
                            .maxDamage((int) (114f * 40))
            )
            .build();

    // 锄头
    public static final SlashBladeDefinition HOE = createBuild(R.Slashblade.hoe)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.hoe$obj)
                            .textureName(R.Slashblade.hoe$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(2f)
                            .maxDamage(5 * 40)
            )
            .build();

    //endregion

    //region t5
    // 阔刃（铁）
    public static final SlashBladeDefinition BROADSWORD_IRON = createBuild(R.Slashblade.broadswordIron)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.broadswordIron$obj)
                            .textureName(R.Slashblade.broadswordIron$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(4f)
                            .maxDamage(2 * 40)
            )
            .build();


    // 青锋（铁）
    public static final SlashBladeDefinition GREEN_BLADE_IRON = createBuild(R.Slashblade.greenBladeIron)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.greenBladeIron$obj)
                            .textureName(R.Slashblade.greenBladeIron$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(4f)
                            .maxDamage(2 * 40)
            )
            .build();
    //endregion

    //region t4


    // 碎白
    public static final SlashBladeDefinition BROKEN_WHITE = createBuild(R.Slashblade.brokenWhite)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.brokenWhite$obj)
                            .textureName(R.Slashblade.brokenWhite$png)
                            .effectColor(new Color(255, 255, 255).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(5f)
                            .maxDamage(4 * 40)
                            .slashArtsType(SlashArtsRegistry.FRAGMENT.getId())
            )
            .build();

    // 黑刃
    public static final SlashBladeDefinition BLACK = createBuild(R.Slashblade.black)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.black$obj)
                            .textureName(R.Slashblade.black$png)
                            .effectColor(new Color(0, 0, 0).getRGB()
                            )
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(5f)
                            .maxDamage(4 * 40)
                            .slashArtsType(SlashArtsRegistry.MULTIPLE_JUDGEMENT_CUT.getId())
            )
            .build();

    // 美工刀
    public static final SlashBladeDefinition ART_KNIFE = createBuild(R.Slashblade.artKnife)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.artKnife$obj)
                            .textureName(R.Slashblade.artKnife$png)
                            .effectColor(new Color(100, 100, 100).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(5f)
                            .maxDamage(2 * 40)
            )
            .build();

    // 八卦剑
    public static final SlashBladeDefinition BA_GUA = createBuild(R.Slashblade.baGua)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.baGua$obj)
                            .textureName(R.Slashblade.baGua$png)
                            .effectColor(new Color(255, 255, 255).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(4f)
                            .maxDamage(3 * 40)
                            .slashArtsType(SlashArtsRegistry.MULTIPLE_DRIVE.getId())
            )
            .build();

    // 物理学圣剑
    public static final SlashBladeDefinition PHYSICS_SWORD = createBuild(R.Slashblade.physicsSword)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.physicsSword$obj)
                            .textureName(R.Slashblade.physicsSword$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
            )
            .build();


    //endregion

    //region t3

    // 青云
    public static final SlashBladeDefinition BLUE_CLOUD = createBuild(R.Slashblade.blueCloud)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.blueCloud$obj)
                            .textureName(R.Slashblade.blueCloud$png)
                            .effectColor(new Color(0xA7C683).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(5f)
                            .maxDamage(6 * 40)
                            .slashArtsType(SlashArtsRegistry.CYAN_GLOW.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // 青云 Lambda
    public static final SlashBladeDefinition BLUE_CLOUD_LAMBDA = createBuild(Recasting.prefix("slashblade/blue_cloud_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.blueCloud$obj)
                            .textureName(R.Slashblade.blueCloud$png)
                            .effectColor(new Color(0xA7C683).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(8 * 40)
                            .slashArtsType(SlashArtsRegistry.CYAN_GLOW_LAMBDA.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // 龙鳞
    public static final SlashBladeDefinition DRAGON_SCALE = createBuild(R.Slashblade.dragonScale)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.dragonScale$obj)
                            .textureName(R.Slashblade.dragonScale$png)
                            .effectColor(new Color(0xB97910).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(5f)
                            .maxDamage((int) (5f * 40))
                            .slashArtsType(SlashArtsRegistry.STORM_PHANTOM_SWORDS.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // 龙鳞 Lambda
    public static final SlashBladeDefinition DRAGON_SCALE_LAMBDA = createBuild(Recasting.prefix("slashblade/dragon_scale_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.dragonScale$obj)
                            .textureName(R.Slashblade.dragonScale$png)
                            .effectColor(new Color(0xB97910).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(5f)
                            .maxDamage((int) (10f * 40))
                            .slashArtsType(SlashArtsRegistry.STORM_PHANTOM_SWORDS_LAMBDA.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // 伞
    public static final SlashBladeDefinition UMBRELLA = createBuild(R.Slashblade.umbrella)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.umbrella$obj)
                            .textureName(R.Slashblade.umbrella$png)
                            .effectColor(new Color(0xC191FF).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(5f)
                            .maxDamage(12 * 40)
                            .slashArtsType(SlashArtsRegistry.MULTIPLE_JUDGEMENT_CUT.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .addSpecialEffects(SpecialEffectsRegistry.BLACK_ROSE, 1)
            .build();

    // 伞 Lambda
    public static final SlashBladeDefinition UMBRELLA_LAMBDA = createBuild(R.Slashblade.umbrellaLambda)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.umbrellaLambda$obj)
                            .textureName(R.Slashblade.umbrella$png)
                            .effectColor(new Color(0xC191FF).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(18 * 40)
                            .slashArtsType(SlashArtsRegistry.INFINITE_JUDGEMENT_CUT.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .addSpecialEffects(SpecialEffectsRegistry.BLACK_ROSE, 1)
            .build();


    // 八卦巨剑
    public static final SlashBladeDefinition BA_GUA_BIG = createBuild(R.Slashblade.baGuaBig)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.baGuaBig$obj)
                            .textureName(R.Slashblade.baGuaBig$png)
                            .effectColor(new Color(255, 255, 255).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(12 * 40)
                            .slashArtsType(SlashArtsRegistry.MATRIX.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // 八卦巨剑 Lambda
    public static final SlashBladeDefinition BA_GUA_BIG_LAMBDA = createBuild(Recasting.prefix("slashblade/ba_gua_big_lambda")
    )
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.baGuaBig$obj)
                            .textureName(R.Slashblade.baGuaBig$png)
                            .effectColor(new Color(255, 255, 255).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(12 * 40)
                            .slashArtsType(SlashArtsRegistry.MATRIX_LAMBDA.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();


    public static final SlashBladeDefinition OBLITERATE = createBuild(R.Slashblade.obliterate)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.obliterate$obj)
                            .textureName(R.Slashblade.obliterate$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .maxDamage(12 * 40)
                            .slashArtsType(SlashArtsRegistry.INFERNO.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // Obliterate Lambda
    public static final SlashBladeDefinition OBLITERATE_LAMBDA = createBuild(R.Slashblade.obliterateLambda)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.obliterateLambda$obj)
                            .textureName(R.Slashblade.obliterate$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .maxDamage(16 * 40)
                            .slashArtsType(SlashArtsRegistry.INFERNO_LAMBDA.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .propertiesDefinitionExtension(new PropertiesDefinitionExtension(1.25f))
            .build();

    // 闪金（shine_gold）
    public static final SlashBladeDefinition SHINE_GOLD = createBuild(R.Slashblade.shineGold)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.shineGold$obj)
                            .textureName(R.Slashblade.shineGold$png)
                            .effectColor(new Color(255, 220, 0).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(24 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_MAX.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    public static final SlashBladeDefinition SHINE_GOLD_LAMBDA = createBuild(Recasting.prefix("slashblade/shine_gold_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.shineGold$obj)
                            .textureName(R.Slashblade.shineGold$png)
                            .effectColor(new Color(255, 220, 0).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(32 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_MAX_LAMBDA.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // 闪茶（shine_tea）
    public static final SlashBladeDefinition SHINE_TEA = createBuild(R.Slashblade.shineTea)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.shineTea$obj)
                            .textureName(R.Slashblade.shineTea$png)
                            .effectColor(new Color(255, 220, 0).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(24 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_ROW.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    public static final SlashBladeDefinition SHINE_TEA_LAMBDA = createBuild(Recasting.prefix("slashblade/shine_tea_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.shineTea$obj)
                            .textureName(R.Slashblade.shineTea$png)
                            .effectColor(new Color(255, 220, 0).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(32 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_ROW_LAMBDA.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();


    //endregion

    //region t2

    // 冰薄荷
    public static final SlashBladeDefinition COOL_MINT = createBuild(R.Slashblade.coolMint)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.coolMint$obj)
                            .textureName(R.Slashblade.coolMint$png)
                            .effectColor(new Color(0xC7F3CB).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(7f)
                            .maxDamage(16 * 40)
                            .slashArtsType(SlashArtsRegistry.FANATICAL_DANCE.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // 冰薄荷 Lambda
    public static final SlashBladeDefinition COOL_MINT_LAMBDA = createBuild(Recasting.prefix("slashblade/cool_mint_lambda")
    )
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.coolMint$obj)
                            .textureName(R.Slashblade.coolMint$png)
                            .effectColor(new Color(0xC7F3CB).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(7f)
                            .maxDamage(24 * 40)
                            .slashArtsType(SlashArtsRegistry.FANATICAL_DANCE_LAMBDA.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // 龙魂
    public static final SlashBladeDefinition DRAGON = createBuild(R.Slashblade.dragon)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.dragon$obj)
                            .textureName(R.Slashblade.dragon$png)
                            .effectColor(new Color(172, 180, 198).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(12 * 40)
                            .slashArtsType(SlashArtsRegistry.SWORD_RAIN.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // 龙魂 Lambda
    public static final SlashBladeDefinition DRAGON_LAMBDA = createBuild(R.Slashblade.dragonLambda)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.dragonLambda$obj)
                            .textureName(R.Slashblade.dragonLambda$png)
                            .effectColor(new Color(255, 255, 0).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(24 * 40)
                            .slashArtsType(SlashArtsRegistry.SWORD_RAIN_LAMBDA.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();


    // 魂刃
    public static final SlashBladeDefinition SOULBLADE = createBuild(R.Slashblade.soulblade)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.soulblade$obj)
                            .textureName(R.Slashblade.soulblade$png)
                            .effectColor(new Color(150, 100, 200).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(18 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.INFERNO_LAMBDA.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .addSpecialEffects(SpecialEffectsRegistry.FLAME_FOAM)
            .build();

    // 太极
    public static final SlashBladeDefinition SUPREME_POLE = createBuild(R.Slashblade.supremePole)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.supremePole$obj)
                            .textureName(R.Slashblade.supremePole$png)
                            .effectColor(new Color(255, 255, 255).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(24 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.MATRIX_LAMBDA.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .addSpecialEffects(SpecialEffectsRegistry.RESOLVE)
            .build();

    public static final SlashBladeDefinition SUPREME_POLE_LAMBDA = createBuild(Recasting.prefix("slashblade/supreme_pole_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.supremePole$obj)
                            .textureName(R.Slashblade.supremePole$png)
                            .effectColor(new Color(255, 255, 255).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(32 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.MATRIX_LAMBDA.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .addSpecialEffects(SpecialEffectsRegistry.RESOLVE_LAMBDA)
            .build();


    //风云
    public static final SlashBladeDefinition WIND_CLOUD = createBuild(R.Slashblade.windCloud)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.windCloud$obj)
                            .textureName(R.Slashblade.windCloud$png)
                            .effectColor(new Color(253, 238, 78).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(24 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.PHANTOM_EXPLOSION.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    public static final SlashBladeDefinition WIND_CLOUD_LAMBDA = createBuild(Recasting.prefix("slashblade/wind_cloud_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.windCloud$obj)
                            .textureName(R.Slashblade.windCloud$png)
                            .effectColor(new Color(253, 238, 78).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(32 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.PHANTOM_EXPLOSION_LAMBDA.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // 灿金（brilliant_gold）
    public static final SlashBladeDefinition BRILLIANT_GOLD = createBuild(R.Slashblade.brilliantGold)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.brilliantGold$obj)
                            .textureName(R.Slashblade.brilliantGold$png)
                            .effectColor(new Color(255, 255, 0).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(24 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_MAX.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    public static final SlashBladeDefinition BRILLIANT_GOLD_LAMBDA = createBuild(Recasting.prefix("slashblade/brilliant_gold_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.brilliantGold$obj)
                            .textureName(R.Slashblade.brilliantGold$png)
                            .effectColor(new Color(255, 255, 0).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(32 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_MAX_LAMBDA.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();


    // 灿茶（brilliant_tea）
    public static final SlashBladeDefinition BRILLIANT_TEA = createBuild(R.Slashblade.brilliantTea)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.brilliantTea$obj)
                            .textureName(R.Slashblade.brilliantTea$png)
                            .effectColor(new Color(255, 255, 0).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(24 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_ROW.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    public static final SlashBladeDefinition BRILLIANT_TEA_LAMBDA = createBuild(Recasting.prefix("slashblade/brilliant_tea_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.brilliantTea$obj)
                            .textureName(R.Slashblade.brilliantTea$png)
                            .effectColor(new Color(255, 255, 0).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(32 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
                            .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_ROW_LAMBDA.getId())
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();


    //endregion

    //region Void SlashBlades

    // t3
    public static final SlashBladeDefinition VOID_1 = createBuild(R.Slashblade.Void.void1)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Void._void$obj)
                            .textureName(R.Slashblade.Void.void1$png)
                            .effectColor(0xFF001E)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(8f)
                            .maxDamage(12 * 40)
                            .slashArtsType(SlashArtsRegistry.VOID_HOLE.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(1.5f))
            .build();

    // t3
    public static final SlashBladeDefinition VOID_2 = createBuild(R.Slashblade.Void.void2)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Void._void$obj)
                            .textureName(R.Slashblade.Void.void2$png)
                            .effectColor(0xFF001E)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(8f)
                            .maxDamage(24 * 40)
                            .slashArtsType(SlashArtsRegistry.VOID_HOLE_PITCH_BLACK.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(1.5f))
            .build();

    // t2
    public static final SlashBladeDefinition VOID_3 = createBuild(R.Slashblade.Void.void3)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Void._void$obj)
                            .textureName(R.Slashblade.Void.void3$png)
                            .effectColor(0xFF001E)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(8f)
                            .maxDamage(32 * 40)
                            .slashArtsType(SlashArtsRegistry.VOID_HOLE_FISHY_RED.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(1.5f))
            .build();
    //endregion

    // ========== Base SlashBlades ==========

    // 云翼
    public static final SlashBladeDefinition SILVER_WING = createBuild(R.Slashblade.silverWing)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.silverWing$obj)
                            .textureName(R.Slashblade.silverWing$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage((int) (8f * 40))
                            .slashArtsType(SlashArtsRegistry.CLOUD_WHEEL.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .propertiesDefinitionExtension(new PropertiesDefinitionExtension(1.15f))
            .build();

    // 云翼 Lambda
    public static final SlashBladeDefinition SILVER_WING_LAMBDA = createBuild(Recasting.prefix("slashblade/silver_wing_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.silverWing$obj)
                            .textureName(R.Slashblade.silverWing$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(7f)
                            .maxDamage(12 * 40)
                            .slashArtsType(SlashArtsRegistry.CLOUD_WHEEL_STORM.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .propertiesDefinitionExtension(new PropertiesDefinitionExtension(1.15f))
            .build();

    // 彩翼
    public static final SlashBladeDefinition COLOR_WING = createBuild(R.Slashblade.colorWing)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.colorWing$obj)
                            .textureName(R.Slashblade.colorWing$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(8f)
                            .maxDamage(14 * 40)
                            .slashArtsType(SlashArtsRegistry.HEAVEN_TWELVE_HIT.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .addSpecialEffects(SpecialEffectsRegistry.COLOR_DYE)
            .build();

    // 彩翼 Lambda
    public static final SlashBladeDefinition COLOR_WING_LAMBDA = createBuild(Recasting.prefix("slashblade/color_wing_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.colorWing$obj)
                            .textureName(R.Slashblade.colorWing$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(8f)
                            .maxDamage(24 * 40)
                            .slashArtsType(SlashArtsRegistry.HEAVEN_TWELVE_HIT_LAMBDA.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .addSpecialEffects(SpecialEffectsRegistry.COLOR_DYE)
            .build();

    // 长空落日
    public static final SlashBladeDefinition LONG_SKY_SUNSET = createBuild(R.Slashblade.longSkySunset)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.longSkySunset$obj)
                            .textureName(R.Slashblade.longSkySunset$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // 长空落日 Lambda
    public static final SlashBladeDefinition LONG_SKY_SUNSET_LAMBDA = createBuild(Recasting.prefix("slashblade/long_sky_sunset_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.longSkySunset$obj)
                            .textureName(R.Slashblade.longSkySunset$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();


    public static final SlashBladeDefinition XUAN_YUAN = createBuild(R.Slashblade.xuanYuan)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.xuanYuan$obj)
                            .textureName(R.Slashblade.xuanYuan$png)
                            .effectColor(new Color(255, 255, 0).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(7f)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();


    // region ========== Fluorescence SlashBlades ==========
    public static final SlashBladeDefinition FLUORESCENCE_1 = createBuild(R.Slashblade.Fluorescence.fluorescence1)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Fluorescence.fluorescence1$obj)
                            .textureName(R.Slashblade.Fluorescence.fluorescence1$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(1f)
                            .maxDamage((int) (24f * 40))
            )
            .build();

    public static final SlashBladeDefinition FLUORESCENCE_2 = createBuild(R.Slashblade.Fluorescence.fluorescence2)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Fluorescence.fluorescence2$obj)
                            .textureName(R.Slashblade.Fluorescence.fluorescence2$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(1f)
                            .maxDamage((int) (24f * 40))
            )
            .build();

    public static final SlashBladeDefinition FLUORESCENCE_3 = createBuild(R.Slashblade.Fluorescence.fluorescence3)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Fluorescence.fluorescence3$obj)
                            .textureName(R.Slashblade.Fluorescence.fluorescence3$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(1f)
                            .maxDamage((int) (24f * 40))
            )
            .build();

    public static final SlashBladeDefinition FLUORESCENCE_4 = createBuild(R.Slashblade.Fluorescence.fluorescence4)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Fluorescence.fluorescence4$obj)
                            .textureName(R.Slashblade.Fluorescence.fluorescence4$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(1f)
                            .maxDamage((int) (24f * 40))
            )
            .build();

    public static final SlashBladeDefinition FLUORESCENCE_5 = createBuild(R.Slashblade.Fluorescence.fluorescence5)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Fluorescence.fluorescence5$obj)
                            .textureName(R.Slashblade.Fluorescence.fluorescence5$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(1f)
                            .maxDamage((int) (24f * 40))
            )
            .build();

    public static final SlashBladeDefinition FLUORESCENCE_6 = createBuild(R.Slashblade.Fluorescence.fluorescence6)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Fluorescence.fluorescence6$obj)
                            .textureName(R.Slashblade.Fluorescence.fluorescence6$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(1f)
                            .maxDamage((int) (24f * 40))
            )
            .build();

    public static final SlashBladeDefinition FLUORESCENCE_7 = createBuild(R.Slashblade.Fluorescence.fluorescence7)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Fluorescence.fluorescence7$obj)
                            .textureName(R.Slashblade.Fluorescence.fluorescence7$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(1f)
                            .maxDamage((int) (24f * 40))
            )
            .build();

    public static final SlashBladeDefinition FLUORESCENCE_8 = createBuild(R.Slashblade.Fluorescence.fluorescence8)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Fluorescence.fluorescence8$obj)
                            .textureName(R.Slashblade.Fluorescence.fluorescence8$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(1f)
                            .maxDamage((int) (24f * 40))
            )
            .build();

    // endregion

    // region ========== Laser SlashBlades ==========
    public static final SlashBladeDefinition LASER_1 = createBuild(R.Slashblade.Laser.laser1)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Laser.laser1$obj)
                            .textureName(R.Slashblade.Laser.laser1$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(10 * 40)
                            .slashArtsType(SlashArtsRegistry.LASER_1.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .addSpecialEffects(SpecialEffectsRegistry.PHOTON_SCAR, 1)
            .build();

    public static final SlashBladeDefinition LASER_2 = createBuild(R.Slashblade.Laser.laser2)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Laser.laser2$obj)
                            .textureName(R.Slashblade.Laser.laser2$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(7f)
                            .maxDamage(15 * 40)
                            .slashArtsType(SlashArtsRegistry.LASER_2.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 2))
            .addSpecialEffects(SpecialEffectsRegistry.PHOTON_SCAR_2, 1)
            .build();

    public static final SlashBladeDefinition LASER_3 = createBuild(R.Slashblade.Laser.laser3)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Laser.laser3$obj)
                            .textureName(R.Slashblade.Laser.laser3$png)
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(8f)
                            .maxDamage(20 * 40)
                            .slashArtsType(SlashArtsRegistry.LASER_3.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .propertiesDefinitionExtension(new PropertiesDefinitionExtension(1.2f))
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 3))
            .addSpecialEffects(SpecialEffectsRegistry.PHOTON_SCAR_3, 1)
            .build();

    // endregion

    // region ========== Special SlashBlades ==========
    public static final SlashBladeDefinition TIL = createBuild(R.Slashblade.Special.til)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Special.til$obj)
                            .textureName(R.Slashblade.Special.til$png)
                            .effectColor(new Color(210, 118, 246).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(10f)
                            .maxDamage(48 * 40)
                            .slashArtsType(SlashArtsRegistry.STELLAR_ROTATION.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .addSpecialEffects(SpecialEffectsRegistry.STAR_BLINK, 1)
            .build();

    public static final SlashBladeDefinition TIL_LAMBDA = createBuild(Recasting.prefix("slashblade/special/til_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Special.til$obj)
                            .textureName(R.Slashblade.Special.til$png)
                            .effectColor(new Color(210, 118, 246).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(12f)
                            .maxDamage(96 * 40)
                            .slashArtsType(SlashArtsRegistry.STELLAR_ROTATION.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .addSpecialEffects(SpecialEffectsRegistry.STAR_BLINK_LAMBDA, 1)
            .build();

    public static final SlashBladeDefinition HTOD = createBuild(R.Slashblade.Special.htod)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Special.htod$obj)
                            .textureName(R.Slashblade.Special.htod$png)
                            .effectColor(new Color(246, 67, 67, 255).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(10f)
                            .maxDamage(48 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    public static final SlashBladeDefinition HTOD_LAMBDA = createBuild(Recasting.prefix("slashblade/special/htod_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Special.htod$obj)
                            .textureName(R.Slashblade.Special.htod$png)
                            .effectColor(new Color(246, 67, 67, 255).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(12f)
                            .maxDamage(96 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    public static final SlashBladeDefinition XING_KONG = createBuild(R.Slashblade.Special.xingKong)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Special.xingKong$obj)
                            .textureName(R.Slashblade.Special.xingKong$png)
                            .effectColor(new Color(0, 17, 86).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(10f)
                            .maxDamage(48 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    public static final SlashBladeDefinition XING_KONG_LAMBDA = createBuild(Recasting.prefix("slashblade/special/xing_kong_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Special.xingKong$obj)
                            .textureName(R.Slashblade.Special.xingKong$png)
                            .effectColor(new Color(0, 17, 86).getRGB()
                            ))
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(12f)
                            .maxDamage(48 * 40)
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // endregion

    //region ========== Star SlashBlades ==========

    // t3
    public static final SlashBladeDefinition STAR_1 = createBuild(R.Slashblade.Star.star1)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Star.star1$obj)
                            .textureName(R.Slashblade.Star.star1$png)
                            .effectColor(new Color(0xE7E5E6).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(4f)
                            .maxDamage(6 * 40)
                            .slashArtsType(SlashArtsRegistry.STAR_1.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(0.75f))
            .build();

    public static final SlashBladeDefinition STAR_2 = createBuild(R.Slashblade.Star.star2)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Star.star2$obj)
                            .textureName(R.Slashblade.Star.star2$png)
                            .effectColor(new Color(0xE7E5E6).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(5f)
                            .maxDamage(12 * 40)
                            .slashArtsType(SlashArtsRegistry.STAR_2.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .build();

    // t3
    public static final SlashBladeDefinition STAR_3 = createBuild(R.Slashblade.Star.star3)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Star.star3$obj)
                            .textureName(R.Slashblade.Star.star3$png)
                            .effectColor(new Color(0xE7E5E6).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(6f)
                            .maxDamage(18 * 40)
                            .slashArtsType(SlashArtsRegistry.STAR_3.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(1.5f))
            .build();

    // t2
    public static final SlashBladeDefinition STAR_4 = createBuild(R.Slashblade.Star.star4)
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Star.star4$obj)
                            .textureName(R.Slashblade.Star.star4$png)
                            .effectColor(new Color(0xE7E5E6).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(7f)
                            .maxDamage(24 * 40)
                            .slashArtsType(SlashArtsRegistry.STAR_4.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(2f))
            .build();

    // t2
    public static final SlashBladeDefinition STAR_4_LAMBDA = createBuild(Recasting.prefix("slashblade/star/star_4_lambda"))
            .renderDefinition(
                    RenderDefinition.Builder.newInstance()
                            .modelName(R.Slashblade.Star.star4$obj)
                            .textureName(R.Slashblade.Star.star4$png)
                            .effectColor(new Color(0xE7E5E6).getRGB())
            )
            .propertiesDefinition(
                    PropertiesDefinition.Builder.newInstance()
                            .baseAttackModifier(8f)
                            .maxDamage(32 * 40)
                            .slashArtsType(SlashArtsRegistry.STAR_4_LAMBDA.getId())
                            .defaultSwordType(List.of(SwordType.BEWITCHED))
            )
            .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
            .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(2f))
            .build();

    // endregion

    private static ResourceLocation getEnchantmentID(Enchantment enchantment) {
        return ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
    }

    private static ResourceKey<SlashBladeDefinition> register(String id) {
        return ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, Recasting.prefix(id)
        );
    }

    public static SlashBladeDefinitionBuild createBuild(String name) {
        return new SlashBladeDefinitionBuild(Recasting.prefix(name)
        );
    }

    public static SlashBladeDefinitionBuild createBuild(ResourceLocation name) {
        return new SlashBladeDefinitionBuild(name);
    }

    @Accessors(fluent = true)
    @Setter
    public static class SlashBladeDefinitionBuild {
        ResourceLocation name;
        RenderDefinition.Builder renderDefinition;
        PropertiesDefinition.Builder propertiesDefinition;
        List<EnchantmentDefinition> enchantments = new ArrayList<>();
        List<SpecialEffectDefinition> specialEffects = new ArrayList<>();

        RenderDefinitionExtension renderDefinitionExtension;
        PropertiesDefinitionExtension propertiesDefinitionExtension;


        public SlashBladeDefinitionBuild(ResourceLocation name) {
            this.name = name;
        }

        public SlashBladeDefinition build() {

            if (name == null) {
                throw new IllegalStateException("SlashBladeDefinitionBuild requires a name");
            }

            // 检查是否有力量附魔，如果没有则添加力量1
            //List<EnchantmentDefinition> finalEnchantments = new ArrayList<>(enchantments);
            //ResourceLocation smiteId = ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE);
            //boolean hasSmite = finalEnchantments.stream()
            //        .anyMatch(e -> e.getEnchantmentID().equals(smiteId));
            //if (!hasSmite) {
            //    finalEnchantments.add(new EnchantmentDefinition(smiteId, 1));
            //}

            if (propertiesDefinitionExtension == null) {
                propertiesDefinitionExtension = new PropertiesDefinitionExtension();
            }

            if (renderDefinitionExtension == null) {
                renderDefinitionExtension = new RenderDefinitionExtension();
            }

            if (renderDefinition == null) {
                renderDefinition = RenderDefinition.Builder.newInstance();
            }

            if (propertiesDefinition == null) {
                propertiesDefinition = PropertiesDefinition.Builder.newInstance();
            }

            for(SpecialEffectDefinition specialEffectDefinition : specialEffects) {
                propertiesDefinition.addSpecialEffect(specialEffectDefinition.specialEffect());
                propertiesDefinitionExtension.setExtendedSpecialLevels(specialEffectDefinition.specialEffect(), specialEffectDefinition.level());
            }

            RenderDefinition renderDefinitionInstance = renderDefinition.build();
            PropertiesDefinition propertiesDefinitionInstance = propertiesDefinition.build();

            for(ResourceLocation specialEffectResourceLocation : propertiesDefinitionInstance.getSpecialEffects()
            ) {
                int extendedSpecialLevels = propertiesDefinitionExtension.getExtendedSpecialLevels(specialEffectResourceLocation);
                if (extendedSpecialLevels > 0) {
                    continue;
                }
                propertiesDefinitionExtension.setExtendedSpecialLevels(specialEffectResourceLocation, 1);
            }

            SlashBladeDefinition slashBladeDefinition = new SlashBladeDefinition(
                    name,
                    renderDefinitionInstance,
                    propertiesDefinitionInstance,
                    enchantments
            );


            //noinspection ConstantValue
            if (slashBladeDefinition instanceof ISlashBladeStateExtension slashBladeStateDefinition) {
                slashBladeStateDefinition.setRecasting$propertiesDefinitionExtension(propertiesDefinitionExtension);
                slashBladeStateDefinition.setRecasting$renderDefinitionExtension(renderDefinitionExtension);
            } else {
                throw new IllegalStateException("SlashBladeDefinition must be a SlashBladeStateExtension");
            }

            return slashBladeDefinition;
        }

        public SlashBladeDefinitionBuild addEnchantmentDefinition(EnchantmentDefinition enchantmentDefinition) {
            enchantments.add(enchantmentDefinition);
            return this;
        }

        public SlashBladeDefinitionBuild addSpecialEffects(RegistryObject<SpecialEffect> specialEffectRegistryObject) {
            addSpecialEffects(specialEffectRegistryObject, 1);
            return this;
        }

        public SlashBladeDefinitionBuild addSpecialEffects(RegistryObject<SpecialEffect> specialEffectRegistryObject, int level) {
            specialEffects.add(new SpecialEffectDefinition(specialEffectRegistryObject.getId(), level));
            return this;
        }
    }

    public record SpecialEffectDefinition(ResourceLocation specialEffect, int level) {
    }


}
