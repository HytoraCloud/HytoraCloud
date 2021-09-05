package de.lystx.hytoracloud.driver.wrapped;


import de.lystx.hytoracloud.driver.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.scheduler.SchedulerFuture;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


@Getter
public class SchedulerObject extends WrappedObject<Scheduler, SchedulerObject> implements Scheduler {

	private static final long serialVersionUID = 8542657582104611395L;
	/**
	 * All pending tasks
	 */
	private final List<SchedulerFuture> tasks;

	/**
	 * The java timer util
	 */
	private final Timer timer;

	public SchedulerObject() {
		this.tasks = new LinkedList<>();
		this.timer = new Timer();
	}

	@Override
	public SchedulerFuture getTask(int id) {
		return new LinkedList<>(this.tasks).stream().filter(task -> task.getId() == id).findFirst().orElse(null);
	}

	@Override @Deprecated
	public void cancelTask(int id) {
		this.cancelTask(this.getTask(id));
	}

	@Override
	public void cancelTask(SchedulerFuture task) {
		if (task != null) {
			task.setCancelled(true);
			this.tasks.removeIf(task1 -> task.getId() == task1.getId());
		}
	}

	@Override
	public void cancelAllTasks() {
		for (SchedulerFuture task : this.getTasks()) {
			this.cancelTask(task);
		}
	}

	@Override
	public SchedulerFuture scheduleRepeatingTaskForTimes(Runnable task, long delay, long period, long times) {
		return scheduleRepeatingTaskForTimes(task, delay, period, times, false);
	}

	@Override
	public SchedulerFuture scheduleRepeatingTaskAsync(Runnable task, long delay, long period, long times) {
		return scheduleRepeatingTaskForTimes(task, delay, period, times, true);
	}

	@Override
	public SchedulerFuture scheduleRepeatingTask(Runnable task, long delay, long period) {
		return repeatTask(task, delay, period, false);
	}

	@Override
	public SchedulerFuture scheduleRepeatingTaskAsync(Runnable task, long delay, long period) {
		return repeatTask(task, delay, period, true);
	}

	@Override
	public SchedulerFuture runTask(Runnable task) {
		SchedulerFutureObject schedulerFutureObject = this.runTask(task, false, false);

		new Thread(() -> {
			schedulerFutureObject.run();
			cancelTask(schedulerFutureObject);
			Thread.interrupted();
		}, "scheduledTask_" + schedulerFutureObject.getId()).start();

		return schedulerFutureObject;
	}

	@Override
	public SchedulerFuture runTaskAsync(Runnable task) {
		SchedulerFutureObject schedulerFutureObject = runTask(task, true, false);

		new Thread(() -> {
			schedulerFutureObject.run();
			cancelTask(schedulerFutureObject);
			Thread.interrupted();
		}, "scheduledTask_" + schedulerFutureObject.getId()).start();

		return schedulerFutureObject;
	}

	@Override
	public SchedulerFuture scheduleDelayedTask(Runnable task, long delay) {
		return delayTask(task, delay, false);
	}

	@Override
	public SchedulerFuture scheduleDelayedTaskAsync(Runnable task, long delay) {
		return delayTask(task, delay, true);
	}

	//Helper method to internally run a task
	private SchedulerFutureObject runTask(Runnable task, boolean async, boolean multipleTimes) {
		if (task == null) {
			return null;
		}
		SchedulerFutureObject schedulerFutureObject = new SchedulerFutureObject(!async, task, generateTaskId(), multipleTimes);
		this.tasks.add(schedulerFutureObject);
		return schedulerFutureObject;
	}

	//Internal helper method to delay task
	public SchedulerFuture delayTask(Runnable task, long delay, boolean async) {

		SchedulerFutureObject schedulerFutureObject = runTask(task, async, false);

		this.timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				schedulerFutureObject.run();
				cancelTask(schedulerFutureObject);
				cancel();
				Thread.interrupted();
			}
		}, delay * 50, 1);

		return schedulerFutureObject;
	}

	//Helper method to repeat tasks
	private SchedulerFuture repeatTask(Runnable task, long delay, long period, boolean async) {
		SchedulerFutureObject schedulerFutureObject = runTask(task, async, true);

		this.timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
					schedulerFutureObject.run();
					if (schedulerFutureObject.isCancelled()) {
						cancelTask(schedulerFutureObject);
						cancel();
						Thread.interrupted();
					}
			}
		}, delay * 50, period * 50);
		return schedulerFutureObject;
	}

	//Helper method to repeat task for times
	private SchedulerFuture scheduleRepeatingTaskForTimes(Runnable task, long delay, long period, final long times, boolean async) {
		SchedulerFutureObject schedulerFutureObject = runTask(task, async, true);

		this.timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
					schedulerFutureObject.run();
					if (schedulerFutureObject.isCancelled() || schedulerFutureObject.getRunTimes() >= times) {
						cancelTask(schedulerFutureObject);
						cancel();
						Thread.interrupted();
					}
			}
		}, delay * 50, period * 50);
		return schedulerFutureObject;
	}

	@Override
	public int generateTaskId() {
		int id = ThreadLocalRandom.current().nextInt();
		if (this.getTask(id) != null) {
			return generateTaskId();
		}
		return id;
	}

	@Override
	Class<SchedulerObject> getWrapperClass() {
		return SchedulerObject.class;
	}

	@Override
	Class<Scheduler> getInterface() {
		return Scheduler.class;
	}
}