package com.til.recasting.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 玩家耀魂掉落冷却数据。
 */
public interface IProudSoulDropCooldown {

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
        public static final Codec<ProudSoulDropCooldown> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codec.LONG.optionalFieldOf("basic_flame", 0L)
                                .forGetter(c -> c.lastDropTimes[DropKind.BASIC_FLAME.ordinal()]),
                        Codec.LONG.optionalFieldOf("soul_cube", 0L)
                                .forGetter(c -> c.lastDropTimes[DropKind.SOUL_CUBE.ordinal()]),
                        Codec.LONG.optionalFieldOf("se_crystal", 0L)
                                .forGetter(c -> c.lastDropTimes[DropKind.SE_CRYSTAL.ordinal()]),
                        Codec.LONG.optionalFieldOf("slash_arts", 0L)
                                .forGetter(c -> c.lastDropTimes[DropKind.SLASH_ARTS.ordinal()]))
                .apply(instance, (basic, cube, crystal, arts) -> {
                    ProudSoulDropCooldown cooldown = new ProudSoulDropCooldown();
                    cooldown.lastDropTimes[DropKind.BASIC_FLAME.ordinal()] = basic;
                    cooldown.lastDropTimes[DropKind.SOUL_CUBE.ordinal()] = cube;
                    cooldown.lastDropTimes[DropKind.SE_CRYSTAL.ordinal()] = crystal;
                    cooldown.lastDropTimes[DropKind.SLASH_ARTS.ordinal()] = arts;
                    return cooldown;
                }));

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
            for (DropKind kind : DropKind.values()) {
                setLastDropTime(kind, other.getLastDropTime(kind));
            }
        }
    }
}
