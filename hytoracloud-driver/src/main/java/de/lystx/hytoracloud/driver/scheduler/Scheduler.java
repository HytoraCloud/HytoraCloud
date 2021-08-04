package de.lystx.hytoracloud.driver.scheduler;

import de.lystx.hytoracloud.driver.wrapped.SchedulerFutureObject;

import java.util.List;

public interface Scheduler {

    /**
     * Gets a {@link SchedulerFutureObject} by its id
     *
     * @param id the id to search for
     * @return task or null if not found
     */
    SchedulerFuture getTask(int id);

    /**
     * Gets a list of all pending {@link SchedulerFutureObject}s
     *
     * @return list of tasks
     */
    List<SchedulerFuture> getTasks();

    /**
     * Cancels a task by its id
     *
     * @param id the id
     * @deprecated use {@link Scheduler#cancelTask(SchedulerFuture)}
     */
    @Deprecated
    void cancelTask(int id);

    /**
     * Cancels a task
     *
     * @param task the task to cancel
     */
    void cancelTask(SchedulerFuture task);

    /**
     * Cancels all tasks
     */
    void cancelAllTasks();

    /**
     * Repeats a task for a given amount of time
     * This is executed synchronously
     *
     * @param task the runnable task
     * @param delay the delay between every execution
     * @param period the period
     * @param times the amount of times
     * @return task
     */
    SchedulerFuture scheduleRepeatingTaskForTimes(Runnable task, long delay, long period, long times);

    /**
     * Repeats a task for a given amount of time
     * This is executed asynchronously
     *
     * @param task the runnable task
     * @param delay the delay between every execution
     * @param period the period
     * @param times the amount of times
     * @return task
     */
    SchedulerFuture scheduleRepeatingTaskAsync(Runnable task, long delay, long period, long times);

    /**
     * Repeats a task
     * This is executed synchronously
     *
     * @param task the runnable task
     * @param delay the delay between every execution
     * @param period the period
     * @return task
     */
    SchedulerFuture scheduleRepeatingTask(Runnable task, long delay, long period);

    /**
     * Repeats a task
     * This is executed asynchronously
     *
     * @param task the runnable task
     * @param delay the delay between every execution
     * @param period the period
     * @return task
     */
    SchedulerFuture scheduleRepeatingTaskAsync(Runnable task, long delay, long period);

    /**
     * Runs a task
     * This is executed synchronously
     *
     * @param task the runnable task
     * @return scheduled task
     */
    SchedulerFuture runTask(Runnable task);

    /**
     * Runs a task
     * This is executed asynchronously
     *
     * @param task the runnable task
     * @return scheduled task
     */
    SchedulerFuture runTaskAsync(Runnable task);

    /**
     * Delays a task
     * This is executed synchronously
     *
     * @param task the task
     * @param delay the delay as Minecraft-Ticks (20 ticks = 1 Second)
     * @return scheduled task
     */
    SchedulerFuture scheduleDelayedTask(Runnable task, long delay);

    /**
     * Delays a task
     * This is executed asynchronously
     *
     * @param task the task
     * @param delay the delay as Minecraft-Ticks (20 ticks = 1 Second)
     * @return scheduled task
     */
    SchedulerFuture scheduleDelayedTaskAsync(Runnable task, long delay);

    /**
     * Searches for a free task id
     * If the id is already in use, it will generate a new one
     * until one id is free to use
     *
     * @return id as int
     */
    int generateTaskId();

}
