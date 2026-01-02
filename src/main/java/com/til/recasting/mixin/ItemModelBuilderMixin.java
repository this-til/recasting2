package com.til.recasting.mixin;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin 用于移除 ModelBuilder.texture 方法中的 checkArgument 检查
 * 允许使用不存在的纹理资源位置
 */
@Mixin(ModelBuilder.class)
public abstract class ItemModelBuilderMixin {

    /**
     * 重定向 checkArgument 调用，使其不执行任何检查
     * 这样可以允许使用不存在的纹理资源位置
     */
    @Redirect(
            method = "texture(Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraftforge/client/model/generators/ModelBuilder;",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/base/Preconditions;checkArgument(ZLjava/lang/String;Ljava/lang/Object;)V"
            ),
            remap = false
    )
    private void recasting$skipTextureCheck(boolean expression, String errorMessageTemplate, Object errorMessageArg) {
        // 不执行任何检查，直接返回
        // 这样可以允许使用不存在的纹理资源位置
    }
}

