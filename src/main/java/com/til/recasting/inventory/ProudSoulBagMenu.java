package com.til.recasting.inventory;

import com.til.recasting.item.ProudSoulBagItem;
import com.til.recasting.item.ProudSoulBagStorage;
import com.til.recasting.network.ProudSoulBagSyncMessage;
import com.til.recasting.registry.RecastingMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 耀魂背包菜单：仅含玩家背包槽；虚拟存储格由网络包驱动。
 */
public class ProudSoulBagMenu extends AbstractContainerMenu {

    public static final int GRID_COLUMNS = 9;
    public static final int GRID_ROWS = 5;
    public static final int PAGE_SIZE = GRID_COLUMNS * GRID_ROWS;

    private final InteractionHand hand;
    private final Inventory playerInventory;
    private final int bagInventoryIndex;

    private List<ProudSoulBagStorage.StoredEntry> clientEntries = Collections.emptyList();
    private boolean clientEntriesReady;

    public ProudSoulBagMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readEnum(InteractionHand.class));
        int size = buf.readVarInt();
        List<ProudSoulBagStorage.StoredEntry> initial = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ItemStack template = ProudSoulBagStorage.readTemplate(buf);
            long count = buf.readVarLong();
            initial.add(new ProudSoulBagStorage.StoredEntry(template, count));
        }
        this.clientEntries = List.copyOf(initial);
        this.clientEntriesReady = true;
    }

    public ProudSoulBagMenu(int containerId, Inventory playerInventory, InteractionHand hand) {
        super(RecastingMenus.PROUD_SOUL_BAG.get(), containerId);
        this.hand = hand;
        this.playerInventory = playerInventory;
        this.bagInventoryIndex = hand == InteractionHand.MAIN_HAND
                ? playerInventory.selected
                : Inventory.SLOT_OFFHAND;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 139 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            int index = col;
            this.addSlot(new Slot(playerInventory, index, 8 + col * 18, 197) {
                @Override
                public boolean mayPickup(@NotNull Player player) {
                    if (hand == InteractionHand.MAIN_HAND && index == playerInventory.selected) {
                        return false;
                    }
                    return super.mayPickup(player);
                }
            });
        }
    }

    public InteractionHand getHand() {
        return hand;
    }

    public @NotNull ItemStack getBagStack() {
        return playerInventory.getItem(bagInventoryIndex);
    }

    public void setClientEntries(List<ProudSoulBagStorage.StoredEntry> entries) {
        this.clientEntries = List.copyOf(entries);
        this.clientEntriesReady = true;
    }

    public @NotNull List<ProudSoulBagStorage.StoredEntry> getDisplayEntries() {
        if (clientEntriesReady) {
            return clientEntries;
        }
        return ProudSoulBagStorage.list(getBagStack(), playerInventory.player.registryAccess());
    }

    public void syncContentsToClient(@Nullable Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        ItemStack bag = getBagStack();
        if (!bag.isEmpty()) {
            playerInventory.setItem(bagInventoryIndex, bag.copy());
        }
        List<ProudSoulBagStorage.StoredEntry> entries =
                ProudSoulBagStorage.list(getBagStack(), player.registryAccess());
        List<ProudSoulBagSyncMessage.StoredEntry> payload = entries.stream()
                .map(entry -> new ProudSoulBagSyncMessage.StoredEntry(entry.template(), entry.count()))
                .toList();
        PacketDistributor.sendToPlayer(serverPlayer, new ProudSoulBagSyncMessage(payload));
        broadcastChanges();
    }

    public static void writeOpenData(
            RegistryFriendlyByteBuf buf,
            InteractionHand hand,
            ItemStack bag,
            Player player
    ) {
        buf.writeEnum(hand);
        List<ProudSoulBagStorage.StoredEntry> entries =
                ProudSoulBagStorage.list(bag, player.registryAccess());
        buf.writeVarInt(entries.size());
        for (ProudSoulBagStorage.StoredEntry entry : entries) {
            ProudSoulBagStorage.writeTemplate(buf, entry.template());
            buf.writeVarLong(entry.count());
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        ItemStack bag = getBagStack();
        return !bag.isEmpty() && bag.getItem() instanceof ProudSoulBagItem;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        if (!ProudSoulBagStorage.isProudSoul(stack)) {
            return ItemStack.EMPTY;
        }
        ItemStack bag = getBagStack();
        if (bag.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        ProudSoulBagStorage.insert(bag, stack, player.registryAccess());
        if (stack.getCount() == copy.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.setChanged();
        syncContentsToClient(player);
        return copy;
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (hand == InteractionHand.MAIN_HAND
                    && slot.container == playerInventory
                    && slot.getSlotIndex() == playerInventory.selected) {
                return;
            }
            if (hand == InteractionHand.OFF_HAND
                    && slot.container == playerInventory
                    && slot.getSlotIndex() == Inventory.SLOT_OFFHAND) {
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
    }

    public void handleVirtualAction(@NotNull Player player, @NotNull Action action, int virtualIndex) {
        ItemStack bag = getBagStack();
        if (bag.isEmpty() || !(bag.getItem() instanceof ProudSoulBagItem)) {
            return;
        }

        var registries = player.registryAccess();
        ItemStack carried = getCarried();
        switch (action) {
            case PICKUP_OR_SET_DOWN -> {
                if (!carried.isEmpty()) {
                    if (ProudSoulBagStorage.isProudSoul(carried)) {
                        ProudSoulBagStorage.insert(bag, carried, registries);
                    }
                } else {
                    ProudSoulBagStorage.StoredEntry entry =
                            ProudSoulBagStorage.getByIndex(bag, virtualIndex, registries);
                    if (entry.template().isEmpty() || entry.count() <= 0) {
                        return;
                    }
                    long want = Math.min(entry.template().getMaxStackSize(), entry.count());
                    ItemStack extracted =
                            ProudSoulBagStorage.extractByIndex(bag, virtualIndex, want, registries);
                    setCarried(extracted);
                }
            }
            case SPLIT_OR_PLACE_SINGLE -> {
                if (!carried.isEmpty()) {
                    if (!ProudSoulBagStorage.isProudSoul(carried)) {
                        return;
                    }
                    ItemStack one = carried.split(1);
                    ProudSoulBagStorage.insert(bag, one, registries);
                    if (!one.isEmpty()) {
                        carried.grow(one.getCount());
                    }
                } else {
                    ProudSoulBagStorage.StoredEntry entry =
                            ProudSoulBagStorage.getByIndex(bag, virtualIndex, registries);
                    if (entry.template().isEmpty() || entry.count() <= 0) {
                        return;
                    }
                    long half = (Math.min(entry.template().getMaxStackSize(), entry.count()) + 1) / 2;
                    ItemStack extracted =
                            ProudSoulBagStorage.extractByIndex(bag, virtualIndex, half, registries);
                    setCarried(extracted);
                }
            }
            case SHIFT_CLICK -> {
                ProudSoulBagStorage.StoredEntry entry =
                        ProudSoulBagStorage.getByIndex(bag, virtualIndex, registries);
                if (entry.template().isEmpty() || entry.count() <= 0) {
                    return;
                }
                long want = Math.min(entry.template().getMaxStackSize(), entry.count());
                ItemStack extracted =
                        ProudSoulBagStorage.extractByIndex(bag, virtualIndex, want, registries);
                if (extracted.isEmpty()) {
                    return;
                }
                if (!player.getInventory().add(extracted) && !extracted.isEmpty()) {
                    ProudSoulBagStorage.insert(bag, extracted, registries);
                    if (!extracted.isEmpty()) {
                        player.drop(extracted, false);
                    }
                }
            }
            case ROLL_EXTRACT_ONE -> {
                if (!carried.isEmpty()) {
                    if (!ItemStack.isSameItemSameComponents(
                            carried,
                            ProudSoulBagStorage.getByIndex(bag, virtualIndex, registries).template()
                    )) {
                        return;
                    }
                    if (carried.getCount() >= carried.getMaxStackSize()) {
                        return;
                    }
                    ItemStack one =
                            ProudSoulBagStorage.extractByIndex(bag, virtualIndex, 1, registries);
                    if (!one.isEmpty()) {
                        carried.grow(1);
                    }
                } else {
                    ItemStack one =
                            ProudSoulBagStorage.extractByIndex(bag, virtualIndex, 1, registries);
                    setCarried(one);
                }
            }
            case ROLL_INSERT_ONE -> {
                if (carried.isEmpty() || !ProudSoulBagStorage.isProudSoul(carried)) {
                    return;
                }
                ItemStack one = carried.split(1);
                ProudSoulBagStorage.insert(bag, one, registries);
                if (!one.isEmpty()) {
                    carried.grow(one.getCount());
                }
            }
            default -> {
            }
        }
        syncContentsToClient(player);
    }

    public enum Action {
        PICKUP_OR_SET_DOWN,
        SPLIT_OR_PLACE_SINGLE,
        SHIFT_CLICK,
        ROLL_EXTRACT_ONE,
        ROLL_INSERT_ONE
    }
}
