package com.til.recasting.mixin;

import com.til.recasting.util.ItemStackShareCapSync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 * 容器同步（limitedTag=true → getShareTag）与创造栏取物（limitedTag=false → getTag）
 * 均在写入 NBT 前嵌入本模组 Cap。
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufItemCapsMixin {

    @Unique
    private static final ThreadLocal<ItemStack> RECASTING$WRITING_STACK = new ThreadLocal<>();

    @Inject(
            method = "writeItemStack(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/network/FriendlyByteBuf;",
            at = @At("HEAD"),
            remap = false
    )
    private void recasting$captureWritingStack(ItemStack stack, boolean limitedTag, CallbackInfoReturnable<FriendlyByteBuf> cir) {
        RECASTING$WRITING_STACK.set(stack);
    }

    @Inject(
            method = "writeItemStack(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/network/FriendlyByteBuf;",
            at = @At("RETURN"),
            remap = false
    )
    private void recasting$clearWritingStack(ItemStack stack, boolean limitedTag, CallbackInfoReturnable<FriendlyByteBuf> cir) {
        RECASTING$WRITING_STACK.remove();
    }

    /**
     * {@code writeItemStack} 为 Forge 方法；在 {@code writeNbt} 前统一补 Cap，覆盖两条分支。
     */
    @ModifyArg(
            method = "writeItemStack(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/network/FriendlyByteBuf;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/FriendlyByteBuf;writeNbt(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/network/FriendlyByteBuf;"
            ),
            index = 0,
            remap = false
    )
    private CompoundTag recasting$appendCapsBeforeWriteNbt(@Nullable CompoundTag tag) {
        ItemStack stack = RECASTING$WRITING_STACK.get();
        if (stack == null || stack.isEmpty()) {
            return tag;
        }
        return ItemStackShareCapSync.appendToShareTag(stack, tag);
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
