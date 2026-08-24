package com.til.recasting.handler;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.til.recasting.Recasting;
import com.til.recasting.constant.RecastingLanguageKeys;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * FE 调试指令：/recasting fe fill|set
 */
@EventBusSubscriber(modid = Recasting.MODID)
public final class RecastingFeCommandHandler {

    private RecastingFeCommandHandler() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("recasting")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("fe")
                                .then(Commands.literal("fill")
                                        .executes(ctx -> fill(ctx, ctx.getSource().getPlayerOrException()))
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> fill(ctx, EntityArgument.getPlayer(ctx, "player")))
                                        )
                                )
                                .then(Commands.literal("set")
                                        .then(Commands.argument("amount", LongArgumentType.longArg(0L))
                                                .executes(ctx -> set(
                                                        ctx,
                                                        ctx.getSource().getPlayerOrException(),
                                                        LongArgumentType.getLong(ctx, "amount")
                                                ))
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(ctx -> set(
                                                                ctx,
                                                                EntityArgument.getPlayer(ctx, "player"),
                                                                LongArgumentType.getLong(ctx, "amount")
                                                        ))
                                                )
                                        )
                                )
                        )
        );
    }

    private static int fill(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!isFeBlade(stack)) {
            ctx.getSource().sendFailure(Component.translatable(RecastingLanguageKeys.COMMAND_FE_NOT_FE_BLADE));
            return 0;
        }
        if (!FeEnergyHelper.fillToCapacity(stack)) {
            ctx.getSource().sendFailure(Component.translatable(RecastingLanguageKeys.COMMAND_FE_FAILED));
            return 0;
        }
        ctx.getSource().sendSuccess(
                () -> Component.translatable(
                        RecastingLanguageKeys.COMMAND_FE_FILL_SUCCESS,
                        player.getDisplayName(),
                        FeEnergyHelper.formatEnergy(FeEnergyHelper.getStored(stack))
                ),
                true
        );
        return 1;
    }

    private static int set(CommandContext<CommandSourceStack> ctx, ServerPlayer player, long amount) {
        ItemStack stack = player.getMainHandItem();
        if (!isFeBlade(stack)) {
            ctx.getSource().sendFailure(Component.translatable(RecastingLanguageKeys.COMMAND_FE_NOT_FE_BLADE));
            return 0;
        }
        if (!FeEnergyHelper.setStored(stack, amount)) {
            ctx.getSource().sendFailure(Component.translatable(RecastingLanguageKeys.COMMAND_FE_FAILED));
            return 0;
        }
        long stored = FeEnergyHelper.getStored(stack);
        ctx.getSource().sendSuccess(
                () -> Component.translatable(
                        RecastingLanguageKeys.COMMAND_FE_SET_SUCCESS,
                        player.getDisplayName(),
                        FeEnergyHelper.formatEnergy(stored)
                ),
                true
        );
        return 1;
    }

    private static boolean isFeBlade(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof ItemSlashBlade
                && FeEnergyHelper.hasFeCapacity(stack);
    }
}
