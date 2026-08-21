package com.til.recasting.capability;

import com.til.recasting.handler.TimeRunManage;
import lombok.extern.log4j.Log4j2;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于实体的定时器接口
 * 用于在特定延迟后执行任务
 */
public interface ITimeRun {
    /**
     * 每 tick 调用一次
     */
    void tick();

    /**
     * 添加定时任务
     */
    void addTimerCell(TimerCell timerCell);

    /**
     * 添加具名定时任务；同名任务会替换旧任务
     */
    void addNamedTimerCell(String name, TimerCell timerCell);

    /**
     * 获取具名定时任务
     */
    @Nullable
    TimerCell getNamedTimerCell(String name);

    /**
     * 移除具名定时任务
     */
    void removeNamedTimerCell(String name);

    default void addTimerCell(Runnable runnable, int time) {
        addTimerCell(new TimerCell(runnable, time));
    }

    default void addNamedTimerCell(String name, Runnable runnable, int time) {
        addNamedTimerCell(name, new TimerCell(runnable, time));
    }

    /**
     * 默认实现
     */
    class TimeRun implements ITimeRun {
        protected final List<TimerCell> runList = new ArrayList<>();
        protected final List<TimerCell> beAdded = new ArrayList<>();
        protected final Map<String, TimerCell> namedTimerCellMap = new HashMap<>();
        protected boolean isRun;
        @Nullable
        protected LivingEntity entity;

        /**
         * 绑定所属实体，供激活表上报使用
         */
        public void setEntity(@Nullable LivingEntity entity) {
            this.entity = entity;
        }

        /**
         * 解除实体绑定并从激活表注销
         */
        public void clearEntity() {
            this.tryDeactivate();
            this.entity = null;
        }

        @Override
        public void tick() {
            this.isRun = true;

            // 处理待添加的任务
            if (!this.beAdded.isEmpty()) {
                this.upBeAdded();
            }

            // 更新所有任务
            if (!this.runList.isEmpty()) {
                for (int i = 0; i < this.runList.size(); ++i) {
                    TimerCell timerCell = this.runList.get(i);
                    timerCell.up();

                    if (!this.isRun) {
                        return;
                    }

                    if (!timerCell.isValid()) {
                        this.removeNamedTimerCell(timerCell);
                        this.runList.remove(i);
                        --i;
                    }
                }
            }

            if (this.runList.isEmpty() && this.beAdded.isEmpty()) {
                this.tryDeactivate();
            }
        }

        @Override
        public void addTimerCell(TimerCell timerCell) {
            if (timerCell.isValid()) {
                this.beAdded.add(timerCell);
                this.tryActivate();
            }
        }

        @Override
        public void addNamedTimerCell(String name, TimerCell timerCell) {
            if (name == null || name.isEmpty() || !timerCell.isValid()) {
                return;
            }

            TimerCell existing = this.namedTimerCellMap.put(name, timerCell);
            if (existing != null) {
                existing.setFail();
            }
            this.beAdded.add(timerCell);
            this.tryActivate();
        }

        @Override
        @Nullable
        public TimerCell getNamedTimerCell(String name) {
            if (name == null || name.isEmpty()) {
                return null;
            }

            TimerCell timerCell = this.namedTimerCellMap.get(name);
            if (timerCell == null) {
                return null;
            }
            if (!timerCell.isValid()) {
                this.namedTimerCellMap.remove(name);
                return null;
            }
            return timerCell;
        }

        @Override
        public void removeNamedTimerCell(String name) {
            if (name == null || name.isEmpty()) {
                return;
            }

            TimerCell timerCell = this.namedTimerCellMap.remove(name);
            if (timerCell != null) {
                timerCell.setFail();
            }
        }

        /**
         * 将待添加的任务追加到运行列表
         */
        protected void upBeAdded() {
            if (this.beAdded.isEmpty()) {
                return;
            }

            this.runList.addAll(this.beAdded);
            this.beAdded.clear();
        }

        protected void removeNamedTimerCell(TimerCell timerCell) {
            this.namedTimerCellMap.entrySet().removeIf(entry -> entry.getValue() == timerCell);
        }

        protected void tryActivate() {
            if (this.entity != null) {
                TimeRunManage.activate(this.entity);
            }
        }

        protected void tryDeactivate() {
            if (this.entity != null) {
                TimeRunManage.deactivate(this.entity);
            }
        }
    }

    /**
     * 定时任务单元
     */
    @Log4j2
    class TimerCell {
        protected Runnable run;
        public final int timer;        // 延迟时间（tick）
        public final boolean cycle;    // 是否循环执行
        protected int time;            // 当前已经过的时间
        protected boolean _use;        // 是否启用
        protected boolean valid;       // 是否有效

        public TimerCell(Runnable run, int time) {
            this(run, time, false);
        }

        /**
         * 创建定时任务
         *
         * @param run   要执行的任务
         * @param timer 延迟时间（tick）
         * @param cycle 是否循环执行
         */
        public TimerCell(Runnable run, int timer, boolean cycle) {
            this._use = true;
            this.valid = true;
            this.run = run;
            this.timer = timer;
            this.cycle = cycle;
        }

        /**
         * 每 tick 更新
         */
        public void up() {
            if (this._use) {
                ++this.time;
                if (this.time >= this.timer) {
                    this.time = 0;
                    try {
                        this.run.run();
                    } catch (Exception e) {
                        log.error("TimerCell run failed", e);
                    }
                    if (!this.cycle) {
                        this.valid = false;
                    }
                }
            }
        }

        /**
         * 启用任务
         *
         * @param nowStart 是否立即执行
         */
        public void use(boolean nowStart) {
            this._use = true;
            this.time = 0;
            if (nowStart) {
                this.time = this.timer;
            }
        }

        /**
         * 暂停任务
         */
        public void end() {
            this._use = false;
        }

        /**
         * 标记为失效，并停止后续 {@link #up()} 回调
         */
        public void setFail() {
            this.valid = false;
            this._use = false;
        }

        /**
         * 是否有效
         */
        public boolean isValid() {
            return this.valid;
        }
    }
}
