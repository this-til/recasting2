package com.til.recasting.mixin;

import com.til.recasting.capability.ISpecialEffectCrystalData;
import com.til.recasting.capability.PropertiesDefinitionExtension;
import com.til.recasting.handler.CapabilityRegistryHandler;
import com.til.recasting.handler.SpecialEffectTooltipHelper;
import com.til.recasting.registry.SlashArtsRegistry;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.registry.SpecialEffectsRegistry;
import mods.flammpfeil.slashblade.registry.specialeffects.SpecialEffect;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 混入 ItemSlashBlade 的 appendSpecialEffects 方法
 * 在原始方法之后添加 ExtendedSpecialEffect 的等级显示
 * 并在 forEach 中过滤掉 ExtendedSpecialEffect
 */
@Mixin(value = ItemSlashBlade.class)
public class ItemSlashBladeMixin {

    /**
     * 在 appendSpecialEffects 方法的开头注入，先处理 ExtendedSpecialEffect
     * 然后让原方法继续处理非 ExtendedSpecialEffect 的 SE
     *
     * @author til
     * @reason 为了支持 ExtendedSpecialEffect 的特殊显示逻辑，需要完全重写此方法
     */
    @Overwrite(remap = false)
    @OnlyIn(Dist.CLIENT)
    public void appendSpecialEffects(List<Component> tooltip, @NotNull ISlashBladeState s) {

    }

    @Inject(method = "appendHoverText", at = @At("RETURN"))
    @OnlyIn(Dist.CLIENT)
    public void appendSpecialEffectHoverText(ItemStack stack, Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn, CallbackInfo ci) {
        stack.getCapability(ItemSlashBlade.BLADESTATE).ifPresent((s) -> {
            if (s.getSpecialEffects().isEmpty()) {
                return;
            }

            Minecraft mcinstance = Minecraft.getInstance();
            Player player = mcinstance.player;

            if (player == null) {
                return;
            }

            IForgeRegistry<SpecialEffect> specialEffects = SpecialEffectsRegistry.REGISTRY.get();

            stack.getCapability(CapabilityRegistryHandler.PROPERTIES_DEFINITION_EXTENSION).ifPresent(es -> {

                for(ResourceLocation specialEffectResourceLocation : s.getSpecialEffects()) {
                    SpecialEffect specialEffect = specialEffects.getValue(specialEffectResourceLocation);

                    if (specialEffect == null) {
                        continue;
                    }

                    boolean showingLevel = SpecialEffect.getRequestLevel(specialEffectResourceLocation) > 0;

                    Component nameComponent;
                    Component valueComponent;
                    if (specialEffect instanceof com.til.recasting.registry.se.ExtendedSpecialEffect extendedSpecialEffect) {
                        nameComponent = SpecialEffect.getDescription(specialEffectResourceLocation)
                                .copy()
                                .withStyle(extendedSpecialEffect.isSpecial() ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY);
                        valueComponent = Component.literal(
                                es.getExtendedSpecialLevels(specialEffectResourceLocation) + "/" + extendedSpecialEffect.getMaxLevel()
                        ).withStyle(ChatFormatting.LIGHT_PURPLE);
                        tooltip.add(SpecialEffectTooltipHelper.createEffectLine(
                                extendedSpecialEffect,
                                nameComponent,
                                valueComponent
                        ));
                    } else {
                        valueComponent = Component.literal(
                                        showingLevel
                                                ? String.valueOf(SpecialEffect.getRequestLevel(specialEffectResourceLocation))
                                                : ""
                                )
                                .withStyle(
                                        SpecialEffect.isEffective(
                                                specialEffectResourceLocation,
                                                player.experienceLevel
                                        )
                                                ? ChatFormatting.RED
                                                : ChatFormatting.DARK_GRAY
                                );
                        nameComponent = SpecialEffect.getDescription(specialEffectResourceLocation)
                                .copy()
                                .withStyle(ChatFormatting.GRAY);
                        tooltip.add(
                                Component.translatable(
                                                "slashblade.tooltip.special_effect",
                                                nameComponent,
                                                valueComponent
                                        )
                                        .withStyle(ChatFormatting.GRAY));
                    }

                    // 按住Shift时显示SE介绍文本
                    if (flagIn.isAdvanced() && specialEffect instanceof com.til.recasting.registry.se.ExtendedSpecialEffect extendedSE) {
                        tooltip.add(SpecialEffectTooltipHelper.createDescription(extendedSE));
                    }
                }

            });


        });

    }

}
