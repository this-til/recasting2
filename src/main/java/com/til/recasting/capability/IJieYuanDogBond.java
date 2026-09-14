package com.til.recasting.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 结缘剑「犬」获取进度：驯服狼、在线陪伴、一次性领取。
 */
public interface IJieYuanDogBond extends INBTSerializable<CompoundTag> {

    long SURVIVAL_TICKS = 6L * 60L * 60L * 20L;

    boolean isClaimed();

    void setClaimed(boolean claimed);

    boolean isBondFulfilled();

    void setBondFulfilled(boolean fulfilled);

    boolean hasActiveBond();

    @Nullable
    UUID getBondedWolfUuid();

    long getCompanionshipTicks();

    void setCompanionshipTicks(long companionshipTicks);

    void addCompanionshipTicks(long ticks);

    void beginBond(UUID wolfUuid);

    void clearActiveBond();

    default boolean isSurvivalComplete() {
        if (isBondFulfilled()) {
            return true;
        }
        if (!hasActiveBond()) {
            return false;
        }
        return getCompanionshipTicks() >= SURVIVAL_TICKS;
    }

    default long remainingSurvivalTicks() {
        if (isBondFulfilled() || !hasActiveBond()) {
            return 0L;
        }
        return Math.max(0L, SURVIVAL_TICKS - getCompanionshipTicks());
    }

    void copyFrom(IJieYuanDogBond other);

    class JieYuanDogBond implements IJieYuanDogBond {
        private static final UUID NO_WOLF = new UUID(0L, 0L);

        private boolean claimed;
        private boolean bondFulfilled;
        private UUID bondedWolfUuid = NO_WOLF;
        private long companionshipTicks;

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
            return !NO_WOLF.equals(bondedWolfUuid);
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
        public long getCompanionshipTicks() {
            return companionshipTicks;
        }

        @Override
        public void setCompanionshipTicks(long companionshipTicks) {
            this.companionshipTicks = Math.max(0L, companionshipTicks);
        }

        @Override
        public void addCompanionshipTicks(long ticks) {
            if (ticks <= 0L) {
                return;
            }
            companionshipTicks += ticks;
        }

        @Override
        public void beginBond(UUID wolfUuid) {
            this.bondedWolfUuid = wolfUuid;
            this.companionshipTicks = 0L;
            this.bondFulfilled = false;
        }

        @Override
        public void clearActiveBond() {
            this.bondedWolfUuid = NO_WOLF;
            this.companionshipTicks = 0L;
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
            companionshipTicks = other.getCompanionshipTicks();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            if (claimed) {
                tag.putBoolean("claimed", true);
            }
            if (bondFulfilled) {
                tag.putBoolean("bond_fulfilled", true);
            }
            if (hasActiveBond()) {
                tag.putUUID("wolf_uuid", bondedWolfUuid);
            }
            if (companionshipTicks > 0L) {
                tag.putLong("companionship_ticks", companionshipTicks);
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            claimed = tag.getBoolean("claimed");
            bondFulfilled = tag.getBoolean("bond_fulfilled");
            if (tag.hasUUID("wolf_uuid")) {
                bondedWolfUuid = tag.getUUID("wolf_uuid");
            } else {
                bondedWolfUuid = NO_WOLF;
            }
            companionshipTicks = tag.getLong("companionship_ticks");
        }
    }
}
