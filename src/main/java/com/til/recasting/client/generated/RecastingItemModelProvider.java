package com.til.recasting.client.generated;

import com.til.recasting.Recasting;
import com.til.recasting.registry.RecastingItems;
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

/**
 * 物品模型数据生成器
 */
public class RecastingItemModelProvider extends ItemModelProvider {

    public RecastingItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Recasting.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        RecastingItems.getAllItems().forEach(this::generateProudsoulModel);
    }

    @SuppressWarnings("UnusedReturnValue")
    public ItemModelBuilder generateProudsoulModel(RegistryObject<Item> item) {
        String name = item.getId().getPath();
        ResourceLocation texture = Recasting.prefix("item/soul");
        ResourceLocation parentModel = Recasting.prefix("item/proudsoul");

        return getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile(parentModel))
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

