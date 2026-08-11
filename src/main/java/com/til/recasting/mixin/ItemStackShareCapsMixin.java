package com.til.recasting.mixin;

import com.til.recasting.Recasting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

/**
 * 网络 ShareTag 默认不含 ForgeCaps；在出口嵌入本模组 ItemStack Cap，入口还原，业务写入点无需双写。
 */
@Mixin(value = ItemStack.class, remap = false)
public abstract class ItemStackShareCapsMixin {

    @Unique
    private static final String RECASTING$FORGE_CAPS = "ForgeCaps";

    @Unique
    private static final String RECASTING$CAP_KEY_PREFIX = Recasting.MODID + ":";

    @Inject(method = "getShareTag", at = @At("RETURN"), cancellable = true)
    private void recasting$appendShareCaps(CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag filteredCaps = recasting$filterRecastingCaps(
                ((CapabilityProviderAccessor) this).recasting$serializeCaps()
        );
        if (filteredCaps == null || filteredCaps.isEmpty()) {
            return;
        }

        CompoundTag share = cir.getReturnValue();
        // 必须 copy：默认实现可能直接返回 live getTag()
        share = share == null ? new CompoundTag() : share.copy();
        share.put(RECASTING$FORGE_CAPS, filteredCaps);
        cir.setReturnValue(share);
    }

    @Inject(method = "readShareTag", at = @At("HEAD"), cancellable = true)
    private void recasting$readShareCaps(@Nullable CompoundTag nbt, CallbackInfo ci) {
        if (nbt == null || !nbt.contains(RECASTING$FORGE_CAPS)) {
            return;
        }

        CompoundTag caps = nbt.getCompound(RECASTING$FORGE_CAPS);
        CompoundTag rest = nbt.copy();
        rest.remove(RECASTING$FORGE_CAPS);

        ItemStack self = (ItemStack) (Object) this;
        self.getItem().readShareTag(self, rest.isEmpty() ? null : rest);
        ((CapabilityProviderAccessor) this).recasting$deserializeCaps(caps);
        ci.cancel();
    }

    @Unique
    @Nullable
    private static CompoundTag recasting$filterRecastingCaps(@Nullable CompoundTag allCaps) {
        if (allCaps == null || allCaps.isEmpty()) {
            return null;
        }

        CompoundTag filtered = new CompoundTag();
        for (String key : allCaps.getAllKeys()) {
            if (!key.startsWith(RECASTING$CAP_KEY_PREFIX)) {
                continue;
            }
            Tag value = allCaps.get(key);
            if (value != null) {
                filtered.put(key, value.copy());
            }
        }
        return filtered.isEmpty() ? null : filtered;
    }
}
