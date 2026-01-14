package com.til.recasting.mixin;

import com.til.recasting.handler.SlashBladeRegistryHelper;
import lombok.extern.slf4j.Slf4j;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.recipe.RequestDefinition;
import mods.flammpfeil.slashblade.registry.slashblade.RenderDefinition;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * Mixin 用于在 RequestDefinition.initItemStack 方法中从 SlashBladeDefinition 注册表
 * 加载同名物品的模型和贴图并复制到 ItemStack 中
 */
@Slf4j
@Mixin(RequestDefinition.class)
public abstract class RequestDefinitionMixin {

    @Shadow
    public abstract ResourceLocation name();

    /**
     * 在 initItemStack 方法返回之前，从 SlashBladeDefinition 注册表中获取模型和贴图
     * 并复制到 ItemStack 的 SlashBladeState 中
     */
    @Inject(method = "initItemStack", at = @At("RETURN"), remap = false)
    private void recasting$copyModelAndTextureFromRegistry(ItemStack blade, CallbackInfo ci) {
        if (blade == null || blade.isEmpty()) {
            return;
        }

        ResourceLocation name = this.name();
        if (name == null || name.equals(SlashBlade.prefix("none"))) {
            return;
        }

        try {
            // 获取 SlashBladeState
            var stateOpt = blade.getCapability(ItemSlashBlade.BLADESTATE);
            if (!stateOpt.isPresent()) {
                log.warn("无法获取 SlashBladeState for item: {}", blade);
                return;
            }

            //noinspection DataFlowIssue
            ISlashBladeState state = stateOpt.orElse(null);

            // 尝试从注册表中获取 SlashBladeDefinition
            // 注意：这里需要访问注册表，但在服务端和客户端的处理方式可能不同
            // 我们首先尝试从 Level 获取，如果没有 Level，则跳过
            Optional<SlashBladeDefinition> definitionOpt = getDefinitionFromRegistry(name);
            
            if (definitionOpt.isPresent()) {
                SlashBladeDefinition definition = definitionOpt.get();
                RenderDefinition renderDef = definition.getRenderDefinition();
                
                // 复制模型和贴图到 ItemStack
                ResourceLocation modelName = renderDef.getModelName();
                ResourceLocation textureName = renderDef.getTextureName();
                
                if (modelName != null) {
                    state.setModel(modelName);
                    log.debug("已设置模型: {} for blade: {}", modelName, name);
                }
                
                if (textureName != null) {
                    state.setTexture(textureName);
                    log.debug("已设置贴图: {} for blade: {}", textureName, name);
                }
                
                // 同时复制其他渲染相关属性
                state.setColorCode(renderDef.getSummonedSwordColor());
                state.setEffectColorInverse(renderDef.isSummonedSwordColorInverse());
                state.setCarryType(renderDef.getStandbyRenderType());
                
                // 重新序列化 state 到 NBT
                blade.getOrCreateTag().put("bladeState", state.serializeNBT());
                
                log.debug("成功从注册表复制模型和贴图 for blade: {}", name);
            } else {
                log.debug("在注册表中未找到 SlashBladeDefinition: {}", name);
            }
        } catch (Exception e) {
            log.error("从注册表复制模型和贴图时发生错误 for blade: {}", name, e);
        }
    }

    /**
     * 从注册表中获取 SlashBladeDefinition
     * 使用 SlashBladeRegistryHelper 辅助类来处理不同环境下的注册表访问
     */
    private Optional<SlashBladeDefinition> getDefinitionFromRegistry(ResourceLocation name) {
        return SlashBladeRegistryHelper.getDefinition(name);
    }
}

