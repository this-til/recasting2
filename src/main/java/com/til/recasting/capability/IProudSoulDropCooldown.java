package com.til.recasting.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 玩家耀魂掉落冷却数据。
 */
public interface IProudSoulDropCooldown extends INBTSerializable<CompoundTag> {

    enum DropKind {
        BASIC_FLAME("basic_flame"),
        SOUL_CUBE("soul_cube"),
        SE_CRYSTAL("se_crystal"),
        SLASH_ARTS("slash_arts");

        private final String key;

        DropKind(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    long getLastDropTime(DropKind kind);

    void setLastDropTime(DropKind kind, long gameTime);

    default boolean isReady(DropKind kind, long gameTime, int cooldownTicks) {
        if (cooldownTicks <= 0) {
            return true;
        }
        return gameTime - getLastDropTime(kind) >= cooldownTicks;
    }

    default void mark(DropKind kind, long gameTime) {
        setLastDropTime(kind, gameTime);
    }

    void copyFrom(IProudSoulDropCooldown other);

    class ProudSoulDropCooldown implements IProudSoulDropCooldown {
        private final long[] lastDropTimes = new long[DropKind.values().length];

        @Override
        public long getLastDropTime(DropKind kind) {
            return lastDropTimes[kind.ordinal()];
        }

        @Override
        public void setLastDropTime(DropKind kind, long gameTime) {
            lastDropTimes[kind.ordinal()] = gameTime;
        }

        @Override
        public void copyFrom(IProudSoulDropCooldown other) {
            for(DropKind kind : DropKind.values()) {
                setLastDropTime(kind, other.getLastDropTime(kind));
            }
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            for(DropKind kind : DropKind.values()) {
                long time = lastDropTimes[kind.ordinal()];
                if (time != 0L) {
                    tag.putLong(kind.getKey(), time);
                }
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            for(DropKind kind : DropKind.values()) {
                lastDropTimes[kind.ordinal()] = tag.getLong(kind.getKey());
            }
        }
    }
}
