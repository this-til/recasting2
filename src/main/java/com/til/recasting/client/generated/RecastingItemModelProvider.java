package com.til.recasting.client.generated;

import com.til.recasting.Recasting;
import com.til.recasting.registry.RecastingItems;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

/**
 * 物品模型数据生成器
 */
public class RecastingItemModelProvider extends ItemModelProvider {

    public RecastingItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Recasting.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        RecastingItems.getAllFlame().forEach(this::generateProudsoulModel);

        generateProudsoulModel(RecastingItems.SE_CRYSTAL, 4);
        generateProudsoulModel(RecastingItems.UPGRADE_VARIANT, 8);
        generateProudsoulModel(RecastingItems.UPGRADE_VARIANT_2, 8);
        generateProudsoulModel(RecastingItems.UPGRADE_VARIANT_3, 8);
        generateProudsoulModel(RecastingItems.UPGRADE_VARIANT_4, 8);
        generateProudsoulModel(RecastingItems.GATHERING_PARTING_VARIANT, 7);

        // 庸魂立方体模型生成
        generateProudsoulModel(RecastingItems.IRON_MEDIUM_SOUL_CUBE, 5);
        generateProudsoulModel(RecastingItems.GOLD_MEDIUM_SOUL_CUBE, 5);
        generateProudsoulModel(RecastingItems.COPPER_MEDIUM_SOUL_CUBE, 5);
        generateProudsoulModel(RecastingItems.DIAMOND_MEDIUM_SOUL_CUBE, 5);
        generateProudsoulModel(RecastingItems.EMERALD_MEDIUM_SOUL_CUBE, 5);
        generateProudsoulModel(RecastingItems.NETHERITE_MEDIUM_SOUL_CUBE, 5);
        generateProudsoulModel(RecastingItems.LAPIS_MEDIUM_SOUL_CUBE, 5);
        generateProudsoulModel(RecastingItems.REDSTONE_MEDIUM_SOUL_CUBE, 5);
    }

    public ItemModelBuilder generateProudsoulModel(RegistryObject<Item> item) {
        ResourceLocation texture = Recasting.prefix("item/soul");
        ResourceLocation parentModel = Recasting.prefix("item/soul");
        return generateProudsoulModel(item, parentModel, texture);
    }

    public ItemModelBuilder generateProudsoulModel(RegistryObject<Item> item, int modelLevel) {
        ResourceLocation texture = Recasting.prefix("item/soul_0");
        return generateProudsoulModel(item, modelLevel, texture);
    }


    public ItemModelBuilder generateProudsoulModel(RegistryObject<Item> item, int modelLevel, ResourceLocation texture) {
        ResourceLocation parentModel = Recasting.prefix("item/soul_" + modelLevel);
        return generateProudsoulModel(item, parentModel, texture);
    }

    public ItemModelBuilder generateProudsoulModel(RegistryObject<Item> item, ResourceLocation model, ResourceLocation texture) {
        return getBuilder(Objects.requireNonNull(item.getId()).getPath())
                .parent(new ModelFile.UncheckedModelFile(model))
                .texture("layer0", texture)
                .guiLight(BlockModel.GuiLight.FRONT)
                .transforms()
                .transform(ItemDisplayContext.GROUND)
                .rotation(0, 0, 0)
                .translation(0, 3, 0)
                .scale(0.5f, 0.5f, 0.5f)
                .end()
                .transform(ItemDisplayContext.GUI)
                .rotation(10, 0, 0)
                .translation(0, 0, 0)
                .scale(0.9f, 0.9f, 0.9f)
                .end()
                .transform(ItemDisplayContext.HEAD)
                .rotation(0, 180, 0)
                .translation(0, 13, 7)
                .scale(1, 1, 1)
                .end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(0, 0, 0)
                .translation(0, 3, 1)
                .scale(0.55f, 0.55f, 0.55f)
                .end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0, -90, 25)
                .translation(1.13f, 3.2f, 1.13f)
                .scale(0.68f, 0.68f, 0.68f)
                .end()
                .transform(ItemDisplayContext.FIXED)
                .rotation(0, 0, 0)
                .translation(0, 0, -10)
                .scale(1, 1, 1)
                .end()
                .end();
    }
}

