package com.til.recasting.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.til.recasting.registry.requir.SlashBladeItems;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * 匹配带指定 {@code SpecialAttackType} 的耀魂宝珠。
 */
public record SlashArtsSphereItemPredicate(ResourceLocation slashArtsId) implements ItemSubPredicate {

    private static final String SPECIAL_ATTACK_TYPE = "SpecialAttackType";

    public static final Codec<SlashArtsSphereItemPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("slash_arts").forGetter(SlashArtsSphereItemPredicate::slashArtsId)
    ).apply(instance, SlashArtsSphereItemPredicate::new));

    @Override
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(SlashBladeItems.PROUDSOUL_SPHERE.get())) {
            return false;
        }
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }
        CompoundTag tag = customData.copyTag();
        if (!tag.contains(SPECIAL_ATTACK_TYPE)) {
            return false;
        }
        ResourceLocation stored = ResourceLocation.tryParse(tag.getString(SPECIAL_ATTACK_TYPE));
        return slashArtsId.equals(stored);
    }
}
