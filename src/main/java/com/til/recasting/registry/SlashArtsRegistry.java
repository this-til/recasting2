package com.til.recasting.registry;

import com.til.recasting.Recasting;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Slash Arts (SA) 注册表
 * <p>
 * 使用示例：
 * 1. 在此类中注册你的 Slash Arts
 * 2. 在 Recasting.java 中注册此注册表到 modEventBus
 * 3. 在 SlashBladeDefinition 中使用 .slashArtsType() 方法指定 SA
 */
public class SlashArtsRegistry {
    /**
     * 创建 DeferredRegister，用于注册 Slash Arts
     */
    public static final DeferredRegister<SlashArts> SLASH_ARTS = DeferredRegister.create(
            SlashArts.REGISTRY_KEY,
            Recasting.MODID
    );


    /**
     * 示例：简单的 SA，使用现有的 ComboState
     * 这个示例使用 JUDGEMENT_CUT 的 ComboState
     */
    public static final RegistryObject<SlashArts> EXAMPLE_SLASH_ART = SLASH_ARTS.register(
            "example_slash_art",
            () -> new SlashArts((entity) ->
                    // 根据实体是否在地面上返回不同的 ComboState
                    entity.onGround()
                            ? ComboStateRegistry.JUDGEMENT_CUT.getId()
                            : ComboStateRegistry.JUDGEMENT_CUT_SLASH_AIR.getId()
            )
                    // 可选：设置 Just 状态（完美时机释放）
                    .setComboStateJust((entity) -> ComboStateRegistry.JUDGEMENT_CUT_SLASH_JUST.getId())
                    // 可选：设置 Super 状态（超级释放）
                    .setComboStateSuper((entity) -> ComboStateRegistry.JUDGEMENT_CUT_END.getId())
    );

    /**
     * 示例：更简单的 SA，只使用一个 ComboState
     */
    public static final RegistryObject<SlashArts> SIMPLE_SLASH_ART = SLASH_ARTS.register(
            "simple_slash_art",
            () -> new SlashArts((entity) -> ComboStateRegistry.CIRCLE_SLASH.getId())
    );
}

