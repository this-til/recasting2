package com.til.recasting.mixin;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Mixin 用于修复 BladeComparator 的比较逻辑
 * 修复当两个命名空间不同且都不是 "slashblade" 时的排序问题
 */
@Mixin(targets = "mods.flammpfeil.slashblade.registry.slashblade.SlashBladeDefinition$BladeComparator")
public abstract class SlashBladeDefinitionBladeComparatorMixin {

    /**
     * 修复比较器逻辑，确保满足比较器契约（自反性、反对称性、传递性）
     * 排序规则：
     * 1. "slashblade" 命名空间的条目优先
     * 2. 其他命名空间按命名空间字母顺序排序
     * 3. 相同命名空间内按路径字母顺序排序
     *
     * @author til
     * @reason 修复比较器逻辑
     */
    @Overwrite(remap = false)
    public int compare(Reference<SlashBladeDefinition> left, Reference<SlashBladeDefinition> right) {
        ResourceLocation leftKey = left.key().location();
        ResourceLocation rightKey = right.key().location();

        String leftNamespace = leftKey.getNamespace();
        String rightNamespace = rightKey.getNamespace();

        // 检查是否是 slashblade 命名空间
        boolean leftIsSlashBlade = leftNamespace.equalsIgnoreCase(SlashBlade.MODID);
        boolean rightIsSlashBlade = rightNamespace.equalsIgnoreCase(SlashBlade.MODID);

        // 规则 1: slashblade 命名空间优先
        if (leftIsSlashBlade && !rightIsSlashBlade) {
            return -1;
        }
        if (!leftIsSlashBlade && rightIsSlashBlade) {
            return 1;
        }

        // 规则 2: 如果命名空间不同，按命名空间排序
        int namespaceCompare = leftNamespace.compareToIgnoreCase(rightNamespace);
        if (namespaceCompare != 0) {
            return namespaceCompare;
        }

        // 规则 3: 命名空间相同，按路径排序
        return leftKey.getPath().compareToIgnoreCase(rightKey.getPath());
    }
}

