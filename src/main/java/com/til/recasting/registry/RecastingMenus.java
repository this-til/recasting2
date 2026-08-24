package com.til.recasting.registry;

import com.til.recasting.Recasting;
import com.til.recasting.inventory.ProudSoulBagMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 容器菜单类型注册。
 */
public final class RecastingMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Recasting.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ProudSoulBagMenu>> PROUD_SOUL_BAG = MENUS.register(
            "proud_soul_bag",
            () -> IMenuTypeExtension.create(ProudSoulBagMenu::new)
    );

    private RecastingMenus() {
    }
}
