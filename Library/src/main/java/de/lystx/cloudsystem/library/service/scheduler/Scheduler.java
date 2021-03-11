package de.lystx.cloudsystem.library.service.scheduler;


import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.CloudServiceType;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Scheduler extends CloudService {

	private static Scheduler instance;
	private final Map<Integer, Task> schedulerMap;

	public Scheduler(CloudLibrary cloudLibrary, String name, CloudServiceType cloudType) {
		super(cloudLibrary, name, cloudType);
		this.schedulerMap = new ConcurrentHashMap<>();
	}

	/**
	 * Task by ID
	 * @param id
	 * @return Task by ID
	 */
	public Task getTask(int id) {
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
	public void cancelTask(Task id) {
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
		Task task = new Task(true, run, taskid, false);
		this.schedulerMap.put(taskid, task);
		return task.getId();
	}

	/**
	 * Schedules something for times
	 * @param task
	 * @param delay
	 * @param period
	 * @param times
	 * @return task ID
	 */
	public Task scheduleRepeatingTaskForTimes(Runnable task, long delay, long period, long times) {
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
	public Task scheduleRepeatingTaskAsync(Runnable task, long delay, long period, long times) {
		return scheduleRepeatingTaskForTimes(task, delay, period, times, true);
	}

	/**
	 * Schedules something repeating
	 * @param task
	 * @param delay
	 * @param period
	 * @return task ID
	 */
	public Task scheduleRepeatingTask(Runnable task, long delay, long period) {
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
	public Task scheduleRepeatingTaskAsync(Runnable task, long delay, long period) {
		return scheduleRepeatingTask(task, delay, period, true);
	}

	/**
	 * Runs a task
	 * @param task
	 * @return task
	 */
	public Task runTask(Runnable task) {
		Task task1 = this.runTask(task, false, false);
		new Thread(() -> { cancelTask(task1);Thread.interrupted();}).start();
		return task1;
	}

	/**
	 * Runs a task
	 * But asynchronous
	 * @param task
	 * @return task
	 */
	public Task runTaskAsync(Runnable task) {
		Task task1 = runTask(task, true, false);
		new Thread(() -> { task1.run();cancelTask(task1);Thread.interrupted(); }).start();
		return task1;
	}

	/**
	 * Schedules a task delayed
	 * @param task
	 * @param delay
	 * @return task
	 */
	public Task scheduleDelayedTask(Runnable task, long delay) {
		return delayTask(task, delay, false);
	}

	/**
	 * Schedules a task delayed
	 * But asynchronous
	 * @param task
	 * @param delay
	 * @return task
	 */
	public Task scheduleDelayedTaskAsync(Runnable task, long delay) {
		return delayTask(task, delay, true);
	}

	/**
	 * Runs a task (private Method)
	 * @param task
	 * @param async
	 * @param multipleTimes
	 * @return task
	 */
	private Task runTask(Runnable task, boolean async, boolean multipleTimes) {
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
			schedulerMap.put(id, new Task(!async, task, id, multipleTimes));
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
	public Task delayTask(Runnable task, long delay, boolean async) {
		if (delay < 0) {
			return null;
		}
		Task t = runTask(task, async, false);
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
	private Task scheduleRepeatingTask(Runnable task, long delay, long period, boolean async) {
		if (period < 0) {
			return null;
		}
		if (delay < 0) {
			return null;
		}
		Task t = runTask(task, async, true);
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
	private Task scheduleRepeatingTaskForTimes(Runnable task, long delay, long period, final long times, boolean async) {
		if (times <= 0) {
			return null;
		}
		if (period < 0) {
			return null;
		}
		if (delay < 0) {
			return null;
		}
		final Task t = runTask(task, async, true);
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
			instance = new Scheduler(null, "Scheduler", CloudServiceType.UTIL);
		}
		return instance;
	}

	/**
	 * Schedules something by Annotation
	 * @param object
	 * @param runnable
	 */
	public static void schedule(Object object, Runnable runnable) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		String method = ste[ste.length - 1 - 1].getMethodName();
		try {
			Method declaredMethod = object.getClass().getDeclaredMethod(method);
			Schedule schedule = declaredMethod.getAnnotation(Schedule.class);
			if (schedule.period() != -1L) {
				getInstance().scheduleRepeatingTask(runnable, schedule.delay(), schedule.period(), !schedule.sync());
			} else {
				getInstance().delayTask(runnable, schedule.delay(), !schedule.sync());
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}