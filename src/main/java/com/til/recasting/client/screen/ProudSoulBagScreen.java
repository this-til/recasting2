package com.til.recasting.client.screen;

import com.til.recasting.inventory.ProudSoulBagMenu;
import com.til.recasting.item.ProudSoulBagStorage;
import com.til.recasting.network.NetworkManager;
import com.til.recasting.network.ProudSoulBagActionMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 耀魂背包界面：搜索、分页、AE2 风格虚拟格交互。
 */
@OnlyIn(Dist.CLIENT)
public class ProudSoulBagScreen extends AbstractContainerScreen<ProudSoulBagMenu> {

    private static final int GRID_LEFT = 8;
    private static final int GRID_TOP = 36;
    private static final int SLOT_SIZE = 18;

    private EditBox searchBox;
    private int page;
    private SortMode sortMode = SortMode.NAME;
    private List<IndexedEntry> frozenView = List.of();
    private boolean wasShiftDown;

    public ProudSoulBagScreen(ProudSoulBagMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.searchBox = new EditBox(this.font, this.leftPos + 8, this.topPos + 18, 100, 12, Component.literal("Search"));
        this.searchBox.setMaxLength(64);
        this.searchBox.setBordered(true);
        this.searchBox.setValue("");
        this.addWidget(this.searchBox);

        this.addRenderableWidget(Button.builder(Component.literal("<"), b -> changePage(-1))
                .bounds(this.leftPos + 112, this.topPos + 16, 18, 16)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal(">"), b -> changePage(1))
                .bounds(this.leftPos + 152, this.topPos + 16, 18, 16)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal("S"), b -> cycleSort())
                .bounds(this.leftPos + 132, this.topPos + 16, 18, 16)
                .build());
    }

    private void cycleSort() {
        this.sortMode = this.sortMode == SortMode.NAME ? SortMode.AMOUNT : SortMode.NAME;
        this.page = 0;
        this.wasShiftDown = false;
    }

    private void changePage(int delta) {
        int maxPage = Math.max(0, (filteredEntries().size() - 1) / ProudSoulBagMenu.PAGE_SIZE);
        this.page = Math.max(0, Math.min(maxPage, this.page + delta));
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.searchBox != null) {
            this.searchBox.tick();
        }
        boolean shift = hasShiftDown();
        if (shift && !wasShiftDown) {
            frozenView = filteredEntries();
        }
        if (!shift) {
            frozenView = List.of();
        }
        wasShiftDown = shift;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFFC6C6C6);
        graphics.fill(x + 4, y + 4, x + imageWidth - 4, y + 132, 0xFF8B8B8B);

        for (int row = 0; row < ProudSoulBagMenu.GRID_ROWS; row++) {
            for (int col = 0; col < ProudSoulBagMenu.GRID_COLUMNS; col++) {
                int sx = x + GRID_LEFT + col * SLOT_SIZE;
                int sy = y + GRID_TOP + row * SLOT_SIZE;
                graphics.fill(sx, sy, sx + 16, sy + 16, 0xFF373737);
            }
        }

        // 玩家物品栏背景槽
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int sx = x + 8 + col * 18;
                int sy = y + 140 + row * 18;
                graphics.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF8B8B8B);
            }
        }
        for (int col = 0; col < 9; col++) {
            int sx = x + 8 + col * 18;
            int sy = y + 198;
            graphics.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF8B8B8B);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        List<IndexedEntry> pageEntries = currentPageEntries();
        for (int i = 0; i < pageEntries.size(); i++) {
            IndexedEntry entry = pageEntries.get(i);
            int col = i % ProudSoulBagMenu.GRID_COLUMNS;
            int row = i / ProudSoulBagMenu.GRID_COLUMNS;
            int sx = this.leftPos + GRID_LEFT + col * SLOT_SIZE;
            int sy = this.topPos + GRID_TOP + row * SLOT_SIZE;
            ItemStack display = entry.entry().template().copyWithCount(1);
            graphics.renderItem(display, sx, sy);
            graphics.renderItemDecorations(this.font, display, sx, sy, formatCount(entry.entry().count()));
        }

        if (this.searchBox != null) {
            this.searchBox.render(graphics, mouseX, mouseY, partialTick);
        }

        int maxPage = Math.max(0, (viewEntries().size() - 1) / ProudSoulBagMenu.PAGE_SIZE);
        graphics.drawString(this.font, (page + 1) + "/" + (maxPage + 1), this.leftPos + 112, this.topPos + 6, 0x404040, false);

        int hovered = hoveredVirtualIndex(mouseX, mouseY);
        if (hovered >= 0 && hovered < pageEntries.size()) {
            IndexedEntry entry = pageEntries.get(hovered);
            ItemStack tip = entry.entry().template().copy();
            tip.setCount(1);
            List<Component> lines = new ArrayList<>();
            lines.add(tip.getHoverName());
            lines.add(Component.literal(String.valueOf(entry.entry().count())));
            graphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
        }

        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.searchBox != null && this.searchBox.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.searchBox);
            return true;
        }
        int local = hoveredVirtualIndex((int) mouseX, (int) mouseY);
        if (local >= 0) {
            List<IndexedEntry> pageEntries = currentPageEntries();
            boolean hasCarried = !this.menu.getCarried().isEmpty();
            if (hasCarried) {
                if (button == 0) {
                    sendAction(ProudSoulBagMenu.Action.PICKUP_OR_SET_DOWN, 0);
                } else if (button == 1) {
                    sendAction(ProudSoulBagMenu.Action.SPLIT_OR_PLACE_SINGLE, 0);
                }
                return true;
            }
            if (local < pageEntries.size()) {
                int storageIndex = pageEntries.get(local).storageIndex();
                if (hasShiftDown() && button == 0) {
                    sendAction(ProudSoulBagMenu.Action.SHIFT_CLICK, storageIndex);
                } else if (button == 0) {
                    sendAction(ProudSoulBagMenu.Action.PICKUP_OR_SET_DOWN, storageIndex);
                } else if (button == 1) {
                    sendAction(ProudSoulBagMenu.Action.SPLIT_OR_PLACE_SINGLE, storageIndex);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int local = hoveredVirtualIndex((int) mouseX, (int) mouseY);
        if (hasShiftDown() && local >= 0) {
            List<IndexedEntry> pageEntries = currentPageEntries();
            if (local < pageEntries.size()) {
                int storageIndex = pageEntries.get(local).storageIndex();
                if (delta > 0) {
                    sendAction(ProudSoulBagMenu.Action.ROLL_EXTRACT_ONE, storageIndex);
                } else {
                    sendAction(ProudSoulBagMenu.Action.ROLL_INSERT_ONE, storageIndex);
                }
                return true;
            }
            if (delta < 0 && !this.menu.getCarried().isEmpty()) {
                // 对准空格但持物：仍尝试滚入（用 index 0 仅当有持物时 ROLL_INSERT 不依赖格）
                sendAction(ProudSoulBagMenu.Action.ROLL_INSERT_ONE, 0);
                return true;
            }
        }
        if (delta > 0) {
            changePage(-1);
        } else {
            changePage(1);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchBox != null && this.searchBox.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.searchBox.setFocused(false);
                return true;
            }
            if (this.searchBox.keyPressed(keyCode, scanCode, modifiers) || this.searchBox.canConsumeInput()) {
                this.page = 0;
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.searchBox != null && this.searchBox.charTyped(codePoint, modifiers)) {
            this.page = 0;
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    private void sendAction(ProudSoulBagMenu.Action action, int storageIndex) {
        NetworkManager.INSTANCE.sendToServer(new ProudSoulBagActionMessage(action, storageIndex));
    }

    private int hoveredVirtualIndex(int mouseX, int mouseY) {
        int relX = mouseX - this.leftPos - GRID_LEFT;
        int relY = mouseY - this.topPos - GRID_TOP;
        if (relX < 0 || relY < 0) {
            return -1;
        }
        int col = relX / SLOT_SIZE;
        int row = relY / SLOT_SIZE;
        if (col >= ProudSoulBagMenu.GRID_COLUMNS || row >= ProudSoulBagMenu.GRID_ROWS) {
            return -1;
        }
        if (relX % SLOT_SIZE >= 16 || relY % SLOT_SIZE >= 16) {
            return -1;
        }
        return row * ProudSoulBagMenu.GRID_COLUMNS + col;
    }

    private List<IndexedEntry> viewEntries() {
        if (hasShiftDown() && !frozenView.isEmpty()) {
            return frozenView;
        }
        return filteredEntries();
    }

    private List<IndexedEntry> currentPageEntries() {
        List<IndexedEntry> all = viewEntries();
        int start = page * ProudSoulBagMenu.PAGE_SIZE;
        if (start >= all.size()) {
            return List.of();
        }
        int end = Math.min(all.size(), start + ProudSoulBagMenu.PAGE_SIZE);
        return all.subList(start, end);
    }

    private List<IndexedEntry> filteredEntries() {
        List<ProudSoulBagStorage.StoredEntry> raw = ProudSoulBagStorage.list(this.menu.getBagStack());
        String query = this.searchBox == null ? "" : this.searchBox.getValue().trim().toLowerCase(Locale.ROOT);
        List<IndexedEntry> result = new ArrayList<>();
        for (int i = 0; i < raw.size(); i++) {
            ProudSoulBagStorage.StoredEntry entry = raw.get(i);
            if (!query.isEmpty()) {
                String name = entry.template().getHoverName().getString().toLowerCase(Locale.ROOT);
                if (!name.contains(query)) {
                    continue;
                }
            }
            result.add(new IndexedEntry(i, entry));
        }
        if (sortMode == SortMode.NAME) {
            result.sort(Comparator.comparing(e -> e.entry().template().getHoverName().getString()));
        } else {
            result.sort(Comparator.comparingLong((IndexedEntry e) -> e.entry().count()).reversed());
        }
        return result;
    }

    private static String formatCount(long count) {
        if (count < 1000) {
            return String.valueOf(count);
        }
        if (count < 1_000_000) {
            return String.format(Locale.ROOT, "%.1fK", count / 1000.0);
        }
        if (count < 1_000_000_000) {
            return String.format(Locale.ROOT, "%.1fM", count / 1_000_000.0);
        }
        return String.format(Locale.ROOT, "%.1fG", count / 1_000_000_000.0);
    }

    private enum SortMode {
        NAME,
        AMOUNT
    }

    private record IndexedEntry(int storageIndex, ProudSoulBagStorage.StoredEntry entry) {
    }
}
