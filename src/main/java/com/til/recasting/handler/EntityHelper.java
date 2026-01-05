package com.til.recasting.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntityHelper {

    public static Vec3 getEntityPosition(Entity owner) {
        return new Vec3(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ());
    }

    public static List<Entity> getTargettableEntitiesWithinAABB(Level world, @Nullable LivingEntity shooter, Vec3 pos, float reach) {

        AABB aabb = new AABB(pos, pos).inflate(reach);


        return world.getEntities(null, aabb).stream()
                .filter(e -> !Objects.equals(e, shooter))
                .filter(e -> EntityPredicateHelper.canTarget(shooter, e))
                .collect(Collectors.toList());


    }
}
