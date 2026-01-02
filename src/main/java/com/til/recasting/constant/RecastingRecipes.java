package com.til.recasting.constant;

import com.til.recasting.generated.RecipeBuilderWrapper;
import com.til.recasting.mixin.RecipeProviderMixin;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.requir.SlashBladeItems;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

/**
 * Recasting 模组的配方定义常量类
 * 所有配方定义都在这里，通过反射自动读取并生成
 * 
 * 使用方式：
 * 使用 lambda 表达式定义 RecipeBuilderWrapper，配方ID会自动使用字段名转小写
 * 
 * 示例（有序合成）：
 * 字段名 PROUDSOUL_RED_RECIPE -> 配方ID: recasting:proudsoul_red_recipe
 * public static final RecipeBuilderWrapper PROUDSOUL_RED_RECIPE = (consumer, recipeId) -> 
 *     ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.PROUDSOUL_RED.get())
 *         .pattern(" R ")
 *         .pattern("RBR")
 *         .pattern(" R ")
 *         .define('R', Items.REDSTONE)
 *         .define('B', Items.BLAZE_POWDER)
 *         .unlockedBy("has_blaze_powder", RecipeProviderMixin.invokeHas(Items.BLAZE_POWDER))
 *         .save(consumer, recipeId);
 * 
 * 示例（无序合成）：
 * 字段名 OBSESSION_FLAME_RECIPE -> 配方ID: recasting:obsession_flame_recipe
 * public static final RecipeBuilderWrapper OBSESSION_FLAME_RECIPE = (consumer, recipeId) ->
 *     ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, RecastingItems.OBSESSION_FLAME.get())
 *         .requires(RecastingItems.PROUDSOUL_RED.get())
 *         .requires(Tags.Items.DYES_ORANGE)
 *         .requires(Items.BONE_MEAL)
 *         .unlockedBy("has_proudsoul_red", RecipeProviderMixin.invokeHas(RecastingItems.PROUDSOUL_RED.get()))
 *         .save(consumer, recipeId);
 */
public class RecastingRecipes {
    
    /**
     * 执念火配方：PROUDSOUL在中间，红石2个、烈焰粉2个、金粒4个
     * 橘红色，焰心处有苍白闪烁
     * 布局：
     * G B G
     * R P R
     * G B G
     * G=金粒, B=烈焰粉, R=红石, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper OBSESSION_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.OBSESSION_FLAME.get())
                    .pattern("GBG")
                    .pattern("RPR")
                    .pattern("GBG")
                    .define('G', Items.GOLD_NUGGET)
                    .define('B', Items.BLAZE_POWDER)
                    .define('R', Items.REDSTONE)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 记忆火配方：PROUDSOUL在中间，青金石 + 玻璃
     * 半透明的琉璃色，焰苗中浮动着朦胧的光影
     * 布局：
     * L G L
     * G P G
     * L G L
     * L=青金石, G=玻璃, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper MEMORY_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.MEMORY_FLAME.get())
                    .pattern("LGL")
                    .pattern("GPG")
                    .pattern("LGL")
                    .define('L', Items.LAPIS_LAZULI)
                    .define('G', Items.GLASS)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 罪孽火配方：PROUDSOUL在中间，下界砖4个、岩浆球2个、恶魂泪2个
     * 污浊的暗红色，带有不祥的黑色脉纹
     * 布局：
     * N G N
     * M P M
     * N G N
     * N=下界砖, G=恶魂泪, M=岩浆球, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper SIN_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.SIN_FLAME.get())
                    .pattern("NGN")
                    .pattern("MPM")
                    .pattern("NGN")
                    .define('N', Items.NETHER_BRICK)
                    .define('G', Items.GHAST_TEAR)
                    .define('M', Items.MAGMA_CREAM)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 圣愿火配方：PROUDSOUL在中间，萤石粉4个、羽毛4个
     * 纯净的乳白色光焰，边缘环绕着淡淡的金色光晕
     * 布局：
     * F G F
     * G P G
     * F G F
     * F=羽毛, G=萤石粉, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper HOLY_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.HOLY_FLAME.get())
                    .pattern("FGF")
                    .pattern("GPG")
                    .pattern("FGF")
                    .define('F', Items.FEATHER)
                    .define('G', Items.GLOWSTONE_DUST)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 混沌火配方：PROUDSOUL在中间，魔影珍珠2个、龙息2个
     * 色彩无定，在同一秒内可能呈现光谱上的任何颜色
     * 布局：
     *   E
     * D P D
     *   E
     * E=魔影珍珠, D=龙息, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper CHAOS_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.CHAOS_FLAME.get())
                    .pattern(" E ")
                    .pattern("DPD")
                    .pattern(" E ")
                    .define('E', Items.ENDER_PEARL)
                    .define('D', Items.DRAGON_BREATH)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 冰核火配方：PROUDSOUL在中间，雪块2个、冰2个、浮冰2个、蓝冰2个
     * 外层炽热亮蓝，内核深邃暗蓝
     * 布局沿用原彼岸火：
     * S B S
     * I P I
     * F B F
     * S=雪块, I=冰, F=浮冰, B=蓝冰, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper ICE_CORE_FLAME_RECIPE = (consumer, recipeId) ->
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
                    .save(consumer, recipeId);

    /**
     * 彼岸火配方：PROUDSOUL在中间，灵魂砂4个、恶魂之泪2个
     * 冰冷的青白色，摇曳如烛，无温度
     * 布局：
     * S S
     * G P G
     * S S
     * S=灵魂砂, G=恶魂之泪, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper OTHER_SHORE_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.OTHER_SHORE_FLAME.get())
                    .pattern(" SG")
                    .pattern("SPS")
                    .pattern("GS ")
                    .define('S', Items.SOUL_SAND)
                    .define('G', Items.GHAST_TEAR)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 因果火配方：PROUDSOUL在中间，红石2个、石英4个、线2个
     * 透明到银红交织，象征命运丝线
     * 布局：
     * Q R Q
     * S P S
     * Q R Q
     * Q=下界石英, R=红石, S=线, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper KARMA_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.KARMA_FLAME.get())
                    .pattern("QRQ")
                    .pattern("SPS")
                    .pattern("QRQ")
                    .define('Q', Items.QUARTZ)
                    .define('R', Items.REDSTONE)
                    .define('S', Items.STRING)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 摇篮火配方：PROUDSOUL在中间，蜂巢块4个、羽毛2个、干草块1个
     * 柔和的鹅黄色，如安稳的烛光
     * 布局：
     * H F H
     * F P F
     * H B H
     * H=蜂巢块, F=羽毛, B=干草块, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper CRADLE_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.CRADLE_FLAME.get())
                    .pattern("HFH")
                    .pattern("FPF")
                    .pattern("HBH")
                    .define('H', Items.HONEYCOMB_BLOCK)
                    .define('F', Items.FEATHER)
                    .define('B', Items.HAY_BLOCK)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 渊寂火配方：PROUDSOUL在中间，黑曜石4个、煤炭块2个、墨囊2个
     * 纯粹的哑光黑，吞噬光线
     * 布局：
     * O C O
     * I P I
     * O C O
     * O=黑曜石, C=煤炭块, I=墨囊, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper ABYSS_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.ABYSS_FLAME.get())
                    .pattern("OCO")
                    .pattern("IPI")
                    .pattern("OCO")
                    .define('O', Items.OBSIDIAN)
                    .define('C', Items.COAL_BLOCK)
                    .define('I', Items.INK_SAC)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 王权火配方：PROUDSOUL在中间，金锭4个、紫水晶碎片2个、青金石块1个
     * 暗金与深紫交织，宛如冠冕
     * 布局：
     * G A G
     * A P A
     * G L G
     * G=金锭, A=紫水晶碎片, L=青金石块, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper ROYAL_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.ROYAL_FLAME.get())
                    .pattern("GAG")
                    .pattern("APA")
                    .pattern("GLG")
                    .define('G', Items.GOLD_INGOT)
                    .define('A', Items.AMETHYST_SHARD)
                    .define('L', Items.LAPIS_BLOCK)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 衔尾火配方：PROUDSOUL在中间，末影之眼4个、铜锭4个
     * 自我循环的环状焰流
     * 布局：
     * E C E
     * C P C
     * E C E
     * E=末影之眼, C=铜锭, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper OUROBOROS_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.OUROBOROS_FLAME.get())
                    .pattern("ECE")
                    .pattern("CPC")
                    .pattern("ECE")
                    .define('E', Items.ENDER_EYE)
                    .define('C', Items.COPPER_INGOT)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 镜生火配方：PROUDSOUL在中间，玻璃板4个、铁锭4个
     * 如水银般反光，可映照他火
     * 布局：
     * G I G
     * I P I
     * G I G
     * G=玻璃板, I=铁锭, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper MIRROR_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.MIRROR_FLAME.get())
                    .pattern("GIG")
                    .pattern("IPI")
                    .pattern("GIG")
                    .define('G', Items.GLASS_PANE)
                    .define('I', Items.IRON_INGOT)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 遗言火配方：PROUDSOUL在中间，白色蜡烛4个、书2本、灵魂火把1个
     * 琥珀金色，封存最后的星芒
     * 布局：
     * C B C
     * B P B
     * C S C
     * C=白色蜡烛, B=书, S=灵魂火把, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper LAST_WORDS_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.LAST_WORDS_FLAME.get())
                    .pattern("CBC")
                    .pattern("BPB")
                    .pattern("CSC")
                    .define('C', Items.WHITE_CANDLE)
                    .define('B', Items.BOOK)
                    .define('S', Items.SOUL_TORCH)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 潮汐火配方：PROUDSOUL在中间，海晶碎片4个、海晶砂粒2个、鹦鹉螺壳1个
     * 随涨落变色，如潮汐月相
     * 布局：
     * S C S
     * C P C
     * S N S
     * S=海晶碎片, C=海晶砂粒, N=鹦鹉螺壳, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper TIDE_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.TIDE_FLAME.get())
                    .pattern("SCS")
                    .pattern("CPC")
                    .pattern("SNS")
                    .define('S', Items.PRISMARINE_SHARD)
                    .define('C', Items.PRISMARINE_CRYSTALS)
                    .define('N', Items.NAUTILUS_SHELL)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 诗烬火配方：PROUDSOUL在中间，任意附魔书2个
     * 朦胧的月白色，焰心跃动着如文字般的淡金符文
     * 布局：
     * B
     *   P
     * B
     * B=附魔书, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper POETRY_ASH_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.POETRY_ASH_FLAME.get())
                    .pattern("  B")
                    .pattern(" P ")
                    .pattern("B  ")
                    .define('B', Items.ENCHANTED_BOOK)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 蜃楼火配方：PROUDSOUL在中间，幻翼膜2个、紫水晶碎片2个
     * 折射的虹彩色，边缘模糊，仿佛隔着一层水汽
     * 布局：
     *   A
     * M P M
     *   A
     * M=幻翼膜, A=紫水晶碎片, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper MIRAGE_FLAME_RECIPE = (consumer, recipeId) ->
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, RecastingItems.MIRAGE_FLAME.get())
                    .pattern(" A ")
                    .pattern("MPM")
                    .pattern(" A ")
                    .define('M', Items.PHANTOM_MEMBRANE)
                    .define('A', Items.AMETHYST_SHARD)
                    .define('P', SlashBladeItems.PROUDSOUL.get())
                    .unlockedBy("has_proudsoul", RecipeProviderMixin.invokeHas(SlashBladeItems.PROUDSOUL.get()))
                    .save(consumer, recipeId);

    /**
     * 匠魂火配方：PROUDSOUL在中间，工作台1、熔炉1、酿造台1、切石机1
     * 沉稳的铜黄色，焰形规整，时有金属光泽闪过
     * 布局：
     *  C 
     * F P B
     *  S 
     * C=工作台, F=熔炉, B=酿造台, S=切石机, P=PROUDSOUL
     */
    public static final RecipeBuilderWrapper CRAFTSMAN_FLAME_RECIPE = (consumer, recipeId) ->
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
                    .save(consumer, recipeId);

}

