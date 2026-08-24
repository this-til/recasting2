package com.til.recasting.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * 结缘剑「犬」获取进度：驯服狼、现实时间陪伴、一次性领取。
 */
public interface IJieYuanDogBond {

    long SURVIVAL_MILLIS = 6L * 60L * 60L * 1000L;

    boolean isClaimed();

    void setClaimed(boolean claimed);

    boolean isBondFulfilled();

    void setBondFulfilled(boolean fulfilled);

    boolean hasActiveBond();

    @Nullable
    UUID getBondedWolfUuid();

    long getTameTimeMillis();

    void beginBond(UUID wolfUuid, long tameTimeMillis);

    void clearActiveBond();

    default boolean isSurvivalComplete(long nowMillis) {
        if (isBondFulfilled()) {
            return true;
        }
        if (!hasActiveBond()) {
            return false;
        }
        return nowMillis - getTameTimeMillis() >= SURVIVAL_MILLIS;
    }

    default long remainingSurvivalMillis(long nowMillis) {
        if (isBondFulfilled() || !hasActiveBond()) {
            return 0L;
        }
        return Math.max(0L, SURVIVAL_MILLIS - (nowMillis - getTameTimeMillis()));
    }

    void copyFrom(IJieYuanDogBond other);

    class JieYuanDogBond implements IJieYuanDogBond {
        private static final UUID NO_WOLF = new UUID(0L, 0L);

        public static final Codec<JieYuanDogBond> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codec.BOOL.optionalFieldOf("claimed", false).forGetter(JieYuanDogBond::isClaimed),
                        Codec.BOOL.optionalFieldOf("bond_fulfilled", false).forGetter(JieYuanDogBond::isBondFulfilled),
                        Codec.STRING.optionalFieldOf("wolf_uuid").forGetter(bond -> {
                            if (!bond.hasActiveBond()) {
                                return Optional.empty();
                            }
                            return Optional.of(bond.bondedWolfUuid.toString());
                        }),
                        Codec.LONG.optionalFieldOf("tame_time", 0L).forGetter(JieYuanDogBond::getTameTimeMillis))
                .apply(instance, (claimed, fulfilled, wolfUuid, tameTime) -> {
                    JieYuanDogBond bond = new JieYuanDogBond();
                    bond.claimed = claimed;
                    bond.bondFulfilled = fulfilled;
                    if (wolfUuid.isPresent()) {
                        try {
                            bond.bondedWolfUuid = UUID.fromString(wolfUuid.get());
                            bond.tameTimeMillis = tameTime;
                        } catch (IllegalArgumentException ignored) {
                            bond.clearActiveBond();
                        }
                    }
                    return bond;
                }));

        private boolean claimed;
        private boolean bondFulfilled;
        private UUID bondedWolfUuid = NO_WOLF;
        private long tameTimeMillis;

        @Override
        public boolean isClaimed() {
            return claimed;
        }

        @Override
        public void setClaimed(boolean claimed) {
            this.claimed = claimed;
        }

        @Override
        public boolean isBondFulfilled() {
            return bondFulfilled;
        }

        @Override
        public void setBondFulfilled(boolean fulfilled) {
            this.bondFulfilled = fulfilled;
        }

        @Override
        public boolean hasActiveBond() {
            return !NO_WOLF.equals(bondedWolfUuid) && tameTimeMillis > 0L;
        }

        @Override
        @Nullable
        public UUID getBondedWolfUuid() {
            if (!hasActiveBond()) {
                return null;
            }
            return bondedWolfUuid;
        }

        @Override
        public long getTameTimeMillis() {
            return tameTimeMillis;
        }

        @Override
        public void beginBond(UUID wolfUuid, long tameTimeMillis) {
            this.bondedWolfUuid = wolfUuid;
            this.tameTimeMillis = tameTimeMillis;
            this.bondFulfilled = false;
        }

        @Override
        public void clearActiveBond() {
            this.bondedWolfUuid = NO_WOLF;
            this.tameTimeMillis = 0L;
        }

        @Override
        public void copyFrom(IJieYuanDogBond other) {
            setClaimed(other.isClaimed());
            setBondFulfilled(other.isBondFulfilled());
            UUID wolfUuid = other.getBondedWolfUuid();
            if (wolfUuid == null) {
                clearActiveBond();
                return;
            }
            bondedWolfUuid = wolfUuid;
            tameTimeMillis = other.getTameTimeMillis();
        }
    }
}
