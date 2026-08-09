
package com.til.recasting.generated;

import com.til.recasting.Recasting;
import com.til.recasting.constant.RecastingSlashBladeKeys;
import com.til.recasting.mixin.RecipeProviderMixin;
import com.til.recasting.recipe.SpecialEffectCrystalIngredient;
import com.til.recasting.registry.RecastingItems;
import com.til.recasting.registry.SpecialEffectsRegistry;
import com.til.recasting.registry.requir.SlashBladeItems;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import mods.flammpfeil.slashblade.recipe.SlashBladeIngredient;
import mods.flammpfeil.slashblade.recipe.SlashBladeShapedRecipeBuilder;
import mods.flammpfeil.slashblade.registry.slashblade.EnchantmentDefinition;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SlashBladeRecipes extends RecipeProvider {

    public SlashBladeRecipes(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BROADSWORD_WOOD.location())
                            .pattern("  W")
                            .pattern(" W ")
                            .pattern("SP ")
                            .define('S', Items.STICK)
                            .define('W', Items.OAK_PLANKS)
                            .define('P', SlashBladeItems.PROUDSOUL.get())
                            .unlockedBy("has_stick", RecipeProviderMixin.invokeHas(Items.STICK))
                            .save(consumer, Recasting.prefix("broadsword_wood_recipe"));
    
        /**
         * 青锋（木）配方：基础配方，无前置刀
         * 材料：木棍2个 + 木板3个
         * S=木棍, W=木板
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.GREEN_BLADE_WOOD.location())
                            .pattern(" W ")
                            .pattern(" W ")
                            .pattern(" SP")
                            .define('S', Items.STICK)
                            .define('W', Items.OAK_PLANKS)
                            .define('P', SlashBladeItems.PROUDSOUL.get())
                            .unlockedBy("has_stick", RecipeProviderMixin.invokeHas(Items.STICK))
                            .save(consumer, Recasting.prefix("green_blade_wood_recipe"));
    
        /**
         * 阔刃（铁）配方：从阔刃（木）升级
         * 要求：杀敌>50、锻造>5
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BROADSWORD_IRON.location())
                            .pattern("  I")
                            .pattern(" I ")
                            .pattern("B  ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BROADSWORD_WOOD.location())
                                            .killCount(50)
                                            .refineCount(5)
                                            .build()))
                            .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                            .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                            .save(consumer, Recasting.prefix("broadsword_iron_recipe"));
    
        /**
         * 青锋（铁）配方：从青锋（木）升级
         * 要求：杀敌>50、锻造>5
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.GREEN_BLADE_IRON.location())
                            .pattern(" I ")
                            .pattern(" I ")
                            .pattern(" B ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.GREEN_BLADE_WOOD.location())
                                            .killCount(50)
                                            .refineCount(5)
                                            .build()))
                            .define('I', SlashBladeItems.PROUDSOUL_INGOT.get())
                            .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                            .save(consumer, Recasting.prefix("green_blade_iron_recipe"));
    
        /**
         * 闪茶配方：从阔刃（铁）升级
         * 要求：杀敌1000、锻造500、海之眷顾3附魔、饵钓2附魔
         * I=金黄庸魂立方体, B=阔刃（铁）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.SHINE_TEA.location())
                            .pattern("  I")
                            .pattern(" I ")
                            .pattern("B  ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BROADSWORD_IRON.location())
                                            .killCount(1000)
                                            .refineCount(500)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FISHING_LUCK), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FISHING_SPEED), 2))
                                            .build()))
                            .define('I', RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get())
                            .unlockedBy("has_gold_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("shine_tea_recipe"));
    
        /**
         * 闪茶 Lambda 配方：闪茶 → ^闪茶
         * 要求：杀敌2000、锻造1000、海之眷顾3附魔、饵钓3附魔、精准采集1附魔
         * I=金黄庸魂立方体, B=闪茶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.SHINE_TEA_LAMBDA.location())
                            .pattern("  I")
                            .pattern(" I ")
                            .pattern("B  ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.SHINE_TEA.location())
                                            .killCount(2000)
                                            .refineCount(1000)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FISHING_LUCK), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FISHING_SPEED), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SILK_TOUCH), 1))
                                            .build()))
                            .define('I', RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get())
                            .unlockedBy("has_gold_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("shine_tea_lambda_recipe"));
    
        /**
         * 灿茶配方：^闪茶 → 灿茶（链：闪 → ^闪 → 灿 → ^灿）
         * 要求：杀敌4000、锻造2000、海之眷顾3附魔、饵钓3附魔、精准采集1附魔、效率5附魔
         * I=照谛核心, B=闪茶 Lambda
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BRILLIANT_TEA.location())
                            .pattern("  I")
                            .pattern(" I ")
                            .pattern("B  ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.SHINE_TEA_LAMBDA.location())
                                            .killCount(4000)
                                            .refineCount(2000)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FISHING_LUCK), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FISHING_SPEED), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SILK_TOUCH), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_EFFICIENCY), 5))
                                            .build()))
                            .define('I', RecastingItems.ILLUMINATING_TRUTH_CORE.get())
                            .unlockedBy("has_illuminating_truth_core", RecipeProviderMixin.invokeHas(RecastingItems.ILLUMINATING_TRUTH_CORE.get()))
                            .save(consumer, Recasting.prefix("brilliant_tea_recipe"));
    
        /**
         * 灿茶 Lambda 配方：灿茶 → ^灿茶
         * 要求：杀敌8000、锻造4000、海之眷顾3附魔、饵钓3附魔、精准采集1附魔、效率5附魔、时运3附魔、忠诚3附魔、激流1附魔、经验修补1附魔
         * I=照谛核心, B=灿茶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BRILLIANT_TEA_LAMBDA.location())
                            .pattern("  I")
                            .pattern(" I ")
                            .pattern("B  ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BRILLIANT_TEA.location())
                                            .killCount(8000)
                                            .refineCount(4000)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FISHING_LUCK), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FISHING_SPEED), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SILK_TOUCH), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_EFFICIENCY), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_FORTUNE), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.LOYALTY), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.RIPTIDE), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MENDING), 1))
                                            .build()))
                            .define('I', RecastingItems.ILLUMINATING_TRUTH_CORE.get())
                            .unlockedBy("has_illuminating_truth_core", RecipeProviderMixin.invokeHas(RecastingItems.ILLUMINATING_TRUTH_CORE.get()))
                            .save(consumer, Recasting.prefix("brilliant_tea_lambda_recipe"));

        /**
         * 荆楚配方（t2）：从灿茶 Lambda 升级
         * 要求：杀敌5000、锻造600、荆棘3附魔、保护4附魔、时运3附魔、精准采集1附魔
         * 材料：霜璇核心4个
         * SE结晶：旋风 2个
         * C=霜璇核心, W=旋风 SE结晶, B=基础刀（灿茶 Lambda，满足要求）
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BRIARLAND.location())
                            .pattern(" WC")
                            .pattern("CBC")
                            .pattern("CW ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BRILLIANT_TEA_LAMBDA.location())
                                            .killCount(5000)
                                            .refineCount(600)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.ALL_DAMAGE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_FORTUNE), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SILK_TOUCH), 1))
                                            .build()))
                            .define('C', RecastingItems.FROST_VORTEX_CORE.get())
                            .define('W', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.WHIRLWIND.getId(), 1))
                            .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                            .save(consumer, Recasting.prefix("briarland_recipe"));

        /**
         * 荆楚 Lambda 配方（t2）：从荆楚升级
         * 要求：杀敌10000、锻造1200、荆棘3附魔、保护4附魔、时运3附魔、精准采集1附魔、效率5附魔、耐久3附魔
         * 材料：燎焰核心4个
         * SE结晶：回旋 2个
         * C=燎焰核心, S=回旋 SE结晶, B=基础刀（荆楚，满足要求）
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BRIARLAND_LAMBDA.location())
                            .pattern(" SC")
                            .pattern("CBC")
                            .pattern("CS ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BRIARLAND.location())
                                            .killCount(10000)
                                            .refineCount(1200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.ALL_DAMAGE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_FORTUNE), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SILK_TOUCH), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_EFFICIENCY), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                            .build()))
                            .define('C', RecastingItems.BLAZING_FLAME_CORE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPIRAL.getId(), 1))
                            .unlockedBy("has_blazing_flame_core", RecipeProviderMixin.invokeHas(RecastingItems.BLAZING_FLAME_CORE.get()))
                            .save(consumer, Recasting.prefix("briarland_lambda_recipe"));
    
        /**
         * 闪金配方：从青锋（铁）升级
         * 要求：杀敌1000、荣耀50000、效率4附魔、精准采集1附魔
         * I=金黄庸魂立方体, B=青锋（铁）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.SHINE_GOLD.location())
                            .pattern(" I ")
                            .pattern(" I ")
                            .pattern(" B ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.GREEN_BLADE_IRON.location())
                                            .killCount(1000)
                                            .proudSoul(50000)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_EFFICIENCY), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SILK_TOUCH), 1))
                                            .build()))
                            .define('I', RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get())
                            .unlockedBy("has_gold_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("shine_gold_recipe"));
    
        /**
         * 闪金 Lambda 配方：闪金 → ^闪金
         * 要求：杀敌2000、荣耀100000、效率5附魔、精准采集1附魔、时运1附魔
         * I=金黄庸魂立方体, B=闪金
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.SHINE_GOLD_LAMBDA.location())
                            .pattern(" I ")
                            .pattern(" I ")
                            .pattern(" B ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.SHINE_GOLD.location())
                                            .killCount(2000)
                                            .proudSoul(100000)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_EFFICIENCY), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SILK_TOUCH), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_FORTUNE), 1))
                                            .build()))
                            .define('I', RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get())
                            .unlockedBy("has_gold_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("shine_gold_lambda_recipe"));
    
        /**
         * 灿金配方：^闪金 → 灿金（链：闪 → ^闪 → 灿 → ^灿）
         * 要求：杀敌4000、荣耀200000、效率5附魔、精准采集1附魔、时运3附魔、海之眷顾2附魔
         * I=照谛核心, B=闪金 Lambda
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BRILLIANT_GOLD.location())
                            .pattern(" I ")
                            .pattern(" I ")
                            .pattern(" B ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.SHINE_GOLD_LAMBDA.location())
                                            .killCount(4000)
                                            .proudSoul(200000)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_EFFICIENCY), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SILK_TOUCH), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_FORTUNE), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FISHING_LUCK), 2))
                                            .build()))
                            .define('I', RecastingItems.ILLUMINATING_TRUTH_CORE.get())
                            .unlockedBy("has_illuminating_truth_core", RecipeProviderMixin.invokeHas(RecastingItems.ILLUMINATING_TRUTH_CORE.get()))
                            .save(consumer, Recasting.prefix("brilliant_gold_recipe"));
    
        /**
         * 灿金 Lambda 配方：灿金 → ^灿金
         * 要求：杀敌8000、荣耀400000、效率5附魔、精准采集1附魔、时运3附魔、海之眷顾3附魔、饵钓3附魔、忠诚3附魔、激流1附魔
         * I=照谛核心, B=灿金
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BRILLIANT_GOLD_LAMBDA.location())
                            .pattern(" I ")
                            .pattern(" I ")
                            .pattern(" B ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BRILLIANT_GOLD.location())
                                            .killCount(8000)
                                            .proudSoul(400000)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_EFFICIENCY), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SILK_TOUCH), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_FORTUNE), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FISHING_LUCK), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FISHING_SPEED), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.LOYALTY), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.RIPTIDE), 1))
                                            .build()))
                            .define('I', RecastingItems.ILLUMINATING_TRUTH_CORE.get())
                            .unlockedBy("has_illuminating_truth_core", RecipeProviderMixin.invokeHas(RecastingItems.ILLUMINATING_TRUTH_CORE.get()))
                            .save(consumer, Recasting.prefix("brilliant_gold_lambda_recipe"));
    
        /**
         * 碎白配方：从青锋（铁）升级
         * 要求：杀敌300、锻造30
         * 材料：执念火2个
         * O=执念火, B=基础刀（青锋铁，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BROKEN_WHITE.location())
                            .pattern("  O")
                            .pattern(" B ")
                            .pattern("O  ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.GREEN_BLADE_IRON.location())
                                            .killCount(300)
                                            .refineCount(30)
                                            .build()))
                            .define('O', RecastingItems.OBSESSION_FLAME.get())
                            .unlockedBy("has_obsession_flame", RecipeProviderMixin.invokeHas(RecastingItems.OBSESSION_FLAME.get()))
                            .save(consumer, Recasting.prefix("broken_white_recipe"));
    
        /**
         * 美工刀配方：从阔刃（铁）升级
         * 要求：杀敌50、锻造20、效率2附魔
         * 材料：匠魂火2个
         * C=匠魂火, B=基础刀（阔刃铁，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.ART_KNIFE.location())
                            .pattern("  C")

                            .pattern(" B ")
                            .pattern("C  ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BROADSWORD_IRON.location())
                                            .killCount(50)
                                            .refineCount(20)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLOCK_EFFICIENCY), 2))
                                            .build()))
                            .define('C', RecastingItems.CRAFTSMAN_FLAME.get())
                            .unlockedBy("has_craftsman_flame", RecipeProviderMixin.invokeHas(RecastingItems.CRAFTSMAN_FLAME.get()))
                            .save(consumer, Recasting.prefix("art_knife_recipe"));
    
        /**
         * 八卦剑配方：从碎白升级
         * 要求：杀敌500、力量2附魔、锋利2附魔
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BA_GUA.location())
                            .pattern("CPC")
                            .pattern("WSK")
                            .pattern("CPC")
                            .define('S',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BROKEN_WHITE.location())
                                            .killCount(500)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 2))
                                            .build()))
                            .define('P', RecastingItems.POETRY_ASH_FLAME.get())
                            .define('W', Items.WHITE_WOOL)
                            .define('K', Items.BLACK_WOOL)
                            .define('C', RecastingItems.CHAOS_FLAME.get())
                            .unlockedBy("has_poetry_ash_flame", RecipeProviderMixin.invokeHas(RecastingItems.POETRY_ASH_FLAME.get()))
                            .save(consumer, Recasting.prefix("ba_gua_recipe"));
    
        /**
         * 八卦剑大配方：从八卦剑升级
         * 要求：杀敌2000、锻造100、力量3附魔、锋利2附魔
         * 材料：诗烬火2个、古铜庸魂立方体4个
         * P=诗烬火, C=古铜庸魂立方体, B=基础刀（八卦剑，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BA_GUA_BIG.location())
                            .pattern("CPC")
                            .pattern("PBP")
                            .pattern("CPC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BA_GUA.location())
                                            .killCount(2000)
                                            .refineCount(100)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 2))
                                            .build()))
                            .define('P', RecastingItems.POETRY_ASH_FLAME.get())
                            .define('C', RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get())
                            .unlockedBy("has_copper_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("ba_gua_big_recipe"));
    
        /**
         * 八卦剑大 Lambda 配方：从八卦剑大升级
         * 要求：杀敌5000、锻造300、力量5附魔、锋利2附魔
         * 材料：古铜色的庸魂立方体6个
         * SE结晶：冲击l1 2个
         * C=古铜色的庸魂立方体, B=基础刀（八卦剑大，满足要求）, I=冲击l1 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BA_GUA_BIG_LAMBDA.location())
                            .pattern("CCC")
                            .pattern("IBI")
                            .pattern("CCC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BA_GUA_BIG.location())
                                            .killCount(5000)
                                            .refineCount(300)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 2))
                                            .build()))
                            .define('C', RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get())
                            .define('I', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IMPACT.getId(), 1))
                            .unlockedBy("has_copper_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("ba_gua_big_lambda_recipe"));
    
        /**
         * 太极配方：从八卦巨剑 Lambda 升级
         * 要求：杀敌12000、锻造800、力量5附魔、锋利5附魔
         * 材料：诗烬火2个、霜璇核心4个、白羊毛1个、黑羊毛1个
         * P=诗烬火, C=霜璇核心, W=白羊毛, K=黑羊毛, B=基础刀（八卦巨剑 Lambda，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.SUPREME_POLE.location())
                            .pattern("CPC")
                            .pattern("WBK")
                            .pattern("CPC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BA_GUA_BIG_LAMBDA.location())
                                            .killCount(12000)
                                            .refineCount(800)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                            .build()))
                            .define('P', RecastingItems.POETRY_ASH_FLAME.get())
                            .define('C', RecastingItems.FROST_VORTEX_CORE.get())
                            .define('W', Items.WHITE_WOOL)
                            .define('K', Items.BLACK_WOOL)
                            .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                            .save(consumer, Recasting.prefix("supreme_pole_recipe"));
    
        /**
         * 太极 Lambda 配方：从太极升级
         * 要求：杀敌16000、锻造1200、力量5附魔、锋利5附魔、抢夺3附魔、耐久3附魔、经验修补1附魔
         * 材料：冥渊核心6个
         * SE结晶：冲击l1 2个
         * C=冥渊核心, B=基础刀（太极，满足要求）, I=冲击l1 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.SUPREME_POLE_LAMBDA.location())
                            .pattern("CCC")
                            .pattern("IBI")
                            .pattern("CCC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.SUPREME_POLE.location())
                                            .killCount(16000)
                                            .refineCount(1200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MOB_LOOTING), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MENDING), 1))
                                            .build()))
                            .define('C', RecastingItems.ABYSS_DEPTH_CORE.get())
                            .define('I', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IMPACT.getId(), 1))
                            .unlockedBy("has_abyss_depth_core", RecipeProviderMixin.invokeHas(RecastingItems.ABYSS_DEPTH_CORE.get()))
                            .save(consumer, Recasting.prefix("supreme_pole_lambda_recipe"));
    
        /***
         * 黑刃
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BLACK.location())
                            .pattern("  S")
                            .pattern(" B ")
                            .pattern("O  ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BROADSWORD_IRON.location())
                                            .killCount(200)
                                            .refineCount(20)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                            .build()))
                            .define('O', Items.OBSIDIAN)
                            .define('S', RecastingItems.SIN_FLAME.get())
                            .unlockedBy("has_sin_flame", RecipeProviderMixin.invokeHas(RecastingItems.SIN_FLAME.get()))
                            .save(consumer, Recasting.prefix("black_recipe"));
    
        /**
         * 伞配方：从黑刃升级
         * 要求：杀敌500、锻造100、锋利3附魔、击退2附魔
         * 材料：漆黑的庸魂立方体2个
         * SE结晶：斩断l1 2个
         * C=漆黑的庸魂立方体, B=基础刀（黑刃，满足要求）, S=斩断l1 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.UMBRELLA.location())
                            .pattern("  S")
                            .pattern("CBC")
                            .pattern("S  ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BLACK.location())
                                            .killCount(500)
                                            .refineCount(100)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.KNOCKBACK), 2))
                                            .build()))
                            .define('C', RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SEVER_BREAK.getId(), 1))
                            .unlockedBy("has_netherite_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("umbrella_recipe"));
    
        /**
         * 伞 Lambda 配方：从伞升级
         * 要求：锋利5附魔、击退2附魔
         * 材料：漆黑的庸魂立方体4个
         * SE结晶：风暴l1 2个、风暴变体l1 2个
         * C=漆黑的庸魂立方体, B=基础刀（伞，满足要求）, S=风暴l1 SE结晶, V=风暴变体l1 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.UMBRELLA_LAMBDA.location())
                            .pattern("CSC")
                            .pattern("VBV")
                            .pattern("CSC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.UMBRELLA.location())
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.KNOCKBACK), 2))
                                            .build()))
                            .define('C', RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM.getId(), 1))
                            .define('V', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM_VARIANT.getId(), 1))
                            .unlockedBy("has_netherite_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("umbrella_lambda_recipe"));

        /**
         * 鬼切配方（t2）：从伞 Lambda 升级
         * 要求：杀敌5000、锻造600、锋利5附魔、击退2附魔、横扫之刃3附魔、爆炸保护2附魔
         * 材料：霜璇核心4个
         * SE结晶：震荡 2个
         * C=霜璇核心, S=震荡 SE结晶, B=基础刀（伞 Lambda，满足要求）
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.ONIKIRI.location())
                            .pattern(" SC")
                            .pattern("CBC")
                            .pattern("CS ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.UMBRELLA_LAMBDA.location())
                                            .killCount(5000)
                                            .refineCount(600)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.KNOCKBACK), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SWEEPING_EDGE), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLAST_PROTECTION), 2))
                                            .build()))
                            .define('C', RecastingItems.FROST_VORTEX_CORE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHOCK.getId(), 1))
                            .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                            .save(consumer, Recasting.prefix("onikiri_recipe"));

        /**
         * 鬼切 Lambda 配方（t2）：从鬼切升级
         * 要求：杀敌10000、锻造1200、锋利5附魔、击退2附魔、横扫之刃3附魔、爆炸保护4附魔、亡灵杀手5附魔
         * 材料：燎焰核心4个
         * SE结晶：过载 2个
         * C=燎焰核心, O=过载 SE结晶, B=基础刀（鬼切，满足要求）
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.ONIKIRI_LAMBDA.location())
                            .pattern(" OC")
                            .pattern("CBC")
                            .pattern("CO ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.ONIKIRI.location())
                                            .killCount(10000)
                                            .refineCount(1200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.KNOCKBACK), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SWEEPING_EDGE), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLAST_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 5))
                                            .build()))
                            .define('C', RecastingItems.BLAZING_FLAME_CORE.get())
                            .define('O', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.OVERLOAD.getId(), 1))
                            .unlockedBy("has_blazing_flame_core", RecipeProviderMixin.invokeHas(RecastingItems.BLAZING_FLAME_CORE.get()))
                            .save(consumer, Recasting.prefix("onikiri_lambda_recipe"));
    
        /**
         * 青云配方：从碎白升级
         * 要求：杀敌500、锻造20、摔落保护4附魔
         * 材料：翠绿的庸魂立方体2个
         * SE结晶：协同l1、十字斩l1
         * E=翠绿的庸魂立方体, B=基础刀（碎白，满足要求）, C=协同l1 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BLUE_CLOUD.location())
                            .pattern(" E ")
                            .pattern("CBC")
                            .pattern(" E ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BROKEN_WHITE.location())
                                            .killCount(500)
                                            .refineCount(20)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FALL_PROTECTION), 4))
                                            .build()))
                            .define('E', RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get())
                            .define('C', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.COOPERATE_WITH.getId(), 1))
                            .unlockedBy("has_emerald_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("blue_cloud_recipe"));
    
        /**
         * 青云 Lambda 配方：从青云升级
         * 要求：杀敌750、锻造125、摔落保护4附魔、深海探索者3附魔
         * 材料：翠绿的庸魂立方体6个
         * SE结晶：十字斩l1 2个
         * E=翠绿的庸魂立方体, B=基础刀（青云，满足要求）, X=十字斩l1 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.BLUE_CLOUD_LAMBDA.location())
                            .pattern("EEE")
                            .pattern("XBX")
                            .pattern("EEE")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BLUE_CLOUD.location())
                                            .killCount(750)
                                            .refineCount(125)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FALL_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.DEPTH_STRIDER), 3))
                                            .build()))
                            .define('E', RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get())
                            .define('X', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.CROSS_CHOP.getId(), 1))
                            .unlockedBy("has_emerald_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("blue_cloud_lambda_recipe"));
    
        /**
         * 冰薄荷配方：从青云 Lambda 升级
         * 要求：杀敌1000、锻造300、摔落保护4附魔、深海探索者3附魔、灵魂疾行3附魔、水下速掘1附魔
         * 材料：霜璇核心4个
         * SE结晶：生长l2、回溯l2
         * D=霜璇核心, B=基础刀（青云 Lambda，满足要求）, G=生长l2 SE结晶, R=回溯l2 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.COOL_MINT.location())
                            .pattern(" DR")
                            .pattern("DBD")
                            .pattern("GD ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BLUE_CLOUD_LAMBDA.location())
                                            .killCount(1000)
                                            .refineCount(300)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FALL_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.DEPTH_STRIDER), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SOUL_SPEED), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.AQUA_AFFINITY), 1))
                                            .build()))
                            .define('D', RecastingItems.FROST_VORTEX_CORE.get())
                            .define('G', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.GROWTH.getId(), 1))
                            .define('R', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1))
                            .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                            .save(consumer, Recasting.prefix("cool_mint_recipe"));
    
        /**
         * 冰薄荷 Lambda 配方：从冰薄荷升级
         * 要求：杀敌200、锻造500、摔落保护4附魔、深海探索者3附魔、灵魂疾行3附魔、水下速掘1附魔、迅捷潜行3附魔、耐久3附魔、经验修补1附魔、引雷1附魔
         * 材料：霜璇核心4个
         * SE结晶：剑气释放l2 2个
         * D=霜璇核心, B=基础刀（冰薄荷，满足要求）, C=剑气释放l2 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.COOL_MINT_LAMBDA.location())
                            .pattern(" DC")
                            .pattern("DBD")
                            .pattern("CD ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.COOL_MINT.location())
                                            .killCount(200)
                                            .refineCount(500)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FALL_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.DEPTH_STRIDER), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SOUL_SPEED), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.AQUA_AFFINITY), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SWIFT_SNEAK), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MENDING), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.CHANNELING), 1))
                                            .build()))
                            .define('D', RecastingItems.FROST_VORTEX_CORE.get())
                            .define('C', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.DRIVE_RELEASE.getId(), 1))
                            .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                            .save(consumer, Recasting.prefix("cool_mint_lambda_recipe"));
    
        /**
         * 惊鸿配方：从冰薄荷 Lambda 升级。
         * 要求：杀敌1000、锻造1000、摔落保护4附魔、深海探索者3附魔、灵魂疾行3附魔
         * 材料：霜璇核心4个
         * SE结晶：审判 2个
         * C=霜璇核心, J=审判 SE结晶, B=基础刀（冰薄荷 Lambda，满足要求）
         */
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.STARTLED_SWAN.location())
                .pattern("C C")
                .pattern("JBJ")
                .pattern("C C")
                .define('B',
                        SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                .name(RecastingSlashBladeKeys.COOL_MINT_LAMBDA.location())
                                .killCount(1000)
                                .refineCount(1000)
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FALL_PROTECTION), 4))
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.DEPTH_STRIDER), 3))
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SOUL_SPEED), 3))
                                .build()))
                .define('C', RecastingItems.FROST_VORTEX_CORE.get())
                .define('J', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.JUDGEMENT.getId(), 1))
                .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                .save(consumer, Recasting.prefix("startled_swan_recipe"));
    
        /**
         * 惊鸿 Lambda 配方：从惊鸿升级。
         * 要求：杀敌2000、锻造2000、摔落保护4附魔、深海探索者3附魔、灵魂疾行3附魔、迅捷潜行3附魔、耐久3附魔、经验修补1附魔、多重射击1附魔
         * 材料：霜璇核心4个、末影之眼2个
         * SE结晶：斩断 2个
         * C=霜璇核心, I=末影之眼, S=斩断 SE结晶, B=基础刀（惊鸿，满足要求）
         */
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.STARTLED_SWAN_LAMBDA.location())
                .pattern("CIC")
                .pattern("SBS")
                .pattern("CIC")
                .define('B',
                        SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                .name(RecastingSlashBladeKeys.STARTLED_SWAN.location())
                                .killCount(2000)
                                .refineCount(2000)
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FALL_PROTECTION), 4))
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.DEPTH_STRIDER), 3))
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SOUL_SPEED), 3))
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SWIFT_SNEAK), 3))
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MENDING), 1))
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MULTISHOT), 1))
                                .build()))
                .define('C', RecastingItems.FROST_VORTEX_CORE.get())
                .define('I', Items.ENDER_EYE)
                .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SEVER_BREAK.getId(), 1))
                .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                .save(consumer, Recasting.prefix("startled_swan_lambda_recipe"));
    
        /**
         * 龙鳞配方：从八卦剑升级
         * 要求：杀敌300、锻造50、火焰保护4附魔、火焰附加1附魔
         * 材料：古铜庸魂立方体2个
         * SE结晶：分裂l1 2个
         * C=古铜庸魂立方体, B=基础刀（八卦剑，满足要求）, I=分裂l1 SE结晶
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.DRAGON_SCALE.location())
                            .pattern("C I")
                            .pattern(" B ")
                            .pattern("I C")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BA_GUA.location())
                                            .killCount(300)
                                            .refineCount(50)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_ASPECT), 1))
                                            .build()))
                            .define('C', RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get())
                            .define('I', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPLIT.getId(), 1))
                            .unlockedBy("has_copper_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("dragon_scale_recipe"));
    
        /**
         * 龙鳞 Lambda 配方：从龙鳞升级
         * 要求：杀敌500、锻造200、火焰保护4附魔、火焰附加2附魔、火矢1附魔
         * 材料：古铜色的庸魂立方体6个
         * SE结晶：冲击l1 2个
         * C=古铜色的庸魂立方体, B=基础刀（龙鳞，满足要求）, I=冲击l1 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.DRAGON_SCALE_LAMBDA.location())
                            .pattern("CCC")
                            .pattern("IBI")
                            .pattern("CCC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.DRAGON_SCALE.location())
                                            .killCount(500)

                                            .refineCount(200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_ASPECT), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FLAMING_ARROWS), 1))
                                            .build()))
                            .define('C', RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get())
                            .define('I', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IMPACT.getId(), 1))
                            .unlockedBy("has_copper_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.COPPER_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("dragon_scale_lambda_recipe"));
    
        /**
         * 龙配方：从龙鳞 Lambda 升级
         * 要求：杀敌1000、锻造300、火焰保护4附魔、火焰附加2附魔、火矢1附魔、爆炸保护4附魔
         * 材料：燎焰核心4个
         * SE结晶：破片l2 4个
         * C=燎焰核心, B=基础刀（龙鳞 Lambda，满足要求）, F=破片l2 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.DRAGON.location())
                            .pattern("CFC")
                            .pattern("FBF")
                            .pattern("CFC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.DRAGON_SCALE_LAMBDA.location())
                                            .killCount(1000)
                                            .refineCount(300)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_ASPECT), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FLAMING_ARROWS), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLAST_PROTECTION), 4))
                                            .build()))
                            .define('C', RecastingItems.BLAZING_FLAME_CORE.get())
                            .define('F', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.FRAGMENT.getId(), 1))
                            .unlockedBy("has_blazing_flame_core", RecipeProviderMixin.invokeHas(RecastingItems.BLAZING_FLAME_CORE.get()))
                            .save(consumer, Recasting.prefix("dragon_recipe"));
    
        /**
         * 龙 Lambda 配方：从龙升级
         * 要求：杀敌2000、锻造500、火焰保护4附魔、火焰附加2附魔、火矢1附魔、爆炸保护4附魔、力量5附魔、击退2附魔
         * 材料：照谛核心4个
         * SE结晶：螺旋l2 4个
         * C=照谛核心, B=基础刀（龙，满足要求）, S=螺旋l2 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.DRAGON_LAMBDA.location())
                            .pattern("CSC")
                            .pattern("SBS")
                            .pattern("CSC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.DRAGON.location())
                                            .killCount(2000)
                                            .refineCount(500)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_ASPECT), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FLAMING_ARROWS), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLAST_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SMITE), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.KNOCKBACK), 2))
                                            .build()))
                            .define('C', RecastingItems.ILLUMINATING_TRUTH_CORE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPIRAL.getId(), 1))
                            .unlockedBy("has_illuminating_truth_core", RecipeProviderMixin.invokeHas(RecastingItems.ILLUMINATING_TRUTH_CORE.get()))
                            .save(consumer, Recasting.prefix("dragon_lambda_recipe"));
    
        /**
         * 风云配方：从苍景 Lambda 升级
         * 要求：杀敌1500、锻造400、力量5附魔、冲击2附魔、无限1附魔、穿透3附魔
         * 材料：照谛核心4个
         * SE结晶：旋风l1 4个
         * C=照谛核心, B=基础刀（苍景 Lambda，满足要求）, W=旋风l1 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.WIND_CLOUD.location())
                            .pattern("CWC")
                            .pattern("WBW")
                            .pattern("CWC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.AZURE_VISTA_LAMBDA.location())
                                            .killCount(1500)
                                            .refineCount(400)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.POWER_ARROWS), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PUNCH_ARROWS), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.INFINITY_ARROWS), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PIERCING), 3))
                                            .build()))
                            .define('C', RecastingItems.ILLUMINATING_TRUTH_CORE.get())
                            .define('W', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.WHIRLWIND.getId(), 1))
                            .unlockedBy("has_illuminating_truth_core", RecipeProviderMixin.invokeHas(RecastingItems.ILLUMINATING_TRUTH_CORE.get()))
                            .save(consumer, Recasting.prefix("wind_cloud_recipe"));
    
        /**
         * 风云 Lambda 配方：从风云升级
         * 要求：杀敌2500、锻造600、力量5附魔、冲击2附魔、无限1附魔、穿透4附魔、抢夺3附魔、节肢杀手2附魔
         * 材料：照谛核心4个
         * SE结晶：风暴l1 2个、风暴变体l1 2个
         * C=照谛核心, B=基础刀（风云，满足要求）, S=风暴l1 SE结晶, V=风暴变体l1 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.WIND_CLOUD_LAMBDA.location())
                            .pattern("CSC")
                            .pattern("VBV")
                            .pattern("CSC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.WIND_CLOUD.location())
                                            .killCount(2500)
                                            .refineCount(600)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.POWER_ARROWS), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PUNCH_ARROWS), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.INFINITY_ARROWS), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PIERCING), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MOB_LOOTING), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BANE_OF_ARTHROPODS), 2))
                                            .build()))
                            .define('C', RecastingItems.ILLUMINATING_TRUTH_CORE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM.getId(), 1))
                            .define('V', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM_VARIANT.getId(), 1))
                            .unlockedBy("has_illuminating_truth_core", RecipeProviderMixin.invokeHas(RecastingItems.ILLUMINATING_TRUTH_CORE.get()))
                            .save(consumer, Recasting.prefix("wind_cloud_lambda_recipe"));

        /**
         * 苍景配方（t3）：从龙鳞 Lambda 升级
         * 要求：杀敌1500、锻造300、力量5附魔、冲击2附魔、无限1附魔、穿透3附魔
         * 材料：翠绿的庸魂立方体4个
         * SE结晶：剑气释放 2个
         * C=翠绿的庸魂立方体, D=剑气释放 SE结晶, B=基础刀（龙鳞 Lambda，满足要求）
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.AZURE_VISTA.location())
                            .pattern(" DC")
                            .pattern("CBC")
                            .pattern("CD ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.DRAGON_SCALE_LAMBDA.location())
                                            .killCount(1500)
                                            .refineCount(300)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.POWER_ARROWS), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PUNCH_ARROWS), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.INFINITY_ARROWS), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PIERCING), 3))
                                            .build()))
                            .define('C', RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get())
                            .define('D', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.DRIVE_RELEASE.getId(), 1))
                            .unlockedBy("has_emerald_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.EMERALD_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("azure_vista_recipe"));

        /**
         * 苍景 Lambda 配方（t3）：从苍景升级
         * 要求：杀敌3000、锻造600、力量5附魔、冲击2附魔、无限1附魔、穿透4附魔、抢夺3附魔、节肢杀手2附魔、穿刺1附魔
         * 材料：钻石庸魂立方体4个
         * SE结晶：生长 2个
         * C=钻石庸魂立方体, G=生长 SE结晶, B=基础刀（苍景，满足要求）
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.AZURE_VISTA_LAMBDA.location())
                            .pattern(" GC")
                            .pattern("CBC")
                            .pattern("CG ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.AZURE_VISTA.location())
                                            .killCount(3000)
                                            .refineCount(600)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.POWER_ARROWS), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PUNCH_ARROWS), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.INFINITY_ARROWS), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PIERCING), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MOB_LOOTING), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BANE_OF_ARTHROPODS), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.IMPALING), 1))
                                            .build()))
                            .define('C', RecastingItems.DIAMOND_MEDIUM_SOUL_CUBE.get())
                            .define('G', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.GROWTH.getId(), 1))
                            .unlockedBy("has_diamond_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.DIAMOND_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("azure_vista_lambda_recipe"));
    
        /**
         * 法棍配方：基础配方，无前置刀
         * 材料：面包3个 + 耀魂1个
         * B=面包, P=耀魂
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.DHARMA_STICK.location())
                            .pattern("  B")
                            .pattern(" B ")
                            .pattern("BP ")
                            .define('B', Items.BREAD)
                            .define('P', SlashBladeItems.PROUDSOUL.get())
                            .unlockedBy("has_bread", RecipeProviderMixin.invokeHas(Items.BREAD))
                            .save(consumer, Recasting.prefix("dharma_stick_recipe"));
    
        /**
         * 法棍 Lambda 配方：从法棍升级
         * 要求：杀敌10000
         * 材料：面包8个
         * D=法棍（满足要求）, B=面包
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.DHARMA_STICK_LAMBDA.location())
                            .pattern("BBB")
                            .pattern("BDB")
                            .pattern("BBB")
                            .define('D',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.DHARMA_STICK.location())
                                            .killCount(10000)
                                            .build()))
                            .define('B', Items.BREAD)
                            .unlockedBy("has_bread", RecipeProviderMixin.invokeHas(Items.BREAD))
                            .save(consumer, Recasting.prefix("dharma_stick_lambda_recipe"));
    
        /**
         * 锄头配方：基础配方，无前置刀
         * 材料：耀魂铁锭2个 + 木棍2个
         * H=耀魂铁锭, S=木棍
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.HOE.location())
                            .pattern(" HH")
                            .pattern(" S ")
                            .pattern(" S ")
                            .define('H', SlashBladeItems.PROUDSOUL_INGOT.get())
                            .define('S', Items.STICK)
                            .unlockedBy("has_stick", RecipeProviderMixin.invokeHas(Items.STICK))
                            .save(consumer, Recasting.prefix("hoe_recipe"));
    
        /**
         * 物理学圣剑配方：整活刀，无前置
         * 材料：铁锭3个（L 形）+ 耀魂1个
         * I=铁锭, P=耀魂
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.PHYSICS_SWORD.location())
                            .pattern("I  ")
                            .pattern("IP ")
                            .pattern("II ")
                            .define('I', Items.IRON_INGOT)
                            .define('P', SlashBladeItems.PROUDSOUL.get())
                            .unlockedBy("has_iron_ingot", RecipeProviderMixin.invokeHas(Items.IRON_INGOT))
                            .save(consumer, Recasting.prefix("physics_sword_recipe"));
    
        /**
         * VOID_1 配方：从黑刃升级
         * 要求：杀敌3000、锻造500、保护4附魔、荆棘1附魔
         * SE结晶：震荡l3 2个
         * 材料：银白色庸魂立方体4个
         * C=银白色庸魂立方体, S=震荡l3 SE结晶, B=基础刀（黑刃，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.VOID_1.location())
                            .pattern(" SC")
                            .pattern("CBC")
                            .pattern("CS ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BLACK.location())
                                            .killCount(1500)
                                            .refineCount(100)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.ALL_DAMAGE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 1))
                                            .build()))
                            .define('C', RecastingItems.IRON_MEDIUM_SOUL_CUBE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SHOCK.getId(), 1))
                            .unlockedBy("has_iron_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.IRON_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("void_1_recipe"));
    
        /**
         * VOID_2 配方：从 VOID_1 升级
         * 要求：杀敌3000、锻造250、保护4附魔、荆棘3附魔
         * SE结晶：生长l3 2个
         * 材料：漆黑庸魂立方体4个
         * C=漆黑庸魂立方体, G=生长l3 SE结晶, B=基础刀（VOID_1，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.VOID_2.location())
                            .pattern(" GC")
                            .pattern("CBC")
                            .pattern("CG ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.VOID_1.location())
                                            .killCount(3000)
                                            .refineCount(250)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.ALL_DAMAGE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .build()))
                            .define('C', RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get())
                            .define('G', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.GROWTH.getId(), 1))
                            .unlockedBy("has_netherite_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("void_2_recipe"));
    
        /**
         * VOID_3 配方：从 VOID_2 升级
         * 要求：杀敌5000、锻造500、保护4附魔、荆棘3附魔、爆炸保护4附魔
         * SE结晶：吸血转化l3 2个
         * 材料：燎焰核心4个
         * C=燎焰核心, L=吸血转化l3 SE结晶, B=基础刀（VOID_2，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.VOID_3.location())
                            .pattern(" LC")
                            .pattern("CBC")
                            .pattern("CL ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.VOID_2.location())
                                            .killCount(5000)
                                            .refineCount(500)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.ALL_DAMAGE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.BLAST_PROTECTION), 4))
                                            .build()))
                            .define('C', RecastingItems.BLAZING_FLAME_CORE.get())
                            .define('L', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.LIFE_STEAL.getId(), 1))
                            .unlockedBy("has_blazing_flame_core", RecipeProviderMixin.invokeHas(RecastingItems.BLAZING_FLAME_CORE.get()))
                            .save(consumer, Recasting.prefix("void_3_recipe"));
    
        /**
         * OBLITERATE 配方：从黑刃升级
         * 要求：杀敌1000、荣耀50000、火焰附加2附魔、火矢1附魔、火焰保护2附魔
         * SE结晶：回溯l1 2个
         * 材料：赤红庸魂立方体4个
         * C=赤红庸魂立方体, R=回溯l1 SE结晶, B=基础刀（黑刃，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.OBLITERATE.location())
                            .pattern(" CR")
                            .pattern("CBC")
                            .pattern("RC ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BLACK.location())
                                            .killCount(1000)
                                            .proudSoul(50000)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_ASPECT), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FLAMING_ARROWS), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_PROTECTION), 2))
                                            .build()))
                            .define('C', RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get())
                            .define('R', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1))
                            .unlockedBy("has_redstone_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("obliterate_recipe"));
    
        /**
         * OBLITERATE Lambda 配方：从 OBLITERATE 升级
         * 要求：杀敌2000、荣耀100000、火焰附加2附魔、火矢1附魔、火焰保护4附魔
         * SE结晶：回溯l2 2个
         * 材料：赤红庸魂立方体4个
         * C=赤红庸魂立方体, R=回溯l2 SE结晶, B=基础刀（OBLITERATE，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.OBLITERATE_LAMBDA.location())
                            .pattern(" CR")
                            .pattern("CBC")
                            .pattern("RC ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.OBLITERATE.location())
                                            .killCount(2000)
                                            .proudSoul(100000)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_ASPECT), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FLAMING_ARROWS), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_PROTECTION), 4))
                                            .build()))
                            .define('C', RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get())
                            .define('R', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1))
                            .unlockedBy("has_redstone_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("obliterate_lambda_recipe"));
    
        /**
         * SOULBLADE 配方：从 OBLITERATE Lambda 升级
         * 要求：杀敌4000、荣耀200000、火焰附加2附魔、火矢1附魔、火焰保护4附魔、荆棘3附魔、引雷1附魔
         * SE结晶：回溯l3 2个
         * 材料：燎焰核心4个
         * C=燎焰核心, R=回溯l3 SE结晶, B=基础刀（OBLITERATE Lambda，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.SOULBLADE.location())
                            .pattern(" CR")
                            .pattern("CBC")
                            .pattern("RC ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.OBLITERATE_LAMBDA.location())
                                            .killCount(4000)
                                            .proudSoul(200000)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_ASPECT), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FLAMING_ARROWS), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.CHANNELING), 1))
                                            .build()))
                            .define('C', RecastingItems.BLAZING_FLAME_CORE.get())
                            .define('R', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1))
                            .unlockedBy("has_blazing_flame_core", RecipeProviderMixin.invokeHas(RecastingItems.BLAZING_FLAME_CORE.get()))
                            .save(consumer, Recasting.prefix("soulblade_recipe"));

        /**
         * SOULBLADE Lambda 配方：从 SOULBLADE 升级
         * 要求：杀敌8000、荣耀400000、火焰附加2附魔、火矢1附魔、火焰保护4附魔、荆棘3附魔、引雷1附魔、锋利5附魔、耐久3附魔
         * SE结晶：回溯l1 2个
         * 材料：燎焰核心6个
         * C=燎焰核心, R=回溯l1 SE结晶, B=基础刀（SOULBLADE，满足要求）
         */


        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.SOULBLADE_LAMBDA.location())
                            .pattern("CRC")
                            .pattern("CBC")
                            .pattern("CRC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.SOULBLADE.location())
                                            .killCount(8000)
                                            .proudSoul(400000)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_ASPECT), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FLAMING_ARROWS), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.CHANNELING), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                            .build()))
                            .define('C', RecastingItems.BLAZING_FLAME_CORE.get())
                            .define('R', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.REGRESSION.getId(), 1))
                            .unlockedBy("has_blazing_flame_core", RecipeProviderMixin.invokeHas(RecastingItems.BLAZING_FLAME_CORE.get()))
                            .save(consumer, Recasting.prefix("soulblade_lambda_recipe"));

        /**
         * STAR_1 配方：从黑刃升级
         * 要求：杀敌1000、锻造200、弹射物保护4附魔
         * 材料：银白色的庸魂立方体4个
         * SE结晶：撕裂l2 4个
         * C=银白色的庸魂立方体, B=基础刀（黑刃，满足要求）, T=撕裂l2 SE结晶
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.STAR_1.location())
                            .pattern("TCT")
                            .pattern("CBC")
                            .pattern("TCT")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BLACK.location())
                                            .killCount(1000)
                                            .refineCount(200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PROJECTILE_PROTECTION), 4))
                                            .build()))
                            .define('C', RecastingItems.IRON_MEDIUM_SOUL_CUBE.get())
                            .define('T', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.TEAR.getId(), 1))
                            .unlockedBy("has_iron_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.IRON_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("star_1_recipe"));
    
        /**
         * STAR_2 配方：从 STAR_1 升级
         * 要求：杀敌2000、锻造400、弹射物保护4附魔、荆棘2附魔
         * SE结晶：旋风l2 4个
         * W=旋风l2 SE结晶, B=基础刀（STAR_1，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.STAR_2.location())
                            .pattern(" W ")
                            .pattern("WBW")
                            .pattern(" W ")
                            .define('B',

                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.STAR_1.location())
                                            .killCount(2000)
                                            .refineCount(400)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PROJECTILE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 2))
                                            .build()))
                            .define('W', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.WHIRLWIND.getId(), 1))
                            .unlockedBy("has_whirlwind_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                            .save(consumer, Recasting.prefix("star_2_recipe"));
    
        /**
         * STAR_3 配方：从 STAR_2 升级
         * 要求：杀敌4000、锻造600、弹射物保护4附魔、荆棘3附魔
         * SE结晶：断却l1 4个
         * A=断却l1 SE结晶, B=基础刀（STAR_2，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.STAR_3.location())
                            .pattern(" A ")
                            .pattern("ABA")
                            .pattern(" A ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.STAR_2.location())
                                            .killCount(4000)
                                            .refineCount(600)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PROJECTILE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .build()))
                            .define('A', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SEVER_BREAK.getId(), 1))
                            .unlockedBy("has_sever_break_crystal", RecipeProviderMixin.invokeHas(RecastingItems.SE_CRYSTAL.get()))
                            .save(consumer, Recasting.prefix("star_3_recipe"));
    
        /**
         * STAR_4 配方：从 STAR_3 升级
         * 要求：杀敌6000、锻造800、弹射物保护4附魔、荆棘3附魔、力量4附魔
         * 材料：照谛核心2个
         * SE结晶：断灭l1 2个
         * C=照谛核心, S=断灭l1 SE结晶, B=基础刀（STAR_3，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.STAR_4.location())
                            .pattern("C S")
                            .pattern(" B ")
                            .pattern("S C")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.STAR_3.location())
                                            .killCount(6000)
                                            .refineCount(800)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PROJECTILE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.POWER_ARROWS), 4))
                                            .build()))
                            .define('C', RecastingItems.ILLUMINATING_TRUTH_CORE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.ANNIHILATION.getId(), 1))
                            .unlockedBy("has_illuminating_truth_core", RecipeProviderMixin.invokeHas(RecastingItems.ILLUMINATING_TRUTH_CORE.get()))
                            .save(consumer, Recasting.prefix("star_4_recipe"));
    
        /**
         * STAR_4_LAMBDA 配方：从 STAR_4 升级
         * 要求：杀敌12000、锻造1600、弹射物保护4附魔、荆棘3附魔、力量5附魔、冲击2附魔、无限1附魔、多重射击1附魔、快速装填3附魔
         * 材料：照谛核心4个
         * SE结晶：风暴l3 2个、风暴变体l3 2个
         * C=照谛核心, S=风暴l3 SE结晶, V=风暴变体l3 SE结晶, B=基础刀（STAR_4，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.STAR_4_LAMBDA.location())
                            .pattern("CVC")
                            .pattern("SBS")
                            .pattern("CVC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.STAR_4.location())
                                            .killCount(12000)
                                            .refineCount(1600)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PROJECTILE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.POWER_ARROWS), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PUNCH_ARROWS), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.INFINITY_ARROWS), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MULTISHOT), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.QUICK_CHARGE), 3))
                                            .build()))
                            .define('C', RecastingItems.ILLUMINATING_TRUTH_CORE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM.getId(), 1))
                            .define('V', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM_VARIANT.getId(), 1))
                            .unlockedBy("has_illuminating_truth_core", RecipeProviderMixin.invokeHas(RecastingItems.ILLUMINATING_TRUTH_CORE.get()))
                            .save(consumer, Recasting.prefix("star_4_lambda_recipe"));
    
        /**
         * LASER_1 配方（t3）：从黑刃升级
         * 要求：杀敌1500、锻造200、保护4附魔、荆棘1附魔
         * 材料：银白色庸魂立方体4个
         * SE结晶：破片 2个
         * C=银白色庸魂立方体, F=破片 SE结晶, B=基础刀（黑刃，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.LASER_1.location())
                            .pattern(" FC")
                            .pattern("CBC")
                            .pattern("CF ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BLACK.location())
                                            .killCount(1500)
                                            .refineCount(200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.ALL_DAMAGE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 1))
                                            .build()))
                            .define('C', RecastingItems.IRON_MEDIUM_SOUL_CUBE.get())
                            .define('F', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.FRAGMENT.getId(), 1))
                            .unlockedBy("has_iron_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.IRON_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("laser_1_recipe"));
    
        /**
         * LASER_2 配方（t3）：从 LASER_1 升级
         * 要求：杀敌3000、锻造400、保护4附魔、荆棘3附魔
         * 材料：金黄庸魂立方体4个
         * SE结晶：分裂 2个
         * C=金黄庸魂立方体, S=分裂 SE结晶, B=基础刀（LASER_1，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.LASER_2.location())
                            .pattern(" SC")
                            .pattern("CBC")
                            .pattern("CS ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.LASER_1.location())
                                            .killCount(3000)
                                            .refineCount(400)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.ALL_DAMAGE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .build()))
                            .define('C', RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPLIT.getId(), 1))
                            .unlockedBy("has_gold_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("laser_2_recipe"));
    
        /**
         * LASER_3 配方（t2）：从 LASER_2 升级
         * 要求：杀敌5000、锻造600、保护4附魔、荆棘3附魔、弹射物保护4附魔
         * 材料：霜璇核心4个
         * SE结晶：冲击 2个
         * C=霜璇核心, I=冲击 SE结晶, B=基础刀（LASER_2，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.LASER_3.location())
                            .pattern(" IC")
                            .pattern("CBC")
                            .pattern("CI ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.LASER_2.location())
                                            .killCount(5000)
                                            .refineCount(600)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.ALL_DAMAGE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PROJECTILE_PROTECTION), 4))
                                            .build()))
                            .define('C', RecastingItems.FROST_VORTEX_CORE.get())
                            .define('I', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IMPACT.getId(), 1))
                            .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                            .save(consumer, Recasting.prefix("laser_3_recipe"));
    
        /**
         * LASER_3_LAMBDA 配方（t2）：从 LASER_3 升级
         * 要求：杀敌10000、锻造1200、保护4附魔、荆棘3附魔、弹射物保护4附魔、水下呼吸3附魔、水下速掘1附魔、耐久3附魔
         * 材料：霜璇核心2个
         * SE结晶：分裂 2个、冲击 2个
         * C=霜璇核心, S=分裂 SE结晶, I=冲击 SE结晶, B=基础刀（LASER_3，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.LASER_3_LAMBDA.location())
                            .pattern("CSI")
                            .pattern(" B ")
                            .pattern("ISC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.LASER_3.location())
                                            .killCount(10000)
                                            .refineCount(1200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.ALL_DAMAGE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.THORNS), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.PROJECTILE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.RESPIRATION), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.AQUA_AFFINITY), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                            .build()))
                            .define('C', RecastingItems.FROST_VORTEX_CORE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPLIT.getId(), 1))
                            .define('I', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IMPACT.getId(), 1))
                            .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                            .save(consumer, Recasting.prefix("laser_3_lambda_recipe"));

        /**
         * 磁暴配方（t3）：从黑刃升级
         * 要求：杀敌1500、锻造200、引雷1附魔、穿刺3附魔
         * 材料：银白色庸魂立方体4个
         * SE结晶：雷霆万钧 2个
         * C=银白色庸魂立方体, T=雷霆万钧 SE结晶, B=基础刀（黑刃，满足要求）
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.MAGNETIC_STORM.location())
                            .pattern(" TC")
                            .pattern("CBC")
                            .pattern("CT ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BLACK.location())
                                            .killCount(1500)
                                            .refineCount(200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.CHANNELING), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.IMPALING), 3))
                                            .build()))
                            .define('C', RecastingItems.IRON_MEDIUM_SOUL_CUBE.get())
                            .define('T', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDER_STRIKE.getId(), 1))
                            .unlockedBy("has_iron_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.IRON_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("magnetic_storm_recipe"));

        /**
         * 磁暴 Lambda 配方（t3）：从磁暴升级
         * 要求：杀敌3000、锻造400、引雷1附魔、穿刺5附魔
         * 材料：金黄庸魂立方体4个
         * SE结晶：电离 2个
         * C=金黄庸魂立方体, I=电离 SE结晶, B=基础刀（磁暴，满足要求）
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.MAGNETIC_STORM_LAMBDA.location())
                            .pattern(" IC")
                            .pattern("CBC")
                            .pattern("CI ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.MAGNETIC_STORM.location())
                                            .killCount(3000)
                                            .refineCount(400)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.CHANNELING), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.IMPALING), 5))
                                            .build()))
                            .define('C', RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get())
                            .define('I', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.IONIZATION.getId(), 1))
                            .unlockedBy("has_gold_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("magnetic_storm_lambda_recipe"));

        /**
         * 磁暴[超限] 配方（t2）：从磁暴 Lambda 升级
         * 要求：杀敌5000、锻造600、引雷1附魔、穿刺5附魔、忠诚3附魔、力量2附魔
         * 材料：霜璇核心4个
         * SE结晶：雷暴 2个
         * C=霜璇核心, T=雷暴 SE结晶, B=基础刀（磁暴 Lambda，满足要求）
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.MAGNETIC_STORM_LIMITS.location())
                            .pattern(" TC")
                            .pattern("CBC")
                            .pattern("CT ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.MAGNETIC_STORM_LAMBDA.location())
                                            .killCount(5000)
                                            .refineCount(600)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.CHANNELING), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.IMPALING), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.LOYALTY), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.POWER_ARROWS), 2))
                                            .build()))
                            .define('C', RecastingItems.FROST_VORTEX_CORE.get())
                            .define('T', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDERSTORM.getId(), 1))
                            .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                            .save(consumer, Recasting.prefix("magnetic_storm_limits_recipe"));

        /**
         * 磁暴[超限] Lambda 配方（t2）：从磁暴[超限] 升级
         * 要求：杀敌10000、锻造1200、引雷1附魔、穿刺5附魔、忠诚3附魔、力量2附魔、耐久3附魔、经验修补1附魔、水下呼吸3附魔
         * 材料：霜璇核心2个
         * SE结晶：雷云 2个、雷暴 2个
         * F=霜璇核心, C=雷云 SE结晶, T=雷暴 SE结晶, B=基础刀（磁暴[超限]，满足要求）
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.MAGNETIC_STORM_LIMITS_LAMBDA.location())
                            .pattern("FT ")
                            .pattern("CBC")
                            .pattern(" TF")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.MAGNETIC_STORM_LIMITS.location())
                                            .killCount(10000)
                                            .refineCount(1200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.CHANNELING), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.IMPALING), 5))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.LOYALTY), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.POWER_ARROWS), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.UNBREAKING), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MENDING), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.RESPIRATION), 3))
                                            .build()))
                            .define('F', RecastingItems.FROST_VORTEX_CORE.get())
                            .define('C', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDER_CLOUD.getId(), 1))
                            .define('T', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDERSTORM.getId(), 1))
                            .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                            .save(consumer, Recasting.prefix("magnetic_storm_limits_lambda_recipe"));
    
        /**
         * FLUORESCENCE_1 配方：从青锋（木）升级
         * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（左上）
         * G=荧光墨囊, B=青锋（木）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.FLUORESCENCE_1.location())
                            .pattern("G  ")
                            .pattern(" B ")
                            .pattern("   ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.GREEN_BLADE_WOOD.location())
                                            .build()))
                            .define('G', Items.GLOW_INK_SAC)
                            .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                            .save(consumer, Recasting.prefix("fluorescence_1_recipe"));
    
        /**
         * FLUORESCENCE_2 配方：从青锋（木）升级
         * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（上中）
         * G=荧光墨囊, B=青锋（木）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.FLUORESCENCE_2.location())
                            .pattern(" G ")
                            .pattern(" B ")
                            .pattern("   ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.GREEN_BLADE_WOOD.location())
                                            .build()))
                            .define('G', Items.GLOW_INK_SAC)
                            .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                            .save(consumer, Recasting.prefix("fluorescence_2_recipe"));
    
        /**
         * FLUORESCENCE_3 配方：从青锋（木）升级
         * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（右上）
         * G=荧光墨囊, B=青锋（木）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.FLUORESCENCE_3.location())
                            .pattern("  G")
                            .pattern(" B ")
                            .pattern("   ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.GREEN_BLADE_WOOD.location())
                                            .build()))
                            .define('G', Items.GLOW_INK_SAC)
                            .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                            .save(consumer, Recasting.prefix("fluorescence_3_recipe"));
    
        /**
         * FLUORESCENCE_4 配方：从青锋（木）升级
         * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（左中）
         * G=荧光墨囊, B=青锋（木）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.FLUORESCENCE_4.location())
                            .pattern("   ")
                            .pattern("GB ")
                            .pattern("   ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.GREEN_BLADE_WOOD.location())
                                            .build()))
                            .define('G', Items.GLOW_INK_SAC)
                            .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                            .save(consumer, Recasting.prefix("fluorescence_4_recipe"));
    
        /**
         * FLUORESCENCE_5 配方：从青锋（木）升级
         * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（右中）
         * G=荧光墨囊, B=青锋（木）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.FLUORESCENCE_5.location())
                            .pattern("   ")
                            .pattern(" BG")
                            .pattern("   ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.GREEN_BLADE_WOOD.location())
                                            .build()))
                            .define('G', Items.GLOW_INK_SAC)
                            .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                            .save(consumer, Recasting.prefix("fluorescence_5_recipe"));
    
        /**
         * FLUORESCENCE_6 配方：从青锋（木）升级
         * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（左下）
         * G=荧光墨囊, B=青锋（木）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.FLUORESCENCE_6.location())
                            .pattern("   ")
                            .pattern(" B ")
                            .pattern("G  ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.GREEN_BLADE_WOOD.location())
                                            .build()))
                            .define('G', Items.GLOW_INK_SAC)
                            .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                            .save(consumer, Recasting.prefix("fluorescence_6_recipe"));
    
        /**
         * FLUORESCENCE_7 配方：从青锋（木）升级
         * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（下中）
         * G=荧光墨囊, B=青锋（木）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.FLUORESCENCE_7.location())
                            .pattern("   ")
                            .pattern(" B ")
                            .pattern(" G ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.GREEN_BLADE_WOOD.location())
                                            .build()))
                            .define('G', Items.GLOW_INK_SAC)
                            .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                            .save(consumer, Recasting.prefix("fluorescence_7_recipe"));
    
        /**
         * FLUORESCENCE_8 配方：从青锋（木）升级
         * 材料：青锋（木）1个（中间）+ 荧光墨囊1个（右下）
         * G=荧光墨囊, B=青锋（木）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.FLUORESCENCE_8.location())
                            .pattern("   ")
                            .pattern(" B ")
                            .pattern("  G")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.GREEN_BLADE_WOOD.location())
                                            .build()))
                            .define('G', Items.GLOW_INK_SAC)
                            .unlockedBy("has_green_blade_wood", RecipeProviderMixin.invokeHas(Items.GLOW_INK_SAC))
                            .save(consumer, Recasting.prefix("fluorescence_8_recipe"));
    
        /**
         * 云翼配方（t3）：从黑刃升级
         * 要求：杀敌1000、锻造200、摔落保护4附魔
         * 材料：银白色庸魂立方体4个
         * SE结晶：分裂 2个
         * C=银白色庸魂立方体, S=分裂 SE结晶, B=基础刀（黑刃，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.SILVER_WING.location())
                            .pattern(" SC")
                            .pattern("CBC")
                            .pattern("CS ")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BLACK.location())
                                            .killCount(1000)
                                            .refineCount(200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FALL_PROTECTION), 4))
                                            .build()))
                            .define('C', RecastingItems.IRON_MEDIUM_SOUL_CUBE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPLIT.getId(), 1))
                            .unlockedBy("has_iron_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.IRON_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("silver_wing_recipe"));
    
        /**
         * 云翼 Lambda 配方（t3）：从云翼升级
         * 要求：杀敌2000、锻造400、摔落保护4附魔、深海探索者3附魔
         * 材料：金黄色庸魂立方体4个
         * SE结晶：风暴 2个、风暴变体 2个
         * C=金黄色庸魂立方体, S=风暴 SE结晶, V=风暴变体 SE结晶, B=基础刀（云翼，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.SILVER_WING_LAMBDA.location())
                            .pattern("CSC")
                            .pattern("VBV")
                            .pattern("CSC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.SILVER_WING.location())
                                            .killCount(2000)
                                            .refineCount(400)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FALL_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.DEPTH_STRIDER), 3))
                                            .build()))

                            .define('C', RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM.getId(), 1))
                            .define('V', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.STORM_VARIANT.getId(), 1))
                            .unlockedBy("has_gold_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.GOLD_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("silver_wing_lambda_recipe"));
    
        /**
         * 彩翼配方（t2）：从云翼 Lambda 升级
         * 要求：杀敌5000、锻造600、摔落保护4附魔、灵魂疾行3附魔、迅捷潜行3附魔、冰霜行者1附魔
         * 材料：霜璇核心4个
         * 染料：红色、黄色、黄绿色、蓝色各1个
         * C=霜璇核心, X=基础刀（云翼 Lambda，满足要求）, R=红色染料, Y=黄色染料, L=黄绿色染料, B=蓝色染料
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.COLOR_WING.location())
                            .pattern("CRC")
                            .pattern("YXL")
                            .pattern("CBC")
                            .define('X',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.SILVER_WING_LAMBDA.location())
                                            .killCount(5000)
                                            .refineCount(600)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FALL_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SOUL_SPEED), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SWIFT_SNEAK), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FROST_WALKER), 1))
                                            .build()))
                            .define('C', RecastingItems.FROST_VORTEX_CORE.get())
                            .define('R', Items.RED_DYE)
                            .define('Y', Items.YELLOW_DYE)
                            .define('L', Items.LIME_DYE)
                            .define('B', Items.BLUE_DYE)
                            .unlockedBy("has_frost_vortex_core", RecipeProviderMixin.invokeHas(RecastingItems.FROST_VORTEX_CORE.get()))
                            .save(consumer, Recasting.prefix("color_wing_recipe"));
    
        /**
         * 彩翼 Lambda 配方（t2）：从彩翼升级
         * 要求：杀敌10000、锻造1200、摔落保护4附魔、灵魂疾行3附魔、迅捷潜行3附魔、冰霜行者2附魔、经验修补1附魔、引雷1附魔、多重射击1附魔、快速装填3附魔
         * 材料：燎焰核心4个、荧光墨囊4个
         * SE结晶：雷暴 2个
         * C=燎焰核心, G=荧光墨囊, T=雷暴 SE结晶, B=基础刀（彩翼，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.COLOR_WING_LAMBDA.location())
                            .pattern("GTG")
                            .pattern("CBC")
                            .pattern("GTG")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.COLOR_WING.location())
                                            .killCount(10000)
                                            .refineCount(1200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FALL_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SOUL_SPEED), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SWIFT_SNEAK), 3))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FROST_WALKER), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MENDING), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.CHANNELING), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.MULTISHOT), 1))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.QUICK_CHARGE), 3))
                                            .build()))
                            .define('C', RecastingItems.BLAZING_FLAME_CORE.get())
                            .define('G', Items.GLOW_INK_SAC)
                            .define('T', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.THUNDERSTORM.getId(), 1))
                            .unlockedBy("has_blazing_flame_core", RecipeProviderMixin.invokeHas(RecastingItems.BLAZING_FLAME_CORE.get()))
                            .save(consumer, Recasting.prefix("color_wing_lambda_recipe"));
    
        /**
         * 长空落日配方（t3）：从黑刃升级
         * 要求：杀敌1500、锻造200、火焰保护4附魔、火焰附加1附魔
         * 材料：赤红庸魂立方体4个
         * SE结晶：分裂 4个
         * C=赤红庸魂立方体, S=分裂 SE结晶, B=基础刀（黑刃，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.LONG_SKY_SUNSET.location())
                            .pattern("CSC")
                            .pattern("SBS")
                            .pattern("CSC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.BLACK.location())
                                            .killCount(1500)
                                            .refineCount(200)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_ASPECT), 1))
                                            .build()))
                            .define('C', RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get())
                            .define('S', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.SPLIT.getId(), 1))
                            .unlockedBy("has_redstone_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("long_sky_sunset_recipe"));
    
        /**
         * 长空落日 Lambda 配方（t3）：从长空落日升级
         * 要求：杀敌3000、锻造400、火焰保护4附魔、火焰附加2附魔、火矢1附魔
         * 材料：赤红庸魂立方体4个
         * SE结晶：破片 4个
         * C=赤红庸魂立方体, F=破片 SE结晶, B=基础刀（长空落日，满足要求）
         */
        
    
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.LONG_SKY_SUNSET_LAMBDA.location())
                            .pattern("CFC")
                            .pattern("FBF")
                            .pattern("CFC")
                            .define('B',
                                    SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                            .name(RecastingSlashBladeKeys.LONG_SKY_SUNSET.location())
                                            .killCount(3000)
                                            .refineCount(400)
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_PROTECTION), 4))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FIRE_ASPECT), 2))
                                            .addEnchantment(new EnchantmentDefinition(
                                                    ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.FLAMING_ARROWS), 1))
                                            .build()))
                            .define('C', RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get())
                            .define('F', SpecialEffectCrystalIngredient.of(SpecialEffectsRegistry.FRAGMENT.getId(), 1))
                            .unlockedBy("has_redstone_medium_soul_cube", RecipeProviderMixin.invokeHas(RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE.get()))
                            .save(consumer, Recasting.prefix("long_sky_sunset_lambda_recipe"));

        /**
         * 屠巫 Lambda：从屠巫升级。
         * C=火毒相变
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.TU_WU_LAMBDA.location())
                .pattern(" C ")
                .pattern("CBC")
                .pattern(" C ")
                .define('B',
                        SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                .name(RecastingSlashBladeKeys.TU_WU.location())
                                .killCount(10000)
                                .refineCount(1200)
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                .build()))
                .define('C', RecastingItems.FIRE_TOXIN_PHASE_TRANSITION.get())
                .unlockedBy("has_fire_toxin_phase_transition", RecipeProviderMixin.invokeHas(RecastingItems.FIRE_TOXIN_PHASE_TRANSITION.get()))
                .save(consumer, Recasting.prefix("tu_wu_lambda_recipe"));

        /**
         * 轩辕·解放 Lambda：从轩辕·解放升级。
         * C=交错相变
         */
        SlashBladeShapedRecipeBuilder.shaped(RecastingSlashBladeKeys.XUAN_YUAN_LIBERATED_LAMBDA.location())
                .pattern(" C ")
                .pattern("CBC")
                .pattern(" C ")
                .define('B',
                        SlashBladeIngredient.of(RequestDefinition.Builder.newInstance()
                                .name(RecastingSlashBladeKeys.XUAN_YUAN_LIBERATED.location())
                                .killCount(10000)
                                .refineCount(1200)
                                .addEnchantment(new EnchantmentDefinition(
                                        ForgeRegistries.ENCHANTMENTS.getKey(Enchantments.SHARPNESS), 5))
                                .build()))
                .define('C', RecastingItems.INTERLACE_PHASE_TRANSITION.get())
                .unlockedBy("has_interlace_phase_transition", RecipeProviderMixin.invokeHas(RecastingItems.INTERLACE_PHASE_TRANSITION.get()))
                .save(consumer, Recasting.prefix("xuan_yuan_liberated_lambda_recipe"));
    
    }

/**
     * 阔刃（木）配方：基础配方，无前置刀
     * 材料：木棍2个 + 木板3个
     * S=木棍, W=木板
     */
    





































































































































}

