package com.til.recasting.constant;

import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/** Recasting SlashBlade 定义的稳定注册 key。 */
public final class RecastingSlashBladeKeys {
    private RecastingSlashBladeKeys() {
    }

    private static ResourceKey<SlashBladeDefinition> lambdaOf(ResourceKey<SlashBladeDefinition> base) {
        ResourceLocation location = base.location();
        return ResourceKey.create(
                SlashBladeDefinition.REGISTRY_KEY,
                ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath() + "_lambda"));
    }

    public static final ResourceKey<SlashBladeDefinition> BROADSWORD_WOOD = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.broadswordWood);
    public static final ResourceKey<SlashBladeDefinition> GREEN_BLADE_WOOD = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.greenBladeWood);
    public static final ResourceKey<SlashBladeDefinition> DHARMA_STICK = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.dharmaStick);
    public static final ResourceKey<SlashBladeDefinition> DHARMA_STICK_LAMBDA = lambdaOf(DHARMA_STICK);
    public static final ResourceKey<SlashBladeDefinition> HOE = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.hoe);
    public static final ResourceKey<SlashBladeDefinition> BROADSWORD_IRON = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.broadswordIron);
    public static final ResourceKey<SlashBladeDefinition> GREEN_BLADE_IRON = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.greenBladeIron);
    public static final ResourceKey<SlashBladeDefinition> BROKEN_WHITE = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.brokenWhite);
    public static final ResourceKey<SlashBladeDefinition> BLACK = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.black);
    public static final ResourceKey<SlashBladeDefinition> ART_KNIFE = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.artKnife);
    public static final ResourceKey<SlashBladeDefinition> BA_GUA = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.baGua);
    public static final ResourceKey<SlashBladeDefinition> PHYSICS_SWORD = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.physicsSword);
    public static final ResourceKey<SlashBladeDefinition> BLUE_CLOUD = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.blueCloud);
    public static final ResourceKey<SlashBladeDefinition> BLUE_CLOUD_LAMBDA = lambdaOf(BLUE_CLOUD);
    public static final ResourceKey<SlashBladeDefinition> DRAGON_SCALE = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.dragonScale);
    public static final ResourceKey<SlashBladeDefinition> DRAGON_SCALE_LAMBDA = lambdaOf(DRAGON_SCALE);
    public static final ResourceKey<SlashBladeDefinition> UMBRELLA = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.umbrella);
    public static final ResourceKey<SlashBladeDefinition> UMBRELLA_LAMBDA = lambdaOf(UMBRELLA);
    public static final ResourceKey<SlashBladeDefinition> BA_GUA_BIG = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.baGuaBig);
    public static final ResourceKey<SlashBladeDefinition> BA_GUA_BIG_LAMBDA = lambdaOf(BA_GUA_BIG);
    public static final ResourceKey<SlashBladeDefinition> OBLITERATE = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.obliterate);
    public static final ResourceKey<SlashBladeDefinition> OBLITERATE_LAMBDA = lambdaOf(OBLITERATE);
    public static final ResourceKey<SlashBladeDefinition> SHINE_GOLD = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.shineGold);
    public static final ResourceKey<SlashBladeDefinition> SHINE_GOLD_LAMBDA = lambdaOf(SHINE_GOLD);
    public static final ResourceKey<SlashBladeDefinition> SHINE_TEA = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.shineTea);
    public static final ResourceKey<SlashBladeDefinition> SHINE_TEA_LAMBDA = lambdaOf(SHINE_TEA);
    public static final ResourceKey<SlashBladeDefinition> SILVER_WING = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.silverWing);
    public static final ResourceKey<SlashBladeDefinition> SILVER_WING_LAMBDA = lambdaOf(SILVER_WING);
    public static final ResourceKey<SlashBladeDefinition> COLOR_WING = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.colorWing);
    public static final ResourceKey<SlashBladeDefinition> COLOR_WING_LAMBDA = lambdaOf(COLOR_WING);
    public static final ResourceKey<SlashBladeDefinition> LONG_SKY_SUNSET = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.longSkySunset);
    public static final ResourceKey<SlashBladeDefinition> LONG_SKY_SUNSET_LAMBDA = lambdaOf(LONG_SKY_SUNSET);
    public static final ResourceKey<SlashBladeDefinition> XUAN_YUAN = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.xuanYuan);
    public static final ResourceKey<SlashBladeDefinition> COOL_MINT = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.coolMint);
    public static final ResourceKey<SlashBladeDefinition> COOL_MINT_LAMBDA = lambdaOf(COOL_MINT);
    public static final ResourceKey<SlashBladeDefinition> DRAGON = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.dragon);
    public static final ResourceKey<SlashBladeDefinition> DRAGON_LAMBDA = lambdaOf(DRAGON);
    public static final ResourceKey<SlashBladeDefinition> SOULBLADE = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.soulblade);
    public static final ResourceKey<SlashBladeDefinition> SUPREME_POLE = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.supremePole);
    public static final ResourceKey<SlashBladeDefinition> SUPREME_POLE_LAMBDA = lambdaOf(SUPREME_POLE);
    public static final ResourceKey<SlashBladeDefinition> WIND_CLOUD = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.windCloud);
    public static final ResourceKey<SlashBladeDefinition> WIND_CLOUD_LAMBDA = lambdaOf(WIND_CLOUD);
    public static final ResourceKey<SlashBladeDefinition> BRILLIANT_GOLD = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.brilliantGold);
    public static final ResourceKey<SlashBladeDefinition> BRILLIANT_GOLD_LAMBDA = lambdaOf(BRILLIANT_GOLD);
    public static final ResourceKey<SlashBladeDefinition> BRILLIANT_TEA = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.brilliantTea);
    public static final ResourceKey<SlashBladeDefinition> BRILLIANT_TEA_LAMBDA = lambdaOf(BRILLIANT_TEA);
    public static final ResourceKey<SlashBladeDefinition> STARTLED_SWAN = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.startledSwan);
    public static final ResourceKey<SlashBladeDefinition> STARTLED_SWAN_LAMBDA = lambdaOf(STARTLED_SWAN);
    public static final ResourceKey<SlashBladeDefinition> BRIARLAND = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.briarland);
    public static final ResourceKey<SlashBladeDefinition> BRIARLAND_LAMBDA = lambdaOf(BRIARLAND);
    public static final ResourceKey<SlashBladeDefinition> ONIKIRI = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.onikiri);
    public static final ResourceKey<SlashBladeDefinition> ONIKIRI_LAMBDA = lambdaOf(ONIKIRI);
    public static final ResourceKey<SlashBladeDefinition> AZURE_VISTA = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.azureVista);
    public static final ResourceKey<SlashBladeDefinition> AZURE_VISTA_LAMBDA = lambdaOf(AZURE_VISTA);
    public static final ResourceKey<SlashBladeDefinition> TIL = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Special.til);
    public static final ResourceKey<SlashBladeDefinition> TIL_LAMBDA = lambdaOf(TIL);
    public static final ResourceKey<SlashBladeDefinition> HTOD = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Special.htod);
    public static final ResourceKey<SlashBladeDefinition> HTOD_LAMBDA = lambdaOf(HTOD);
    public static final ResourceKey<SlashBladeDefinition> XING_KONG = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Special.xingKong);
    public static final ResourceKey<SlashBladeDefinition> XING_KONG_LAMBDA = lambdaOf(XING_KONG);
    public static final ResourceKey<SlashBladeDefinition> TU_WU = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Special.tuWu);
    public static final ResourceKey<SlashBladeDefinition> XUAN_YUAN_LIBERATED = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Special.xuanYuanLiberated);
    public static final ResourceKey<SlashBladeDefinition> FLUORESCENCE_1 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Fluorescence.fluorescence1);
    public static final ResourceKey<SlashBladeDefinition> FLUORESCENCE_2 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Fluorescence.fluorescence2);
    public static final ResourceKey<SlashBladeDefinition> FLUORESCENCE_3 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Fluorescence.fluorescence3);
    public static final ResourceKey<SlashBladeDefinition> FLUORESCENCE_4 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Fluorescence.fluorescence4);
    public static final ResourceKey<SlashBladeDefinition> FLUORESCENCE_5 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Fluorescence.fluorescence5);
    public static final ResourceKey<SlashBladeDefinition> FLUORESCENCE_6 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Fluorescence.fluorescence6);
    public static final ResourceKey<SlashBladeDefinition> FLUORESCENCE_7 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Fluorescence.fluorescence7);
    public static final ResourceKey<SlashBladeDefinition> FLUORESCENCE_8 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Fluorescence.fluorescence8);
    public static final ResourceKey<SlashBladeDefinition> LASER_1 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Laser.laser1);
    public static final ResourceKey<SlashBladeDefinition> LASER_2 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Laser.laser2);
    public static final ResourceKey<SlashBladeDefinition> LASER_3 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Laser.laser3);
    public static final ResourceKey<SlashBladeDefinition> LASER_3_LAMBDA = lambdaOf(LASER_3);
    public static final ResourceKey<SlashBladeDefinition> MAGNETIC_STORM = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.magneticStorm);
    public static final ResourceKey<SlashBladeDefinition> MAGNETIC_STORM_LAMBDA = lambdaOf(MAGNETIC_STORM);
    public static final ResourceKey<SlashBladeDefinition> MAGNETIC_STORM_LIMITS = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.magneticStormLimits);
    public static final ResourceKey<SlashBladeDefinition> MAGNETIC_STORM_LIMITS_LAMBDA = lambdaOf(MAGNETIC_STORM_LIMITS);
    public static final ResourceKey<SlashBladeDefinition> STAR_1 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Star.star1);
    public static final ResourceKey<SlashBladeDefinition> STAR_2 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Star.star2);
    public static final ResourceKey<SlashBladeDefinition> STAR_3 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Star.star3);
    public static final ResourceKey<SlashBladeDefinition> STAR_4 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Star.star4);
    public static final ResourceKey<SlashBladeDefinition> STAR_4_LAMBDA = lambdaOf(STAR_4);
    public static final ResourceKey<SlashBladeDefinition> VOID_1 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Void.void1);
    public static final ResourceKey<SlashBladeDefinition> VOID_2 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Void.void2);
    public static final ResourceKey<SlashBladeDefinition> VOID_3 = ResourceKey.create(SlashBladeDefinition.REGISTRY_KEY, R.Slashblade.Void.void3);
}
