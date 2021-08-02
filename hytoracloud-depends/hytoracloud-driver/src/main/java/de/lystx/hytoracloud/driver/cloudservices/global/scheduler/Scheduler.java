package de.lystx.hytoracloud.driver.cloudservices.global.scheduler;


import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ICloudServiceInfo(
		name = "Scheduler",
		type = CloudServiceType.UTIL,
		description = {
				"This class is used to delay / repeat ",
				"actions and cancel those actions while working",
				"and executing code"
		},
		version = 1.2
)
public class Scheduler implements ICloudService {

	private static Scheduler instance;
	private final Map<Integer, ScheduledTask> schedulerMap;

	public Scheduler() {
		this.schedulerMap = new ConcurrentHashMap<>();
	}

	/**
	 * Task by ID
	 * @param id
	 * @return Task by ID
	 */
	public ScheduledTask getTask(int id) {
		return schedulerMap.getOrDefault(id, null);
	}

	/**
	 * Cancels a task
	 * @param id
	 */
	public void cancelTask(int id) {
		this.cancelTask(this.getTask(id));
	}

	/**
	 * Cancels a single Task
	 * @param id
	 */
	public void cancelTask(ScheduledTask id) {
		if (id != null) {
			id.setCancelled(true);
			schedulerMap.remove(id.getId());
		}
	}

	/**
	 * Cancels all tasks
	 */
	public void cancelTasks() {
		for (Integer r : schedulerMap.keySet()) {
			schedulerMap.get(r).setCancelled(true);
		}
		schedulerMap.clear();
	}

	/**
	 * Schedules while something
	 * @param run
	 * @param delay
	 * @param repeat
	 * @return task ID
	 */
	public int scheduleAsyncWhile(Runnable run, long delay, long repeat) {
		int taskid = new Random().nextInt(2147483647);
		ScheduledTask scheduledTask = new ScheduledTask(true, run, taskid, false);
		this.schedulerMap.put(taskid, scheduledTask);
		return scheduledTask.getId();
	}

	/**
	 * Schedules something for times
	 * @param task
	 * @param delay
	 * @param period
	 * @param times
	 * @return task ID
	 */
	public ScheduledTask scheduleRepeatingTaskForTimes(Runnable task, long delay, long period, long times) {
		return scheduleRepeatingTaskForTimes(task, delay, period, times, false);
	}

	/**
	 * Schedules something for times
	 * But asynchronous
	 * @param task
	 * @param delay
	 * @param period
	 * @param times
	 * @return task ID
	 */
	public ScheduledTask scheduleRepeatingTaskAsync(Runnable task, long delay, long period, long times) {
		return scheduleRepeatingTaskForTimes(task, delay, period, times, true);
	}

	/**
	 * Schedules something repeating
	 * @param task
	 * @param delay
	 * @param period
	 * @return task ID
	 */
	public ScheduledTask scheduleRepeatingTask(Runnable task, long delay, long period) {
		return scheduleRepeatingTask(task, delay, period, false);
	}

	/**
	 * Schedules something repeating
	 * But asynchronous
	 * @param task
	 * @param delay
	 * @param period
	 * @return task ID
	 */
	public ScheduledTask scheduleRepeatingTaskAsync(Runnable task, long delay, long period) {
		return scheduleRepeatingTask(task, delay, period, true);
	}

	/**
	 * Runs a task
	 * @param task
	 * @return task
	 */
	public ScheduledTask runTask(Runnable task) {
		ScheduledTask scheduledTask1 = this.runTask(task, false, false);
		new Thread(() -> { cancelTask(scheduledTask1);Thread.interrupted();}).start();
		return scheduledTask1;
	}

	/**
	 * Runs a task
	 * But asynchronous
	 * @param task
	 * @return task
	 */
	public ScheduledTask runTaskAsync(Runnable task) {
		ScheduledTask scheduledTask1 = runTask(task, true, false);
		new Thread(() -> { scheduledTask1.run();cancelTask(scheduledTask1);Thread.interrupted(); }).start();
		return scheduledTask1;
	}

	/**
	 * Schedules a task delayed
	 * @param task
	 * @param delay
	 * @return task
	 */
	public ScheduledTask scheduleDelayedTask(Runnable task, long delay) {
		return delayTask(task, delay, false);
	}

	/**
	 * Schedules a task delayed
	 * But asynchronous
	 * @param task
	 * @param delay
	 * @return task
	 */
	public ScheduledTask scheduleDelayedTaskAsync(Runnable task, long delay) {
		return delayTask(task, delay, true);
	}

	/**
	 * Runs a task (private Method)
	 * @param task
	 * @param async
	 * @param multipleTimes
	 * @return task
	 */
	private ScheduledTask runTask(Runnable task, boolean async, boolean multipleTimes) {
		if (task == null) {
			return null;
		}
		int id = 0;
		while (true) {
			if (!schedulerMap.containsKey(id)) {
				id++;
				break;
			}
		}
		try {
			schedulerMap.put(id, new ScheduledTask(!async, task, id, multipleTimes));
		} catch (Exception e) {
			//ignoring this error
		}
		return schedulerMap.get(id);
	}

	/**
	 * Delays Task (Private Method)
	 * @param task
	 * @param delay
	 * @param async
	 * @return task
	 */
	public ScheduledTask delayTask(Runnable task, long delay, boolean async) {
		if (delay < 0) {
			return null;
		}
		ScheduledTask t = runTask(task, async, false);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
					t.run();
					cancelTask(t);
					cancel();
					Thread.interrupted();
			}
		}, delay*50,1);
		return t;
	}

	/**
	 * Repeats task (private Method)
	 * @param task
	 * @param delay
	 * @param period
	 * @param async
	 * @return Task
	 */
	private ScheduledTask scheduleRepeatingTask(Runnable task, long delay, long period, boolean async) {
		if (period < 0) {
			return null;
		}
		if (delay < 0) {
			return null;
		}
		ScheduledTask t = runTask(task, async, true);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
					t.run();
					if(t.isCancelled()) {
					cancelTask(t);
					cancel();
					Thread.interrupted();
					}
			}
		}, delay * 50, period * 50);
		return t;
	}

	/**
	 * Repeats for times
	 * @param task
	 * @param delay
	 * @param period
	 * @param times
	 * @param async
	 * @return Task
	 */
	private ScheduledTask scheduleRepeatingTaskForTimes(Runnable task, long delay, long period, final long times, boolean async) {
		if (times <= 0) {
			return null;
		}
		if (period < 0) {
			return null;
		}
		if (delay < 0) {
			return null;
		}
		final ScheduledTask t = runTask(task, async, true);
		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
					t.run();
					if(t.isCancelled()||t.getRunTimes()>=times) {
					cancelTask(t);
					cancel();
					Thread.interrupted();
					}
			}
		}, delay * 50, period * 50);
		return t;
	}



	/**
	 * Returns instance
	 * @return
	 */
	public static Scheduler getInstance() {
		if (instance == null) {
			instance = new Scheduler();
		}
		return instance;
	}

	@Override
	public void reload() {

	}

	@Override
	public void save() {

	}
}