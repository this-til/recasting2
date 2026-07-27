package com.til.recasting.registry;

import com.til.recasting.Config;
import com.til.recasting.Recasting;
import com.til.recasting.constant.RecastingLanguageKeys;
import com.til.recasting.capability.ISpecialEffectCrystalData;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.SpecialEffectTooltipHelper;
import com.til.recasting.item.ProudSoulItem;
import com.til.recasting.registry.se.ExtendedSpecialEffect;
import com.til.recasting.util.Gradient;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Recasting 模组的物品注册类
 * 包含不同颜色的魂火变体
 */
public class RecastingItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Recasting.MODID);

    // 执念火：躁动不定的橘红色，焰心处有苍白闪烁
    public static final RegistryObject<Item> OBSESSION_FLAME = ITEMS.register("obsession_flame", () -> new ProudSoulItem(new Item.Properties(), createObsessionFlameGradient(), 15.0f));

    // 记忆火：半透明的琉璃色，焰苗中浮动着朦胧的光影
    public static final RegistryObject<Item> MEMORY_FLAME = ITEMS.register("memory_flame", () -> new ProudSoulItem(new Item.Properties(), createMemoryFlameGradient(), 25.0f));

    // 罪孽火：污浊的暗红色，带有不祥的黑色脉纹
    public static final RegistryObject<Item> SIN_FLAME = ITEMS.register("sin_flame", () -> new ProudSoulItem(new Item.Properties(), createSinFlameGradient(), 18.0f));

    // 圣愿火：纯净的乳白色光焰，边缘环绕着淡淡的金色光晕
    public static final RegistryObject<Item> HOLY_FLAME = ITEMS.register("holy_flame", () -> new ProudSoulItem(new Item.Properties(), createHolyFlameGradient(), 22.0f));

    // 混沌火：色彩无定，在同一秒内可能呈现光谱上的任何颜色
    public static final RegistryObject<Item> CHAOS_FLAME = ITEMS.register("chaos_flame", () -> new ProudSoulItem(new Item.Properties(), Gradient.createRainbowGradient(), 20.0f));

    // 彼岸火：冰冷的青白色，摇曳如烛，无温度
    public static final RegistryObject<Item> OTHER_SHORE_FLAME = ITEMS.register("other_shore_flame", () -> new ProudSoulItem(new Item.Properties(), createOtherShoreFlameGradient(), 30.0f));

    // 诗烬火：朦胧的月白色，焰心跃动着如文字般的淡金符文
    public static final RegistryObject<Item> POETRY_ASH_FLAME = ITEMS.register("poetry_ash_flame", () -> new ProudSoulItem(new Item.Properties(), createPoetryAshFlameGradient(), 24.0f));

    // 蜃楼火：折射的虹彩色，边缘模糊，仿佛隔着一层水汽
    public static final RegistryObject<Item> MIRAGE_FLAME = ITEMS.register("mirage_flame", () -> new ProudSoulItem(new Item.Properties(), Gradient.createRainbowGradient(), 28.0f));

    // 匠魂火：沉稳的铜黄色，焰形规整，时有金属光泽闪过
    public static final RegistryObject<Item> CRAFTSMAN_FLAME = ITEMS.register("craftsman_flame", () -> new ProudSoulItem(new Item.Properties(), createCraftsmanFlameGradient(), 35.0f));

    // 冰核火：外层是炽热的亮蓝色，内核却是深邃的、仿佛能吸收光线的绝对暗蓝
    public static final RegistryObject<Item> ICE_CORE_FLAME = ITEMS.register("ice_core_flame", () -> new ProudSoulItem(new Item.Properties(), createIceCoreFlameGradient(), 20.0f));

    // 因果火：无形的透明之火，只有当它烧灼命运丝线时，才会泛起密麻交错的银线与血线
    public static final RegistryObject<Item> KARMA_FLAME = ITEMS.register("karma_flame", () -> new ProudSoulItem(new Item.Properties(), createKarmaFlameGradient(), 16.0f));

    // 摇篮火：柔和的鹅黄色，光芒如同最安稳的烛光
    public static final RegistryObject<Item> CRADLE_FLAME = ITEMS.register("cradle_flame", () -> new ProudSoulItem(new Item.Properties(), createCradleFlameGradient(), 40.0f));

    // 渊寂火：纯粹的哑光黑色，吞噬周围光线
    public static final RegistryObject<Item> ABYSS_FLAME = ITEMS.register("abyss_flame", () -> new ProudSoulItem(new Item.Properties(), createAbyssFlameGradient(), 25.0f) {
        @Override
        public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<net.minecraft.network.chat.Component> components, @NotNull TooltipFlag flag) {
            super.appendHoverText(stack, level, components, flag);
            components.add(Component.empty());
            components.add(Component.translatable(RecastingLanguageKeys.TOOLTIP_ABYSS_FLAME_EXTRACT).withStyle(ChatFormatting.GRAY));
        }
    });

    // 王权火：威严的暗金色与深紫色交织，焰形升腾如帝王冠冕
    public static final RegistryObject<Item> ROYAL_FLAME = ITEMS.register("royal_flame", () -> new ProudSoulItem(new Item.Properties(), createRoyalFlameGradient(), 26.0f));

    // 衔尾火：一种自我吞噬的莫比乌斯环状焰流，颜色在银灰与暗蓝间循环
    public static final RegistryObject<Item> OUROBOROS_FLAME = ITEMS.register("ouroboros_flame", () -> new ProudSoulItem(new Item.Properties(), createOuroborosFlameGradient(), 18.0f));

    // 镜生火：如水银般高度反光的镜面之色，完美映照出它所"模仿"之火的外貌
    public static final RegistryObject<Item> MIRROR_FLAME = ITEMS.register("mirror_flame", () -> new ProudSoulItem(new Item.Properties(), createMirrorFlameGradient(), 22.0f));

    // 遗言火：琥珀金色，焰心深处封存着一点即将消散的、代表生命最后色彩的星芒
    public static final RegistryObject<Item> LAST_WORDS_FLAME = ITEMS.register("last_words_flame", () -> new ProudSoulItem(new Item.Properties(), createLastWordsFlameGradient(), 32.0f));

    // 潮汐火：随着涨落，颜色从新月时的深紫，到满月时如海浪泡沫般的银白
    public static final RegistryObject<Item> TIDE_FLAME = ITEMS.register("tide_flame", () -> new ProudSoulItem(new Item.Properties(), createTideFlameGradient(), 60.0f));

    // SE 结晶
    public static final RegistryObject<Item> SE_CRYSTAL = ITEMS.register("se_crystal", () ->
            new ProudSoulItem(new Item.Properties(), null, 16.f) {

                private final Gradient effective = Gradient.createFromColor(new Color(0xFFFF00).getRGB());
                private final Gradient invalid = Gradient.createFromColor(new Color(0x818488).getRGB());

                @Override
                public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<net.minecraft.network.chat.Component> components, @NotNull TooltipFlag flag) {
                    // 使用能力系统获取数据
                    stack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
                        if (!data.hasSpecialEffect()) {
                            return;
                        }

                        ResourceLocation seLocation = data.getSpecialEffectType();
                        if (seLocation == null) {
                            return;
                        }

                        // 从注册表中获取 SpecialEffect
                        if (!mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().containsKey(seLocation)) {
                            return;
                        }

                        SpecialEffect se = mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValue(seLocation);

                        if (!(se instanceof ExtendedSpecialEffect extendedSE)) {
                            return;
                        }

                        // 获取等级和最大等级
                        int currentLevel = data.getSpecialEffectLevel();
                        int maxLevel = extendedSE.getMaxLevel();

                        components.add(SpecialEffectTooltipHelper.createEffectLine(
                                extendedSE,
                                Component.translatable(se.getDescriptionId()),
                                Component.literal(currentLevel + "/" + maxLevel)
                        ));

                        // 添加特殊效果介绍
                        components.add(SpecialEffectTooltipHelper.createDescription(extendedSE));

                        // 空一行
                        components.add(Component.empty());

                        // 添加铭刻规则
                        components.add(
                                Component.translatable(RecastingLanguageKeys.TOOLTIP_ENGRAVING_RULE_TITLE)
                                        .withStyle(ChatFormatting.YELLOW)
                        );
                        if (Config.isUnlimitedSeEngraving()) {
                            components.add(
                                    Component.translatable(RecastingLanguageKeys.TOOLTIP_ENGRAVING_RULE_MAIN_UNLIMITED)
                                            .withStyle(ChatFormatting.GRAY)
                            );
                            components.add(
                                    Component.translatable(RecastingLanguageKeys.TOOLTIP_ENGRAVING_RULE_UNLIMITED_HINT)
                                            .withStyle(ChatFormatting.DARK_GRAY)
                            );
                        } else {
                            components.add(
                                    Component.translatable(
                                            RecastingLanguageKeys.TOOLTIP_ENGRAVING_RULE_MAIN,
                                            Config.getNormalSeEngravingLimit(),
                                            Config.getSpecialSeEngravingLimit()
                                    ).withStyle(ChatFormatting.GRAY)
                            );
                        }
                        if (extendedSE.isSpecial()) {
                            components.add(
                                    Component.translatable(RecastingLanguageKeys.TOOLTIP_SPECIAL_SE_EXTRACT)
                                            .withStyle(ChatFormatting.GRAY)
                            );
                        } else {
                            components.add(
                                    Component.translatable(RecastingLanguageKeys.TOOLTIP_ENGRAVING_RULE_UPGRADE)
                                            .withStyle(ChatFormatting.GRAY)
                            );
                            components.add(
                                    Component.translatable(RecastingLanguageKeys.TOOLTIP_ENGRAVING_RULE_ERASE)
                                            .withStyle(ChatFormatting.GRAY)
                            );
                        }
                    });
                }

                @Override
                public @Nullable Gradient getGradient(ItemStack itemStack, int level) {
                    ISpecialEffectCrystalData iSpecialEffectCrystalData = itemStack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).orElse(null);

                    if (iSpecialEffectCrystalData == null) {
                        return invalid;
                    }

                    return iSpecialEffectCrystalData.getSpecialEffectLevel() <= 0
                            ? invalid
                            : effective;

                }
            }
    );

    // 聚散变体
    public static final RegistryObject<Item> GATHERING_PARTING_VARIANT = ITEMS.register("gathering_parting_variant", () ->
            new ProudSoulItem(new Item.Properties(), null, 16.f)
    );

    // 升格变体
    public static final RegistryObject<Item> UPGRADE_VARIANT = ITEMS.register("upgrade_variant", () ->
            new ProudSoulItem(new Item.Properties(), null, 16.f)
    );

    // 升格变体 II：金色
    public static final RegistryObject<Item> UPGRADE_VARIANT_2 = ITEMS.register("upgrade_variant_2", () ->
            new ProudSoulItem(new Item.Properties(), createUpgradeVariant2Gradient(), 16.f)
    );

    // 升格变体 III：紫色
    public static final RegistryObject<Item> UPGRADE_VARIANT_3 = ITEMS.register("upgrade_variant_3", () ->
            new ProudSoulItem(new Item.Properties(), createUpgradeVariant3Gradient(), 16.f)
    );

    // 升格变体 IV：白色/彩虹
    public static final RegistryObject<Item> UPGRADE_VARIANT_4 = ITEMS.register("upgrade_variant_4", () ->
            new ProudSoulItem(new Item.Properties(), createUpgradeVariant4Gradient(), 16.f)
    );

    // ========== 庸魂立方体 ==========
    // 银灰庸魂立方体
    public static final RegistryObject<Item> IRON_MEDIUM_SOUL_CUBE = ITEMS.register("iron_medium_soul_cube", () ->
            new ProudSoulItem(new Item.Properties(), createIronMediumSoulCubeGradient(), 16.0f));

    // 金黄庸魂立方体
    public static final RegistryObject<Item> GOLD_MEDIUM_SOUL_CUBE = ITEMS.register("gold_medium_soul_cube", () ->
            new ProudSoulItem(new Item.Properties(), createGoldMediumSoulCubeGradient(), 16.0f));

    // 古铜庸魂立方体
    public static final RegistryObject<Item> COPPER_MEDIUM_SOUL_CUBE = ITEMS.register("copper_medium_soul_cube", () ->
            new ProudSoulItem(new Item.Properties(), createCopperMediumSoulCubeGradient(), 16.0f));

    // 天蓝庸魂立方体
    public static final RegistryObject<Item> DIAMOND_MEDIUM_SOUL_CUBE = ITEMS.register("diamond_medium_soul_cube", () ->
            new ProudSoulItem(new Item.Properties(), createDiamondMediumSoulCubeGradient(), 16.0f));

    // 翠绿庸魂立方体
    public static final RegistryObject<Item> EMERALD_MEDIUM_SOUL_CUBE = ITEMS.register("emerald_medium_soul_cube", () ->
            new ProudSoulItem(new Item.Properties(), createEmeraldMediumSoulCubeGradient(), 16.0f));

    // 漆黑庸魂立方体
    public static final RegistryObject<Item> NETHERITE_MEDIUM_SOUL_CUBE = ITEMS.register("netherite_medium_soul_cube", () ->
            new ProudSoulItem(new Item.Properties(), createNetheriteMediumSoulCubeGradient(), 16.0f));

    // 靛蓝庸魂立方体
    public static final RegistryObject<Item> LAPIS_MEDIUM_SOUL_CUBE = ITEMS.register("lapis_medium_soul_cube", () ->
            new ProudSoulItem(new Item.Properties(), createLapisMediumSoulCubeGradient(), 16.0f));

    // 赤红庸魂立方体
    public static final RegistryObject<Item> REDSTONE_MEDIUM_SOUL_CUBE = ITEMS.register("redstone_medium_soul_cube", () ->
            new ProudSoulItem(new Item.Properties(), createRedstoneMediumSoulCubeGradient(), 16.0f));


    /**
     * 创建执念火渐变：橘红色，焰心处有苍白闪烁
     */
    private static Gradient createObsessionFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.8f, 0.3f, 0.0f, 1.0f, 0.0f));  // 暗橘红
        colors.add(new Gradient.ColorKey(1.0f, 0.5f, 0.1f, 1.0f, 0.3f));  // 橘红
        colors.add(new Gradient.ColorKey(0.95f, 0.95f, 0.9f, 1.0f, 0.5f)); // 苍白闪烁
        colors.add(new Gradient.ColorKey(1.0f, 0.5f, 0.1f, 1.0f, 0.7f));  // 橘红
        colors.add(new Gradient.ColorKey(0.8f, 0.3f, 0.0f, 1.0f, 1.0f)); // 暗橘红
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建记忆火渐变：半透明的琉璃色，焰苗中浮动着朦胧的光影
     */
    private static Gradient createMemoryFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.3f, 0.7f, 0.8f, 0.7f, 0.0f));  // 暗琉璃色（半透明）
        colors.add(new Gradient.ColorKey(0.4f, 0.8f, 0.9f, 0.8f, 0.4f));  // 琉璃色
        colors.add(new Gradient.ColorKey(0.5f, 0.9f, 1.0f, 0.9f, 0.6f));  // 亮琉璃色（朦胧光影）
        colors.add(new Gradient.ColorKey(0.4f, 0.8f, 0.9f, 0.8f, 0.8f));  // 琉璃色
        colors.add(new Gradient.ColorKey(0.3f, 0.7f, 0.8f, 0.7f, 1.0f));  // 暗琉璃色
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建罪孽火渐变：污浊的暗红色，带有不祥的黑色脉纹
     */
    private static Gradient createSinFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.2f, 0.0f, 0.0f, 1.0f, 0.0f));  // 暗红
        colors.add(new Gradient.ColorKey(0.4f, 0.05f, 0.05f, 1.0f, 0.3f)); // 污浊红
        colors.add(new Gradient.ColorKey(0.1f, 0.0f, 0.0f, 1.0f, 0.5f));  // 黑色脉纹
        colors.add(new Gradient.ColorKey(0.4f, 0.05f, 0.05f, 1.0f, 0.7f)); // 污浊红
        colors.add(new Gradient.ColorKey(0.2f, 0.0f, 0.0f, 1.0f, 1.0f));  // 暗红
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建圣愿火渐变：纯净的乳白色光焰，边缘环绕着淡淡的金色光晕
     */
    private static Gradient createHolyFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.95f, 0.95f, 0.9f, 1.0f, 0.0f)); // 乳白
        colors.add(new Gradient.ColorKey(1.0f, 1.0f, 0.98f, 1.0f, 0.4f)); // 亮乳白
        colors.add(new Gradient.ColorKey(1.0f, 0.95f, 0.7f, 1.0f, 0.6f)); // 金色光晕
        colors.add(new Gradient.ColorKey(1.0f, 1.0f, 0.98f, 1.0f, 0.8f)); // 亮乳白
        colors.add(new Gradient.ColorKey(0.95f, 0.95f, 0.9f, 1.0f, 1.0f)); // 乳白
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建彼岸火渐变：冰冷的青白色，摇曳如烛
     */
    private static Gradient createOtherShoreFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.7f, 0.85f, 0.95f, 1.0f, 0.0f)); // 青白
        colors.add(new Gradient.ColorKey(0.8f, 0.9f, 1.0f, 1.0f, 0.5f));  // 亮青白
        colors.add(new Gradient.ColorKey(0.7f, 0.85f, 0.95f, 1.0f, 1.0f)); // 青白
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建诗烬火渐变：朦胧的月白色，焰心跃动着如文字般的淡金符文
     */
    private static Gradient createPoetryAshFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.9f, 0.9f, 0.95f, 1.0f, 0.0f));  // 月白
        colors.add(new Gradient.ColorKey(0.95f, 0.95f, 1.0f, 1.0f, 0.4f)); // 亮月白
        colors.add(new Gradient.ColorKey(1.0f, 0.95f, 0.8f, 1.0f, 0.6f));  // 淡金符文
        colors.add(new Gradient.ColorKey(0.95f, 0.95f, 1.0f, 1.0f, 0.8f)); // 亮月白
        colors.add(new Gradient.ColorKey(0.9f, 0.9f, 0.95f, 1.0f, 1.0f));  // 月白
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建匠魂火渐变：沉稳的铜黄色，时有金属光泽闪过
     */
    private static Gradient createCraftsmanFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.6f, 0.4f, 0.2f, 1.0f, 0.0f));   // 暗铜黄
        colors.add(new Gradient.ColorKey(0.8f, 0.6f, 0.3f, 1.0f, 0.4f));   // 铜黄
        colors.add(new Gradient.ColorKey(0.95f, 0.85f, 0.6f, 1.0f, 0.6f));  // 金属光泽
        colors.add(new Gradient.ColorKey(0.8f, 0.6f, 0.3f, 1.0f, 0.8f));   // 铜黄
        colors.add(new Gradient.ColorKey(0.6f, 0.4f, 0.2f, 1.0f, 1.0f));   // 暗铜黄
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建冰核火渐变：外层是炽热的亮蓝色，内核却是深邃的、仿佛能吸收光线的绝对暗蓝
     */
    private static Gradient createIceCoreFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.0f, 0.0f, 0.05f, 1.0f, 0.0f));  // 绝对暗蓝（内核）
        colors.add(new Gradient.ColorKey(0.2f, 0.4f, 0.6f, 1.0f, 0.3f));   // 暗蓝
        colors.add(new Gradient.ColorKey(0.4f, 0.7f, 1.0f, 1.0f, 0.5f));   // 亮蓝（外层）
        colors.add(new Gradient.ColorKey(0.2f, 0.4f, 0.6f, 1.0f, 0.7f));   // 暗蓝
        colors.add(new Gradient.ColorKey(0.0f, 0.0f, 0.05f, 1.0f, 1.0f));  // 绝对暗蓝
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建因果火渐变：透明到银红交织
     */
    private static Gradient createKarmaFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.8f, 0.8f, 0.8f, 0.3f, 0.0f));  // 半透明银
        colors.add(new Gradient.ColorKey(0.9f, 0.9f, 0.95f, 0.6f, 0.4f));  // 银线
        colors.add(new Gradient.ColorKey(0.8f, 0.2f, 0.2f, 0.8f, 0.6f));   // 血线
        colors.add(new Gradient.ColorKey(0.9f, 0.9f, 0.95f, 0.6f, 0.8f));  // 银线
        colors.add(new Gradient.ColorKey(0.8f, 0.8f, 0.8f, 0.3f, 1.0f));  // 半透明银
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建摇篮火渐变：柔和的鹅黄色，光芒如同最安稳的烛光
     */
    private static Gradient createCradleFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.95f, 0.9f, 0.7f, 1.0f, 0.0f));  // 鹅黄
        colors.add(new Gradient.ColorKey(1.0f, 0.95f, 0.8f, 1.0f, 0.5f));  // 亮鹅黄
        colors.add(new Gradient.ColorKey(0.95f, 0.9f, 0.7f, 1.0f, 1.0f));  // 鹅黄
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建渊寂火渐变：纯粹的哑光黑色，吞噬周围光线
     */
    private static Gradient createAbyssFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.0f, 0.0f, 0.0f, 1.0f, 0.0f));   // 纯黑
        colors.add(new Gradient.ColorKey(0.05f, 0.05f, 0.05f, 1.0f, 0.5f)); // 极暗灰
        colors.add(new Gradient.ColorKey(0.0f, 0.0f, 0.0f, 1.0f, 1.0f));   // 纯黑
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建王权火渐变：威严的暗金色与深紫色交织
     */
    private static Gradient createRoyalFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.4f, 0.3f, 0.1f, 1.0f, 0.0f));   // 暗金
        colors.add(new Gradient.ColorKey(0.6f, 0.5f, 0.2f, 1.0f, 0.3f));   // 金
        colors.add(new Gradient.ColorKey(0.3f, 0.1f, 0.4f, 1.0f, 0.5f));   // 深紫
        colors.add(new Gradient.ColorKey(0.6f, 0.5f, 0.2f, 1.0f, 0.7f));   // 金
        colors.add(new Gradient.ColorKey(0.4f, 0.3f, 0.1f, 1.0f, 1.0f));   // 暗金
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建衔尾火渐变：颜色在银灰与暗蓝间循环
     */
    private static Gradient createOuroborosFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.5f, 0.5f, 0.6f, 1.0f, 0.0f));   // 银灰
        colors.add(new Gradient.ColorKey(0.3f, 0.3f, 0.5f, 1.0f, 0.5f));   // 暗蓝
        colors.add(new Gradient.ColorKey(0.5f, 0.5f, 0.6f, 1.0f, 1.0f));   // 银灰
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建镜生火渐变：如水银般高度反光的镜面之色
     */
    private static Gradient createMirrorFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.7f, 0.7f, 0.75f, 1.0f, 0.0f));  // 水银色
        colors.add(new Gradient.ColorKey(0.9f, 0.9f, 0.95f, 1.0f, 0.4f));  // 亮水银
        colors.add(new Gradient.ColorKey(1.0f, 1.0f, 1.0f, 1.0f, 0.6f));   // 镜面反光
        colors.add(new Gradient.ColorKey(0.9f, 0.9f, 0.95f, 1.0f, 0.8f));  // 亮水银
        colors.add(new Gradient.ColorKey(0.7f, 0.7f, 0.75f, 1.0f, 1.0f));  // 水银色
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建遗言火渐变：琥珀金色，焰心深处封存着一点即将消散的星芒
     */
    private static Gradient createLastWordsFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.8f, 0.6f, 0.3f, 1.0f, 0.0f));   // 琥珀金
        colors.add(new Gradient.ColorKey(0.9f, 0.7f, 0.4f, 1.0f, 0.4f));   // 亮琥珀金
        colors.add(new Gradient.ColorKey(1.0f, 0.95f, 0.8f, 1.0f, 0.6f));  // 星芒（即将消散）
        colors.add(new Gradient.ColorKey(0.9f, 0.7f, 0.4f, 1.0f, 0.8f));   // 亮琥珀金
        colors.add(new Gradient.ColorKey(0.8f, 0.6f, 0.3f, 1.0f, 1.0f));   // 琥珀金
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建潮汐火渐变：从新月时的深紫，到满月时如海浪泡沫般的银白
     */
    private static Gradient createTideFlameGradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.2f, 0.1f, 0.3f, 1.0f, 0.0f));   // 深紫（新月）
        colors.add(new Gradient.ColorKey(0.4f, 0.3f, 0.5f, 1.0f, 0.25f));  // 中紫
        colors.add(new Gradient.ColorKey(0.6f, 0.6f, 0.7f, 1.0f, 0.5f));    // 淡紫
        colors.add(new Gradient.ColorKey(0.85f, 0.9f, 0.95f, 1.0f, 0.75f)); // 银白（满月）
        colors.add(new Gradient.ColorKey(0.2f, 0.1f, 0.3f, 1.0f, 1.0f));   // 深紫（新月）
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建银灰庸魂立方体渐变：银灰色
     */
    private static Gradient createIronMediumSoulCubeGradient() {
        return Gradient.createFromColor(0xD8D8D8);
    }

    /**
     * 创建金黄庸魂立方体渐变：金黄色
     */
    private static Gradient createGoldMediumSoulCubeGradient() {
        return Gradient.createFromColor(0xFFD700);
    }

    /**
     * 创建古铜庸魂立方体渐变：古铜色
     */
    private static Gradient createCopperMediumSoulCubeGradient() {
        return Gradient.createFromColor(0xB87333);
    }

    /**
     * 创建天蓝庸魂立方体渐变：天蓝色
     */
    private static Gradient createDiamondMediumSoulCubeGradient() {
        return Gradient.createFromColor(0x00FFFF);
    }

    /**
     * 创建翠绿庸魂立方体渐变：翠绿色
     */
    private static Gradient createEmeraldMediumSoulCubeGradient() {
        return Gradient.createFromColor(0x00FF00);
    }

    /**
     * 创建漆黑庸魂立方体渐变：漆黑色
     */
    private static Gradient createNetheriteMediumSoulCubeGradient() {
        return Gradient.createFromColor(0x1C1C1C);
    }

    /**
     * 创建靛蓝庸魂立方体渐变：靛蓝色
     */
    private static Gradient createLapisMediumSoulCubeGradient() {
        return Gradient.createFromColor(0x4166F5);
    }

    /**
     * 创建赤红庸魂立方体渐变：赤红色
     */
    private static Gradient createRedstoneMediumSoulCubeGradient() {
        return Gradient.createFromColor(0xFF0000);
    }

    /**
     * 创建升格变体 II 渐变：金色
     */
    private static Gradient createUpgradeVariant2Gradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.6f, 0.4f, 0.1f, 1.0f, 0.0f));   // 暗金色
        colors.add(new Gradient.ColorKey(0.9f, 0.7f, 0.2f, 1.0f, 0.4f));   // 金色
        colors.add(new Gradient.ColorKey(1.0f, 0.9f, 0.5f, 1.0f, 0.6f));  // 亮金色
        colors.add(new Gradient.ColorKey(0.9f, 0.7f, 0.2f, 1.0f, 0.8f));   // 金色
        colors.add(new Gradient.ColorKey(0.6f, 0.4f, 0.1f, 1.0f, 1.0f));   // 暗金色
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建升格变体 III 渐变：紫色
     */
    private static Gradient createUpgradeVariant3Gradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.3f, 0.1f, 0.4f, 1.0f, 0.0f));   // 暗紫色
        colors.add(new Gradient.ColorKey(0.5f, 0.2f, 0.6f, 1.0f, 0.4f));   // 紫色
        colors.add(new Gradient.ColorKey(0.7f, 0.4f, 0.9f, 1.0f, 0.6f));  // 亮紫色
        colors.add(new Gradient.ColorKey(0.5f, 0.2f, 0.6f, 1.0f, 0.8f));   // 紫色
        colors.add(new Gradient.ColorKey(0.3f, 0.1f, 0.4f, 1.0f, 1.0f));   // 暗紫色
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 创建升格变体 IV 渐变：白色/彩虹（最高级别）
     */
    private static Gradient createUpgradeVariant4Gradient() {
        List<Gradient.ColorKey> colors = new ArrayList<>();
        colors.add(new Gradient.ColorKey(0.9f, 0.9f, 1.0f, 1.0f, 0.0f));   // 淡蓝白
        colors.add(new Gradient.ColorKey(1.0f, 0.8f, 0.8f, 1.0f, 0.2f));   // 淡粉
        colors.add(new Gradient.ColorKey(1.0f, 1.0f, 0.9f, 1.0f, 0.4f));  // 淡黄
        colors.add(new Gradient.ColorKey(0.8f, 1.0f, 0.8f, 1.0f, 0.6f));   // 淡绿
        colors.add(new Gradient.ColorKey(0.8f, 0.8f, 1.0f, 1.0f, 0.8f));   // 淡蓝
        colors.add(new Gradient.ColorKey(0.9f, 0.9f, 1.0f, 1.0f, 1.0f));   // 淡蓝白
        return new Gradient(colors, null, Gradient.Mode.BLEND);
    }

    /**
     * 获取所有注册的物品列表
     */
    public static List<RegistryObject<Item>> getAllItems() {
        return Arrays.asList(
                OBSESSION_FLAME,
                MEMORY_FLAME,
                SIN_FLAME,
                HOLY_FLAME,
                CHAOS_FLAME,
                OTHER_SHORE_FLAME,
                POETRY_ASH_FLAME,
                MIRAGE_FLAME,
                CRAFTSMAN_FLAME,
                ICE_CORE_FLAME,
                KARMA_FLAME,
                CRADLE_FLAME,
                ABYSS_FLAME,
                ROYAL_FLAME,
                OUROBOROS_FLAME,
                MIRROR_FLAME,
                LAST_WORDS_FLAME,
                TIDE_FLAME,
                SE_CRYSTAL,
                GATHERING_PARTING_VARIANT,
                UPGRADE_VARIANT,
                UPGRADE_VARIANT_2,
                UPGRADE_VARIANT_3,
                UPGRADE_VARIANT_4,
                IRON_MEDIUM_SOUL_CUBE,
                GOLD_MEDIUM_SOUL_CUBE,
                COPPER_MEDIUM_SOUL_CUBE,
                DIAMOND_MEDIUM_SOUL_CUBE,
                EMERALD_MEDIUM_SOUL_CUBE,
                NETHERITE_MEDIUM_SOUL_CUBE,
                LAPIS_MEDIUM_SOUL_CUBE,
                REDSTONE_MEDIUM_SOUL_CUBE
        );
    }

    public static List<RegistryObject<Item>> getAllFlame() {
        return Arrays.asList(
                OBSESSION_FLAME,
                MEMORY_FLAME,
                SIN_FLAME,
                HOLY_FLAME,
                CHAOS_FLAME,
                OTHER_SHORE_FLAME,
                POETRY_ASH_FLAME,
                MIRAGE_FLAME,
                CRAFTSMAN_FLAME,
                ICE_CORE_FLAME,
                KARMA_FLAME,
                CRADLE_FLAME,
                ABYSS_FLAME,
                ROYAL_FLAME,
                OUROBOROS_FLAME,
                MIRROR_FLAME,
                LAST_WORDS_FLAME,
                TIDE_FLAME
        );
    }

    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        // 检查是否是 slashblade:slashblade 物品组
        if (!event.getTabKey().location().equals(ResourceLocation.fromNamespaceAndPath("slashblade", "slashblade"))) {
            return;
        }

        // 将所有 RecastingItems 中的物品添加到该物品组
        RecastingItems.getAllFlame().stream()
                .map(RegistryObject::get)
                .forEach(event::accept);

        event.accept(GATHERING_PARTING_VARIANT);
        event.accept(UPGRADE_VARIANT);
        event.accept(UPGRADE_VARIANT_2);
        event.accept(UPGRADE_VARIANT_3);
        event.accept(UPGRADE_VARIANT_4);

        // 添加庸魂立方体
        event.accept(IRON_MEDIUM_SOUL_CUBE);
        event.accept(GOLD_MEDIUM_SOUL_CUBE);
        event.accept(COPPER_MEDIUM_SOUL_CUBE);
        event.accept(DIAMOND_MEDIUM_SOUL_CUBE);
        event.accept(EMERALD_MEDIUM_SOUL_CUBE);
        event.accept(NETHERITE_MEDIUM_SOUL_CUBE);
        event.accept(LAPIS_MEDIUM_SOUL_CUBE);
        event.accept(REDSTONE_MEDIUM_SOUL_CUBE);

        //mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValues().stream()
        //        .filter(se -> se instanceof ExtendedSpecialEffect)
        //        .map(se -> (ExtendedSpecialEffect) se)
        //        .flatMap(se -> IntStream.range(1, se.getMaxLevel() + 1)
        //                .mapToObj(
        //                        level -> {
        //                            ItemStack itemStack = new ItemStack(SlashBladeItems.PROUDSOUL_CRYSTAL.get());
        //                            CompoundTag tag = itemStack.getOrCreateTag();
        //                            tag.putString("SpecialEffectType", Objects.requireNonNull(mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getKey(se)).toString());
        //                            tag.putInt("SpecialEffectTypeLevel", level);
        //                            return itemStack;
        //                        }
        //                )
        //        )
        //        .forEach(event::accept);


        mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getValues().stream()
                .filter(se -> se instanceof ExtendedSpecialEffect)
                .map(se -> (ExtendedSpecialEffect) se)
                .flatMap(se -> {
                    // 特殊 SE 不提供 0 级结晶（去除走铁砧渊寂火）
                    int startLevel = se.isSpecial() ? 1 : 0;
                    return IntStream.range(startLevel, se.getMaxLevel() + 1)
                            .mapToObj(
                                    level -> {
                                        ItemStack itemStack = new ItemStack(RecastingItems.SE_CRYSTAL.get());
                                        itemStack.getCapability(CapabilityRegistryHandler.SE_CRYSTAL_DATA).ifPresent(data -> {
                                            ResourceLocation seKey = Objects.requireNonNull(mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry.REGISTRY.get().getKey(se));
                                            data.setSpecialEffectType(seKey);
                                            data.setSpecialEffectLevel(level);
                                        });
                                        return itemStack;
                                    }
                            );
                })
                .forEach(event::accept);
    }
}
