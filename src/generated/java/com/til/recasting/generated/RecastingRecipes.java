package com.til.recasting.generated;

import com.til.recasting.Recasting;
import com.til.recasting.mixin.RecipeProviderMixin;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.requir.SlashBladeItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RecastingRecipes extends RecipeProvider {

    public RecastingRecipes(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        /**
         * 执念火配方：PROUDSOUL在中间，红石2个、烈焰粉2个、金粒4个
         * 橘红色，焰心处有苍白闪烁
         * G=金粒, B=烈焰粉, R=红石, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.OBSESSION_FLAME.get())
                .pattern("GBG")
                .pattern("RPR")
                .pattern("GBG")
                .define('G', Items.GOLD_NUGGET)
                .define('B', Items.BLAZE_POWDER)
                .define('R', Items.REDSTONE)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("obsession_flame_recipe"));
    
        /**
         * 记忆火配方：PROUDSOUL在中间，青金石 + 玻璃
         * 半透明的琉璃色，焰苗中浮动着朦胧的光影
         * L=青金石, G=玻璃, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.MEMORY_FLAME.get())
                .pattern("LGL")
                .pattern("GPG")
                .pattern("LGL")
                .define('L', Items.LAPIS_LAZULI)
                .define('G', Items.GLASS)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("memory_flame_recipe"));
    
        /**
         * 罪孽火配方：PROUDSOUL在中间，下界砖4个、岩浆球2个、恶魂泪2个
         * 污浊的暗红色，带有不祥的黑色脉纹
         * N=下界砖, G=恶魂泪, M=岩浆球, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.SIN_FLAME.get())
                .pattern("NGN")
                .pattern("MPM")
                .pattern("NGN")
                .define('N', Items.NETHER_BRICK)
                .define('G', Items.GHAST_TEAR)
                .define('M', Items.MAGMA_CREAM)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("sin_flame_recipe"));
    
        /**
         * 圣愿火配方：PROUDSOUL在中间，萤石粉4个、羽毛4个
         * 纯净的乳白色光焰，边缘环绕着淡淡的金色光晕
         * F=羽毛, G=萤石粉, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.HOLY_FLAME.get())
                .pattern("FGF")
                .pattern("GPG")
                .pattern("FGF")
                .define('F', Items.FEATHER)
                .define('G', Items.GLOWSTONE_DUST)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("holy_flame_recipe"));
    
        /**
         * 混沌火配方：PROUDSOUL在中间，魔影珍珠2个、龙息2个
         * 色彩无定，在同一秒内可能呈现光谱上的任何颜色
         * E=魔影珍珠, D=龙息, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.CHAOS_FLAME.get())
                .pattern(" E ")
                .pattern("DPD")
                .pattern(" E ")
                .define('E', Items.ENDER_PEARL)
                .define('D', Items.DRAGON_BREATH)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("chaos_flame_recipe"));
    
        /**
         * 冰核火配方：PROUDSOUL在中间，雪块2个、冰2个、浮冰2个、蓝冰2个
         * 外层炽热亮蓝，内核深邃暗蓝
         * 布局沿用原彼岸火：
         * S=雪块, I=冰, F=浮冰, B=蓝冰, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.ICE_CORE_FLAME.get())
                .pattern("SBF")
                .pattern("IPI")
                .pattern("FBS")
                .define('S', Items.SNOW_BLOCK)
                .define('I', Items.ICE)
                .define('F', Items.PACKED_ICE)
                .define('B', Items.BLUE_ICE)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("ice_core_flame_recipe"));
    
        /**
         * 彼岸火配方：PROUDSOUL在中间，灵魂砂4个、恶魂之泪2个
         * 冰冷的青白色，摇曳如烛，无温度
         * S=灵魂砂, G=恶魂之泪, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.OTHER_SHORE_FLAME.get())
                .pattern(" SG")
                .pattern("SPS")
                .pattern("GS ")
                .define('S', Items.SOUL_SAND)
                .define('G', Items.GHAST_TEAR)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("other_shore_flame_recipe"));
    
        /**
         * 因果火配方：PROUDSOUL在中间，红石2个、石英4个、线2个
         * 透明到银红交织，象征命运丝线
         * Q=下界石英, R=红石, S=线, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.KARMA_FLAME.get())
                .pattern("QRQ")
                .pattern("SPS")
                .pattern("QRQ")
                .define('Q', Items.QUARTZ)
                .define('R', Items.REDSTONE)
                .define('S', Items.STRING)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("karma_flame_recipe"));
    
        /**
         * 摇篮火配方：PROUDSOUL在中间，蜂巢块4个、羽毛2个、干草块1个
         * 柔和的鹅黄色，如安稳的烛光
         * H=蜂巢块, F=羽毛, B=干草块, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.CRADLE_FLAME.get())
                .pattern("HFH")
                .pattern("FPF")
                .pattern("HBH")
                .define('H', Items.HONEYCOMB_BLOCK)
                .define('F', Items.FEATHER)
                .define('B', Items.HAY_BLOCK)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("cradle_flame_recipe"));
    
        /**
         * 渊寂火配方：PROUDSOUL在中间，黑曜石4个、煤炭块2个、墨囊2个
         * 纯粹的哑光黑，吞噬光线
         * O=黑曜石, C=煤炭块, I=墨囊, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.ABYSS_FLAME.get())
                .pattern("OCO")
                .pattern("IPI")
                .pattern("OCO")
                .define('O', Items.OBSIDIAN)
                .define('C', Items.COAL_BLOCK)
                .define('I', Items.INK_SAC)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("abyss_flame_recipe"));
    
        /**
         * 王权火配方：PROUDSOUL在中间，金锭4个、紫水晶碎片2个、青金石块1个
         * 暗金与深紫交织，宛如冠冕
         * G=金锭, A=紫水晶碎片, L=青金石块, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.ROYAL_FLAME.get())
                .pattern("GAG")
                .pattern("APA")
                .pattern("GLG")
                .define('G', Items.GOLD_INGOT)
                .define('A', Items.AMETHYST_SHARD)
                .define('L', Items.LAPIS_BLOCK)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("royal_flame_recipe"));
    
        /**
         * 衔尾火配方：PROUDSOUL在中间，末影之眼4个、铜锭4个
         * 自我循环的环状焰流
         * E=末影之眼, C=铜锭, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.OUROBOROS_FLAME.get())
                .pattern("ECE")
                .pattern("CPC")
                .pattern("ECE")
                .define('E', Items.ENDER_EYE)
                .define('C', Items.COPPER_INGOT)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("ouroboros_flame_recipe"));
    
        /**
         * 镜生火配方：PROUDSOUL在中间，玻璃板4个、铁锭4个
         * 如水银般反光，可映照他火
         * G=玻璃板, I=铁锭, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.MIRROR_FLAME.get())
                .pattern("GIG")
                .pattern("IPI")
                .pattern("GIG")
                .define('G', Items.GLASS_PANE)
                .define('I', Items.IRON_INGOT)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("mirror_flame_recipe"));
    
        /**
         * 遗言火配方：PROUDSOUL在中间，白色蜡烛4个、书2本、灵魂火把1个
         * 琥珀金色，封存最后的星芒
         * C=白色蜡烛, B=书, S=灵魂火把, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.LAST_WORDS_FLAME.get())
                .pattern("CBC")
                .pattern("BPB")
                .pattern("CSC")
                .define('C', Items.WHITE_CANDLE)
                .define('B', Items.BOOK)
                .define('S', Items.SOUL_TORCH)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("last_words_flame_recipe"));
    
        /**
         * 潮汐火配方：PROUDSOUL在中间，海晶碎片4个、海晶砂粒2个、鹦鹉螺壳1个
         * 随涨落变色，如潮汐月相
         * S=海晶碎片, C=海晶砂粒, N=鹦鹉螺壳, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.TIDE_FLAME.get())
                .pattern("SCS")
                .pattern("CPC")
                .pattern("SNS")
                .define('S', Items.PRISMARINE_SHARD)
                .define('C', Items.PRISMARINE_CRYSTALS)
                .define('N', Items.NAUTILUS_SHELL)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("tide_flame_recipe"));
    
        /**
         * 诗烬火配方：PROUDSOUL在中间，任意附魔书2个
         * 朦胧的月白色，焰心跃动着如文字般的淡金符文
         * B=附魔书, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.POETRY_ASH_FLAME.get())
                .pattern("  B")
                .pattern(" P ")
                .pattern("B  ")
                .define('B', Items.ENCHANTED_BOOK)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("poetry_ash_flame_recipe"));
    
        /**
         * 蜃楼火配方：PROUDSOUL在中间，幻翼膜2个、紫水晶碎片2个
         * 折射的虹彩色，边缘模糊，仿佛隔着一层水汽
         * M=幻翼膜, A=紫水晶碎片, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.MIRAGE_FLAME.get())
                .pattern(" A ")
                .pattern("MPM")
                .pattern(" A ")
                .define('M', Items.PHANTOM_MEMBRANE)
                .define('A', Items.AMETHYST_SHARD)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("mirage_flame_recipe"));
    
        /**
         * 匠魂火配方：PROUDSOUL在中间，工作台1、熔炉1、酿造台1、切石机1
         * 沉稳的铜黄色，焰形规整，时有金属光泽闪过
         * C=工作台, F=熔炉, B=酿造台, S=切石机, P=PROUDSOUL
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.CRAFTSMAN_FLAME.get())
                .pattern(" C ")
                .pattern("FPB")
                .pattern(" S ")
                .define('C', Items.CRAFTING_TABLE)
                .define('F', Items.FURNACE)
                .define('B', Items.BREWING_STAND)
                .define('S', Items.STONECUTTER)
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("craftsman_flame_recipe"));
    
        /**
         * 银灰庸魂立方体配方：中间铁块，PROUDSOUL 4个，镜生火 4个
         * P=PROUDSOUL, M=镜生火, B=铁块
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.IRON_MEDIUM_SOUL_CUBE.get())
                .pattern("PMP")
                .pattern("MBM")
                .pattern("PMP")
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .define('M', RecastingItems.MIRROR_FLAME.get())
                .define('B', Items.IRON_BLOCK)
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("iron_medium_soul_cube_recipe"));
    
        /**
         * 金黄庸魂立方体配方：中间金块，PROUDSOUL 4个，圣愿火 4个
         * P=PROUDSOUL, H=圣愿火, B=金块
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get())
                .pattern("PHP")
                .pattern("HBH")
                .pattern("PHP")
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .define('H', RecastingItems.HOLY_FLAME.get())
                .define('B', Items.GOLD_BLOCK)
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("gold_medium_soul_cube_recipe"));
    
        /**
         * 古铜庸魂立方体配方：中间铜块，PROUDSOUL 4个，匠魂火 4个
         * P=PROUDSOUL, C=匠魂火, B=铜块
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get())
                .pattern("PCP")
                .pattern("CBC")
                .pattern("PCP")
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .define('C', RecastingItems.CRAFTSMAN_FLAME.get())
                .define('B', Items.COPPER_BLOCK)
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("copper_medium_soul_cube_recipe"));
    
        /**
         * 天蓝庸魂立方体配方：中间钻石块，PROUDSOUL 4个，冰核火 4个
         * P=PROUDSOUL, I=冰核火, B=钻石块
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.DIAMOND_MEDIUM_SOUL_CUBE.get())
                .pattern("PIP")
                .pattern("IBI")
                .pattern("PIP")
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .define('I', RecastingItems.ICE_CORE_FLAME.get())
                .define('B', Items.DIAMOND_BLOCK)
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("diamond_medium_soul_cube_recipe"));
    
        /**
         * 翠绿庸魂立方体配方：中间绿宝石块，PROUDSOUL 4个，混沌火 4个
         * P=PROUDSOUL, C=混沌火, B=绿宝石块
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get())
                .pattern("PCP")
                .pattern("CBC")
                .pattern("PCP")
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .define('C', RecastingItems.CHAOS_FLAME.get())
                .define('B', Items.EMERALD_BLOCK)
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("emerald_medium_soul_cube_recipe"));
    
        /**
         * 漆黑庸魂立方体配方：中间下界合金块，PROUDSOUL 4个，渊寂火 4个
         * P=PROUDSOUL, A=渊寂火, B=下界合金块
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get())
                .pattern("PAP")
                .pattern("ABA")
                .pattern("PAP")
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .define('A', RecastingItems.ABYSS_FLAME.get())
                .define('B', Items.NETHERITE_BLOCK)
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("netherite_medium_soul_cube_recipe"));
    
        /**
         * 靛蓝庸魂立方体配方：中间青金石块，PROUDSOUL 4个，记忆火 4个
         * P=PROUDSOUL, M=记忆火, B=青金石块
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.LAPIS_MEDIUM_SOUL_CUBE.get())
                .pattern("PMP")
                .pattern("MBM")
                .pattern("PMP")
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .define('M', RecastingItems.MEMORY_FLAME.get())
                .define('B', Items.LAPIS_BLOCK)
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("lapis_medium_soul_cube_recipe"));
    
        /**
         * 赤红庸魂立方体配方：中间红石块，PROUDSOUL 4个，执念火 4个
         * P=PROUDSOUL, O=执念火, B=红石块
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get())
                .pattern("POP")
                .pattern("OBO")
                .pattern("POP")
                .define('P', SlashBladeItems.PROUDSOUL.get())
                .define('O', RecastingItems.OBSESSION_FLAME.get())
                .define('B', Items.REDSTONE_BLOCK)
                .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                .save(consumer, Recasting.prefix("redstone_medium_soul_cube_recipe"));
    
        /**
         * 聚散变体配方：EMERALD_MEDIUM_SOUL_CUBE在中间，SLASHBLADE_WHITE 4个
         * 输出4个 GATHERING_PARTING_VARIANT
         * W=SLASHBLADE_WHITE, E=EMERALD_MEDIUM_SOUL_CUBE
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.GATHERING_PARTING_VARIANT.get(), 4)
                .pattern("WWW")
                .pattern("WEW")
                .pattern("WWW")
                .define('W', SlashBladeItems.PROUDSOUL_TRAPEZOHEDRON.get())
                .define('E', RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get())
                .unlockedBy("has_slashblade_white", RecipeProviderMixin.invokeHas(SlashBladeItems.SLASHBLADE_WHITE.get()))
                .save(consumer, Recasting.prefix("gathering_parting_variant_recipe"));
    
        /**
         * 升格变体配方：下界之星在中间，REDSTONE_MEDIUM_SOUL_CUBE 4个，GATHERING_PARTING_VARIANT 4个
         * R=REDSTONE_MEDIUM_SOUL_CUBE, G=GATHERING_PARTING_VARIANT, N=下界之星
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.UPGRADE_VARIANT.get())
                .pattern("RGR")
                .pattern("GNG")
                .pattern("RGR")
                .define('R', RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get())
                .define('G', RecastingItems.GATHERING_PARTING_VARIANT.get())
                .define('N', Items.NETHER_STAR)
                .unlockedBy("has_nether_star", RecipeProviderMixin.invokeHas(Items.NETHER_STAR))
                .save(consumer, Recasting.prefix("upgrade_variant_recipe"));
    
        /**
         * 升格变体 II 配方：UPGRADE_VARIANT在中间，IRON_MEDIUM_SOUL_CUBE COPPER_MEDIUM_SOUL_CUBE 2个，POETRY_ASH_FLAME 4个
         * P=POETRY_ASH_FLAME, G=COPPER_MEDIUM_SOUL_CUBE, I=IRON_MEDIUM_SOUL_CUBE, U=UPGRADE_VARIANT
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.UPGRADE_VARIANT_2.get())
                .pattern("PCP")
                .pattern("IUI")
                .pattern("PCP")
                .define('P', RecastingItems.POETRY_ASH_FLAME.get())
                .define('C', RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get())
                .define('I', RecastingItems.IRON_MEDIUM_SOUL_CUBE.get())
                .define('U', RecastingItems.UPGRADE_VARIANT.get())
                .unlockedBy("has_upgrade_variant", RecipeProviderMixin.invokeHas(RecastingItems.UPGRADE_VARIANT.get()))
                .save(consumer, Recasting.prefix("upgrade_variant_2_recipe"));
    
        /**
         * 升格变体 III 配方：UPGRADE_VARIANT_2在中间，GOLD_MEDIUM_SOUL_CUBE 2个，COPPER_MEDIUM_SOUL_CUBE 2个，CRADLE_FLAME 4个
         * C=CRADLE_FLAME, G=GOLD_MEDIUM_SOUL_CUBE, P=COPPER_MEDIUM_SOUL_CUBE, U=UPGRADE_VARIANT_2
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.UPGRADE_VARIANT_3.get())
                .pattern("CGC")
                .pattern("PUP")
                .pattern("CGC")
                .define('C', RecastingItems.CRADLE_FLAME.get())
                .define('G', RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get())
                .define('P', RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get())
                .define('U', RecastingItems.UPGRADE_VARIANT_2.get())
                .unlockedBy("has_upgrade_variant_2", RecipeProviderMixin.invokeHas(RecastingItems.UPGRADE_VARIANT_2.get()))
                .save(consumer, Recasting.prefix("upgrade_variant_3_recipe"));
    
        /**
         * 升格变体 IV 配方：UPGRADE_VARIANT_3在中间，DIAMOND_MEDIUM_SOUL_CUBE 2个，LAPIS_MEDIUM_SOUL_CUBE 2个，ROYAL_FLAME 4个
         * R=ROYAL_FLAME, D=DIAMOND_MEDIUM_SOUL_CUBE, L=LAPIS_MEDIUM_SOUL_CUBE, U=UPGRADE_VARIANT_3
         */
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.UPGRADE_VARIANT_4.get())
                .pattern("RDR")
                .pattern("LUL")
                .pattern("RDR")
                .define('R', RecastingItems.ROYAL_FLAME.get())
                .define('D', RecastingItems.DIAMOND_MEDIUM_SOUL_CUBE.get())
                .define('L', RecastingItems.LAPIS_MEDIUM_SOUL_CUBE.get())
                .define('U', RecastingItems.UPGRADE_VARIANT_3.get())
                .unlockedBy("has_upgrade_variant_3", RecipeProviderMixin.invokeHas(RecastingItems.UPGRADE_VARIANT_3.get()))
                .save(consumer, Recasting.prefix("upgrade_variant_4_recipe"));
    
    }






























































}

