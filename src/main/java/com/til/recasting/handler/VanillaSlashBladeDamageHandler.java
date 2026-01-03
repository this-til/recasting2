package com.til.recasting.handler;

import com.til.recasting.Config;
import com.til.recasting.event.AttackAmplifierEvent;
import com.til.recasting.registry.RecastingAttackTypes;
import mods.flammpfeil.slashblade.SlashBladeConfig;
import mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider;
import mods.flammpfeil.slashblade.capability.slashblade.ISlashBladeState;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.til.recasting.Recasting.MODID;

/**
 * 原版 SlashBlade 伤害加成事件监听器
 * 将原版的伤害计算逻辑（各种加成的乘区）独立处理
 */
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VanillaSlashBladeDamageHandler {

    @SubscribeEvent
    public static void onAttackAmplifier(AttackAmplifierEvent event) {

        // === 第一乘区：基础加成 ===

        // 1. 附魔加成（杀手类附魔：亡灵杀手、节肢杀手）
        ItemStack weapon = event.getAttacker().getMainHandItem();

        // 亡灵杀手：对不死生物伤害加成
        if (event.getTarget() instanceof net.minecraft.world.entity.LivingEntity living) {
            int smiteLevel = weapon.getEnchantmentLevel(Enchantments.SMITE);
            if (smiteLevel > 0 && living.getMobType() == MobType.UNDEAD) {
                event.addModifiedRatioAmplifier((float) (smiteLevel * Config.SMITE_ATTACK_BONUS.get()));
            }

            // 节肢杀手：对节肢生物伤害加成
            int baneOfArthropodsLevel = weapon.getEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS);
            if (baneOfArthropodsLevel > 0 && living.getMobType() == MobType.ARTHROPOD) {
                event.addModifiedRatioAmplifier((float) (baneOfArthropodsLevel * Config.BANE_OF_ARTHROPODS_ATTACK_BONUS.get()));
            }
        }

        // 2. 评分等级加成
        // 从最低（0.0）到最高（6.0）线性增长，增幅从 0% 到配置的最大值
        float rankLevel = event.getAttacker()
                .getCapability(ConcentrationRankCapabilityProvider.RANK_POINT)
                .map(rp -> rp.getRankLevel(event.getAttacker().level().getGameTime()))
                .orElse(0.0f);

        // 线性映射：rankLevel [0.0, 6.0] -> 增幅 [0%, rankMaxBonus]
        float rankMaxBonus = Config.RANK_MAX_BONUS.get().floatValue();
        float rankBonus = Math.min(rankLevel / 6.0f * rankMaxBonus, rankMaxBonus);
        if (rankBonus > 0) {
            event.addModifiedRatioAmplifier(rankBonus);
        }

        // 3. 火焰附加附魔
        Entity target = event.getTarget();
        int fireAspect = weapon.getEnchantmentLevel(Enchantments.FIRE_ASPECT);
        if (fireAspect > 0 && !target.fireImmune()) {
            event.addDamageSourceInfo(
                    event.getAttacker().damageSources().onFire(),
                    (float) (Config.FIRE_ASPECT_DAMAGE.get() * fireAspect)
            );
        }

        // 4. 力量附魔（仅对召唤剑攻击生效）
        int powerLevel = weapon.getEnchantmentLevel(Enchantments.POWER_ARROWS);
        if (powerLevel > 0 && event.getAttackTypeList().contains(RecastingAttackTypes.SUMMOND_SWORD_ATTACK.get())) {
            event.addModifiedRatioAmplifier((float) (Config.POWER_ATTACK_BONUS.get() * powerLevel));
        }

        // 5. 精炼等级加成
        event.getItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(state -> {
            int refine = state.getRefine();
            if (refine > 0) {
                event.addModifiedRatioAmplifier((float) (Config.REFINE_ATTACK_BONUS.get() * refine));
            }

            // 6. 击杀数加成
            int killCount = state.getKillCount();
            if (killCount > 1000) {
                event.addMechanismModifiedRatioAmplifier(Config.THOUSAND_KILL_ATTACK_BONUS.get().floatValue());
            }
            if (killCount > 10000) {
                event.addMechanismModifiedRatioAmplifier(Config.TEN_THOUSAND_KILL_ATTACK_BONUS.get().floatValue());
            }

            // 7. 精炼数里程碑加成（第二乘区）
            if (refine > 1000) {
                event.addMechanismModifiedRatioAmplifier(Config.THOUSAND_REFINE_ATTACK_BONUS.get().floatValue());
            }
            if (refine > 10000) {
                event.addMechanismModifiedRatioAmplifier(Config.TEN_THOUSAND_REFINE_ATTACK_BONUS.get().floatValue());
            }
        });

        // === 第二乘区：机制加成 ===

        // 5. 配置文件中的全局伤害倍率
        double configDamageMultiplier = SlashBladeConfig.SLASHBLADE_DAMAGE_MULTIPLIER.get();
        if (configDamageMultiplier != 1.0) {
            event.addMechanismModifiedRatioAmplifier((float) (configDamageMultiplier - 1));
        }
    }
}

