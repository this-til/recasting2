package com.til.recasting.mixin;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * 修复 BladeComparator 跨命名空间排序。
 */
@Mixin(value = SlashBladeDefinition.BladeComparator.class)
public abstract class SlashBladeDefinitionBladeComparatorMixin {

    @Overwrite(remap = false)
    public int compare(Holder.Reference<SlashBladeDefinition> left, Holder.Reference<SlashBladeDefinition> right) {
        ResourceLocation leftKey = left.key().location();
        ResourceLocation rightKey = right.key().location();

        String leftNamespace = leftKey.getNamespace();
        String rightNamespace = rightKey.getNamespace();

        boolean leftIsSlashBlade = leftNamespace.equalsIgnoreCase(SlashBlade.MODID);
        boolean rightIsSlashBlade = rightNamespace.equalsIgnoreCase(SlashBlade.MODID);

        if (leftIsSlashBlade && !rightIsSlashBlade) {
            return -1;
        }
        if (!leftIsSlashBlade && rightIsSlashBlade) {
            return 1;
        }

        int namespaceCompare = leftNamespace.compareToIgnoreCase(rightNamespace);
        if (namespaceCompare != 0) {
            return namespaceCompare;
        }

        return leftKey.getPath().compareToIgnoreCase(rightKey.getPath());
    }
}
