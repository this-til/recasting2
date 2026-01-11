package com.til.recasting.recipe;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.til.recasting.registry.RecastingItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SE结晶的自定义材料匹配器
 * 用于在配方中匹配特定特殊效果类型和等级的 SE结晶
 */
public class SpecialEffectCrystalIngredient extends Ingredient {
    private final Set<Item> items;
    private final SpecialEffectCrystalRequest request;

    protected SpecialEffectCrystalIngredient(Set<Item> items, SpecialEffectCrystalRequest request) {
        super(items.stream().map(item -> {
            ItemStack stack = new ItemStack(item);
            // 复制 NBT 以防止堆栈修改原始数据
            request.initItemStack(stack);
            return new Ingredient.ItemValue(stack);
        }));
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cannot create a SECrystalIngredient with no items");
        }
        this.items = Collections.unmodifiableSet(items);
        this.request = request;
    }

    public static SpecialEffectCrystalIngredient of(ItemLike item, SpecialEffectCrystalRequest request) {
        return new SpecialEffectCrystalIngredient(Set.of(item.asItem()), request);
    }

    public static SpecialEffectCrystalIngredient of(SpecialEffectCrystalRequest request) {
        return new SpecialEffectCrystalIngredient(Set.of(RecastingItems.SE_CRYSTAL.get()), request);
    }

    public static SpecialEffectCrystalIngredient of(ItemLike item, ResourceLocation specialEffectType, int level) {
        return new SpecialEffectCrystalIngredient(Set.of(item.asItem()),
                SpecialEffectCrystalRequest.Builder.newInstance()
                        .specialEffectType(specialEffectType)
                        .level(level)
                        .build());
    }

    public static SpecialEffectCrystalIngredient of(ResourceLocation specialEffectType, int level) {
        return new SpecialEffectCrystalIngredient(Set.of(RecastingItems.SE_CRYSTAL.get()),
                SpecialEffectCrystalRequest.Builder.newInstance()
                        .specialEffectType(specialEffectType)
                        .level(level)
                        .build());
    }

    /**
     * 创建一个空白的 SE结晶 匹配器（匹配任何 SE结晶）
     */
    public static SpecialEffectCrystalIngredient blankNameless() {
        return of(SpecialEffectCrystalRequest.Builder.newInstance().build());
    }

    @Override
    public boolean test(ItemStack input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return items.contains(input.getItem()) && this.request.test(input);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public @NotNull IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", Objects.requireNonNull(CraftingHelper.getID(Serializer.INSTANCE)).toString());
        if (items.size() == 1) {
            json.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(items.iterator().next())).toString());
        } else {
            JsonArray items = new JsonArray();
            // 确保保存到 JSON 时集合中物品的顺序是确定性的
            this.items.stream().map(ForgeRegistries.ITEMS::getKey).sorted().forEach(name -> items.add(name.toString()));
            json.add("items", items);
        }
        json.add("request", this.request.toJson());
        return json;
    }

    public static class Serializer implements IIngredientSerializer<SpecialEffectCrystalIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull SpecialEffectCrystalIngredient parse(FriendlyByteBuf buffer) {
            Set<Item> items = Stream.generate(() -> buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS))
                    .limit(buffer.readVarInt()).collect(Collectors.toSet());
            SpecialEffectCrystalRequest request = SpecialEffectCrystalRequest.fromNetwork(buffer);
            return new SpecialEffectCrystalIngredient(items, request);
        }

        @Override
        public @NotNull SpecialEffectCrystalIngredient parse(JsonObject json) {
            // 解析物品
            Set<Item> items;
            if (json.has("item")) {
                items = Set.of(CraftingHelper.getItem(GsonHelper.getAsString(json, "item"), true));
            } else if (json.has("items")) {
                ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
                JsonArray itemArray = GsonHelper.getAsJsonArray(json, "items");
                for (int i = 0; i < itemArray.size(); i++) {
                    builder.add(CraftingHelper.getItem(GsonHelper.convertToString(itemArray.get(i), "items[" + i + ']'),
                            true));
                }
                items = builder.build();
            } else {
                throw new JsonSyntaxException("Must set either 'item' or 'items'");
            }
            var request = SpecialEffectCrystalRequest.fromJSON(json.getAsJsonObject("request"));
            return new SpecialEffectCrystalIngredient(items, request);
        }

        @Override
        public void write(FriendlyByteBuf buffer, SpecialEffectCrystalIngredient ingredient) {
            buffer.writeVarInt(ingredient.items.size());
            for (Item item : ingredient.items) {
                buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
            }
            ingredient.request.toNetwork(buffer);
        }
    }
}

