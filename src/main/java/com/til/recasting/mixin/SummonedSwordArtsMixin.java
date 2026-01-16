package com.til.recasting.mixin;

import com.til.recasting.Config;
import com.til.recasting.entity.SummondSwordEntity;
import com.til.recasting.registry.RecastingEntities;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.ability.SummonedSwordArts;
import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.capability.inputstate.CapabilityInputState;
import mods.flammpfeil.slashblade.capability.slashblade.SlashBladeState;
import mods.flammpfeil.slashblade.entity.*;
import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.item.SwordType;
import mods.flammpfeil.slashblade.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Mixin 用于修改召唤剑实体的基础伤害值
 * 将 powerLevel（力量附魔等级）替换为配置的基础伤害倍率
 */
@Mixin(value = SummonedSwordArts.class)
public abstract class SummonedSwordArtsMixin {

    /**
     * @author til
     * @reason 使用自己的类型
     */
    @SubscribeEvent
    @Overwrite(remap = false)
    public void onInputChange(InputCommandEvent event) {
    }

}
