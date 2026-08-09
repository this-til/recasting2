package com.til.recasting.registry.se;

import com.til.recasting.event.DoSlashExtendEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 催熟
 * 挥刀时对周围随机作物施加骨粉效果
 */
public class FertilizeSpecialEffect extends ExtendedSpecialEffect {

    private final int range = 2;

    @SubscribeEvent
    public void onEvent(DoSlashExtendEvent event) {
        if (!hasSpecialEffect(event.getSlashBladeState())) {
            return;
        }

        if (!(event.getUser().level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos origin = event.getUser().blockPosition();
        List<BlockPos> crops = new ArrayList<>();

        for(int x = -range; x <= range; x++) {
            for(int y = -range; y <= range; y++) {
                for(int z = -range; z <= range; z++) {
                    BlockPos pos = origin.offset(x, y, z);
                    BlockState state = serverLevel.getBlockState(pos);
                    if (!(state.getBlock() instanceof CropBlock crop)) {
                        continue;
                    }
                    if (!crop.isValidBonemealTarget(serverLevel, pos, state, false)) {
                        continue;
                    }
                    crops.add(pos.immutable());
                }
            }
        }

        if (crops.isEmpty()) {
            return;
        }

        BlockPos target = crops.get(serverLevel.getRandom().nextInt(crops.size()));
        BoneMealItem.growCrop(ItemStack.EMPTY, serverLevel, target);
        serverLevel.levelEvent(1505, target, 0);
    }
}
