package com.til.recasting.registry;

import com.til.recasting.Recasting;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.event.client.UserPoseOverrider;
import mods.flammpfeil.slashblade.event.handler.FallHandler;
import mods.flammpfeil.slashblade.init.DefaultResources;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import mods.flammpfeil.slashblade.slasharts.JudgementCut;
import mods.flammpfeil.slashblade.slasharts.SlashArts;
import mods.flammpfeil.slashblade.util.AdvancementHelper;
import mods.flammpfeil.slashblade.util.AttackManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Recasting Combo State 注册表
 * <p>
 * 用于为 SlashArts (SA) 创建自定义的 Combo State（连招状态）
 * <p>
 * 使用说明：
 * 1. 直接在 SlashArtsRegistry 中使用 registerExtendedSA() 方法
 * 2. ComboState 会自动创建并关联到 SlashArts
 * 3. 如果需要单独的 ComboState（不关联 SA），可以在此注册
 * <p>
 * ComboState.Builder 常用方法：
 * - startAndEnd(int start, int end)：设置动画起止帧
 * - priority(int priority)：优先级，数值越高优先级越高
 * - motionLoc(ResourceLocation)：动画文件位置
 * - speed(float speed)：播放速度倍率
 * - loop()：循环播放
 * - timeout(int ms)：超时时间（毫秒）
 * - aerial()：标记为空中动作
 * - next(Function)：下一个状态
 * - nextOfTimeout(Function)：超时后的下一个状态
 * - clickAction(Consumer)：点击时执行的动作
 * - addTickAction(Consumer)：每 tick 执行的动作
 * - addHoldAction(Consumer)：持续按住时执行的动作
 * - addHitEffect(BiConsumer)：命中效果
 * - releaseAction(BiFunction)：释放时的动作
 */
public class RecastingComboStateRegistry {

    /**
     * 创建 DeferredRegister，用于注册 Combo States
     */
    public static final DeferredRegister<ComboState> COMBO_STATE = DeferredRegister.create(
            ComboState.REGISTRY_KEY,
            Recasting.MODID
    );

    /**
     * 创建注册表实例
     */
    public static final Supplier<IForgeRegistry<ComboState>> REGISTRY = COMBO_STATE.makeRegistry(RegistryBuilder::new);



}

