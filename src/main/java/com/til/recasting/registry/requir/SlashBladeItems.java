package com.til.recasting.registry.requir;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * SlashBlade 模组物品引用（不在本模组重复注册）。
 */
public final class SlashBladeItems {

    private static final String SLASHBLADE_MODID = "slashblade";

    public static final ItemReference PROUDSOUL = new ItemReference("proudsoul");
    public static final ItemReference PROUDSOUL_INGOT = new ItemReference("proudsoul_ingot");
    public static final ItemReference PROUDSOUL_TINY = new ItemReference("proudsoul_tiny");
    public static final ItemReference PROUDSOUL_SPHERE = new ItemReference("proudsoul_sphere");
    public static final ItemReference PROUDSOUL_CRYSTAL = new ItemReference("proudsoul_crystal");
    public static final ItemReference PROUDSOUL_TRAPEZOHEDRON = new ItemReference("proudsoul_trapezohedron");
    public static final ItemReference SLASHBLADE_WOOD = new ItemReference("slashblade_wood");
    public static final ItemReference SLASHBLADE_BAMBOO = new ItemReference("slashblade_bamboo");
    public static final ItemReference SLASHBLADE_SILVERBAMBOO = new ItemReference("slashblade_silverbamboo");
    public static final ItemReference SLASHBLADE_WHITE = new ItemReference("slashblade_white");
    public static final ItemReference SLASHBLADE = new ItemReference("slashblade");
    public static final ItemReference BLADESTAND_1 = new ItemReference("bladestand_1");
    public static final ItemReference BLADESTAND_2 = new ItemReference("bladestand_2");
    public static final ItemReference BLADESTAND_V = new ItemReference("bladestand_v");
    public static final ItemReference BLADESTAND_S = new ItemReference("bladestand_s");
    public static final ItemReference BLADESTAND_1W = new ItemReference("bladestand_1w");
    public static final ItemReference BLADESTAND_2W = new ItemReference("bladestand_2w");

    private SlashBladeItems() {
    }

    public static final class ItemReference {
        private final ResourceLocation id;

        private ItemReference(String path) {
            this.id = ResourceLocation.fromNamespaceAndPath(SLASHBLADE_MODID, path);
        }

        public Item get() {
            return BuiltInRegistries.ITEM.get(this.id);
        }

        public ResourceLocation id() {
            return this.id;
        }
    }
}
