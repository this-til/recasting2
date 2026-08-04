package com.til.recasting.client.screen;

import com.til.recasting.inventory.ProudSoulBagMenu;
import com.til.recasting.item.ProudSoulBagStorage;
import com.til.recasting.network.NetworkManager;
import com.til.recasting.network.ProudSoulBagActionMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 耀魂背包界面：搜索、分页、AE2 风格虚拟格交互。
 */
@OnlyIn(Dist.CLIENT)
public class ProudSoulBagScreen extends AbstractContainerScreen<ProudSoulBagMenu> {

    /**
     * 与双层箱子 GUI 同尺寸（176×222）。
     * 虚拟格使用箱子前 5 行（y=18 起）；第 6 行用面板色盖住。
     */
    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png");

    /** 原版双层箱子面板灰，用于遮罩多余格子 */
    private static final int PANEL_COLOR = 0xFFC6C6C6;

    private static final int GRID_LEFT = 8;
    private static final int GRID_TOP = 18;
    private static final int SLOT_SIZE = 18;

    private EditBox searchBox;
    private int page;
    private SortMode sortMode = SortMode.NAME;
    /** 按住 Shift 时冻结展示顺序（仅模板身份），数量仍从最新同步数据解析。 */
    private List<ItemStack> frozenOrderKeys = List.of();
    private boolean wasShiftDown;

    public ProudSoulBagScreen(ProudSoulBagMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.titleLabelY = 6;
        this.inventoryLabelY = 126;
    }

    @Override
    protected void init() {
        super.init();
        // 搜索框放在标题行右侧，避免压住第一行格子
        this.searchBox = new EditBox(this.font, this.leftPos + 70, this.topPos + 5, 60, 12, Component.literal("Search"));
        this.searchBox.setMaxLength(64);
        this.searchBox.setBordered(true);
        this.searchBox.setValue("");
        this.addWidget(this.searchBox);

        this.addRenderableWidget(Button.builder(Component.literal("<"), b -> changePage(-1))
                .bounds(this.leftPos + 132, this.topPos + 4, 12, 12)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal("S"), b -> cycleSort())
                .bounds(this.leftPos + 146, this.topPos + 4, 12, 12)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal(">"), b -> changePage(1))
                .bounds(this.leftPos + 160, this.topPos + 4, 12, 12)
                .build());
    }

    private void cycleSort() {
        this.sortMode = this.sortMode == SortMode.NAME ? SortMode.AMOUNT : SortMode.NAME;
        this.page = 0;
        this.frozenOrderKeys = List.of();
        this.wasShiftDown = false;
    }

    private void changePage(int delta) {
        int maxPage = Math.max(0, (viewEntries().size() - 1) / ProudSoulBagMenu.PAGE_SIZE);
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
            // 只锁定顺序，不锁数量；避免连续 Shift 操作要等松手才看到变化
            this.frozenOrderKeys = filteredEntries().stream()
                    .map(entry -> entry.entry().template().copyWithCount(1))
                    .toList();
        }
        if (!shift) {
            this.frozenOrderKeys = List.of();
        }
        wasShiftDown = shift;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        // 盖住第 6 行箱子格（y=108），本界面只用前 5 行虚拟格
        graphics.fill(this.leftPos + 7, this.topPos + 107, this.leftPos + 169, this.topPos + 125, PANEL_COLOR);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        List<IndexedEntry> pageEntries = currentPageEntries();
        for (int i = 0; i < pageEntries.size(); i++) {
            IndexedEntry entry = pageEntries.get(i);
            if (entry.entry().count() <= 0 || entry.storageIndex() < 0) {
                continue;
            }
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
        graphics.drawString(
                this.font,
                (page + 1) + "/" + (maxPage + 1),
                this.leftPos + 48,
                this.topPos + 6,
                0x404040,
                false
        );

        int hovered = hoveredVirtualIndex(mouseX, mouseY);
        if (hovered >= 0 && hovered < pageEntries.size()) {
            IndexedEntry entry = pageEntries.get(hovered);
            if (entry.storageIndex() >= 0 && entry.entry().count() > 0) {
                List<Component> lines = collectAppendHoverHints(entry.entry().template());
                lines.add(Component.literal(String.valueOf(entry.entry().count())).withStyle(ChatFormatting.DARK_GRAY));
                graphics.renderTooltip(this.font, lines, Optional.empty(), mouseX, mouseY);
            }
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
                if (storageIndex < 0 || pageEntries.get(local).entry().count() <= 0) {
                    return true;
                }
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
                if (storageIndex < 0 || pageEntries.get(local).entry().count() <= 0) {
                    return true;
                }
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
        if (hasShiftDown() && !frozenOrderKeys.isEmpty()) {
            return resolveFrozenView();
        }
        return filteredEntries();
    }

    /**
     * 保持 Shift 按下瞬间的相对顺序，数量与 storageIndex 用最新同步数据刷新；
     * 已取空的类型直接移除（不留空气位），新出现的类型追加到末尾。
     */
    private List<IndexedEntry> resolveFrozenView() {
        List<ProudSoulBagStorage.StoredEntry> raw = this.menu.getDisplayEntries();
        String query = this.searchBox == null ? "" : this.searchBox.getValue().trim().toLowerCase(Locale.ROOT);
        List<IndexedEntry> result = new ArrayList<>(frozenOrderKeys.size());
        List<ItemStack> nextKeys = new ArrayList<>(frozenOrderKeys.size());

        for (ItemStack key : frozenOrderKeys) {
            IndexedEntry matched = null;
            for (int i = 0; i < raw.size(); i++) {
                ProudSoulBagStorage.StoredEntry entry = raw.get(i);
                if (entry.count() <= 0 || !ItemStack.isSameItemSameTags(entry.template(), key)) {
                    continue;
                }
                matched = new IndexedEntry(i, entry);
                break;
            }
            // 已取空或不匹配搜索：直接丢掉，禁止留下 air 占位
            if (matched == null || !matchesSearch(matched.entry().template(), query)) {
                continue;
            }
            result.add(matched);
            nextKeys.add(matched.entry().template().copyWithCount(1));
        }

        for (int i = 0; i < raw.size(); i++) {
            ProudSoulBagStorage.StoredEntry entry = raw.get(i);
            if (entry.count() <= 0) {
                continue;
            }
            boolean already = false;
            for (ItemStack key : nextKeys) {
                if (ItemStack.isSameItemSameTags(entry.template(), key)) {
                    already = true;
                    break;
                }
            }
            if (already || !matchesSearch(entry.template(), query)) {
                continue;
            }
            result.add(new IndexedEntry(i, entry));
            nextKeys.add(entry.template().copyWithCount(1));
        }

        this.frozenOrderKeys = List.copyOf(nextKeys);
        return result;
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
        List<ProudSoulBagStorage.StoredEntry> raw = this.menu.getDisplayEntries();
        String query = this.searchBox == null ? "" : this.searchBox.getValue().trim().toLowerCase(Locale.ROOT);
        List<IndexedEntry> result = new ArrayList<>();
        for (int i = 0; i < raw.size(); i++) {
            ProudSoulBagStorage.StoredEntry entry = raw.get(i);
            if (!matchesSearch(entry.template(), query)) {
                continue;
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

    /**
     * 搜索匹配物品名与 {@link net.minecraft.world.item.Item#appendHoverText} 追加内容。
     */
    private boolean matchesSearch(ItemStack stack, String query) {
        if (query.isEmpty() || stack.isEmpty()) {
            return true;
        }
        for (Component line : collectAppendHoverHints(stack)) {
            if (line.getString().toLowerCase(Locale.ROOT).contains(query)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 名称 + 附魔名 + {@code Item.appendHoverText}，不走 {@code getTooltipFromItem}。
     */
    private List<Component> collectAppendHoverHints(ItemStack stack) {
        ItemStack tip = stack.copyWithCount(1);
        List<Component> lines = new ArrayList<>();
        lines.add(tip.getHoverName());
        if (tip.isEnchanted()) {
            ItemStack.appendEnchantmentNames(lines, tip.getEnchantmentTags());
        }
        TooltipFlag flag =
                this.minecraft != null && this.minecraft.options.advancedItemTooltips
                        ? TooltipFlag.Default.ADVANCED
                        : TooltipFlag.Default.NORMAL;
        Level level = this.minecraft != null ? this.minecraft.level : null;
        tip.getItem().appendHoverText(tip, level, lines, flag);
        return lines;
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
