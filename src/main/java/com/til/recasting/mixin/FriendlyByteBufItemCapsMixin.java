package com.til.recasting.mixin;

import com.til.recasting.util.ItemStackShareCapSync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

/**
 * ItemStack.getShareTag 为 IForgeItemStack 默认方法，无法直接 Inject；在容器同步读写处嵌入 Cap。
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufItemCapsMixin {

    /**
     * {@code writeItemStack(ItemStack, boolean)} 为 Forge 补丁方法（vanilla 仅有 writeItem）。
     */
    @Redirect(
            method = "writeItemStack(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/network/FriendlyByteBuf;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getShareTag()Lnet/minecraft/nbt/CompoundTag;",
                    remap = false
            ),
            remap = false
    )
    private CompoundTag recasting$writeShareTagWithCaps(ItemStack stack) {
        // 走 Item#getShareTag，避开对本 Redirect 的递归
        return ItemStackShareCapSync.appendToShareTag(stack, stack.getItem().getShareTag(stack));
    }

    @Redirect(
            method = "readItem()Lnet/minecraft/world/item/ItemStack;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;readShareTag(Lnet/minecraft/nbt/CompoundTag;)V",
                    remap = false
            )
    )
    private void recasting$readShareTagWithCaps(ItemStack stack, @Nullable CompoundTag nbt) {
        ItemStackShareCapSync.readFromShareTag(stack, nbt);
    }
}
