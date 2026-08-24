package com.til.recasting.client.registry.instance;

import com.til.recasting.entity.StandardizationAttackEntity;

import java.util.function.Consumer;

/**
 * 在客户端实体创建或扩展列表变化时挂载专属客户端逻辑。
 */
public class EntityClientExtension {

    private final Consumer<StandardizationAttackEntity> attach;

    public EntityClientExtension(Consumer<StandardizationAttackEntity> attach) {
        this.attach = attach;
    }

    public void apply(StandardizationAttackEntity entity) {
        attach.accept(entity);
    }
}
