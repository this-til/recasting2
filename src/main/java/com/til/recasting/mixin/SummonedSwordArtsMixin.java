package com.til.recasting.mixin;

import com.til.recasting.Config;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.ability.SummonedSwordArts;
import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.entity.EntityAbstractSummonedSword;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.InputCommand;
import mods.flammpfeil.slashblade.util.StatHelper;
import mods.flammpfeil.slashblade.util.VectorHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.EnumSet;
import java.util.Optional;

/**
 * Mixin 用于修改召唤剑实体的基础伤害值
 * 将 powerLevel（力量附魔等级）替换为配置的基础伤害倍率
 */
@Mixin(value = SummonedSwordArts.class, remap = false)
public abstract class SummonedSwordArtsMixin {

    /**
     * 在 onInputChange 方法中修改 powerLevel 变量的值
     * 在 if (powerLevel <= 0) return; 之后将其设置为 0
     */
    @ModifyVariable(
            method = "onInputChange",
            at = @At(value = "LOAD", ordinal = 1),
            ordinal = 0
    )
    private int modifyPowerLevel(int powerLevel) {
        if (powerLevel > 0) {
            powerLevel = 0;
        }
        return powerLevel;
    }

}
