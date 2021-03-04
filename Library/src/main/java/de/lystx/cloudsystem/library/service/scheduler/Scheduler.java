package de.lystx.cloudsystem.library.service.scheduler;


import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;

import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class Scheduler extends CloudService {

	public final Map<Integer, Task> schedulerMap;

	public Scheduler(CloudLibrary cloudLibrary, String name, Type type) {
		super(cloudLibrary, name, type);
		this.schedulerMap = new ConcurrentHashMap<>();
	}


	public Task getTask(int id) {
		if (schedulerMap.containsKey(id))
			return schedulerMap.get(id);
		return null;
	}

	public void cancelTask(int id) {
		if (schedulerMap.containsKey(id)) {
			schedulerMap.get(id).cancel();
			schedulerMap.remove(id);
		}
	}

	public void cancelTask(Task id) {
		id.cancel();
		schedulerMap.remove(id.getId());
	}

	public void cancelAll() {
		for (Integer r : schedulerMap.keySet()) {
			schedulerMap.get(r).cancel();
		}
		schedulerMap.clear();
	}

	public int scheduleAsyncWhile(Runnable run, long delay, long repeat) {
		int taskid = new Random().nextInt(2147483647);
		Task task = new Task(true, run, taskid, false);
		this.schedulerMap.put(taskid, task);
		return task.getId();
	}

	public Task scheduleRepeatingTaskForTimes(Runnable task, long delay, long period, long times) {
		return scheduleRepeatingTaskForTimes(task, delay, period, times, false);
	}

	public Task scheduleRepeatingTaskAsync(Runnable task, long delay, long period, long times) {
		return scheduleRepeatingTaskForTimes(task, delay, period, times, true);
	}

	public Task scheduleRepeatingTask(Runnable task, long delay, long period) {
		return scheduleRepeatingTask(task, delay, period, false);
	}

	public Task scheduleRepeatingTaskAsync(Runnable task, long delay, long period) {
		return scheduleRepeatingTask(task, delay, period, true);
	}

	public Task runTask(Runnable task) {
		final Task t = runTask(task, false, false);
		new Thread(new Runnable() {
			public void run() {
				cancelTask(t);
				Thread.interrupted();
			}
		}).start();
		return t;
	}

	public Task runTaskAsync(Runnable task) {
		final Task t = runTask(task, true, false);
		new Thread(new Runnable() {
			public void run() {
				t.run();
				cancelTask(t);
				Thread.interrupted();
			}
		}).start();
		return t;
	}

	public Task scheduleDelayedTask(Runnable task, long delay) {
		return delayTask(task, delay, false);
	}


	public Task scheduleDelayedTaskAsync(Runnable task, long delay) {
		return delayTask(task, delay, true);
	}

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
		} catch (Exception ignored) {}
		return schedulerMap.get(id);
	}

	private Task delayTask(Runnable task, long delay, boolean async) {
		if (delay < 0) {
			return null;
		}
		final Task t = runTask(task, async, false);
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

	private Task scheduleRepeatingTask(Runnable task, long delay, long period, boolean async) {
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
					if(t.isCancelled()) {
					cancelTask(t);
					cancel();
					Thread.interrupted();
					}
			}
		}, delay*50, period*50);
		return t;
	}

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
					if(t.isCancelled()||t.runTimes()>=times) {
					cancelTask(t);
					cancel();
					Thread.interrupted();
					}
			}
		}, delay*50, period*50);
		return t;
	}
}