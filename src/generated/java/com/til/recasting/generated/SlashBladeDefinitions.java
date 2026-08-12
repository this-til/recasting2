
package com.til.recasting.generated;

import com.til.recasting.Recasting;
import com.til.recasting.constant.R;
import com.til.recasting.constant.RecastingSlashBladeKeys;
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
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.RegistryObject;

public final class SlashBladeDefinitions {

    public static void registerAll(BootstapContext<SlashBladeDefinition> bootstrap) {
        //region t6
        // 阔刃（木）
        register(bootstrap, RecastingSlashBladeKeys.BROADSWORD_WOOD)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.broadswordWood$obj)
                                .textureName(R.Slashblade.broadswordWood$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(3f)
                                .maxDamage(8 * 40)
                )
                .build();


        // 青锋（木）
        register(bootstrap, RecastingSlashBladeKeys.GREEN_BLADE_WOOD)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.greenBladeWood$obj)
                                .textureName(R.Slashblade.greenBladeWood$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(3f)
                                .maxDamage(8 * 40)
                )
                .build();

        // 法棍
        register(bootstrap, RecastingSlashBladeKeys.DHARMA_STICK)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.dharmaStick$obj)
                                .textureName(R.Slashblade.dharmaStick$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(0.1f)
                                .maxDamage((int) (114f * 40))
                )
                .addSpecialEffects(SpecialEffectsRegistry.EAT)
                .build();

        // 法棍 Lambda
        register(bootstrap, RecastingSlashBladeKeys.DHARMA_STICK_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.dharmaStick$obj)
                                .textureName(R.Slashblade.dharmaStick$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(0.1f)
                                .maxDamage((int) (114f * 80))
                )
                .addSpecialEffects(SpecialEffectsRegistry.ENLARGE)
                .build();

        // 锄头
        register(bootstrap, RecastingSlashBladeKeys.HOE)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.hoe$obj)
                                .textureName(R.Slashblade.hoe$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(2f)
                                .maxDamage(8 * 40)
                )
                .addSpecialEffects(SpecialEffectsRegistry.FERTILIZE)
                .build();

        //endregion

        //region t5
        // 阔刃（铁）
        register(bootstrap, RecastingSlashBladeKeys.BROADSWORD_IRON)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.broadswordIron$obj)
                                .textureName(R.Slashblade.broadswordIron$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(4f)
                                .maxDamage(12 * 40)
                )
                .build();


        // 青锋（铁）
        register(bootstrap, RecastingSlashBladeKeys.GREEN_BLADE_IRON)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.greenBladeIron$obj)
                                .textureName(R.Slashblade.greenBladeIron$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(4f)
                                .maxDamage(12 * 40)
                )
                .build();
        //endregion

        //region t4


        // 碎白
        register(bootstrap, RecastingSlashBladeKeys.BROKEN_WHITE)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.brokenWhite$obj)
                                .textureName(R.Slashblade.brokenWhite$png)
                                .effectColor(new Color(255, 255, 255).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(5f)
                                .maxDamage(24 * 40)
                                .slashArtsType(SlashArtsRegistry.FRAGMENT.getId())
                )
                .build();

        // 黑刃
        register(bootstrap, RecastingSlashBladeKeys.BLACK)
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
                                .maxDamage(24 * 40)
                                .slashArtsType(SlashArtsRegistry.MULTIPLE_JUDGEMENT_CUT.getId())
                )
                .build();

        // 美工刀
        register(bootstrap, RecastingSlashBladeKeys.ART_KNIFE)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.artKnife$obj)
                                .textureName(R.Slashblade.artKnife$png)
                                .effectColor(new Color(100, 100, 100).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(5f)
                                .maxDamage(24 * 40)
                )
                .build();

        // 八卦剑
        register(bootstrap, RecastingSlashBladeKeys.BA_GUA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.baGua$obj)
                                .textureName(R.Slashblade.baGua$png)
                                .effectColor(new Color(255, 255, 255).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(5f)
                                .maxDamage(24 * 40)
                                .slashArtsType(SlashArtsRegistry.MULTIPLE_DRIVE.getId())
                )
                .build();

        // 物理学圣剑
        register(bootstrap, RecastingSlashBladeKeys.PHYSICS_SWORD)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.physicsSword$obj)
                                .textureName(R.Slashblade.physicsSword$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(5f)
                                .maxDamage(24 * 40)
                )
                .addSpecialEffects(SpecialEffectsRegistry.STUN)
                .build();


        //endregion

        //region t3

        // 青云
        register(bootstrap, RecastingSlashBladeKeys.BLUE_CLOUD)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.blueCloud$obj)
                                .textureName(R.Slashblade.blueCloud$png)
                                .effectColor(new Color(0xA7C683).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.CYAN_GLOW.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 青云 Lambda
        register(bootstrap, RecastingSlashBladeKeys.BLUE_CLOUD_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.blueCloud$obj)
                                .textureName(R.Slashblade.blueCloud$png)
                                .effectColor(new Color(0xA7C683).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.CYAN_GLOW_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 龙鳞
        register(bootstrap, RecastingSlashBladeKeys.DRAGON_SCALE)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.dragonScale$obj)
                                .textureName(R.Slashblade.dragonScale$png)
                                .effectColor(new Color(0xB97910).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.STORM_PHANTOM_SWORDS.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 龙鳞 Lambda
        register(bootstrap, RecastingSlashBladeKeys.DRAGON_SCALE_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.dragonScale$obj)
                                .textureName(R.Slashblade.dragonScale$png)
                                .effectColor(new Color(0xB97910).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.STORM_PHANTOM_SWORDS_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 伞
        register(bootstrap, RecastingSlashBladeKeys.UMBRELLA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.umbrella$obj)
                                .textureName(R.Slashblade.umbrella$png)
                                .effectColor(new Color(0xC191FF).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.MULTIPLE_JUDGEMENT_CUT.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.BLACK_ROSE, 1)
                .build();

        // 伞 Lambda
        register(bootstrap, RecastingSlashBladeKeys.UMBRELLA_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.umbrellaLambda$obj)
                                .textureName(R.Slashblade.umbrella$png)
                                .effectColor(new Color(0xC191FF).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.INFINITE_JUDGEMENT_CUT.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.BLACK_ROSE, 1)
                .build();


        // 八卦巨剑
        register(bootstrap, RecastingSlashBladeKeys.BA_GUA_BIG)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.baGuaBig$obj)
                                .textureName(R.Slashblade.baGuaBig$png)
                                .effectColor(new Color(255, 255, 255).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.MATRIX.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 八卦巨剑 Lambda
        register(bootstrap, RecastingSlashBladeKeys.BA_GUA_BIG_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.baGuaBig$obj)
                                .textureName(R.Slashblade.baGuaBig$png)
                                .effectColor(new Color(255, 255, 255).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.MATRIX_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 烈焰
        register(bootstrap, RecastingSlashBladeKeys.OBLITERATE)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.obliterate$obj)
                                .textureName(R.Slashblade.obliterate$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.INFERNO.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 烈焰 Lambda
        register(bootstrap, RecastingSlashBladeKeys.OBLITERATE_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.obliterateLambda$obj)
                                .textureName(R.Slashblade.obliterate$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.INFERNO_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))

                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension(1.25f))
                .build();

        // 闪金（shine_gold）
        register(bootstrap, RecastingSlashBladeKeys.SHINE_GOLD)
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
                                .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_MAX.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        register(bootstrap, RecastingSlashBladeKeys.SHINE_GOLD_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.shineGold$obj)
                                .textureName(R.Slashblade.shineGold$png)
                                .effectColor(new Color(255, 220, 0).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_MAX_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 闪茶（shine_tea）
        register(bootstrap, RecastingSlashBladeKeys.SHINE_TEA)
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
                                .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_ROW.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        register(bootstrap, RecastingSlashBladeKeys.SHINE_TEA_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.shineTea$obj)
                                .textureName(R.Slashblade.shineTea$png)
                                .effectColor(new Color(255, 220, 0).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_ROW_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 云翼
        register(bootstrap, RecastingSlashBladeKeys.SILVER_WING)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.silverWing$obj)
                                .textureName(R.Slashblade.silverWing$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.CLOUD_WHEEL.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension(1.15f))
                .build();

        // 云翼 Lambda
        register(bootstrap, RecastingSlashBladeKeys.SILVER_WING_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.silverWing$obj)
                                .textureName(R.Slashblade.silverWing$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.CLOUD_WHEEL_STORM.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension(1.15f))
                .build();


        // 长空落日
        register(bootstrap, RecastingSlashBladeKeys.LONG_SKY_SUNSET)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.longSkySunset$obj)
                                .textureName(R.Slashblade.longSkySunset$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.LONG_SKY_SUNSET.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.LONG_SKY_SUNSET, 1)
                .build();

        // 长空落日 Lambda
        register(bootstrap, RecastingSlashBladeKeys.LONG_SKY_SUNSET_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.longSkySunset$obj)
                                .textureName(R.Slashblade.longSkySunset$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.LONG_SKY_SUNSET_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 2))
                .addSpecialEffects(SpecialEffectsRegistry.LONG_SKY_SUNSET, 1)
                .build();

        // 轩辕剑
        //register(bootstrap, RecastingSlashBladeKeys.XUAN_YUAN)
        //        .renderDefinition(
        //                RenderDefinition.Builder.newInstance()
        //                        .modelName(R.Slashblade.xuanYuan$obj)
        //                        .textureName(R.Slashblade.xuanYuan$png)
        //                        .effectColor(new Color(255, 255, 0).getRGB())
        //        )
        //        .propertiesDefinition(
        //                PropertiesDefinition.Builder.newInstance()
        //                        .baseAttackModifier(6f)
        //                        .maxDamage(32 * 40)
        //                        .defaultSwordType(List.of(SwordType.BEWITCHED))
        //        )
        //        .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
        //        .build();

        // 玄澪
        register(bootstrap, RecastingSlashBladeKeys.XUAN_MING)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.xuanMing$obj)
                                .textureName(R.Slashblade.xuanMing$png)
                                .effectColor(new Color(0x8A7A5C).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.RAPID_PHANTOM_SWORDS_DENSE.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 玄澪 Lambda
        register(bootstrap, RecastingSlashBladeKeys.XUAN_MING_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.xuanMing$obj)
                                .textureName(R.Slashblade.xuanMing$png)
                                .effectColor(new Color(0x8A7A5C).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.RAPID_PHANTOM_SWORDS_DENSE_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 苍景
        register(bootstrap, RecastingSlashBladeKeys.AZURE_VISTA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.azureVista$obj)
                                .textureName(R.Slashblade.azureVista$png)
                                .effectColor(new Color(0x4C8A8D).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.PHANTOM_EXPLOSION.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 苍景 Lambda
        register(bootstrap, RecastingSlashBladeKeys.AZURE_VISTA_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.azureVista$obj)
                                .textureName(R.Slashblade.azureVista$png)
                                .effectColor(new Color(0x4C8A8D).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.PHANTOM_EXPLOSION_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 结缘剑「犬」 t3 特典 TODO 贴图模型
        register(bootstrap, RecastingSlashBladeKeys.JIE_YUAN_DOG)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .effectColor(new Color(0x280040).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.DOG_BITE.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.DOG_BOND, 1)
                .build();

        //endregion

        //region t2

        // 彩耀之星 t2 TODO 贴图模型 / 特效
        register(bootstrap, RecastingSlashBladeKeys.RADIANT_STAR)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .effectColor(new Color(0xFF66CC).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .slashArtsType(SlashArtsRegistry.OTHERWORLD_SLASH.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .propertiesDefinitionExtension(
                        new PropertiesDefinitionExtension().feCapacity(1_000_000_000_000L)
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.FOCUSED_ENERGY_BLADE, 1)
                .build();

        // 冰薄荷
        register(bootstrap, RecastingSlashBladeKeys.COOL_MINT)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.coolMint$obj)
                                .textureName(R.Slashblade.coolMint$png)
                                .effectColor(new Color(0xC7F3CB).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .slashArtsType(SlashArtsRegistry.FANATICAL_DANCE.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 冰薄荷 Lambda
        register(bootstrap, RecastingSlashBladeKeys.COOL_MINT_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.coolMint$obj)
                                .textureName(R.Slashblade.coolMint$png)
                                .effectColor(new Color(0xC7F3CB).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .slashArtsType(SlashArtsRegistry.FANATICAL_DANCE_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 彩翼
        register(bootstrap, RecastingSlashBladeKeys.COLOR_WING)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.colorWing$obj)
                                .textureName(R.Slashblade.colorWing$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .slashArtsType(SlashArtsRegistry.HEAVEN_TWELVE_HIT.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.COLOR_DYE)
                .build();

        // 彩翼 Lambda
        register(bootstrap, RecastingSlashBladeKeys.COLOR_WING_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.colorWing$obj)
                                .textureName(R.Slashblade.colorWing$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .slashArtsType(SlashArtsRegistry.HEAVEN_TWELVE_HIT_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.COLOR_DYE)
                .build();

        // 龙魂
        register(bootstrap, RecastingSlashBladeKeys.DRAGON)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.dragon$obj)
                                .textureName(R.Slashblade.dragon$png)
                                .effectColor(new Color(172, 180, 198).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .slashArtsType(SlashArtsRegistry.SWORD_RAIN.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 龙魂 Lambda
        register(bootstrap, RecastingSlashBladeKeys.DRAGON_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.dragonLambda$obj)
                                .textureName(R.Slashblade.dragonLambda$png)
                                .effectColor(new Color(255, 255, 0).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .slashArtsType(SlashArtsRegistry.SWORD_RAIN_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();


        // 魂刃
        register(bootstrap, RecastingSlashBladeKeys.SOULBLADE)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.soulblade$obj)
                                .textureName(R.Slashblade.soulblade$png)
                                .effectColor(new Color(150, 100, 200).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.INFERNO_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.FLAME_FOAM)
                .build();

        register(bootstrap, RecastingSlashBladeKeys.SOULBLADE_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.soulblade$obj)
                                .textureName(R.Slashblade.soulblade$png)
                                .effectColor(new Color(150, 100, 200).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.INFERNO_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.FLAME_FOAM_LAMBDA)
                .build();

        // 太极
        register(bootstrap, RecastingSlashBladeKeys.SUPREME_POLE)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.supremePole$obj)
                                .textureName(R.Slashblade.supremePole$png)
                                .effectColor(new Color(255, 255, 255).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.MATRIX_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.RESOLVE)
                .build();

        register(bootstrap, RecastingSlashBladeKeys.SUPREME_POLE_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.supremePole$obj)
                                .textureName(R.Slashblade.supremePole$png)
                                .effectColor(new Color(255, 255, 255).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.MATRIX_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.RESOLVE_LAMBDA)
                .build();


        //风云
        register(bootstrap, RecastingSlashBladeKeys.WIND_CLOUD)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.windCloud$obj)
                                .textureName(R.Slashblade.windCloud$png)
                                .effectColor(new Color(253, 238, 78).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.JADE_DOMAIN.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        register(bootstrap, RecastingSlashBladeKeys.WIND_CLOUD_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.windCloud$obj)
                                .textureName(R.Slashblade.windCloud$png)
                                .effectColor(new Color(253, 238, 78).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.JADE_DOMAIN_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 灿金（brilliant_gold）
        register(bootstrap, RecastingSlashBladeKeys.BRILLIANT_GOLD)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.brilliantGold$obj)
                                .textureName(R.Slashblade.brilliantGold$png)
                                .effectColor(new Color(255, 255, 0).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_MAX_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.GOLDEN_HALBERD, 1)
                .build();

        register(bootstrap, RecastingSlashBladeKeys.BRILLIANT_GOLD_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.brilliantGold$obj)
                                .textureName(R.Slashblade.brilliantGold$png)
                                .effectColor(new Color(255, 255, 0).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)

                                .maxDamage(128 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_MAX_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.GOLDEN_HALBERD_LAMBDA, 1)
                .build();


        // 灿茶（brilliant_tea）
        register(bootstrap, RecastingSlashBladeKeys.BRILLIANT_TEA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.brilliantTea$obj)
                                .textureName(R.Slashblade.brilliantTea$png)
                                .effectColor(new Color(255, 255, 0).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_ROW_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.TEA_AROMA, 1)
                .build();

        register(bootstrap, RecastingSlashBladeKeys.BRILLIANT_TEA_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.brilliantTea$obj)
                                .textureName(R.Slashblade.brilliantTea$png)
                                .effectColor(new Color(255, 255, 0).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.ZANTETSUDEN_ROW_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.TEA_AROMA_LAMBDA, 1)
                .build();

        // 惊鸿
        register(bootstrap, RecastingSlashBladeKeys.STARTLED_SWAN)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.startledSwan$obj)
                                .textureName(R.Slashblade.startledSwan$png)
                                .effectColor(new Color(0x9A4D4D).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .slashArtsType(SlashArtsRegistry.FLEETING_SHADOW.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 惊鸿 Lambda
        register(bootstrap, RecastingSlashBladeKeys.STARTLED_SWAN_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.startledSwan$obj)
                                .textureName(R.Slashblade.startledSwan$png)
                                .effectColor(new Color(0x9A4D4D).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .slashArtsType(SlashArtsRegistry.FLEETING_SHADOW_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();


        // 荆楚
        register(bootstrap, RecastingSlashBladeKeys.BRIARLAND)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.briarland$obj)
                                .textureName(R.Slashblade.briarland$png)
                                .effectColor(new Color(0x5D6E2D).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .slashArtsType(SlashArtsRegistry.RIFT_GALE.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 荆楚 Lambda
        register(bootstrap, RecastingSlashBladeKeys.BRIARLAND_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.briarland$obj)
                                .textureName(R.Slashblade.briarland$png)
                                .effectColor(new Color(0x5D6E2D).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .slashArtsType(SlashArtsRegistry.RIFT_GALE_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 鬼切
        register(bootstrap, RecastingSlashBladeKeys.ONIKIRI)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.onikiri$obj)
                                .textureName(R.Slashblade.onikiri$png)
                                .effectColor(new Color(0x6A5B43).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .slashArtsType(SlashArtsRegistry.SOUL_SEVER.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 鬼切 Lambda
        register(bootstrap, RecastingSlashBladeKeys.ONIKIRI_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.onikiri$obj)
                                .textureName(R.Slashblade.onikiri$png)
                                .effectColor(new Color(0x6A5B43).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .slashArtsType(SlashArtsRegistry.SOUL_SEVER_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();


        //endregion

        //region t1

        // 星缕
        register(bootstrap, RecastingSlashBladeKeys.STAR_THREAD)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Special.starThread$obj)
                                .textureName(R.Slashblade.Special.starThread$png)
                                .effectColor(new Color(210, 118, 246).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(8f)
                                .maxDamage(192 * 40)
                                .slashArtsType(SlashArtsRegistry.STELLAR_ROTATION.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.STAR_BLINK, 1)
                .build();

        // 星缕 Lambda
        register(bootstrap, RecastingSlashBladeKeys.STAR_THREAD_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Special.starThread$obj)
                                .textureName(R.Slashblade.Special.starThread$png)
                                .effectColor(new Color(210, 118, 246).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(8f)
                                .maxDamage(256 * 40)
                                .slashArtsType(SlashArtsRegistry.STELLAR_ROTATION.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.STAR_BLINK_LAMBDA, 1)
                .build();

        // 末辉
        register(bootstrap, RecastingSlashBladeKeys.FINAL_GLOW)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Special.finalGlow$obj)
                                .textureName(R.Slashblade.Special.finalGlow$png)
                                .effectColor(new Color(246, 67, 67, 255).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(8f)
                                .maxDamage(192 * 40)
                                .slashArtsType(SlashArtsRegistry.FINAL_SUPERNOVA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 末辉 Lambda
        register(bootstrap, RecastingSlashBladeKeys.FINAL_GLOW_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Special.finalGlow$obj)
                                .textureName(R.Slashblade.Special.finalGlow$png)
                                .effectColor(new Color(246, 67, 67, 255).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(8f)
                                .maxDamage(256 * 40)
                                .slashArtsType(SlashArtsRegistry.FINAL_SUPERNOVA_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 穹鳞
        register(bootstrap, RecastingSlashBladeKeys.VAULT_SCALE)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Special.vaultScale$obj)
                                .textureName(R.Slashblade.Special.vaultScale$png)
                                .effectColor(new Color(0, 17, 86).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(8f)
                                .maxDamage(192 * 40)
                                .slashArtsType(SlashArtsRegistry.UNLIMITED_BLADE_WORKS.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 穹鳞 Lambda
        register(bootstrap, RecastingSlashBladeKeys.VAULT_SCALE_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Special.vaultScale$obj)
                                .textureName(R.Slashblade.Special.vaultScale$png)
                                .effectColor(new Color(0, 17, 86).getRGB()
                                ))
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(8f)
                                .maxDamage(256 * 40)
                                .slashArtsType(SlashArtsRegistry.UNLIMITED_BLADE_WORKS_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // 屠巫
        register(bootstrap, RecastingSlashBladeKeys.TU_WU)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Special.tuWu$obj)
                                .textureName(R.Slashblade.Special.tuWu$png)
                                .effectColor(new Color(0xA5527B).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(8f)
                                .maxDamage(192 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.MYRIAD_SILENCE.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.TU_WU_BLOOD_CURSE, 1)
                .build();

        // 屠巫 Lambda
        register(bootstrap, RecastingSlashBladeKeys.TU_WU_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Special.tuWu$obj)
                                .textureName(R.Slashblade.Special.tuWu$png)
                                .effectColor(new Color(0xA5527B).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(8f)
                                .maxDamage(256 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.MYRIAD_SILENCE_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.TU_WU_BLOOD_CURSE_LAMBDA, 1)
                .build();

        // 轩辕·解放
        register(bootstrap, RecastingSlashBladeKeys.XUAN_YUAN_LIBERATED)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Special.xuanYuanLiberated$obj)
                                .textureName(R.Slashblade.Special.xuanYuanLiberated$png)
                                .effectColor(new Color(0xD1B45A).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(8f)
                                .maxDamage(192 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.PHENOMENAL_RETURN.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN, 1)
                .build();

        // 轩辕·解放 Lambda
        register(bootstrap, RecastingSlashBladeKeys.XUAN_YUAN_LIBERATED_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Special.xuanYuanLiberated$obj)
                                .textureName(R.Slashblade.Special.xuanYuanLiberated$png)
                                .effectColor(new Color(0xD1B45A).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(8f)
                                .maxDamage(256 * 40)
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                                .slashArtsType(SlashArtsRegistry.PHENOMENAL_RETURN_LAMBDA.getId())
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .addSpecialEffects(SpecialEffectsRegistry.HUMAN_EMPEROR_DOMAIN_LAMBDA, 1)
                .build();

        //endregion

        // region ========== Fluorescence SlashBlades ==========
        register(bootstrap, RecastingSlashBladeKeys.FLUORESCENCE_1)
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

        register(bootstrap, RecastingSlashBladeKeys.FLUORESCENCE_2)
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

        register(bootstrap, RecastingSlashBladeKeys.FLUORESCENCE_3)
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

        register(bootstrap, RecastingSlashBladeKeys.FLUORESCENCE_4)
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

        register(bootstrap, RecastingSlashBladeKeys.FLUORESCENCE_5)
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

        register(bootstrap, RecastingSlashBladeKeys.FLUORESCENCE_6)
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

        register(bootstrap, RecastingSlashBladeKeys.FLUORESCENCE_7)
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

        register(bootstrap, RecastingSlashBladeKeys.FLUORESCENCE_8)
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

        // t3
        register(bootstrap, RecastingSlashBladeKeys.LASER_1)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Laser.laser1$obj)
                                .textureName(R.Slashblade.Laser.laser1$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.LASER_1.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // t3
        register(bootstrap, RecastingSlashBladeKeys.LASER_2)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Laser.laser2$obj)
                                .textureName(R.Slashblade.Laser.laser2$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.LASER_2.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 2))
                .build();


        // t2
        register(bootstrap, RecastingSlashBladeKeys.LASER_3)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Laser.laser3$obj)
                                .textureName(R.Slashblade.Laser.laser3$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .slashArtsType(SlashArtsRegistry.LASER_3.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension(1.2f))
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 3))
                .addSpecialEffects(SpecialEffectsRegistry.PHOTON_SCAR, 1)
                .build();

        // t2
        register(bootstrap, RecastingSlashBladeKeys.LASER_3_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Laser.laser3$obj)
                                .textureName(R.Slashblade.Laser.laser3$png)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .slashArtsType(SlashArtsRegistry.LASER_3_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension(1.2f))
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 3))
                .addSpecialEffects(SpecialEffectsRegistry.PHOTON_SCAR, 1)
                .build();

        // endregion

        // region ========== Magnetic Storm SlashBlades ==========

        // t3 磁暴
        register(bootstrap, RecastingSlashBladeKeys.MAGNETIC_STORM)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.magneticStorm$obj)
                                .textureName(R.Slashblade.magneticStorm$png)
                                .effectColor(new Color(0x4DD9FF).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.LIGHTNING_CHAIN_1.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // t3 磁暴 Lambda
        register(bootstrap, RecastingSlashBladeKeys.MAGNETIC_STORM_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.magneticStorm$obj)
                                .textureName(R.Slashblade.magneticStorm$png)
                                .effectColor(new Color(0x4DD9FF).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.LIGHTNING_CHAIN_2.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 2))
                .build();

        // t2 磁暴[超限]
        register(bootstrap, RecastingSlashBladeKeys.MAGNETIC_STORM_LIMITS)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.magneticStormLimits$obj)
                                .textureName(R.Slashblade.magneticStorm$png)
                                .effectColor(new Color(0x4DD9FF).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .slashArtsType(SlashArtsRegistry.LIGHTNING_CHAIN_3.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension(1.2f))
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 3))
                .addSpecialEffects(SpecialEffectsRegistry.STATIC_AFTERGLOW, 1)
                .build();

        // t2 磁暴[超限] Lambda
        register(bootstrap, RecastingSlashBladeKeys.MAGNETIC_STORM_LIMITS_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.magneticStormLimits$obj)
                                .textureName(R.Slashblade.magneticStorm$png)
                                .effectColor(new Color(0x4DD9FF).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .slashArtsType(SlashArtsRegistry.LIGHTNING_CHAIN_3_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension(1.2f))
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 3))
                .addSpecialEffects(SpecialEffectsRegistry.STATIC_AFTERGLOW_LAMBDA, 1)
                .build();

        // endregion

        //region ========== Star SlashBlades ==========

        // t3
        register(bootstrap, RecastingSlashBladeKeys.STAR_1)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Star.star1$obj)
                                .textureName(R.Slashblade.Star.star1$png)
                                .effectColor(new Color(0xE7E5E6).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.STAR_1.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(0.75f))
                .build();

        // t3
        register(bootstrap, RecastingSlashBladeKeys.STAR_2)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Star.star2$obj)
                                .textureName(R.Slashblade.Star.star2$png)
                                .effectColor(new Color(0xE7E5E6).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(48 * 40)
                                .slashArtsType(SlashArtsRegistry.STAR_2.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .build();

        // t3
        register(bootstrap, RecastingSlashBladeKeys.STAR_3)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Star.star3$obj)
                                .textureName(R.Slashblade.Star.star3$png)
                                .effectColor(new Color(0xE7E5E6).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.STAR_3.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(1.5f))
                .build();

        // t2
        register(bootstrap, RecastingSlashBladeKeys.STAR_4)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Star.star4$obj)
                                .textureName(R.Slashblade.Star.star4$png)
                                .effectColor(new Color(0xE7E5E6).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .slashArtsType(SlashArtsRegistry.STAR_4.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(2f))
                .build();

        // t2
        register(bootstrap, RecastingSlashBladeKeys.STAR_4_LAMBDA)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Star.star4$obj)
                                .textureName(R.Slashblade.Star.star4$png)
                                .effectColor(new Color(0xE7E5E6).getRGB())
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(128 * 40)
                                .slashArtsType(SlashArtsRegistry.STAR_4_LAMBDA.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(2f))
                .build();

        // endregion

        //region ========== Void SlashBlades ==========

        // t3
        register(bootstrap, RecastingSlashBladeKeys.VOID_1)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Void._void$obj)
                                .textureName(R.Slashblade.Void.void1$png)
                                .effectColor(0xFF001E)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(32 * 40)
                                .slashArtsType(SlashArtsRegistry.VOID_HOLE.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(1.5f))
                .build();

        // t3
        register(bootstrap, RecastingSlashBladeKeys.VOID_2)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Void._void$obj)
                                .textureName(R.Slashblade.Void.void2$png)
                                .effectColor(0xFF001E)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(6f)
                                .maxDamage(64 * 40)
                                .slashArtsType(SlashArtsRegistry.VOID_HOLE_PITCH_BLACK.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(1.5f))
                .build();

        // t2
        register(bootstrap, RecastingSlashBladeKeys.VOID_3)
                .renderDefinition(
                        RenderDefinition.Builder.newInstance()
                                .modelName(R.Slashblade.Void._void$obj)
                                .textureName(R.Slashblade.Void.void3$png)
                                .effectColor(0xFF001E)
                )
                .propertiesDefinition(
                        PropertiesDefinition.Builder.newInstance()
                                .baseAttackModifier(7f)
                                .maxDamage(96 * 40)
                                .slashArtsType(SlashArtsRegistry.VOID_HOLE_FISHY_RED.getId())
                                .defaultSwordType(List.of(SwordType.BEWITCHED))
                )
                .addEnchantmentDefinition(new EnchantmentDefinition(ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 1))
                .propertiesDefinitionExtension(new PropertiesDefinitionExtension().attackDistance(1.5f))
                .build();
        //endregion


    }

    private static ResourceLocation getEnchantmentID(Enchantment enchantment) {
        return ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
    }

    private static SlashBladeDefinitionBuild register(BootstapContext<SlashBladeDefinition> bootstrap, ResourceKey<SlashBladeDefinition> key) {
        return new SlashBladeDefinitionBuild(bootstrap, key);
    }

    @Accessors(fluent = true)
    @Setter
    private static class SlashBladeDefinitionBuild {
        private final BootstapContext<SlashBladeDefinition> bootstrap;
        private final ResourceKey<SlashBladeDefinition> key;
        RenderDefinition.Builder renderDefinition;
        PropertiesDefinition.Builder propertiesDefinition;
        List<EnchantmentDefinition> enchantments = new ArrayList<>();
        List<SpecialEffectDefinition> specialEffects = new ArrayList<>();

        RenderDefinitionExtension renderDefinitionExtension;
        PropertiesDefinitionExtension propertiesDefinitionExtension;

        public SlashBladeDefinitionBuild(BootstapContext<SlashBladeDefinition> bootstrap, ResourceKey<SlashBladeDefinition> key) {
            this.bootstrap = bootstrap;
            this.key = key;
        }

        public SlashBladeDefinition build() {

            if (key == null) {
                throw new IllegalStateException("SlashBladeDefinitionBuild requires a key");
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
                    key.location(),
                    renderDefinitionInstance,
                    propertiesDefinitionInstance,
                    enchantments,
                    null
            );


            //noinspection ConstantValue
            if (slashBladeDefinition instanceof ISlashBladeStateExtension slashBladeStateDefinition) {
                slashBladeStateDefinition.setRecasting$propertiesDefinitionExtension(propertiesDefinitionExtension);
                slashBladeStateDefinition.setRecasting$renderDefinitionExtension(renderDefinitionExtension);
            } else {
                throw new IllegalStateException("SlashBladeDefinition must be a SlashBladeStateExtension");
            }

            bootstrap.register(key, slashBladeDefinition);
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
