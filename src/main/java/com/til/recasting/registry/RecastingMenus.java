package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.inventory.ProudSoulBagMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 容器菜单类型注册。
 */
public final class RecastingMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Recasting.MODID);

    public static final RegistryObject<MenuType<ProudSoulBagMenu>> PROUD_SOUL_BAG = MENUS.register(
            "proud_soul_bag",
            () -> IForgeMenuType.create(ProudSoulBagMenu::new)
    );

    private RecastingMenus() {
    }
}
