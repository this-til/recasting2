package com.til.recasting.capability;

import java.util.ArrayList;
import java.util.List;

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

    default void addTimerCell(Runnable runnable, int time) {
        addTimerCell(new TimerCell(runnable, time));
    }

    /**
     * 默认实现
     */
    class TimeRun implements ITimeRun {
        protected final List<TimerCell> runList = new ArrayList<>();
        protected final List<TimerCell> beAdded = new ArrayList<>();
        protected boolean isRun;

        @Override
        public void tick() {
            this.isRun = true;

            // 处理待添加的任务
            if (!this.beAdded.isEmpty()) {
                this.upBeAdded();
            }

            // 更新所有任务
            if (!this.runList.isEmpty()) {
                for(int i = 0; i < this.runList.size(); ++i) {
                    TimerCell timerCell = this.runList.get(i);
                    timerCell.up();

                    if (!this.isRun) {
                        return;
                    }

                    if (!timerCell.isValid()) {
                        this.runList.remove(i);
                        --i;
                    }
                }
            }
        }

        @Override
        public void addTimerCell(TimerCell timerCell) {
            if (timerCell.isValid()) {
                this.beAdded.add(timerCell);
            }
        }

        /**
         * 将待添加的任务按优先级插入到运行列表
         */
        protected void upBeAdded() {
            for(TimerCell timerCell : this.beAdded) {
                boolean needInsert = true;

                for(int i = 0; i < this.runList.size(); ++i) {
                    TimerCell timerCell1 = this.runList.get(i);
                    if (timerCell1.priority <= timerCell.priority) {
                        this.runList.add(i, timerCell);
                        needInsert = false;
                        break;
                    }
                }

                if (needInsert) {
                    this.runList.add(timerCell);
                }
            }

            this.beAdded.clear();
        }
    }

    /**
     * 定时任务单元
     */
    class TimerCell {
        protected Runnable run;
        public final int timer;        // 延迟时间（tick）
        public final boolean cycle;    // 是否循环执行
        protected int time;            // 当前已经过的时间
        protected boolean _use;        // 是否启用
        protected boolean valid;       // 是否有效
        public int priority;           // 优先级

        public TimerCell(Runnable run, int time) {
            this(run, time, 0);
        }

        /**
         * 创建一次性定时任务
         *
         * @param run      要执行的任务
         * @param time     延迟时间（tick）
         * @param priority 优先级
         */
        public TimerCell(Runnable run, int time, int priority) {
            this(run, time, false, priority);
        }

        /**
         * 创建定时任务
         *
         * @param run      要执行的任务
         * @param timer    延迟时间（tick）
         * @param cycle    是否循环执行
         * @param priority 优先级
         */
        public TimerCell(Runnable run, int timer, boolean cycle, int priority) {
            this._use = true;
            this.valid = true;
            this.run = run;
            this.timer = timer;
            this.cycle = cycle;
            this.priority = priority;
        }

        /**
         * 每 tick 更新
         */
        public void up() {
            if (this._use) {
                ++this.time;
                if (this.time >= this.timer) {
                    this.time = 0;
                    this.run.run();
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
         * 标记为失效
         */
        public void setFail() {
            this.valid = false;
        }

        /**
         * 是否有效
         */
        public boolean isValid() {
            return this.valid;
        }
    }
}

